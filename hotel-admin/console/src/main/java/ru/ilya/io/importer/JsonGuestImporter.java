package ru.ilya.io.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ilya.exceptions.GuestException;
import ru.ilya.io.JsonUtil;
import ru.ilya.model.Guest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonGuestImporter {
    private static final Logger logger = LoggerFactory.getLogger(JsonGuestImporter.class);
    private static final String FILE_PATH = "core/src/main/resources/guests.json";

    private final ObjectMapper mapper;
    private final JsonUtil jsonUtil;

    public JsonGuestImporter(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
        this.mapper = jsonUtil.getMapper();
    }

    public List<Guest> importJson() {
        logger.info("Импорт гостей из JSON");
        try {
            File file = new File(FILE_PATH);
            boolean wasCreated = !file.exists();
            jsonUtil.createFileIfNeeded(file);

            if (wasCreated || file.length() == 0) {
                logger.info("Файл гостей пуст или не существовал, возвращаем пустой список");
                return new ArrayList<>();
            }

            List<Guest> guests = mapper.readValue(file, new TypeReference<List<Guest>>() {
            });
            logger.info("Импортировано {} гостей", guests.size());
            return guests;
        } catch (IOException e) {
            logger.error("Ошибка при импорте гостей", e);
            throw new GuestException("Ошибка при загрузке данных гостей.", e);
        }
    }
}
