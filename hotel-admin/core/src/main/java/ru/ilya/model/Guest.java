package ru.ilya.model;

import ru.ilya.model.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;  //для неизвестного поля

@JsonIgnoreProperties(ignoreUnknown = true)
public class Guest {
	private int id;
	private String name;
	private Room room;
	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	private List<Service> services = new ArrayList<>();

   private static int idCounter = 1;

   // для сериализации
   public Guest(){}

	public Guest(String name, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
		this.id = idCounter++;
      this.name = name;
		this.room = room;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
	}

   public Guest(int id, String name, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
    this.id = id;
    this.name = name;
    this.room = room;
    this.checkInDate = checkInDate;
    this.checkOutDate = checkOutDate;

    if(id>idCounter){
      idCounter=id+1;
    }
   }

   public void setId(int id){
      this.id=id;
   }

	public int getId() {
		return id;
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
		return "ID: " + id + " | " + name + " (Номер: " + room.getNumber() + ", с " + checkInDate + " по " + checkOutDate + ")";
	}

	public String getStayInfo() {
		return name + " был с " + checkInDate + " по " + checkOutDate;
	}

	 @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guest)) return false;
        Guest guest = (Guest) o;
        return Objects.equals(id, guest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
