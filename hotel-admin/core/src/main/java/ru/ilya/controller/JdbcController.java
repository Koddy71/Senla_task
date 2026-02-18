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
        logger.info("Start processing command: restoreRooms");
        try {
            List<Room> rooms = roomDao.findAll();
            for (Room room : rooms) {
                try {
                    roomService.addRoom(room);
                } catch (Exception e) {
                    logger.error("Error adding room {}", room.getNumber(), e);
                }
            }
            logger.info("restoreRooms processed successfully: {} rooms restored", rooms.size());
        } catch (Exception e) {
            logger.error("Error processing restoreRooms", e);
        }
    }

    public void restoreServices(ServiceManager serviceManager) {
        logger.info("Start processing command: restoreServices");
        try {
            List<Service> services = serviceDao.findAll();
            for (Service service : services) {
                try {
                    serviceManager.addService(service);
                } catch (Exception e) {
                    logger.error("Error adding service {}", service.getName(), e);
                }
            }
            int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
            Service.setIdCounter(maxId + 1);

            logger.info("restoreServices processed successfully: {} services restored", services.size());
        } catch (Exception e) {
            logger.error("Error processing restoreServices", e);
        }
    }

    public void restoreGuests(RoomService roomService, GuestService guestService) {
        logger.info("Start processing command: restoreGuests");
        try {
            List<Guest> guests = guestDao.findAll();
            for (Guest guest : guests) {
                try {
                    Room room = roomService.findRoom(guest.getRoom().getNumber());

                    if (room == null) {
                        logger.error("Guest {} assigned to non-existing room", guest.getName());
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
                    logger.error("Error restoring guest {}", guest.getName(), e);
                }
            }
            logger.info("restoreGuests processed successfully: {} guests restored", guests.size());
        } catch (Exception e) {
            logger.error("Error processing restoreGuests", e);
        }
    }

    public void clearDatabase() {
        logger.info("Start processing command: clearDatabase");
        try {
            guestDao.deleteAll();
            serviceDao.deleteAll();
            roomDao.deleteAll();
            logger.info("clearDatabase processed successfully");
        } catch (Exception e) {
            logger.error("Error processing clearDatabase", e);
        }
    }

    public void saveRooms(RoomService roomService) {
        logger.info("Start processing command: saveRooms");
        for (Room room : roomService.getAllRooms()) {
            try {
                roomDao.create(room);
            } catch (Exception e) {
                logger.error("Error saving room {}", room.getNumber(), e);
            }
        }
        logger.info("saveRooms processed successfully");
    }

    public void saveServices(ServiceManager serviceManager) {
        logger.info("Start processing command: saveServices");
        for (Service service : serviceManager.getAllServices()) {
            try {
                serviceDao.create(service);
            } catch (Exception e) {
                logger.error("Error saving service {}", service.getName(), e);
            }
        }
        logger.info("saveServices processed successfully");
    }

    public void saveGuests(GuestService guestService) {
        logger.info("Start processing command: saveGuests");
        for (Guest guest : guestService.getAllGuests()) {
            try {
                guestDao.create(guest);
            } catch (Exception e) {
                logger.error("Error saving guest {}", guest.getName(), e);
            }
        }
        logger.info("saveGuests processed successfully");
    }
}
