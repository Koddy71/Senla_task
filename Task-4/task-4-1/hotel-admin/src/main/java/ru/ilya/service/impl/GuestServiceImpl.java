package ru.ilya.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

public class GuestServiceImpl implements GuestService{
	private Map<Integer, Guest> guests = new HashMap<>();
    private RoomService roomService;
    private ServiceManager serviceManager;

    public GuestServiceImpl(RoomService roomService, ServiceManager serviceManager) {
        this.roomService = roomService;
        this.serviceManager = serviceManager;
    }

    @Override
    public Guest checkIn(String guestName, int roomNumber, LocalDate from, LocalDate to) {
        if (guestName == null || from == null || to == null || !to.isAfter(from)) return null;

        Room room = roomService.findRoom(roomNumber);
        if (room == null) return null;
        if (room.getStatus() != RoomStatus.AVAILABLE) return null;
        if (!room.isFreeOn(from) || !room.isFreeOn(to.minusDays(1))) return null;

        Guest guest = new Guest(guestName, room, from, to);
        guests.put(guest.getId(), guest); 
        room.setStatus(RoomStatus.OCCUPIED);
        room.addStay(guest);
        return guest;
    }

    @Override
    public boolean checkOut(int guestId) {
        Guest g = guests.remove(guestId);
        if (g == null)
            return false;
        Room r = g.getRoom();
        r.setStatus(RoomStatus.AVAILABLE);
        return true;
    }

    @Override
    public List<Guest> getAllGuests() {
        return new ArrayList<>(guests.values());
    }

    @Override
    public List<Guest> getGuestsSorted(String sortBy) {
        List<Guest> sorted = new ArrayList<>(guests.values());
        if ("name".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparing(Guest::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("checkoutDate".equalsIgnoreCase(sortBy)) {
            sorted.sort(Comparator.comparing(Guest::getCheckOutDate).reversed());
        }
        return sorted;
    }

    @Override
    public int getGuestCount() {
        return guests.size();
    }

    @Override
    public Guest findGuestById(int id) {
        return guests.get(id);
    }
}
