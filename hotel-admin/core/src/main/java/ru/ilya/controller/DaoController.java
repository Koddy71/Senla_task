package ru.ilya.controller;

import ru.ilya.autodi.Inject;
import ru.ilya.dao.GuestDao;
import ru.ilya.dao.RoomDao;
import ru.ilya.dao.ServiceDao;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

import java.util.List;

public class DaoController {

    @Inject
    private RoomDao roomDao;

    @Inject
    private ServiceDao serviceDao;

    @Inject
    private GuestDao guestDao;

    public DaoController() {
    }

    public void restoreRooms(RoomService roomService) {
        try {
            List<Room> rooms = roomDao.findAll();
            for (Room room : rooms) {
                try {
                    roomService.addRoom(room);
                } catch (Exception e) {
                    System.err.println("Ошибка при добавлении комнаты " + room.getNumber() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке комнат: " + e.getMessage());
        }
    }

    public void restoreServices(ServiceManager serviceManager) {
        try {
            List<Service> services = serviceDao.findAll();
            for (Service service : services) {
                try {
                    serviceManager.addService(service);
                    int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
                    Service.setIdCounter(maxId + 1);
                } catch (Exception e) {
                    System.err.println("Ошибка при добавлении услуги " + service.getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке услуг: " + e.getMessage());
        }
    }

    public void restoreGuests(RoomService roomService, GuestService guestService) {
        try {
            List<Guest> guests = guestDao.findAll();
            for (Guest guest : guests) {
                try {
                    Room room = roomService.findRoom(guest.getRoom().getNumber());

                    if (room == null) {
                        System.err.println("Гость " + guest.getName() + " заселяется в несуществующую комнату");
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
                    System.err.println("Ошибка при восстановлении гостя " + guest.getName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке гостей: " + e.getMessage());
        }
    }

    public void clearDatabase() {
        try {
            guestDao.deleteAll();
            serviceDao.deleteAll();
            roomDao.deleteAll();
        } catch (Exception e) {
            System.err.println("Ошибка при очистке базы данных: " + e.getMessage());
        }
    }

    public void saveRooms(RoomService roomService) {
        for (Room room : roomService.getAllRooms()) {
            try {
                roomDao.create(room);
            } catch (Exception e) {
                System.err.println("Ошибка при сохранении комнаты " + room.getNumber() + ": " + e.getMessage());
            }
        }
    }

    public void saveServices(ServiceManager serviceManager) {
        for (Service service : serviceManager.getAllServices()) {
            try {
                serviceDao.create(service);
            } catch (Exception e) {
                System.err.println("Ошибка при сохранении услуги " + service.getName() + ": " + e.getMessage());
            }
        }
    }

    public void saveGuests(GuestService guestService) {
        for (Guest guest : guestService.getAllGuests()) {
            try {
                guestDao.create(guest);
            } catch (Exception e) {
                System.err.println("Ошибка при сохранении гостя " + guest.getName() + ": " + e.getMessage());
            }
        }
    }
}