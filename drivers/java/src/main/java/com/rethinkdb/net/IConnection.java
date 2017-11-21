package com.rethinkdb.net;

import com.rethinkdb.ast.ReqlAst;
import com.rethinkdb.model.OptArgs;

import java.io.Closeable;

public interface IConnection extends Closeable {
    <T> T run(ReqlAst term, OptArgs globalOpts, Class<?> pojo, Long timeout);

    void runNoReply(ReqlAst term, OptArgs globalOpts);

    default <T> T run(ReqlAst term, OptArgs globalOpts) {
        return run(term, globalOpts, null, null);
    }

    default <T> T run(ReqlAst term, Class<?> pojo) {
        return run(term, new OptArgs(), pojo, null);
    }

    default <T> T run(ReqlAst term, OptArgs globalOpts, Class<?> pojo) {
        return run(term, globalOpts, pojo, null);
    }

    default <T> T run(ReqlAst term, Long timeout) {
        return run(term, new OptArgs(), null, timeout);
    }

    default <T> T run(ReqlAst term, OptArgs globalOpts, Long timeout) {
        return run(term, globalOpts, null, timeout);
    }

    default <T> T run(ReqlAst term, Class<?> pojo, Long timeout) {
        return run(term, new OptArgs(), pojo, timeout);
    }

    default <T> T run(ReqlAst term) {
        return run(term, new OptArgs(), null, null);
    }

    default void runNoReply(ReqlAst term) {
        runNoReply(term, new OptArgs());
    }
}
