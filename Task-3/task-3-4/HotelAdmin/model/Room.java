package model;

public class Room {
	private int number;
	private RoomStatus status;
	private int price;

	public Room(int number, int price) {
		this.number = number;
		this.price = price;
		this.status = RoomStatus.AVAILABLE;
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

	public String getInfo() {
		return "Номер: " + number +
				", Цена: " + price +
				", Статус: " + status.getDescription();
	}
}