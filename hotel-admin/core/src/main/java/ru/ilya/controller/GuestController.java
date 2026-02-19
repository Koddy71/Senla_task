package ru.ilya.controller;

import ru.ilya.autodi.Inject;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuestController {

    private static final Logger logger = LoggerFactory.getLogger(GuestController.class);

    @Inject
    private GuestService guestService;

    private final Scanner sc = new Scanner(System.in);

    public GuestController() {
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

    private LocalDate safeDate() {
        String input = sc.nextLine().trim();
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Неверная дата. Формат: гггг-мм-дд");
            logger.error("Ошибка парсинга даты: {}", input);
            return null;
        }
    }

    public void checkInGuest() {
        logger.info("Начало обработки команды: checkInGuest");
        try {
            System.out.print("Введите имя гостя: ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                logger.error("checkInGuest прерван: пустое имя");
                return;
            }

            System.out.print("Введите ID комнаты: ");
            Integer roomId = safeInt();
            if (roomId == null)
                return;

            System.out.print("Дата заезда (гггг-мм-дд): ");
            LocalDate from = safeDate();
            if (from == null)
                return;

            System.out.print("Дата выезда (гггг-мм-дд): ");
            LocalDate to = safeDate();
            if (to == null)
                return;

            Guest guest = guestService.checkIn(name, roomId, from, to);
            if (guest != null) {
                System.out.println("Гость заселён! ID: " + guest.getId());
                logger.info("checkInGuest успешно выполнен: гость {}", guest.getId());
            } else {
                logger.error("checkInGuest не удался для имени {}", name);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении checkInGuest", e);
        }
    }

    public void checkOutGuest() {
        logger.info("Начало обработки команды: checkOutGuest");
        try {
            System.out.print("Введите ID гостя: ");
            Integer id = safeInt();
            if (id == null)
                return;

            boolean ok = guestService.checkOut(id);
            if (ok) {
                logger.info("checkOutGuest успешно выполнен: гость {}", id);
            } else {
                logger.error("checkOutGuest не удался: гость {} не найден", id);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении checkOutGuest", e);
        }
    }

    public void showAllGuests() {
        logger.info("Начало обработки команды: showAllGuests");
        try {
            List<Guest> guests = guestService.getAllGuests();
            if (guests.isEmpty()) {
                logger.info("showAllGuests: гости не найдены");
                return;
            }
            for (Guest g : guests) {
                System.out.println(g.getInfo());
            }
            logger.info("showAllGuests успешно выполнен: {} гостей", guests.size());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении showAllGuests", e);
        }
    }

    public void findGuestById() {
        logger.info("Начало обработки команды: findGuestById");
        try {
            System.out.print("Введите ID гостя: ");
            Integer id = safeInt();
            if (id == null)
                return;

            Guest g = guestService.findGuestById(id);
            if (g != null) {
                logger.info("findGuestById успешно выполнен: гость {}", id);
            } else {
                logger.error("findGuestById не удался: гость {} не найден", id);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении findGuestById", e);
        }
    }

    public void showGuestCount() {
        logger.info("Начало обработки команды: showGuestCount");
        try {
            int count = guestService.getGuestCount();
            System.out.println("Количество гостей: " + count);
            logger.info("showGuestCount успешно выполнен: {} гостей", count);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении showGuestCount", e);
        }
    }

    public void sortGuests() {
        logger.info("Начало обработки команды: sortGuests");
        try {
            System.out.print("Сортировать по ('name' или 'checkoutDate'): ");
            String sortBy = sc.nextLine();
            List<Guest> sorted = guestService.getGuestsSorted(sortBy);

            for (Guest g : sorted) {
                System.out.println(g.getInfo());
            }
            logger.info("sortGuests успешно выполнен: {} гостей отсортировано по {}", sorted.size(), sortBy);
        } catch (Exception e) {
            logger.error("Ошибка при выполнении sortGuests", e);
        }
    }

    public void addService() {
        logger.info("Начало обработки команды: addServiceToGuest");
        try {
            System.out.print("Введите ID гостя: ");
            Integer guestId = safeInt();
            if (guestId == null)
                return;

            System.out.print("Введите ID услуги: ");
            Integer serviceId = safeInt();
            if (serviceId == null)
                return;

            boolean success = guestService.addServiceToGuest(guestId, serviceId);
            if (success) {
                logger.info("addServiceToGuest успешно выполнен: гость {}, услуга {}", guestId, serviceId);
            } else {
                logger.error("addServiceToGuest не удался: гость {} или услуга {} не найдены", guestId, serviceId);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении addServiceToGuest", e);
        }
    }

    public void removeService() {
        logger.info("Начало обработки команды: removeServiceFromGuest");
        try {
            System.out.print("Введите ID гостя: ");
            Integer guestId = safeInt();
            if (guestId == null)
                return;

            System.out.print("Введите ID услуги: ");
            Integer serviceId = safeInt();
            if (serviceId == null)
                return;

            boolean success = guestService.removeServiceFromGuest(guestId, serviceId);
            if (success) {
                logger.info("removeServiceFromGuest успешно выполнен: гость {}, услуга {}", guestId, serviceId);
            } else {
                logger.error("removeServiceFromGuest не удался: гость {} или услуга {} не найдены", guestId, serviceId);
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении removeServiceFromGuest", e);
        }
    }
}