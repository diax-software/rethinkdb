package com.rethinkdb.net;

import com.rethinkdb.ast.Query;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.gen.exc.ReqlRuntimeError;
import com.rethinkdb.gen.proto.ResponseType;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Cursor<T> implements Iterator<T>, Iterable<T>, Closeable {

    private static class DefaultCursor<T> extends Cursor<T> {
        public final Converter.FormatOptions fmt;
        private final Class<T> pojoClass;

        public DefaultCursor(Connection connection, Query query, Response firstResponse, Class<T> pojoClass) {
            super(connection, query, firstResponse);

            this.pojoClass = pojoClass;
            fmt = new Converter.FormatOptions(query.globalOptions);
        }

        @SuppressWarnings("unchecked")
        T getNext(Optional<Long> timeout) throws TimeoutException {

            while (items.size() == 0) {
                maybeSendContinue();
                waitOnCursorItems(timeout);

                if (items.size() != 0) {
                    break;
                }

                error.ifPresent(exc -> {
                    throw exc;
                });
            }

            Object value = Converter.convertPseudotypes(items.pop(), fmt);
            return Util.convertToPojo(value, pojoClass);
        }

        /* This isn't great, but the Java iterator protocol relies on hasNext,
         so it must be implemented in a reasonable way */
        public boolean hasNext() {
            try {
                if (items.size() > 0) {
                    return true;
                }
                if (error.isPresent()) {
                    return false;
                }
                if (_isFeed) {
                    return true;
                }

                maybeSendContinue();
                waitOnCursorItems(Optional.empty());

                return items.size() > 0;
            } catch (TimeoutException toe) {
                throw new RuntimeException("Timeout can't happen here");
            }
        }
    }

    public static <T> Cursor<T> create(Connection connection, Query query, Response firstResponse, Class<T> pojoClass) {
        return new DefaultCursor<T>(connection, query, firstResponse, pojoClass);
    }

    // public immutable members
    public final long token;
    protected final boolean _isFeed;
    // immutable members
    protected final Connection connection;
    protected final Query query;
    protected boolean alreadyIterated = false;
    protected Future<Response> awaitingContinue = null;
    protected Optional<RuntimeException> error = Optional.empty();
    // mutable members
    protected Deque<T> items = new ArrayDeque<>();
    protected int outstandingRequests = 0;
    protected int threshold = 1;

    public Cursor(Connection connection, Query query, Response firstResponse) {
        this.connection = connection;
        this.query = query;
        this.token = query.token;
        this._isFeed = firstResponse.isFeed();
        connection.addToCache(query.token, this);
        maybeSendContinue();
        extendInternal(firstResponse);
    }

    // Abstract methods
    abstract T getNext(Optional<Long> timeout) throws TimeoutException;

    public void close() {
        connection.removeFromCache(this.token);
        if (!error.isPresent()) {
            error = Optional.of(new NoSuchElementException());
            if (connection.isOpen()) {
                outstandingRequests += 1;
                connection.stop(this);
            }
        }
    }

    public Iterator<T> iterator() {
        if (!alreadyIterated) {
            alreadyIterated = true;
            return this;
        }
        throw new ReqlDriverError("The results of this query have already been consumed.");
    }

    public ArrayList<T> bufferedItems() {
        return new ArrayList<>(items);
    }

    public int bufferedSize() {
        return items.size();
    }

    void extend(Response response) {
        outstandingRequests -= 1;
        maybeSendContinue();
        extendInternal(response);
    }

    private void extendInternal(Response response) {
        threshold = response.data.length();
        if (!error.isPresent()) {
            if (response.isPartial()) {
                items.addAll(response.data);
            } else if (response.isSequence()) {
                items.addAll(response.data);
                error = Optional.of(new NoSuchElementException());
            } else {
                error = Optional.of(response.makeError(query));
            }
        }
        if (outstandingRequests == 0 && error.isPresent()) {
            connection.removeFromCache(response.token);
        }
    }

    public boolean isFeed() {
        return this._isFeed;
    }

    protected void maybeSendContinue() {
        if (!error.isPresent()
            && items.size() < threshold
            && outstandingRequests == 0) {
            outstandingRequests += 1;
            this.awaitingContinue = connection.continue_(this);
        }
    }

    public T next(long timeout) throws TimeoutException {
        return getNext(Optional.of(timeout));
    }

    void setError(String errMsg) {
        if (!error.isPresent()) {
            error = Optional.of(new ReqlRuntimeError(errMsg));
            Response dummyResponse = Response
                .make(query.token, ResponseType.SUCCESS_SEQUENCE)
                .build();
            extendInternal(dummyResponse);
        }
    }
    /**
     * Iterates over all elements of this cursor and returns them as a list
     *
     * @return The list of this cursor's elements
     */
    public List<T> toList() {
        List<T> list = new ArrayList<T>();
        forEachRemaining(list::add);
        return list;
    }

    public T next() {
        try {
            return getNext(Optional.empty());
        } catch (TimeoutException toe) {
            throw new RuntimeException("Timeout can't happen here");
        }
    }

    protected void waitOnCursorItems(Optional<Long> timeout) throws TimeoutException {
        Response res = null;
        try {
            if (timeout.isPresent()) {
                res = this.awaitingContinue.get(timeout.get(), TimeUnit.MILLISECONDS);
            } else {
                res = this.awaitingContinue.get();
            }
        } catch (TimeoutException exc) {
            throw exc;
        } catch (Exception e) {
            throw new ReqlDriverError(e);
        }
        this.extend(res);
    }

}
