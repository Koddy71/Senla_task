package ru.ilya.io;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private final ObjectMapper mapper;

    public JsonUtil() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void createFileIfNeeded(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                logger.error("Не удалось создать директорию: {}", parent.getPath());
                throw new IOException("Не удалось создать директорию: " + parent.getPath());
            }
        }
        if (!file.exists()) {
            if (!file.createNewFile()) {
                logger.error("Не удалось создать файл: {}", file.getPath());
                throw new IOException("Не удалось создать файл: " + file.getPath());
            }
        }
    }
}
