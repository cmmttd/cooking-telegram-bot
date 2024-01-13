package com.belogrudovw.cookingbot.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public final class FilesUtil {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static <T> void recover(String folderPath, Class<T> clazz, Consumer<T> consumer) {
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            Iterator<Path> iterator = paths.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                if (Files.isRegularFile(path)) {
                    String readString = Files.readString(path);
                    var parsedObject = objectMapper.readValue(readString, clazz);
                    consumer.accept(parsedObject);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to recover from " + folderPath, e);
        }
    }

    public static <T> void backup(String folderPath, Stream<Pair<String, T>> values) {
        values
                .forEach(pair -> {
                    String fileName = pair.key();
                    try {
                        Path path = Paths.get(folderPath + fileName + ".json");
                        Files.createFile(path);
                        String json = objectMapper.writeValueAsString(pair.value());
                        Files.writeString(path, json);
                    } catch (FileAlreadyExistsException e) {
                        log.warn("File already exists: {}", fileName + ".json");
                    } catch (JsonProcessingException e) {
                        log.error("Json parsing exception: {}", fileName, e);
                    } catch (IOException e) {
                        log.error("Recipe to file writing error: {}", fileName, e);
                    }
                });
    }
}