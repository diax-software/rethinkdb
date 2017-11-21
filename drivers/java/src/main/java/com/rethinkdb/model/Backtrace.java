package com.rethinkdb.model;

import org.json.JSONArray;

import java.util.Optional;

public class Backtrace {

    public static Optional<Backtrace> fromJSONArray(JSONArray rawBacktrace) {
        if (rawBacktrace == null || rawBacktrace.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new Backtrace(rawBacktrace));
        }
    }

    private JSONArray rawBacktrace;

    private Backtrace(JSONArray rawBacktrace) {
        this.rawBacktrace = rawBacktrace;
    }

    public JSONArray getRawBacktrace() {
        return rawBacktrace;
    }
}
