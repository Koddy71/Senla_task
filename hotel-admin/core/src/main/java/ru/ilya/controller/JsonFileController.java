package ru.ilya.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonFileController {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileController.class);

    private static final String GUESTS_FILE_PATH = "core/src/main/resources/guests.json";
    private static final String ROOMS_FILE_PATH = "core/src/main/resources/rooms.json";
    private static final String SERVICES_FILE_PATH = "core/src/main/resources/services.json";

    private final ObjectMapper mapper;

    public JsonFileController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void saveGuests(List<Guest> guests) {
        logger.info("Начало обработки команды: saveGuestsToJson");
        try {
            File file = new File(GUESTS_FILE_PATH);
            createFileIfNeeded(file);
            mapper.writeValue(file, guests);
            logger.info("saveGuestsToJson успешно выполнен: сохранено {} гостей", guests.size());
        } catch (IOException e) {
            logger.error("Ошибка при выполнении saveGuestsToJson", e);
            throw new RuntimeException("Ошибка при сохранении данных гостей.", e);
        }
    }

    public void saveRooms(List<Room> rooms) {
        logger.info("Начало обработки команды: saveRoomsToJson");
        try {
            File file = new File(ROOMS_FILE_PATH);
            createFileIfNeeded(file);
            mapper.writeValue(file, rooms);
            logger.info("saveRoomsToJson успешно выполнен: сохранено {} комнат", rooms.size());
        } catch (IOException e) {
            logger.error("Ошибка при выполнении saveRoomsToJson", e);
            throw new RuntimeException("Ошибка при сохранении данных комнат.", e);
        }
    }

    public void saveServices(List<Service> services) {
        logger.info("Начало обработки команды: saveServicesToJson");
        try {
            File file = new File(SERVICES_FILE_PATH);
            createFileIfNeeded(file);
            mapper.writeValue(file, services);
            logger.info("saveServicesToJson успешно выполнен: сохранено {} услуг", services.size());
        } catch (IOException e) {
            logger.error("Ошибка при выполнении saveServicesToJson", e);
            throw new RuntimeException("Ошибка при сохранении данных услуг.", e);
        }
    }

    public List<Guest> loadGuests() {
        logger.info("Начало обработки команды: loadGuestsFromJson");
        try {
            File file = new File(GUESTS_FILE_PATH);
            boolean wasCreated = !file.exists();
            createFileIfNeeded(file);

            if (wasCreated) {
                logger.info("Файл с гостями не существовал. Создан новый пустой файл.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                logger.info("Файл с гостями существует, но пуст.");
                return new ArrayList<>();
            }

            List<Guest> guests = mapper.readValue(file, new TypeReference<List<Guest>>() {
            });
            logger.info("loadGuestsFromJson успешно выполнен: загружено {} гостей", guests.size());
            return guests;

        } catch (IOException e) {
            logger.error("Ошибка при выполнении loadGuestsFromJson", e);
            throw new RuntimeException("Ошибка при загрузке данных гостей.", e);
        }
    }

    public List<Room> loadRooms() {
        logger.info("Начало обработки команды: loadRoomsFromJson");
        try {
            File file = new File(ROOMS_FILE_PATH);
            boolean wasCreated = !file.exists();
            createFileIfNeeded(file);

            if (wasCreated) {
                logger.info("Файл с комнатами не существовал. Создан новый пустой файл.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                logger.info("Файл с комнатами существует, но пуст.");
                return new ArrayList<>();
            }

            List<Room> rooms = mapper.readValue(file, new TypeReference<List<Room>>() {
            });
            logger.info("loadRoomsFromJson успешно выполнен: загружено {} комнат", rooms.size());
            return rooms;

        } catch (IOException e) {
            logger.error("Ошибка при выполнении loadRoomsFromJson", e);
            throw new RuntimeException("Ошибка при загрузке данных комнат.", e);
        }
    }

    public List<Service> loadServices() {
        logger.info("Начало обработки команды: loadServicesFromJson");
        try {
            File file = new File(SERVICES_FILE_PATH);
            boolean wasCreated = !file.exists();
            createFileIfNeeded(file);

            if (wasCreated) {
                logger.info("Файл с услугами не существовал. Создан новый пустой файл.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                logger.info("Файл с услугами существует, но пуст.");
                return new ArrayList<>();
            }

            List<Service> services = mapper.readValue(file, new TypeReference<List<Service>>() {
            });
            int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
            Service.setIdCounter(maxId + 1);

            logger.info("loadServicesFromJson успешно выполнен: загружено {} услуг", services.size());
            return services;

        } catch (IOException e) {
            logger.error("Ошибка при выполнении loadServicesFromJson", e);
            throw new RuntimeException("Ошибка при загрузке данных услуг.", e);
        }
    }

    private void createFileIfNeeded(File file) throws IOException {
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