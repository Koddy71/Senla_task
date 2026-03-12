package ru.ilya.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;
import ru.ilya.dao.jpa.RoomDaoJpa;

@Transactional
@Component
public class RoomServiceImpl implements RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final AppConfig appConfig;
    private final RoomDaoJpa roomDao;

    @Autowired
    public RoomServiceImpl(AppConfig appConfig, RoomDaoJpa roomDao) {
        this.appConfig = appConfig;
        this.roomDao = roomDao;
    }

    @Override
    public boolean addRoom(Room room) {
        logger.info("Начало добавления комнаты");
        if (room == null || roomDao.findById(room.getNumber()) != null) {
            logger.info("Добавление комнаты не выполнено");
            return false;
        }
        roomDao.create(room);
        logger.info("Добавление комнаты завершено успешно. Номер: {}", room.getNumber());
        return true;
    }

    @Override
    public boolean removeRoom(int number) {
        logger.info("Начало удаления комнаты с номером {}", number);
        boolean result = roomDao.delete(number);
        logger.info("Удаление комнаты завершено. Результат: {}", result);
        return result;
    }

    @Override
    public Room findRoom(int number) {
        logger.info("Поиск комнаты с номером {}", number);
        Room room = roomDao.findById(number);
        logger.info("Поиск комнаты завершён");
        return room;
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
            roomDao.update(room);
            logger.info("Изменение статуса завершено успешно");
            return true;
        }
        logger.info("Изменение статуса не выполнено: комната {} не найдена", number);
        return false;
    }

    @Override
    public boolean changePrice(int number, int newPrice) {
        logger.info("Начало изменения цены комнаты {} на {}", number, newPrice);
        Room room = findRoom(number);
        if (room != null && newPrice > 0) {
            room.setPrice(newPrice);
            roomDao.update(room);
            logger.info("Изменение цены завершено успешно");
            return true;
        }
        logger.info("Изменение цены не выполнено");
        return false;
    }

    @Override
    public List<Room> getAllRooms() {
        logger.info("Получение списка всех комнат");
        List<Room> result = roomDao.findAll();
        logger.info("Получение списка завершено. Найдено комнат: {}", result.size());
        return result;
    }

    @Override
    public List<Room> getFreeRooms() {
        logger.info("Получение списка свободных комнат на сегодня");
        List<Room> freeRooms = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Room r : roomDao.findAll()) {
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
        for (Room r : roomDao.findAll()) {
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
        for (Room r : roomDao.findAll()) {
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
        List<Room> sorted = new ArrayList<>(roomDao.findAll());

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
