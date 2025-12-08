package ru.ilya.state;

import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ProgramState {
   private List<Guest> guests = new ArrayList<>();
   private List<Room> rooms = new ArrayList<>();
   private List<Service> services = new ArrayList<>();

   public ProgramState() {
   }

   public ProgramState(List<Guest> guests, List<Room> rooms, List<Service> services) {
      this.guests = (guests != null) ? guests : new ArrayList<>();
      this.rooms = (rooms != null) ? rooms : new ArrayList<>();
      this.services = (services != null) ? services : new ArrayList<>();
   }

   public List<Guest> getGuests() {
      return guests;
   }

   public List<Room> getRooms() {
      return rooms;
   }

   public List<Service> getServices() {
      return services;
   }
}
