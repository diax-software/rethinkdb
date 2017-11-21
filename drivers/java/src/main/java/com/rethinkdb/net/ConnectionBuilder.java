package com.rethinkdb.net;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.util.Objects;

/**
 * Connection.Builder should be used to build a Connection instance.
 */
public class ConnectionBuilder implements Cloneable {
    private String authKey;
    private InputStream certFile;
    private String dbname;
    private String hostname;
    private String password;
    private Integer port;
    private SSLContext sslContext;
    private Long timeout;
    private String user;

    public ConnectionBuilder clone() throws CloneNotSupportedException {
        ConnectionBuilder c = (ConnectionBuilder) super.clone();
        c.hostname = hostname;
        c.port = port;
        c.dbname = dbname;
        c.certFile = certFile;
        c.sslContext = sslContext;
        c.timeout = timeout;
        c.authKey = authKey;
        c.user = user;
        c.password = password;
        return c;
    }

    public ConnectionBuilder authKey(String authKey) {
        this.authKey = Objects.requireNonNull(authKey);
        return this;
    }

    public ConnectionBuilder certFile(InputStream certFile) {
        this.certFile = Objects.requireNonNull(certFile);
        return this;
    }

    public Connection connect() {
        return new Connection(this).reconnect();
    }

    public ConnectionBuilder db(String db) {
        dbname = Objects.requireNonNull(db);
        return this;
    }

    public ConnectionBuilder hostname(String hostname) {
        this.hostname = Objects.requireNonNull(hostname);
        return this;
    }

    public ConnectionPool pool() {
        return new ConnectionPool(this);
    }

    public ConnectionBuilder port(int port) {
        this.port = Objects.requireNonNull(port);
        return this;
    }

    public ConnectionBuilder sslContext(SSLContext sslContext) {
        this.sslContext = Objects.requireNonNull(sslContext);
        return this;
    }

    public ConnectionBuilder timeout(long timeout) {
        this.timeout = Objects.requireNonNull(timeout);
        return this;
    }

    public ConnectionBuilder user(String user, String password) {
        this.user = Objects.requireNonNull(user);
        this.password = Objects.requireNonNull(password);
        return this;
    }
}
