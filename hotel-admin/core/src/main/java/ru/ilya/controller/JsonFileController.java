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
        logger.info("Start processing command: saveGuestsToJson");
        try {
            File file = new File(GUESTS_FILE_PATH);
            createFileIfNeeded(file);
            mapper.writeValue(file, guests);
            logger.info("saveGuestsToJson processed successfully: {} guests saved", guests.size());
        } catch (IOException e) {
            logger.error("Error processing saveGuestsToJson", e);
            throw new RuntimeException("Ошибка при сохранении данных гостей.", e);
        }
    }

    public void saveRooms(List<Room> rooms) {
        logger.info("Start processing command: saveRoomsToJson");
        try {
            File file = new File(ROOMS_FILE_PATH);
            createFileIfNeeded(file);
            mapper.writeValue(file, rooms);
            logger.info("saveRoomsToJson processed successfully: {} rooms saved", rooms.size());
        } catch (IOException e) {
            logger.error("Error processing saveRoomsToJson", e);
            throw new RuntimeException("Ошибка при сохранении данных комнат.", e);
        }
    }

    public void saveServices(List<Service> services) {
        logger.info("Start processing command: saveServicesToJson");
        try {
            File file = new File(SERVICES_FILE_PATH);
            createFileIfNeeded(file);
            mapper.writeValue(file, services);
            logger.info("saveServicesToJson processed successfully: {} services saved", services.size());
        } catch (IOException e) {
            logger.error("Error processing saveServicesToJson", e);
            throw new RuntimeException("Ошибка при сохранении данных услуг.", e);
        }
    }

    public List<Guest> loadGuests() {
        logger.info("Start processing command: loadGuestsFromJson");
        try {
            File file = new File(GUESTS_FILE_PATH);
            boolean wasCreated = !file.exists();
            createFileIfNeeded(file);

            if (wasCreated) {
                logger.info("Guests file did not exist. Created new empty file.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                logger.info("Guests file exists but is empty.");
                return new ArrayList<>();
            }

            List<Guest> guests = mapper.readValue(file, new TypeReference<List<Guest>>() {
            });
            logger.info("loadGuestsFromJson processed successfully: {} guests loaded", guests.size());
            return guests;

        } catch (IOException e) {
            logger.error("Error processing loadGuestsFromJson", e);
            throw new RuntimeException("Ошибка при загрузке данных гостей.", e);
        }
    }

    public List<Room> loadRooms() {
        logger.info("Start processing command: loadRoomsFromJson");
        try {
            File file = new File(ROOMS_FILE_PATH);
            boolean wasCreated = !file.exists();
            createFileIfNeeded(file);

            if (wasCreated) {
                logger.info("Rooms file did not exist. Created new empty file.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                logger.info("Rooms file exists but is empty.");
                return new ArrayList<>();
            }

            List<Room> rooms = mapper.readValue(file, new TypeReference<List<Room>>() {
            });
            logger.info("loadRoomsFromJson processed successfully: {} rooms loaded", rooms.size());
            return rooms;

        } catch (IOException e) {
            logger.error("Error processing loadRoomsFromJson", e);
            throw new RuntimeException("Ошибка при загрузке данных комнат.", e);
        }
    }

    public List<Service> loadServices() {
        logger.info("Start processing command: loadServicesFromJson");
        try {
            File file = new File(SERVICES_FILE_PATH);
            boolean wasCreated = !file.exists();
            createFileIfNeeded(file);

            if (wasCreated) {
                logger.info("Services file did not exist. Created new empty file.");
                return new ArrayList<>();
            }

            if (file.length() == 0) {
                logger.info("Services file exists but is empty.");
                return new ArrayList<>();
            }

            List<Service> services = mapper.readValue(file, new TypeReference<List<Service>>() {
            });
            int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
            Service.setIdCounter(maxId + 1);

            logger.info("loadServicesFromJson processed successfully: {} services loaded", services.size());
            return services;

        } catch (IOException e) {
            logger.error("Error processing loadServicesFromJson", e);
            throw new RuntimeException("Ошибка при загрузке данных услуг.", e);
        }
    }

    private void createFileIfNeeded(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                logger.error("Failed to create directory: {}", parent.getPath());
                throw new IOException("Не удалось создать директорию: " + parent.getPath());
            }
        }
        if (!file.exists()) {
            if (!file.createNewFile()) {
                logger.error("Failed to create file: {}", file.getPath());
                throw new IOException("Не удалось создать файл: " + file.getPath());
            }
        }
    }
}
