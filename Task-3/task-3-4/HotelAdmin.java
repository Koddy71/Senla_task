import java.util.ArrayList;
import java.util.Scanner;

class Room {
	int number;
	boolean occupied;
	boolean underMaintenance;
	int price;

	Room(int number, int price) {
		this.number = number;
		this.price = price;
		this.occupied = false;
		this.underMaintenance = false;
	}

	public String getInfo() {
		return "Номер: " + number +
				", Цена: " + price +
				", " + (occupied ? "Занят" : "Свободен") +
				", " + (underMaintenance ? "На ремонте" : "Рабочий");
	}
}

class Service {
	String name;
	int price;

	Service(String name, int price) {
		this.name = name;
		this.price = price;
	}

	public String getInfo() {
		return name + " - " + price + " руб.";
	}
}

public class HotelAdmin {
	static ArrayList<Room> rooms = new ArrayList<>();
	static ArrayList<Service> services = new ArrayList<>();
	static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		boolean exit = false;
		while (!exit) {
			System.out.println("\n--- Электронный администратор гостиницы ---");
			System.out.println("1. Поселить в номер");
			System.out.println("2. Выселить из номера");
			System.out.println("3. Изменить статус номера");
			System.out.println("4. Изменить цену номера или услуги");
			System.out.println("5. Добавить номер или услугу");
			System.out.println("6. Показать все номера и услуги");
			System.out.println("0. Выход");
			System.out.print("Выберите действие: ");
			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
				case 1 -> checkIn();
				case 2 -> checkOut();
				case 3 -> changeRoomStatus();
				case 4 -> changePrice();
				case 5 -> addRoomOrService();
				case 6 -> showAll();
				case 0 -> exit = true;
				default -> System.out.println("Неверный выбор!");
			}
		}
	}

	static void checkIn() {
		System.out.print("Введите номер для поселения: ");
		int number = scanner.nextInt();
		for (Room room : rooms) {
			if (room.number == number) {
				if (!room.occupied && !room.underMaintenance) {
					room.occupied = true;
					System.out.println("Гость успешно поселён в номер " + number);
				} else {
					System.out.println("Номер занят или на ремонте!");
				}
				return;
			}
		}
		System.out.println("Номер не найден!");
	}

	static void checkOut() {
		System.out.print("Введите номер для выселения: ");
		int number = scanner.nextInt();
		for (Room room : rooms) {
			if (room.number == number) {
				if (room.occupied) {
					room.occupied = false;
					System.out.println("Гость выселен из номера " + number);
				} else {
					System.out.println("Номер уже свободен!");
				}
				return;
			}
		}
		System.out.println("Номер не найден!");
	}

	static void changeRoomStatus() {
		System.out.print("Введите номер: ");
		int number = scanner.nextInt();
		for (Room room : rooms) {
			if (room.number == number) {
				System.out.println("1. На ремонте\n2. Рабочий");
				int status = scanner.nextInt();
				if (status == 1) {
					room.underMaintenance = true;
				} else {
					room.underMaintenance = false;
				}
				System.out.println("Статус изменен.");
				return;
			}
		}
		System.out.println("Номер не найден!");
	}

	static void changePrice() {
		System.out.println("1. Изменить цену номера\n2. Изменить цену услуги");
		int choice = scanner.nextInt();
		scanner.nextLine();
		if (choice == 1) {
			System.out.print("Введите номер: ");
			int number = scanner.nextInt();
			for (Room room : rooms) {
				if (room.number == number) {
					System.out.print("Введите новую цену: ");
					room.price = scanner.nextInt();
					System.out.println("Цена изменена.");
					return;
				}
			}
			System.out.println("Номер не найден!");
		} else if (choice == 2) {
			System.out.print("Введите название услуги: ");
			String name = scanner.nextLine();
			for (Service service : services) {
				if (service.name.equalsIgnoreCase(name)) {
					System.out.print("Введите новую цену: ");
					service.price = scanner.nextInt();
					System.out.println("Цена изменена.");
					return;
				}
			}
			System.out.println("Услуга не найдена!");
		} else {
			System.out.println("Неверное число!");
		}
	}

	static void addRoomOrService() {
		System.out.println("1. Добавить номер\n2. Добавить услугу");
		int choice = scanner.nextInt();
		scanner.nextLine();
		if (choice == 1) {
			System.out.print("Введите номер: ");
			int number = scanner.nextInt();
			System.out.print("Введите цену: ");
			int price = scanner.nextInt();
			rooms.add(new Room(number, price));
			System.out.println("Номер добавлен.");
		} else if (choice == 2) {
			System.out.print("Введите название услуги: ");
			String name = scanner.nextLine();
			System.out.print("Введите цену: ");
			int price = scanner.nextInt();
			services.add(new Service(name, price));
			System.out.println("Услуга добавлена.");
		} else {
			System.out.println("Неверное число!");
		}
	}

	static void showAll() {
		System.out.println("\n--- Номера ---");
		for (Room room : rooms) {
			System.out.println(room.getInfo());
		}
		System.out.println("\n--- Услуги ---");
		for (Service service : services) {
			System.out.println(service.getInfo());
		}
	}
}
