package ru.ilya.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Guest {
	private String name;
	private Room room;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private List<Service> services = new ArrayList<>();

	public Guest(String name, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
		this.name = name;
		this.room = room;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

	public String getName() {
		return name;
	}

	public Room getRoom() {
		return room;
	}

	public LocalDate getCheckInDate() {
		return checkInDate;
	}

	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}

	public List<Service> getServices() {
		return services;
	}

	public void addService(Service service) {
		services.add(service);
	}

	public double getTotalCost() {
		long nights = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
		if (nights <= 0)
			nights = 1;
		double roomCost = room.getPrice() * nights;
		double servicesCost = 0;
		for (Service s : services) {
			servicesCost += s.getPrice();
		}
		return roomCost + servicesCost;
	}

	public String getInfo(){
		return name + " (Номер: " + room.getNumber() + ", с " + checkInDate + " по " + checkOutDate + ")";
	}

	public String getStayInfo() {
		return name + " был с " + checkInDate + " по " + checkOutDate;
	}
}
