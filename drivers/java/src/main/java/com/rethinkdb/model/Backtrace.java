package com.rethinkdb.model;

import org.json.JSONArray;

public class Backtrace {
    public static Backtrace fromJSONArray(JSONArray rawBacktrace) {
        if (rawBacktrace == null || rawBacktrace.length() == 0) return null;
        return new Backtrace(rawBacktrace);
    }

    private JSONArray rawBacktrace;

    private Backtrace(JSONArray rawBacktrace) {
        this.rawBacktrace = rawBacktrace;
    }

    public JSONArray getRawBacktrace() {
        return rawBacktrace;
    }
}
