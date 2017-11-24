package com.rethinkdb.net;

import com.rethinkdb.Cursor;
import com.rethinkdb.ast.Query;
import com.rethinkdb.ast.ReqlAst;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.model.Arguments;
import com.rethinkdb.model.OptArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Connection implements IConnection {
    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    // public immutable
    private final String hostname;
    private final int port;
    private final Map<Long, CompletableFuture<Response>> awaiters = new ConcurrentHashMap<>();
    private final Handshake handshake;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicLong nextToken = new AtomicLong();
    private Exception awaiterException = null;
    private Long connectTimeout;
    private Map<Long, CursorImpl<?>> cursorCache = new ConcurrentHashMap<>();
    // private mutable
    private String dbname;
    // execution stuff
    private ExecutorService exec;
    // network stuff
    private SocketWrapper socket;
    private SSLContext sslContext;

    public Connection(ConnectionBuilder builder) {
        this.dbname = builder.dbname();

        if (builder.authKey() != null && builder.user() != null) {
            throw new ReqlDriverError("Either `authKey` or `user` can be used, but not both.");
        }

        String user = builder.user(), password = builder.password();

        handshake = new Handshake(user, password);
        hostname = builder.hostname();
        port = builder.port();
        // is certFile provided? if so, it has precedence over SSLContext
        this.sslContext = Crypto.handleCertfile(builder.certFile(), builder.sslContext());
        connectTimeout = builder.timeout();
    }

    @Override
    public <T> T run(ReqlAst term, OptArgs globalOpts, Class<?> pojoClass, Long timeout) {
        setDefaultDB(globalOpts);
        Query q = Query.start(newToken(), term, globalOpts);
        if (globalOpts.containsKey("noreply")) {
            throw new ReqlDriverError(
                "Don't provide the noreply option as an optarg. " +
                    "Use `.runNoReply` instead of `.run`");
        }
        return runQuery(q, pojoClass);
    }

    @Override
    public void runNoReply(ReqlAst term, OptArgs globalOpts) {
        setDefaultDB(globalOpts);
        globalOpts.with("noreply", true);
        runQueryNoreply(Query.start(newToken(), term, globalOpts));
    }

    @Override
    public void close(boolean shouldNoreplyWait) {
        // disconnect
        try {
            if (shouldNoreplyWait) {
                noreplyWait();
            }
        } finally {
            // reset token
            nextToken.set(0);

            // clear cursor cache
            for (CursorImpl<?> cursor : cursorCache.values()) {
                cursor.setError("Connection is closed.");
            }
            cursorCache.clear();

            // handle current awaiters
            for (CompletableFuture<Response> awaiter : this.awaiters.values()) {
                // what happened?
                if (this.awaiterException != null) { // an exception
                    awaiter.completeExceptionally(this.awaiterException);
                } else { // probably canceled
                    awaiter.cancel(true);
                }
            }
            awaiters.clear();

            // terminate response pump
            if (exec != null && !exec.isShutdown()) {
                exec.shutdown();
            }

            // close the socket
            if (socket != null) socket.close();
        }

    }

    void addToCache(long token, CursorImpl<?> cursor) {
        cursorCache.put(token, cursor);
    }

    public SocketAddress clientAddress() {
        return socket == null ? null : socket.clientAddress();
    }

    public Integer clientPort() {
        return socket == null ? null : socket.clientPort();
    }

    public void connect() throws TimeoutException {
        connect(null);
    }

    public void connect(Long timeout) throws TimeoutException {
        final SocketWrapper sock = new SocketWrapper(hostname, port, sslContext, timeout != null ? timeout : connectTimeout);
        sock.connect(handshake);
        socket = sock;

        // start response pump
        exec = Executors.newSingleThreadExecutor();
        exec.submit((Runnable) () -> {
            // pump responses until canceled
            while (true) {
                // validate socket is open
                if (!isOpen()) {
                    awaiterException = new IOException("The socket is closed, exiting response pump.");
                    this.close();
                    break;
                }

                // read response and send it to whoever is waiting, if anyone
                try {
                    if (socket == null) throw new ReqlDriverError("No socket available.");
                    final Response response = this.socket.read();
                    final CompletableFuture<Response> awaiter = awaiters.remove(response.token);
                    if (awaiter != null) {
                        awaiter.complete(response);
                    }
                } catch (Exception e) {
                    awaiterException = e;
                    this.close();
                    break;
                }
            }
        });
    }

    public Future<Response> continue_(CursorImpl<?> cursor) {
        return sendQuery(Query.continue_(cursor.token));
    }

    public String db() {
        return dbname;
    }

    public boolean isOpen() {
        return socket != null && socket.isOpen();
    }

    public long newToken() {
        return nextToken.incrementAndGet();
    }

    public void noreplyWait() {
        runQuery(Query.noreplyWait(newToken()), null);
    }

    public Connection reconnect() {
        try {
            return reconnect(false, null);
        } catch (TimeoutException toe) {
            throw new RuntimeException("Timeout can't happen here.");
        }
    }

    public Connection reconnect(boolean noreplyWait, Long timeout) throws TimeoutException {
        if (timeout == null) {
            timeout = connectTimeout;
        }

        close(noreplyWait);
        connect(timeout);
        return this;
    }

    public void removeFromCache(long token) {
        cursorCache.remove(token);
    }

    /**
     * Runs a query and blocks until a response is retrieved.
     *
     * @param query
     * @param pojoClass
     * @param <T>
     * @return
     */
    private <T> T runQuery(Query query, Class<?> pojoClass) {
        Response res = null;
        try {
            res = sendQuery(query).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ReqlDriverError(e);
        }

        if (res.isAtom()) {
            try {
                Converter.FormatOptions fmt = new Converter.FormatOptions(query.globalOptions);
                Object value = ((List) Converter.convertPseudotypes(res.data, fmt)).get(0);
                return Util.convertToPojo(value, pojoClass);
            } catch (IndexOutOfBoundsException ex) {
                throw new ReqlDriverError("Atom response was empty!", ex);
            }
        } else if (res.isPartial() || res.isSequence()) {
            Cursor<?> cursor = CursorImpl.create(this, query, res, pojoClass);
            return (T) cursor;
        } else if (res.isWaitComplete()) {
            return null;
        } else {
            throw res.makeError(query);
        }
    }

    private void runQueryNoreply(Query query) {
        sendQueryNoreply(query);
    }

    /**
     * Writes a query and returns a completable future.
     * Said completable future value will eventually be set by the runnable response pump (see {@link #connect}).
     *
     * @param query the query to execute.
     * @return a completable future.
     */
    private Future<Response> sendQuery(Query query) {
        // check if response pump is running
        if (!exec.isShutdown() && !exec.isTerminated()) {
            final CompletableFuture<Response> awaiter = new CompletableFuture<>();
            awaiters.put(query.token, awaiter);
            try {
                lock.lock();
                if (socket == null) throw new ReqlDriverError("No socket available.");
                socket.write(query.serialize());
                return awaiter.toCompletableFuture();
            } finally {
                lock.unlock();
            }
        }

        // shouldn't be here
        throw new ReqlDriverError("Can't write query because response pump is not running.");
    }

    /**
     * Writes a query without waiting for a response
     *
     * @param query the query to execute.
     */
    private void sendQueryNoreply(Query query) {
        // check if response pump is running
        if (!exec.isShutdown() && !exec.isTerminated()) {
            try {
                lock.lock();
                if (socket == null) throw new ReqlDriverError("No socket available.");
                socket.write(query.serialize());
                return;
            } finally {
                lock.unlock();
            }
        }

        // shouldn't be here
        throw new ReqlDriverError("Can't write query because response pump is not running.");
    }

    private void setDefaultDB(OptArgs globalOpts) {
        if (!globalOpts.containsKey("db") && dbname != null) {
            // Only override the db global arg if the user hasn't
            // specified one already and one is specified on the connection
            globalOpts.with("db", dbname);
        }
        if (globalOpts.containsKey("db")) {
            // The db arg must be wrapped in a db ast object
            globalOpts.with("db", new Db(Arguments.make(globalOpts.get("db"))));
        }
    }

    public void stop(CursorImpl<?> cursor) {
        // While the server does reply to the stop request, we ignore that reply.
        // This works because the response pump in `connect` ignores replies for which
        // no waiter exists.
        runQueryNoreply(Query.stop(cursor.token));
    }

    public OptionalLong timeout() {
        return connectTimeout == null ? OptionalLong.empty() : OptionalLong.of(connectTimeout);
    }

    public void use(String db) {
        dbname = db;
    }
}
