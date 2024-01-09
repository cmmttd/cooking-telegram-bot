package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Chat;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class ChatStorageInMemory implements Storage<Long, Chat> {

    private final Map<Long, Chat> chats = new ConcurrentHashMap<>();

    @Override
    public void save(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    @Override
    public Optional<Chat> get(Long id) {
        return Optional.ofNullable(chats.get(id));
    }

    @Override
    public boolean contains(Long id) {
        return chats.containsKey(id);
    }

    @Override
    public Stream<Chat> all() {
        return chats.values().stream();
    }
}