package ru.ilya.io.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ilya.exceptions.RoomException;
import ru.ilya.io.JsonUtil;
import ru.ilya.model.Room;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonRoomImporter {
    private static final Logger logger = LoggerFactory.getLogger(JsonRoomImporter.class);
    private static final String FILE_PATH = "core/src/main/resources/rooms.json";

    private final ObjectMapper mapper;
    private final JsonUtil jsonUtil;

    public JsonRoomImporter(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
        this.mapper = jsonUtil.getMapper();
    }

    public List<Room> importJson() {
        logger.info("Импорт комнат из JSON");
        try {
            File file = new File(FILE_PATH);
            boolean wasCreated = !file.exists();
            jsonUtil.createFileIfNeeded(file);

            if (wasCreated || file.length() == 0) {
                logger.info("Файл комнат пуст или не существовал, возвращаем пустой список");
                return new ArrayList<>();
            }

            List<Room> rooms = mapper.readValue(file, new TypeReference<List<Room>>() {
            });
            logger.info("Импортировано {} комнат", rooms.size());
            return rooms;
        } catch (IOException e) {
            logger.error("Ошибка при импорте комнат", e);
            throw new RoomException("Ошибка при загрузке данных комнат.", e);
        }
    }
}
