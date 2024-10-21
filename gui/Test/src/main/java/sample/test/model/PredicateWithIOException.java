package sample.test.model;

import java.io.IOException;

@FunctionalInterface
public interface PredicateWithIOException<T> {
    boolean test(T t) throws IOException;
}

