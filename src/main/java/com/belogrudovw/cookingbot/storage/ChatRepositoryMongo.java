package com.belogrudovw.cookingbot.storage;

import com.belogrudovw.cookingbot.domain.Chat;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepositoryMongo extends MongoRepository<Chat, Long> {
}