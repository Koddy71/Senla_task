package ru.ilya.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autodi.Inject;
import ru.ilya.autoconfig.AppConfig;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

public class GuestServiceImpl implements GuestService {

    private static final Logger logger = LoggerFactory.getLogger(GuestServiceImpl.class);

    private Map<Integer, Guest> guests = new HashMap<>();

    @Inject
    private RoomService roomService;

    @Inject
    private ServiceManager serviceManager;

    @Inject
    private AppConfig appConfig;

    public GuestServiceImpl() {
        logger.info("GuestServiceImpl инициализирован");
    }

    @Override
    public Guest checkIn(String guestName, int number, LocalDate from, LocalDate to) {
        logger.info("Начало регистрации заезда: имя={}, комната={}, с {} по {}", guestName, number, from, to);

        if (guestName == null || from == null || to == null || !to.isAfter(from)) {
            logger.info("Регистрация заезда не выполнена: неверные входные данные");
            return null;
        }

        Room room = roomService.findRoom(number);
        if (room == null) {
            logger.info("Регистрация заезда не выполнена: комната {} не найдена", number);
            return null;
        }
        if (!room.isFreeOn(from) || !room.isFreeOn(to.minusDays(1))) {
            logger.info("Регистрация заезда не выполнена: комната {} занята в указанные даты", number);
            return null;
        }

        Guest guest = new Guest(guestName, room, from, to);
        guests.put(guest.getId(), guest);
        room.addStay(guest);

        int limit = appConfig.getRoomHistoryLimit();
        List<Guest> history = room.getStayHistory();
        if (history.size() > limit) {
            history.remove(0);
        }

        logger.info("Регистрация заезда завершена успешно: гость ID={}", guest.getId());
        return guest;
    }

    @Override
    public boolean checkOut(int guestId) {
        logger.info("Начало выселения гостя ID={}", guestId);
        Guest g = guests.remove(guestId);
        if (g == null) {
            logger.info("Выселение не выполнено: гость ID={} не найден", guestId);
            return false;
        }
        logger.info("Выселение завершено успешно: гость ID={}", guestId);
        return true;
    }

    @Override
    public List<Guest> getAllGuests() {
        logger.info("Получение списка всех гостей");
        List<Guest> result = new ArrayList<>(guests.values());
        logger.info("Получение списка завершено. Найдено гостей: {}", result.size());
        return result;
    }

    @Override
    public List<Guest> getGuestsSorted(String sortBy) {
        logger.info("Начало сортировки гостей по полю '{}'", sortBy);
        List<Guest> sorted = new ArrayList<>(guests.values());
        if ("name".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparing(Guest::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("checkoutDate".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparing(Guest::getCheckOutDate).reversed());
        } else {
            sorted.sort(Comparator.comparing(Guest::getName, String.CASE_INSENSITIVE_ORDER));
        }
        logger.info("Сортировка завершена. Получено гостей: {}", sorted.size());
        return sorted;
    }

    @Override
    public int getGuestCount() {
        logger.info("Подсчёт количества гостей");
        int count = guests.size();
        logger.info("Подсчёт завершён. Гостей: {}", count);
        return count;
    }

    @Override
    public Guest findGuestById(int id) {
        logger.info("Поиск гостя по ID {}", id);
        Guest guest = guests.get(id);
        logger.info("Поиск гостя завершён");
        return guest;
    }

    @Override
    public boolean addServiceToGuest(int guestId, int serviceId) {
        logger.info("Начало добавления услуги ID={} гостю ID={}", serviceId, guestId);
        Guest guest = guests.get(guestId);
        Service service = serviceManager.findService(serviceId);
        if (service == null || guest == null) {
            logger.info("Добавление услуги не выполнено: гость или услуга не найдены");
            return false;
        }

        if (!guest.getServices().contains(service)) {
            guest.addService(service);
            logger.info("Добавление услуги завершено успешно");
            return true;
        }

        logger.info("Добавление услуги не выполнено: услуга уже добавлена гостю");
        return false;
    }

    @Override
    public boolean removeServiceFromGuest(int guestId, int serviceId) {
        logger.info("Начало удаления услуги ID={} у гостя ID={}", serviceId, guestId);
        Guest guest = guests.get(guestId);
        Service service = serviceManager.findService(serviceId);
        if (service == null || guest == null) {
            logger.info("Удаление услуги не выполнено: гость или услуга не найдены");
            return false;
        }

        if (guest.getServices().contains(service)) {
            guest.removeService(service);
            logger.info("Удаление услуги завершено успешно");
            return true;
        }

        logger.info("Удаление услуги не выполнено: услуга не найдена у гостя");
        return false;
    }
}