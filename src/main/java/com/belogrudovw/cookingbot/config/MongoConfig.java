package com.belogrudovw.cookingbot.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        var converters = new ArrayList<>();
        converters.add(new QueueWritingConverter());
        converters.add(new QueueReadingConverter());
        return new MongoCustomConversions(converters);
    }

    @WritingConverter
    static class QueueWritingConverter implements Converter<Queue<?>, List<?>> {
        @Override
        public List<?> convert(Queue<?> source) {
            return new LinkedList<>(source);
        }
    }

    @ReadingConverter
    static class QueueReadingConverter implements Converter<List<?>, Queue<?>> {
        @Override
        public Queue<?> convert(List<?> source) {
            Queue<Object> q = new CircularFifoQueue<>(20);
            q.addAll(source);
            return q;
        }
    }
}
