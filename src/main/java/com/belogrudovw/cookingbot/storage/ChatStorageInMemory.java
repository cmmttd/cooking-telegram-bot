package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.util.FilesUtil;
import com.belogrudovw.cookingbot.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatStorageInMemory implements Storage<Long, Chat> {

    private final Map<Long, Chat> chats = new ConcurrentHashMap<>();

    @Value("${recovery.chats-path}")
    private String chatsFolderPath;

    @PostConstruct
    void recover() {
        if (chatsFolderPath != null) {
            log.info("Chat recovery starts from the folder: {}", chatsFolderPath);
            FilesUtil.recover(chatsFolderPath, Chat.class, this::save);
            log.info("Chat recovery completed successfully for: {}", this.all().count());
        }
    }

    @PreDestroy
    void backup() {
        if (chatsFolderPath != null) {
            log.info("Chat data backup starts from the folder: {}", chatsFolderPath);
            var chatStream = this.all().map(chat -> new Pair<>(String.valueOf(chat.getId()), chat));
            FilesUtil.backupForce(chatsFolderPath, chatStream);
            log.info("Chat data backup completed successfully for: {}", this.all().count());
        }
    }

    @Override
    public void save(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    @Override
    public Optional<Chat> findById(Long chatId) {
        return Optional.ofNullable(chats.get(chatId));
    }

    @Override
    public List<Chat> findByIds(Collection<Long> ids) {
        return chats.entrySet().stream()
                .filter(e -> ids.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .toList();
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