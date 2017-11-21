package com.rethinkdb.net;

import com.rethinkdb.ast.ReqlAst;
import com.rethinkdb.model.OptArgs;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionPool implements IConnection {

    private final ConnectionBuilder builder;
    private final Queue<Connection> connections;
    private boolean closeConnections = false;

    public ConnectionPool(ConnectionBuilder builder, int capacity) {
        this.builder = builder;
        this.connections = new LinkedBlockingQueue<>(capacity);
    }

    public ConnectionPool(ConnectionBuilder builder) {
        this.builder = builder;
        this.connections = new LinkedBlockingQueue<>();
    }

    @Override
    public void close() {
        closeConnections = true;
        for (Connection connection : connections) {
            connection.close();
        }
    }

    @Override
    public <T> T run(ReqlAst term, OptArgs globalOpts, Class<?> pojo, Long timeout) {
        Connection conn = null;
        try {
            conn = retrieve();
            return conn.run(term, globalOpts, pojo, timeout);
        } finally {
            if (conn != null) requeue(conn);
        }
    }

    @Override
    public void runNoReply(ReqlAst term, OptArgs globalOpts) {
        Connection conn = null;
        try {
            conn = retrieve();
            conn.runNoReply(term, globalOpts);
        } finally {
            if (conn != null) requeue(conn);
        }
    }

    private Connection retrieve() {
        Connection c = connections.poll();
        if (c == null) return builder.connect();

        if (!c.isOpen()) c.reconnect();
        return c;
    }

    public void open() {
        closeConnections = false;
    }

    private void requeue(Connection connection) {
        if (closeConnections && connection.isOpen()) {
            connection.close();
            return;
        }

        if (!connection.isOpen()) return;

        if (!connections.offer(connection)) {
            connection.close();
        }
    }

    public void withConnection(Consumer<Connection> consumer) {
        Connection conn = null;
        try {
            conn = retrieve();
            consumer.accept(conn);
        } finally {
            if (conn != null) requeue(conn);
        }
    }

    public <T> T withConnection(Function<Connection, T> consumer) {
        Connection conn = null;
        try {
            conn = retrieve();
            return consumer.apply(conn);
        } finally {
            if (conn != null) requeue(conn);
        }
    }
}
