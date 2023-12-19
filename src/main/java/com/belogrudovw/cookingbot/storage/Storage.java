package com.belogrudovw.cookingbot.storage;

import java.util.Optional;
import java.util.stream.Stream;

public interface Storage<K, V> {
    void save(V chat);

    Optional<V> get(K id);

    boolean contains(K id);

    Stream<V> all();
}
