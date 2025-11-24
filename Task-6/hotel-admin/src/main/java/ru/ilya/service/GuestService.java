 package ru.ilya.service;

import java.time.LocalDate;
import java.util.List;

import ru.ilya.model.Guest;

public interface GuestService {
	Guest checkIn(String guestName, int id, LocalDate from, LocalDate to);
   boolean checkOut(int guestId);
   List<Guest> getAllGuests();
   List<Guest> getGuestsSorted(String sortedBy);
   int getGuestCount();
   Guest findGuestById(int id);
}
