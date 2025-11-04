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
	private Map<String, Guest> guests = new HashMap<>();
    private RoomService roomService;
    private ServiceManager serviceManager;

    public GuestServiceImpl(RoomService roomService, ServiceManager serviceManager) {
        this.roomService = roomService;
        this.serviceManager = serviceManager;
    }

    @Override
    public boolean checkIn(String guestName, int roomNumber, LocalDate from, LocalDate to) {
        if (guestName == null || from == null || to == null || !to.isAfter(from)) return false;
        Room room = roomService.findRoom(roomNumber);
        if (room == null) return false;

        if (room.getStatus() != RoomStatus.AVAILABLE) return false;
        if (!room.isFreeOn(from) || !room.isFreeOn(to.minusDays(1))) return false;

        Guest guest = new Guest(guestName, room, from, to);
        guests.put(guestName.toLowerCase(), guest);

        room.setStatus(RoomStatus.OCCUPIED);

        room.addStay(guest);
        return true;
    }

    @Override
    public boolean checkOut(String guestName) {
        if (guestName == null) return false;
        Guest g = guests.remove(guestName.toLowerCase());
        if (g == null) return false;
        Room r = g.getRoom();
        r.setStatus(RoomStatus.AVAILABLE);
        return true;
    }

    @Override
    public List<Guest> getAllGuests() {
        return new ArrayList<>(guests.values());
    }

    @Override
    public List<Guest> getGuestsSortedByCheckoutDate() {
        List<Guest> sorted = new ArrayList<>(guests.values());
        sorted.sort(Comparator.comparing(Guest::getCheckOutDate));
        Collections.reverse(sorted);
        return sorted;
    }

    @Override
    public List<Guest> getGuestsSortedByName() {
        List<Guest> sorted = new ArrayList<>(guests.values());
        sorted.sort(Comparator.comparing(Guest::getName, String.CASE_INSENSITIVE_ORDER));
        Collections.reverse(sorted);
        return sorted;
    }

    @Override
    public int getGuestCount() {
        return guests.size();
    }

    @Override
    public Guest findGuestByName(String name) {
        if (name == null) return null;
        return guests.get(name.toLowerCase());
    }
}
