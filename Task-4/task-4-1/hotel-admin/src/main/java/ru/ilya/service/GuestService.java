package ru.ilya.service;

import java.time.LocalDate;
import java.util.List;

import ru.ilya.model.Guest;

public interface GuestService {
	boolean checkIn(String guestName, int roomNumber, LocalDate from, LocalDate to);
    boolean checkOut(String guestName);
    List<Guest> getAllGuests();
    List<Guest> getGuestsSortedByName();
    List<Guest> getGuestsSortedByCheckoutDate();
    int getGuestCount();
    Guest findGuestByName(String name);
}
