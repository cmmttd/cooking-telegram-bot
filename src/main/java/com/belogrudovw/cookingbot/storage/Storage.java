package com.belogrudovw.cookingbot.storage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;

@Validated
public interface Storage<K, V> {
    void save(@Valid V v);

    Optional<V> findById(K id);

    List<V> findByIds(Collection<K> ids);

    boolean contains(K id);

    Stream<V> all();

    long size();
}