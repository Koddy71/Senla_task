package ru.ilya.controller;

import ru.ilya.autodi.Inject;
import ru.ilya.dao.jpa.GuestDaoJpa;
import ru.ilya.dao.jpa.RoomDaoJpa;
import ru.ilya.dao.jpa.ServiceDaoJpa;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaController {

    private static final Logger logger = LoggerFactory.getLogger(JpaController.class);

    @Inject
    private RoomDaoJpa roomDao;

    @Inject
    private ServiceDaoJpa serviceDao;

    @Inject
    private GuestDaoJpa guestDao;

    public JpaController() {
    }

    public void restoreRooms(RoomService roomService) {
        logger.info("Начало обработки команды: restoreRooms (JPA)");
        try {
            List<Room> rooms = roomDao.findAll();
            for (Room room : rooms) {
                try {
                    roomService.addRoom(room);
                    room.setStatus(RoomStatus.AVAILABLE);
                } catch (Exception e) {
                    logger.error("Ошибка при добавлении комнаты {}", room.getNumber(), e);
                }
            }
            logger.info("restoreRooms успешно выполнено (JPA): восстановлено {} комнат", rooms.size());
        } catch (Exception e) {
            logger.error("Ошибка при обработке restoreRooms (JPA)", e);
        }
    }

    public void restoreServices(ServiceManager serviceManager) {
        logger.info("Начало обработки команды: restoreServices");
        try {
            List<Service> services = serviceDao.findAll();
            for (Service service : services) {
                try {
                    serviceManager.addService(service);
                } catch (Exception e) {
                    logger.error("Ошибка при добавлении услуги {}", service.getName(), e);
                }
            }
            int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
            Service.setIdCounter(maxId + 1);

            logger.info("restoreServices успешно выполнено: восстановлено {} услуг", services.size());
        } catch (Exception e) {
            logger.error("Ошибка при обработке restoreServices", e);
        }
    }

    public void restoreGuests(RoomService roomService, GuestService guestService) {
        logger.info("Начало обработки команды: restoreGuests (JPA)");
        try {
            List<Guest> guests = guestDao.findAll();
            for (Guest guest : guests) {
                try {
                    Room room = roomService.findRoom(guest.getRoom().getNumber());

                    if (room == null) {
                        logger.error("Гость {} привязан к несуществующей комнате", guest.getName());
                        continue;
                    }

                    Guest g = guestService.checkIn(
                            guest.getName(),
                            guest.getRoom().getNumber(),
                            guest.getCheckInDate(),
                            guest.getCheckOutDate());

                    if (g != null) {
                        for (Service service : guest.getServices()) {
                            guestService.addServiceToGuest(g.getId(), service.getId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("Ошибка при восстановлении гостя {}", guest.getName(), e);
                }
            }
            logger.info("restoreGuests успешно выполнено: восстановлено {} гостей", guests.size());
        } catch (Exception e) {
            logger.error("Ошибка при обработке restoreGuests", e);
        }
    }

    public void clearDatabase() {
        logger.info("Начало обработки команды: clearDatabase (JPA)");
        try {
            guestDao.deleteAll();
            serviceDao.deleteAll();
            roomDao.deleteAll();
            logger.info("clearDatabase успешно выполнено (JPA)");
        } catch (Exception e) {
            logger.error("Ошибка при обработке clearDatabase (JPA)", e);
        }
    }

    public void saveRooms(RoomService roomService) {
        logger.info("Начало обработки команды: saveRooms (JPA)");
        for (Room room : roomService.getAllRooms()) {
            try {
                roomDao.create(room);
            } catch (Exception e) {
                logger.error("Ошибка при сохранении комнаты {}", room.getNumber(), e);
            }
        }
        logger.info("saveRooms успешно выполнено (JPA)");
    }

    public void saveServices(ServiceManager serviceManager) {
        logger.info("Начало обработки команды: saveServices (JPA)");
        for (Service service : serviceManager.getAllServices()) {
            try {
                serviceDao.create(service);
            } catch (Exception e) {
                logger.error("Ошибка при сохранении услуги {}", service.getName(), e);
            }
        }
        logger.info("saveServices успешно выполнено (JPA)");
    }

    public void saveGuests(GuestService guestService) {
        logger.info("Начало обработки команды: saveGuests (JPA)");
        for (Guest guest : guestService.getAllGuests()) {
            try {
                guestDao.create(guest);
            } catch (Exception e) {
                logger.error("Ошибка при сохранении гостя {}", guest.getName(), e);
            }
        }
        logger.info("saveGuests успешно выполнено (JPA)");
    }
}