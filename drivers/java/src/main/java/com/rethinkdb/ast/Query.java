package com.rethinkdb.ast;

import com.rethinkdb.gen.proto.QueryType;
import com.rethinkdb.model.OptArgs;
import com.rethinkdb.net.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/* An instance for a query that has been sent to the server. Keeps
 * track of its token, the args to .run() it was called with, and its
 * query type.
*/

@Slf4j
public class Query {
    public static Query continue_(long token) {
        return new Query(QueryType.CONTINUE, token, null, new OptArgs());
    }

    public static Query noreplyWait(long token) {
        return new Query(QueryType.NOREPLY_WAIT, token, null, new OptArgs());
    }

    public static Query start(long token, ReqlAst term, OptArgs globalOptions) {
        return new Query(QueryType.START, token, term, globalOptions);
    }

    public static Query stop(long token) {
        return new Query(QueryType.STOP, token, null, new OptArgs());
    }

    public final OptArgs globalOptions;
    @Nullable
    public final ReqlAst term;
    public final long token;
    public final QueryType type;

    public Query(QueryType type, long token, ReqlAst term, OptArgs globalOptions) {
        this.type = type;
        this.token = token;
        this.term = term;
        this.globalOptions = globalOptions;
    }

    public Query(QueryType type, long token) {
        this(type, token, null, new OptArgs());
    }

    public ByteBuffer serialize() {
        JSONArray array = new JSONArray().put(type.value);

        if (term != null) {
            array.put(term.build());
        }

        if (!globalOptions.isEmpty()) {
            array.put(ReqlAst.buildOptarg(globalOptions));
        }

        String query = array.toString();

        byte[] output = query.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = Util.leByteBuffer(Long.BYTES + Integer.BYTES + output.length)
            .putLong(token)
            .putInt(output.length)
            .put(output);

        log.debug("JSON Send: Token: {} {}", token, query);

        return buffer;
    }
}
