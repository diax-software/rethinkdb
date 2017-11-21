package com.rethinkdb.ast;

import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.gen.proto.TermType;
import com.rethinkdb.model.Arguments;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.IConnection;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all reql queries.
 */
public class ReqlAst {
    public static Map<String, Object> buildOptarg(OptArgs opts) {
        Map<String, Object> result = new HashMap<>(opts.size());
        opts.forEach((name, arg) -> result.put(name, arg.build()));
        return result;
    }

    protected final Arguments args;
    protected final OptArgs optargs;
    protected final TermType termType;

    protected ReqlAst(TermType termType, Arguments args, OptArgs optargs) {
        if (termType == null) {
            throw new ReqlDriverError("termType can't be null!");
        }

        this.termType = termType;
        this.args = args != null ? args : new Arguments();
        this.optargs = optargs != null ? optargs : new OptArgs();
    }

    @Override
    public String toString() {
        return "ReqlAst{" +
            "termType=" + termType +
            ", args=" + args +
            ", optargs=" + optargs +
            '}';
    }

    protected Object build() {
        // Create a JSON object from the Ast
        JSONArray list = new JSONArray()
            .put(termType.value)
            .put(args.isEmpty() ?
                new JSONArray() :
                new JSONArray(args.stream().map(ReqlAst::build).toArray())
            );

        if (optargs.size() > 0) {
            list.put(buildOptarg(optargs));
        }

        return list;
    }

    /**
     * Runs this query via connection {@code conn} with default options and returns an atom result
     * or a sequence result as a cursor. The atom result either has a primitive type (e.g., {@code Integer})
     * or represents a JSON object as a {@code Map<String, Object>}. The cursor is a {@code com.rethinkdb.net.Cursor}
     * which may be iterated to get a sequence of atom results
     *
     * @param conn The connection to run this query
     * @param <T>  The type of result
     * @return The result of this query
     */
    public <T> T run(IConnection conn) {
        return conn.run(this, new OptArgs());
    }

    /**
     * Runs this query via connection {@code conn} with options {@code runOpts} and returns an atom result
     * or a sequence result as a cursor. The atom result either has a primitive type (e.g., {@code Integer})
     * or represents a JSON object as a {@code Map<String, Object>}. The cursor is a {@code com.rethinkdb.net.Cursor}
     * which may be iterated to get a sequence of atom results
     *
     * @param conn    The connection to run this query
     * @param runOpts The options to run this query with
     * @param <T>     The type of result
     * @return The result of this query
     */
    public <T> T run(IConnection conn, OptArgs runOpts) {
        return conn.run(this, runOpts);
    }

    /**
     * Runs this query via connection {@code conn} with default options and returns an atom result
     * or a sequence result as a cursor. The atom result representing a JSON object is converted
     * to an object of type {@code Class<P>} specified with {@code pojoClass}. The cursor
     * is a {@code com.rethinkdb.net.Cursor} which may be iterated to get a sequence of atom results
     * of type {@code Class<P>}
     *
     * @param conn      The connection to run this query
     * @param pojoClass The class of POJO to convert to
     * @param <T>       The type of result
     * @param <P>       The type of POJO to convert to
     * @return The result of this query (either a {@code P or a Cursor<P>}
     */
    public <T, P> T run(IConnection conn, Class<P> pojoClass) {
        return conn.run(this, new OptArgs(), pojoClass);
    }

    /**
     * Runs this query via connection {@code conn} with options {@code runOpts} and returns an atom result
     * or a sequence result as a cursor. The atom result representing a JSON object is converted
     * to an object of type {@code Class<P>} specified with {@code pojoClass}. The cursor
     * is a {@code com.rethinkdb.net.Cursor} which may be iterated to get a sequence of atom results
     * of type {@code Class<P>}
     *
     * @param conn      The connection to run this query
     * @param runOpts   The options to run this query with
     * @param pojoClass The class of POJO to convert to
     * @param <T>       The type of result
     * @param <P>       The type of POJO to convert to
     * @return The result of this query (either a {@code P or a Cursor<P>}
     */
    public <T, P> T run(IConnection conn, OptArgs runOpts, Class<P> pojoClass) {
        return conn.run(this, runOpts, pojoClass);
    }

    public void runNoReply(IConnection conn) {
        conn.runNoReply(this, new OptArgs());
    }

    public void runNoReply(IConnection conn, OptArgs globalOpts) {
        conn.runNoReply(this, globalOpts);
    }
}
