package ru.ilya.io.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ilya.exceptions.RoomException;
import ru.ilya.io.JsonUtil;
import ru.ilya.model.Room;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class JsonRoomExporter {
    private static final Logger logger = LoggerFactory.getLogger(JsonRoomExporter.class);
    private static final String FILE_PATH = "core/src/main/resources/rooms.json";

    private final ObjectMapper mapper;
    private final JsonUtil jsonUtil;

    public JsonRoomExporter(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
        this.mapper = jsonUtil.getMapper();
    }

    public void exportJson(List<Room> rooms) {
        logger.info("Экспорт комнат в JSON");
        try {
            File file = new File(FILE_PATH);
            jsonUtil.createFileIfNeeded(file);
            mapper.writeValue(file, rooms);
            logger.info("Экспортировано {} комнат", rooms.size());
        } catch (IOException e) {
            logger.error("Ошибка при экспорте комнат", e);
            throw new RoomException("Ошибка при сохранении данных комнат.", e);
        }
    }
}
