package com.rethinkdb.net;

import com.rethinkdb.Cursor;
import com.rethinkdb.ast.Query;
import com.rethinkdb.gen.exc.ReqlDriverError;
import com.rethinkdb.gen.exc.ReqlRuntimeError;
import com.rethinkdb.gen.proto.ResponseType;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CursorImpl<T> implements Cursor<T> {

    static <T> Cursor<T> create(Connection connection, Query query, Response firstResponse, Class<T> pojoClass) {
        return new CursorImpl<T>(connection, query, firstResponse, pojoClass);
    }

    // immutable members
    protected final Connection connection;
    // public immutable members
    final long token;
    private final boolean _isFeed;
    private final Converter.FormatOptions fmt;
    private final Class<T> pojoClass;
    private final Query query;
    protected RuntimeException error;
    private boolean alreadyIterated = false;
    private Future<Response> awaitingContinue = null;
    // mutable members
    private Deque<T> items = new ArrayDeque<>();
    private int outstandingRequests = 0;
    private int threshold = 1;

    public CursorImpl(Connection connection, Query query, Response firstResponse, Class<T> pojoClass) {
        this.connection = connection;
        this.query = query;
        this.token = query.token;
        this._isFeed = firstResponse.isFeed();
        connection.addToCache(query.token, this);
        maybeSendContinue();
        extendInternal(firstResponse);

        this.pojoClass = pojoClass;
        fmt = new Converter.FormatOptions(query.globalOptions);
    }

    @Override
    public ArrayList<T> bufferedItems() {
        return new ArrayList<>(items);
    }

    @Override
    public int bufferedSize() {
        return items.size();
    }

    @Override
    public boolean isFeed() {
        return this._isFeed;
    }

    @Override
    public T next(long timeout) throws TimeoutException {
        return getNext(timeout);
    }

    @Override
    public List<T> toList() {
        List<T> list = new ArrayList<T>();
        forEachRemaining(list::add);
        return list;
    }

    @Override
    public void close() {
        connection.removeFromCache(this.token);
        if (error == null) {
            error = new NoSuchElementException();
            if (connection.isOpen()) {
                outstandingRequests += 1;
                connection.stop(this);
            }
        }
    }

    /* This isn't great, but the Java iterator protocol relies on hasNext,
     so it must be implemented in a reasonable way */
    @Override
    public boolean hasNext() {
        try {
            if (items.size() > 0) {
                return true;
            }
            if (error != null) {
                return false;
            }
            if (_isFeed) {
                return true;
            }

            maybeSendContinue();
            waitOnCursorItems(null);

            return items.size() > 0;
        } catch (TimeoutException toe) {
            throw new RuntimeException("Timeout can't happen here");
        }
    }

    @Override
    public T next() {
        try {
            return getNext(null);
        } catch (TimeoutException e) {
            throw new RuntimeException("Timeout can't happen here");
        }
    }

    @Override
    public Iterator<T> iterator() {
        if (!alreadyIterated) {
            alreadyIterated = true;
            return this;
        }
        throw new ReqlDriverError("The results of this query have already been consumed.");
    }

    private void extend(Response response) {
        outstandingRequests -= 1;
        maybeSendContinue();
        extendInternal(response);
    }

    private void extendInternal(Response response) {
        threshold = response.data.length();
        if (error == null) {
            if (response.isPartial()) {
                items.addAll((Collection<? extends T>) response.data.toList());
            } else if (response.isSequence()) {
                items.addAll((Collection<? extends T>) response.data.toList());
                error = new NoSuchElementException();
            } else {
                error = response.makeError(query);
            }
        }
        if (outstandingRequests == 0 && error != null) {
            connection.removeFromCache(response.token);
        }
    }

    @SuppressWarnings("unchecked")
    private T getNext(Long timeout) throws TimeoutException {

        while (items.size() == 0) {
            maybeSendContinue();
            waitOnCursorItems(timeout);

            if (items.size() != 0) break;
            if (error != null) throw error;
        }

        Object value = Converter.convertPseudotypes(items.pop(), fmt);
        return Util.convertToPojo(value, pojoClass);
    }

    private void maybeSendContinue() {
        if (error == null
            && items.size() < threshold
            && outstandingRequests == 0) {
            outstandingRequests += 1;
            this.awaitingContinue = connection.continue_(this);
        }
    }

    void setError(String errMsg) {
        if (error == null) {
            error = new ReqlRuntimeError(errMsg);
            Response dummyResponse = Response
                .make(query.token, ResponseType.SUCCESS_SEQUENCE)
                .build();
            extendInternal(dummyResponse);
        }
    }

    private void waitOnCursorItems(Long timeout) throws TimeoutException {
        Response res = null;
        try {
            if (timeout != null) {
                res = this.awaitingContinue.get(timeout, TimeUnit.MILLISECONDS);
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
