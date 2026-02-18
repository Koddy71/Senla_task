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
            logger.error("Integer parsing error", e);
            return null;
        }
    }

    private LocalDate safeDate() {
        String input = sc.nextLine().trim();
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Неверная дата. Формат: гггг-мм-дд");
            logger.error("Date parsing error: {}", input);
            return null;
        }
    }

    public void checkInGuest() {
        logger.info("Start processing command: checkInGuest");
        try {
            System.out.print("Введите имя гостя: ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                logger.error("checkInGuest aborted: empty name");
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
                logger.info("checkInGuest processed successfully: guest {}", guest.getId());
            } else {
                logger.error("checkInGuest failed for name {}", name);
            }
        } catch (Exception e) {
            logger.error("Error processing checkInGuest", e);
        }
    }

    public void checkOutGuest() {
        logger.info("Start processing command: checkOutGuest");
        try {
            System.out.print("Введите ID гостя: ");
            Integer id = safeInt();
            if (id == null)
                return;

            boolean ok = guestService.checkOut(id);
            if (ok) {
                logger.info("checkOutGuest processed successfully: guest {}", id);
            } else {
                logger.error("checkOutGuest failed: guest {} not found", id);
            }
        } catch (Exception e) {
            logger.error("Error processing checkOutGuest", e);
        }
    }

    public void showAllGuests() {
        logger.info("Start processing command: showAllGuests");
        try {
            List<Guest> guests = guestService.getAllGuests();
            if (guests.isEmpty()) {
                logger.info("showAllGuests: no guests found");
                return;
            }
            for (Guest g : guests) {
                System.out.println(g.getInfo());
            }
            logger.info("showAllGuests processed successfully: {} guests", guests.size());
        } catch (Exception e) {
            logger.error("Error processing showAllGuests", e);
        }
    }

    public void findGuestById() {
        logger.info("Start processing command: findGuestById");
        try {
            System.out.print("Введите ID гостя: ");
            Integer id = safeInt();
            if (id == null)
                return;

            Guest g = guestService.findGuestById(id);
            if (g != null) {
                logger.info("findGuestById processed successfully: guest {}", id);
            } else {
                logger.error("findGuestById failed: guest {} not found", id);
            }
        } catch (Exception e) {
            logger.error("Error processing findGuestById", e);
        }
    }

    public void showGuestCount() {
        logger.info("Start processing command: showGuestCount");
        try {
            int count = guestService.getGuestCount();
            System.out.println("Количество гостей: " + count);
            logger.info("showGuestCount processed successfully: {} guests", count);
        } catch (Exception e) {
            logger.error("Error processing showGuestCount", e);
        }
    }

    public void sortGuests() {
        logger.info("Start processing command: sortGuests");
        try {
            System.out.print("Сортировать по ('name' или 'checkoutDate'): ");
            String sortBy = sc.nextLine();
            List<Guest> sorted = guestService.getGuestsSorted(sortBy);

            for (Guest g : sorted) {
                System.out.println(g.getInfo());
            }
            logger.info("sortGuests processed successfully: {} guests sorted by {}", sorted.size(), sortBy);
        } catch (Exception e) {
            logger.error("Error processing sortGuests", e);
        }
    }

    public void addService() {
        logger.info("Start processing command: addServiceToGuest");
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
                logger.info("addServiceToGuest processed successfully: guest {}, service {}", guestId, serviceId);
            } else {
                logger.error("addServiceToGuest failed: guest {} or service {} not found", guestId, serviceId);
            }
        } catch (Exception e) {
            logger.error("Error processing addServiceToGuest", e);
        }
    }

    public void removeService() {
        logger.info("Start processing command: removeServiceFromGuest");
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
                logger.info("removeServiceFromGuest processed successfully: guest {}, service {}", guestId, serviceId);
            } else {
                logger.error("removeServiceFromGuest failed: guest {} or service {} not found", guestId, serviceId);
            }
        } catch (Exception e) {
            logger.error("Error processing removeServiceFromGuest", e);
        }
    }
}
