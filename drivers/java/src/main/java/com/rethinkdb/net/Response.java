package com.rethinkdb.net;

import com.rethinkdb.ErrorBuilder;
import com.rethinkdb.ast.Query;
import com.rethinkdb.gen.exc.ReqlError;
import com.rethinkdb.gen.proto.ErrorType;
import com.rethinkdb.gen.proto.ResponseNote;
import com.rethinkdb.gen.proto.ResponseType;
import com.rethinkdb.model.Backtrace;
import com.rethinkdb.model.Profile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class Response {
    static class Builder {
        Backtrace backtrace = null;
        JSONArray data = new JSONArray();
        ErrorType errorType = null;
        List<ResponseNote> notes = new ArrayList<>();
        Profile profile;
        ResponseType responseType;
        long token;

        Builder(long token, ResponseType responseType) {
            this.token = token;
            this.responseType = responseType;
        }

        Response build() {
            return new Response(
                token,
                responseType,
                data,
                notes,
                profile,
                backtrace,
                errorType
            );
        }

        Builder setBacktrace(JSONArray backtrace) {
            this.backtrace = Backtrace.fromJSONArray(backtrace);
            return this;
        }

        Builder setData(JSONArray data) {
            if (data != null) {
                this.data = data;
            }
            return this;
        }

        Builder setErrorType(int value) {
            this.errorType = ErrorType.fromValue(value);
            return this;
        }

        Builder setNotes(List<ResponseNote> notes) {
            this.notes.addAll(notes);
            return this;
        }

        Builder setProfile(JSONArray profile) {
            this.profile = Profile.fromJSONArray(profile);
            return this;
        }
    }

    static final Logger logger = LoggerFactory.getLogger(Query.class);

    public static Response parseFrom(long token, ByteBuffer buf) {
        if (Response.logger.isDebugEnabled()) {
            Response.logger.debug(
                "JSON Recv: Token: {} {}", token, Util.bufferToString(buf));
        }
        JSONObject jsonResp = Util.toJSON(buf);
        ResponseType responseType = ResponseType.fromValue(
            ((Long) jsonResp.get("t")).intValue()
        );
        ArrayList<Long> responseNoteVals = (ArrayList<Long>) jsonResp
            .getOrDefault("n", new ArrayList());
        ArrayList<ResponseNote> responseNotes = responseNoteVals
            .stream()
            .map(Long::intValue)
            .map(ResponseNote::maybeFromValue)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toCollection(ArrayList::new));
        Builder res = new Builder(token, responseType);
        if (jsonResp.containsKey("e")) {
            res.setErrorType(((Long) jsonResp.get("e")).intValue());
        }
        return res.setNotes(responseNotes)
            .setProfile((JSONArray) jsonResp.optJSONArray("p"))
            .setBacktrace((JSONArray) jsonResp.optJSONArray("b"))
            .setData((JSONArray) jsonResp.optJSONArray("r"))
            .build();
    }

    static Builder make(long token, ResponseType type) {
        return new Builder(token, type);
    }

    public final Optional<Backtrace> backtrace;
    public final JSONArray data;
    public final Optional<ErrorType> errorType;
    public final ArrayList<ResponseNote> notes;
    public final Optional<Profile> profile;
    public final long token;
    public final ResponseType type;

    private Response(long token,
        ResponseType responseType,
        JSONArray data,
        List<ResponseNote> responseNotes,
        Profile profile,
        Backtrace backtrace,
        ErrorType errorType
    ) {
        this.token = token;
        this.type = responseType;
        this.data = data;
        this.notes = responseNotes;
        this.profile = profile;
        this.backtrace = backtrace;
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "Response{" +
            "token=" + token +
            ", type=" + type +
            ", notes=" + notes +
            ", data=" + data +
            ", profile=" + profile +
            ", backtrace=" + backtrace +
            '}';
    }

    /* What type of success the response contains */
    boolean isAtom() {
        return type == ResponseType.SUCCESS_ATOM;
    }

    /* Whether the response is any kind of error */
    boolean isError() {
        return type.isError();
    }

    /* Whether the response is any kind of feed */
    boolean isFeed() {
        return notes.stream().anyMatch(ResponseNote::isFeed);
    }

    boolean isPartial() {
        return type == ResponseType.SUCCESS_PARTIAL;
    }

    boolean isSequence() {
        return type == ResponseType.SUCCESS_SEQUENCE;
    }

    boolean isWaitComplete() {
        return type == ResponseType.WAIT_COMPLETE;
    }

    ReqlError makeError(Query query) {
        String msg = data.size() > 0 ?
            (String) data.get(0)
            : "Unknown error message";
        return new ErrorBuilder(msg, type)
            .setBacktrace(backtrace)
            .setErrorType(errorType)
            .setTerm(query)
            .build();
    }
}
