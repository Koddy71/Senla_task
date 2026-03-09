package ru.ilya.io.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ilya.exceptions.GuestException;
import ru.ilya.io.JsonUtil;
import ru.ilya.model.Guest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class JsonGuestExporter {
    private static final Logger logger = LoggerFactory.getLogger(JsonGuestExporter.class);
    private static final String FILE_PATH = "core/src/main/resources/guests.json";

    private final ObjectMapper mapper;
    private final JsonUtil jsonUtil;

    public JsonGuestExporter(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
        this.mapper = jsonUtil.getMapper();
    }

    public void exportJson(List<Guest> guests) {
        logger.info("Экспорт гостей в JSON");
        try {
            File file = new File(FILE_PATH);
            jsonUtil.createFileIfNeeded(file);
            mapper.writeValue(file, guests);
            logger.info("Экспортировано {} гостей", guests.size());
        } catch (IOException e) {
            logger.error("Ошибка при экспорте гостей", e);
            throw new GuestException("Ошибка при сохранении данных гостей.", e);
        }
    }
}
