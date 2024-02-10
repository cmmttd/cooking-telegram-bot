package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.config.RecoveryProperties;
import com.belogrudovw.cookingbot.domain.Chat;
import com.belogrudovw.cookingbot.util.FilesUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import jakarta.annotation.PostConstruct;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "storage", name = "source", havingValue = "mongo")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatStorageMongo implements Storage<Long, Chat> {

    ChatRepositoryMongo chatRepositoryMongo;
    RecoveryProperties recoveryProperties;

    @PostConstruct
    void recover() {
        if (recoveryProperties.isNeeded() && recoveryProperties.chatsPath() != null) {
            log.info("Chat recovery starts from the folder: {}", recoveryProperties.chatsPath());
            int recovered = FilesUtil.recover(recoveryProperties.chatsPath(), Chat.class, this::save);
            log.info("Chat recovery completed successfully for: {}", recovered);
        }
    }

    @Override
    public void save(Chat chat) {
        chatRepositoryMongo.save(chat);
    }

    @Override
    public Optional<Chat> findById(Long id) {
        return chatRepositoryMongo.findById(id);
    }

    @Override
    public List<Chat> findByIds(Collection<Long> ids) {
        return chatRepositoryMongo.findAllById(ids);
    }

    @Override
    public boolean contains(Long id) {
        return chatRepositoryMongo.existsById(id);
    }

    @Override
    public Stream<Chat> all() {
        return chatRepositoryMongo.findAll().stream();
    }

    @Override
    public long size() {
        return chatRepositoryMongo.count();
    }
}