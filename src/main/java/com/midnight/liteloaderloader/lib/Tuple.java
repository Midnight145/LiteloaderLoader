package com.midnight.liteloaderloader.lib;

import java.util.Objects;

public class Tuple<T, J> {

    protected final T first;
    protected final J second;

    public Tuple(T first, J second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public J getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple<?, ?>tuple)) return false;
        return Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Tuple{" + "first=" + first + ", second=" + second + '}';
    }

    public static <T, J> Tuple<T, J> of(T first, J second) {
        return new Tuple<>(first, second);
    }
}
