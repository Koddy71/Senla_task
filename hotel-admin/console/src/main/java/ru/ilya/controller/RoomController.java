package ru.ilya.controller;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;
import ru.ilya.autoconfig.AppConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

@Component
public class RoomController {
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    private final RoomService roomService;
    private final AppConfig appConfig;

    private final Scanner sc = new Scanner(System.in);

    @Autowired
    public RoomController(RoomService roomService, AppConfig appConfig) {
        this.roomService = roomService;
        this.appConfig = appConfig;
    }

    private Integer safeInt() {
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Введите корректное число.");
            logger.error("Ошибка парсинга числа", e);
            return null;
        }
    }

    public void addRoom() {
        logger.info("Начало обработки команды: addRoom");
        try {
            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("addRoom прервана: некорректный ввод номера комнаты");
                return;
            }

            System.out.print("Введите цену: ");
            Integer price = safeInt();
            if (price == null) {
                logger.error("addRoom прервана: некорректный ввод цены для комнаты {}", number);
                return;
            }

            System.out.print("Введите вместимость: ");
            Integer capacity = safeInt();
            if (capacity == null) {
                logger.error("addRoom прервана: некорректный ввод вместимости для комнаты {}", number);
                return;
            }

            System.out.print("Введите количество звёзд: ");
            Integer stars = safeInt();
            if (stars == null) {
                logger.error("addRoom прервана: некорректный ввод звёзд для комнаты {}", number);
                return;
            }

            Room room = new Room(number, price, capacity, stars);
            boolean ok = roomService.addRoom(room);
            if (ok) {
                System.out.println("Комната добавлена!");
                logger.info("addRoom успешно выполнена для комнаты {}", number);
            } else {
                System.out.println("Ошибка добавления!");
                logger.error("addRoom не удалась: сервис вернул false для комнаты {}", number);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении addRoom", e);
            System.out.println("Произошла ошибка при добавлении комнаты: " + e.getMessage());
        }
    }

    public void removeRoom() {
        logger.info("Начало обработки команды: removeRoom");
        try {
            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("removeRoom прервана: некорректный ввод номера комнаты");
                return;
            }

            boolean ok = roomService.removeRoom(number);
            if (ok) {
                System.out.println("Комната удалена!");
                logger.info("removeRoom успешно выполнена для комнаты {}", number);
            } else {
                System.out.println("Комната не найдена!");
                logger.error("removeRoom не удалась: комната {} не найдена", number);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении removeRoom", e);
            System.out.println("Произошла ошибка при удалении комнаты: " + e.getMessage());
        }
    }

    public void showAllRooms() {
        logger.info("Начало обработки команды: showAllRooms");
        try {
            List<Room> rooms = roomService.getAllRooms();
            if (rooms.isEmpty()) {
                System.out.println("Нет комнат.");
                logger.info("showAllRooms успешно выполнена: нет комнат для отображения");
                return;
            }
            for (Room r : rooms) {
                System.out.println(r.getInfo());
            }
            logger.info("showAllRooms успешно выполнена: отображено {} комнат", rooms.size());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении showAllRooms", e);
            System.out.println("Произошла ошибка при показе всех комнат: " + e.getMessage());
        }
    }

    public void findRoomByNumber() {
        logger.info("Начало обработки команды: findRoomByNumber");
        try {
            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("findRoomByNumber прервана: некорректный ввод номера комнаты");
                return;
            }

            Room r = roomService.findRoom(number);
            if (r != null) {
                System.out.println(r.getInfo());
                logger.info("findRoomByNumber успешно выполнена: найдена комната {}", number);
            } else {
                System.out.println("Комната не найдена.");
                logger.error("findRoomByNumber не удалась: комната {} не найдена", number);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении findRoomByNumber", e);
            System.out.println("Произошла ошибка при поиске комнаты: " + e.getMessage());
        }
    }

    public void changeRoomStatus() {
        logger.info("Начало обработки команды: changeRoomStatus");
        try {
            if (!appConfig.isRoomStatusChangeEnable()) {
                System.out.println("Изменение статуса отключено (config.properties)");
                logger.info("changeRoomStatus прервана: отключено в конфигурации");
                return;
            }

            System.out.print("Введите номер комнаты: ");
            Integer number = safeInt();
            if (number == null) {
                logger.error("changeRoomStatus прервана: некорректный ввод номера комнаты");
                return;
            }

            System.out.print("Введите статус (AVAILABLE / OCCUPIED / MAINTENANCE / RESERVED): ");
            String s = sc.nextLine().trim().toUpperCase();

            try {
                RoomStatus status = RoomStatus.valueOf(s);
                boolean ok = roomService.changeStatus(number, status);
                if (ok) {
                    System.out.println("Статус изменён!");
                    logger.info("changeRoomStatus успешно выполнена для комнаты {} -> {}", number, status);
                } else {
                    System.out.println("Комната не найдена.");
                    logger.error("changeRoomStatus не удалась: комната {} не найдена", number);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный статус.");
                logger.error("changeRoomStatus прервана: неверный статус '{}'", s);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении changeRoomStatus", e);
            System.out.println("Произошла ошибка при изменении статуса комнаты: " + e.getMessage());
        }
    }

    public void getRoomsFreeByDate() {
        logger.info("Начало обработки команды: getRoomsFreeByDate");
        try {
            System.out.print("Введите дату (гггг-мм-дд): ");
            String input = sc.nextLine();

            LocalDate date;
            try {
                date = LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты.");
                logger.error("getRoomsFreeByDate прервана: неверный формат даты '{}'", input, e);
                return;
            }

            List<Room> rooms = roomService.getRoomsFreeByDate(date);
            if (rooms.isEmpty()) {
                System.out.println("Нет доступных комнат на эту дату.");
                logger.info("getRoomsFreeByDate успешно выполнена: нет свободных комнат на {}", date);
            } else {
                System.out.println("Доступные комнаты:");
                for (Room r : rooms) {
                    System.out.println(r.getInfo());
                }
                logger.info("getRoomsFreeByDate успешно выполнена: {} комнат свободно на {}", rooms.size(), date);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении getRoomsFreeByDate", e);
            System.out.println("Произошла ошибка при поиске доступных комнат: " + e.getMessage());
        }
    }

    public void sortRooms() {
        logger.info("Начало обработки команды: sortRooms");
        try {
            System.out.print("Сортировать по ('price', 'capacity', 'stars'): ");
            String sortBy = sc.nextLine();

            List<Room> sorted = roomService.getRoomsSorted(sortBy);
            for (Room r : sorted) {
                System.out.println(r.getInfo());
            }
            logger.info("sortRooms успешно выполнена по '{}', получено {} комнат", sortBy, sorted.size());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении sortRooms", e);
            System.out.println("Произошла ошибка при сортировке комнат: " + e.getMessage());
        }
    }
}