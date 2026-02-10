package ru.ilya.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ilya.autodi.Inject;
import ru.ilya.autoconfig.AppConfig;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

public class GuestServiceImpl implements GuestService {
   private Map<Integer, Guest> guests = new HashMap<>();

   @Inject
   private RoomService roomService;

   @Inject
   private ServiceManager serviceManager;

   @Inject
   private AppConfig appConfig;

   public GuestServiceImpl() {
   }

   @Override
   public Guest checkIn(String guestName, int number, LocalDate from, LocalDate to) {
      if (guestName == null || from == null || to == null || !to.isAfter(from))
         return null;

      Room room = roomService.findRoom(number);
      if (room == null)
         return null;
      if (!room.isFreeOn(from) || !room.isFreeOn(to.minusDays(1)))
         return null;

      Guest guest = new Guest(guestName, room, from, to);
      guests.put(guest.getId(), guest);
      room.addStay(guest);

      int limit = appConfig.getRoomHistoryLimit();
      List<Guest> history = room.getStayHistory();
      if (history.size() > limit) {
         history.remove(0);
      }

      return guest;
   }

   @Override
   public boolean checkOut(int guestId) {
      Guest g = guests.remove(guestId);
      if (g == null)
         return false;
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
      } else {
         sorted.sort(Comparator.comparing(Guest::getName, String.CASE_INSENSITIVE_ORDER));
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

   @Override
   public boolean addServiceToGuest(int guestId, int serviceId) {
      Guest guest = guests.get(guestId);
      Service service = serviceManager.findService(serviceId);
      if (service == null || guest == null) {
         return false;
      }

      if (!guest.getServices().contains(service)) {
         guest.addService(service);
         return true;
      }

      return false;
   }

   @Override
   public boolean removeServiceFromGuest(int guestId, int serviceId) {
      Guest guest = guests.get(guestId);
      Service service = serviceManager.findService(serviceId);
      if (service == null || guest == null) {
         return false;
      }

      if (guest.getServices().contains(service)) {
         guest.removeService(service);
         return true;
      }

      return false;
   }

}
