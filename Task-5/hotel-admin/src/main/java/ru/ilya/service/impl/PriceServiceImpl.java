package ru.ilya.service.impl;

import ru.ilya.model.Priceable;
import ru.ilya.model.Room;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriceServiceImpl implements PriceService{
	private final RoomService roomService;
	private final ServiceManager serviceManager;
   private static PriceServiceImpl instance;

	private PriceServiceImpl(RoomService roomService, ServiceManager serviceManager) {
		this.roomService = roomService;
		this.serviceManager = serviceManager;
	}

	public List<Priceable> getRoomsAndServices(String orderBy) {
       List<Priceable> result = new ArrayList<>();

       List<Room> roomList = new ArrayList<>(roomService.getAllRooms());
       List<Service> serviceList = new ArrayList<>(serviceManager.getAllServices());

       roomList.sort(Comparator.comparingInt(Room::getPrice));
       serviceList.sort(Comparator.comparingInt(Service::getPrice));

       if ("service".equalsIgnoreCase(orderBy)) {
          result.addAll(serviceList);
          result.addAll(roomList);
       } else { 
          result.addAll(roomList);
          result.addAll(serviceList);
       }

       return result;
    }

    public static PriceServiceImpl getInstance(RoomService roomService, ServiceManager serviceManager){
      if (instance==null){
         instance = new PriceServiceImpl(roomService, serviceManager);
      }
      return instance;
    }
}