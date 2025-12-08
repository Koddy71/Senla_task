package ru.ilya.state;

import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;

import java.util.List;

public class ProgramState{
   private List<Guest> guests;
   private List<Room> rooms;
   private List<Service> services;

   public ProgramState() {}

   public ProgramState(List<Guest> guests, List<Room> rooms, List<Service> services) {
      this.guests = guests;
      this.rooms = rooms;
      this.services = services;
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