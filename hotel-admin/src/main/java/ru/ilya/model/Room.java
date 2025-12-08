package ru.ilya.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.ilya.config.RoomConfig;

public class Room extends Priceable{
	private int number;
	private RoomStatus status;
	private int price;
	private int capacity; 
	private int stars;    

   @JsonIgnore
	private List<Guest> stayHistory = new ArrayList<>();

	public Room(int number, int price, int capacity, int stars) {
		this.number = number;
		this.price = price;
		this.capacity = capacity;
		this.stars = stars;
		this.status = RoomStatus.AVAILABLE;
	}

    public Room(int number, int price, int capacity, int stars, RoomStatus status) {
       this.number = number;
       this.price = price;
       this.capacity = capacity;
       this.stars = stars;
       this.status = status;
    }

	public void setStatus(RoomStatus status){
		this.status=status;
	}

	public RoomStatus getStatus(){
		return status;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public void setNumber(int number){
		this.number=number;
	}

	public int getNumber() {
		return number;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public int getStars() {
		return stars;
	}

	public boolean isFreeOn(LocalDate date) {
		for (Guest s : stayHistory) {
			if (( !date.isBefore(s.getCheckInDate()) ) && ( !date.isAfter(s.getCheckOutDate()) )) {
					return false;
			}
		}
		return true;
	}

	public void addStay(Guest guest) {
		stayHistory.add(guest);

      int limit = RoomConfig.getHistoryRoomLimit();
      List<Guest> history = getStayHistory();
      if (history.size()>limit){
         history.remove(0);
      }
	}

   @JsonIgnore
	public List<Guest> getStayHistory(){
		return stayHistory;
	}

	public List<Guest> getLastStays(int n) {
		int size = stayHistory.size();
		if (size == 0)
			return List.of();
		int from = Math.max(0, size - n);
		return new ArrayList<>(stayHistory.subList(from, size));
	}

	public String getInfo() {
		return "Номер: " + number +
				", Цена: " + price +
				", Вместимость: " + capacity +
				", Звёзды: " + stars +
				", Статус: " + status.getDescription();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Room))
			return false;
		Room room = (Room) o;
		return number == room.number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

}