package com.rethinkdb;

import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Cursor<T> extends Iterable<T>, Iterator<T>, Closeable {
    List<T> bufferedItems();

    int bufferedSize();

    boolean isFeed();

    /**
     * Returns the next element in the iteration, waiting up to the
     * specified wait time if necessary for an element to become available.
     *
     * @param timeout how long to wait before giving up, in milliseconds
     * @return the next element in the iteration
     * @throws TimeoutException if the given timeout elapses before
     *                          any task successfully completes
     */
    T next(long timeout) throws TimeoutException;

    /**
     * Iterates over all elements of this cursor and returns them as a list
     *
     * @return The list of this cursor's elements
     */
    List<T> toList();

    /**
     * Returns the next element in the iteration, waiting up to the
     * specified wait time if necessary for an element to become available.
     *
     * @param timeout how long to wait before giving up, in units of
     *                {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the
     *                {@code timeout} parameter
     * @return the next element in the iteration
     * @throws TimeoutException if the given timeout elapses before
     *                          any task successfully completes
     */
    default T next(long timeout, TimeUnit unit) throws TimeoutException {
        return next(unit.toMillis(timeout));
    }
}
