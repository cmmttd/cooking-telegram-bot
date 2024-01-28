package com.belogrudovw.cookingbot.util;

import java.io.FileNotFoundException;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@Slf4j
@UtilityClass
public final class FilesUtil {

    private static final ObjectMapper objectMapper;
    private static final ResourcePatternResolver resourceResolver;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        resourceResolver = new PathMatchingResourcePatternResolver();
    }

    public static String recoverString(String fileUri) {
        try {
            Resource resource = resourceResolver.getResource(CLASSPATH_URL_PREFIX + fileUri);
            if (resource.isReadable()) {
                return Files.readString(Paths.get(resource.getURI()));
            }
            throw new FileNotFoundException("File not found: " + fileUri);
        } catch (IOException e) {
            throw new RuntimeException("Failed to recover from " + fileUri, e);
        }
    }

    // TODO: 28/01/2024 Remove crappy recovery
    public static <T> void recover(String folderUri, Class<T> clazz, Consumer<T> consumer) {
        try (Stream<Path> paths = Files.walk(Paths.get(folderUri))) {
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
            throw new RuntimeException("Failed to recover from " + folderUri, e);
        }
    }

    public static <T> void backup(String folderUri, Stream<Pair<String, T>> values) {
        values.forEach(writeFile(folderUri, false));
    }

    public static <T> void backupForce(String folderUri, Stream<Pair<String, T>> values) {
        values.forEach(writeFile(folderUri, true));
    }

    private static <T> Consumer<Pair<String, T>> writeFile(String folderUri, boolean isForceRewrite) {
        return pair -> {
            String fileName = pair.key() + ".json";
            try {
                Path path = Paths.get(folderUri + fileName);
                if (isForceRewrite && Files.exists(path)) {
                    Files.delete(path);
                }
                Files.createFile(path);
                String json = objectMapper.writeValueAsString(pair.value());
                Files.writeString(path, json);
                log.info("File saved: {}", fileName);
            } catch (FileAlreadyExistsException e) {
                log.debug("File already exists: {}", fileName);
            } catch (JsonProcessingException e) {
                log.error("Json parsing exception: {}", fileName, e);
            } catch (IOException e) {
                log.error("Recipe to file writing error: {}", fileName, e);
            }
        };
    }
}