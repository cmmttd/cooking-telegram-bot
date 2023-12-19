package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Chat;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class InMemoryChatStorage implements Storage<Long, Chat> {

    private static final Map<Long, Chat> CACHE = new ConcurrentHashMap<>();

    @Override
    public void save(Chat chat) {
        CACHE.put(chat.getId(), chat);
    }

    @Override
    public Optional<Chat> get(Long id) {
        return Optional.ofNullable(CACHE.get(id));
    }

    @Override
    public boolean contains(Long id) {
        return CACHE.containsKey(id);
    }

    @Override
    public Stream<Chat> all() {
        return CACHE.values().stream();
    }
}