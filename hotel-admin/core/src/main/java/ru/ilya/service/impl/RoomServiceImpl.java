package ru.ilya.service.impl;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.autodi.Inject;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private Map<Integer, Room> rooms = new HashMap<>();

    @Inject
    private AppConfig appConfig;

    public RoomServiceImpl() {
        logger.info("RoomServiceImpl инициализирован");
    }

    @Override
    public boolean addRoom(Room room) {
        logger.info("Начало добавления комнаты");
        if (room == null || rooms.containsKey(room.getNumber())) {
            logger.info("Добавление комнаты не выполнено");
            return false;
        }
        rooms.put(room.getNumber(), room);
        logger.info("Добавление комнаты завершено успешно. Номер: {}", room.getNumber());
        return true;
    }

    @Override
    public boolean removeRoom(int number) {
        logger.info("Начало удаления комнаты с номером {}", number);
        boolean result = rooms.remove(number) != null;
        logger.info("Удаление комнаты завершено. Результат: {}", result);
        return result;
    }

    @Override
    public Room findRoom(int number) {
        logger.info("Поиск комнаты с номером {}", number);
        Room room = rooms.get(number);
        logger.info("Поиск комнаты завершён");
        return room;
    }

    @Override
    public boolean checkIn(int number) {
        logger.info("Начало заселения в комнату {}", number);
        Room room = findRoom(number);
        boolean result = room != null && room.isFreeOn(LocalDate.now());
        logger.info("Заселение завершено. Результат: {}", result);
        return result;
    }

    @Override
    public boolean checkOut(int number) {
        logger.info("Начало выселения из комнаты {}", number);
        Room room = findRoom(number);
        boolean result = room != null;
        logger.info("Выселение завершено. Результат: {}", result);
        return result;
    }

    @Override
    public boolean changeStatus(int number, RoomStatus status) {
        logger.info("Начало изменения статуса комнаты {} на {}", number, status);
        if (!appConfig.isRoomStatusChangeEnable()) {
            System.out.println("Изменение статуса номеров отключено в настройках.");
            logger.info("Изменение статуса не выполнено (отключено в конфигурации)");
            return false;
        }

        Room room = findRoom(number);
        if (room != null) {
            room.setStatus(status);
            logger.info("Изменение статуса завершено успешно");
            return true;
        }
        logger.info("Изменение статуса не выполнено: комната {} не найдена", number);
        return false;
    }

    public boolean changePrice(int number, int newPrice) {
        logger.info("Начало изменения цены комнаты {} на {}", number, newPrice);
        Room room = findRoom(number);
        if (room != null && newPrice > 0) {
            room.setPrice(newPrice);
            logger.info("Изменение цены завершено успешно");
            return true;
        }
        logger.info("Изменение цены не выполнено");
        return false;
    }

    @Override
    public List<Room> getAllRooms() {
        logger.info("Получение списка всех комнат");
        List<Room> result = new ArrayList<>(rooms.values());
        logger.info("Получение списка завершено. Найдено комнат: {}", result.size());
        return result;
    }

    @Override
    public List<Room> getFreeRooms() {
        logger.info("Получение списка свободных комнат на сегодня");
        List<Room> freeRooms = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Room r : rooms.values()) {
            if (r.isFreeOn(today)) {
                freeRooms.add(r);
            }
        }
        logger.info("Получение свободных комнат завершено. Найдено: {}", freeRooms.size());
        return freeRooms;
    }

    @Override
    public int countFreeRooms() {
        logger.info("Подсчёт количества свободных комнат на сегодня");
        int count = 0;
        LocalDate today = LocalDate.now();
        for (Room r : rooms.values()) {
            if (r.isFreeOn(today)) {
                count++;
            }
        }
        logger.info("Подсчёт завершён. Свободных комнат: {}", count);
        return count;
    }

    @Override
    public List<Room> getRoomsFreeByDate(LocalDate date) {
        logger.info("Получение списка комнат, свободных на дату {}", date);
        List<Room> freeByDate = new ArrayList<>();
        for (Room r : rooms.values()) {
            if (r.isFreeOn(date)) {
                freeByDate.add(r);
            }
        }
        logger.info("Получение завершено. Найдено комнат: {}", freeByDate.size());
        return freeByDate;
    }

    @Override
    public List<Room> getRoomsSorted(String sortBy) {
        logger.info("Начало сортировки комнат по полю '{}'", sortBy);
        List<Room> sorted = new ArrayList<>(rooms.values());

        if ("price".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparingDouble(Room::getPrice).reversed());
        } else if ("capacity".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparingInt(Room::getCapacity).reversed());
        } else if ("stars".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparingInt(Room::getStars).reversed());
        } else {
            sorted.sort(Comparator.comparingDouble(Room::getPrice).reversed());
        }

        logger.info("Сортировка завершена. Получено комнат: {}", sorted.size());
        return sorted;
    }
}