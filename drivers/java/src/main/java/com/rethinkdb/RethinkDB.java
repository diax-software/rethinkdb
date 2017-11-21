package com.rethinkdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rethinkdb.gen.model.TopLevel;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.ConnectionBuilder;

public class RethinkDB extends TopLevel {

    /**
     * The Singleton to use to begin interacting with RethinkDB Driver
     */
    public static final RethinkDB r = new RethinkDB();

    private static ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    public static void setObjectMapper(ObjectMapper mapper1) {
        mapper = mapper1;
    }

    public ConnectionBuilder connection() {
        return Connection.build();
    }
}
