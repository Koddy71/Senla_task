package ru.ilya.controller;

import ru.ilya.autodi.Inject;
import ru.ilya.dao.jdbc.GuestDaoJdbc;
import ru.ilya.dao.jdbc.RoomDaoJdbc;
import ru.ilya.dao.jdbc.ServiceDaoJdbc;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcController {

    private static final Logger logger = LoggerFactory.getLogger(JdbcController.class);

    @Inject
    private RoomDaoJdbc roomDao;

    @Inject
    private ServiceDaoJdbc serviceDao;

    @Inject
    private GuestDaoJdbc guestDao;

    public JdbcController() {
    }

    public void restoreRooms(RoomService roomService) {
        logger.info("Начало обработки команды: restoreRooms");
        try {
            List<Room> rooms = roomDao.findAll();
            for (Room room : rooms) {
                try {
                    roomService.addRoom(room);
                } catch (Exception e) {
                    logger.error("Ошибка добавления комнаты {}", room.getNumber(), e);
                }
            }
            logger.info("restoreRooms успешно выполнен: восстановлено {} комнат", rooms.size());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении restoreRooms", e);
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
                    logger.error("Ошибка добавления услуги {}", service.getName(), e);
                }
            }
            int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
            Service.setIdCounter(maxId + 1);

            logger.info("restoreServices успешно выполнен: восстановлено {} услуг", services.size());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении restoreServices", e);
        }
    }

    public void restoreGuests(RoomService roomService, GuestService guestService) {
        logger.info("Начало обработки команды: restoreGuests");
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
                    logger.error("Ошибка восстановления гостя {}", guest.getName(), e);
                }
            }
            logger.info("restoreGuests успешно выполнен: восстановлено {} гостей", guests.size());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении restoreGuests", e);
        }
    }

    public void clearDatabase() {
        logger.info("Начало обработки команды: clearDatabase");
        try {
            guestDao.deleteAll();
            serviceDao.deleteAll();
            roomDao.deleteAll();
            logger.info("clearDatabase успешно выполнен");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении clearDatabase", e);
        }
    }

    public void saveRooms(RoomService roomService) {
        logger.info("Начало обработки команды: saveRooms");
        for (Room room : roomService.getAllRooms()) {
            try {
                roomDao.create(room);
            } catch (Exception e) {
                logger.error("Ошибка сохранения комнаты {}", room.getNumber(), e);
            }
        }
        logger.info("saveRooms успешно выполнен");
    }

    public void saveServices(ServiceManager serviceManager) {
        logger.info("Начало обработки команды: saveServices");
        for (Service service : serviceManager.getAllServices()) {
            try {
                serviceDao.create(service);
            } catch (Exception e) {
                logger.error("Ошибка сохранения услуги {}", service.getName(), e);
            }
        }
        logger.info("saveServices успешно выполнен");
    }

    public void saveGuests(GuestService guestService) {
        logger.info("Начало обработки команды: saveGuests");
        for (Guest guest : guestService.getAllGuests()) {
            try {
                guestDao.create(guest);
            } catch (Exception e) {
                logger.error("Ошибка сохранения гостя {}", guest.getName(), e);
            }
        }
        logger.info("saveGuests успешно выполнен");
    }
}