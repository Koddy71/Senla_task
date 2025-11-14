package ru.ilya.controller;

import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class GuestController {
	private GuestService guestService;
	private Scanner sc = new Scanner(System.in);

	public GuestController(GuestService guestService) {
		this.guestService = guestService;
	}

	public void checkInGuest() {
		System.out.print("Введите имя гостя: ");
		String name = sc.nextLine();
		System.out.print("Введите номер комнаты: ");
		int roomNumber = sc.nextInt();
		sc.nextLine(); 

		System.out.print("Дата заезда (гггг-мм-дд): ");
		LocalDate from = LocalDate.parse(sc.nextLine());
		System.out.print("Дата выезда (гггг-мм-дд): ");
		LocalDate to = LocalDate.parse(sc.nextLine());

		Guest guest = guestService.checkIn(name, roomNumber, from, to);
		if (guest != null) {
			System.out.println("Гость заселён! ID: " + guest.getId());
		} else {
			System.out.println("Ошибка заселения!");
		}
	}

	public void checkOutGuest() {
		System.out.print("Введите ID гостя: ");
		int id = sc.nextInt();
		sc.nextLine();

		boolean ok = guestService.checkOut(id);
		System.out.println(ok ? "Гость выселен!" : "Ошибка выселения!");
	}

	public void showAllGuests() {
		List<Guest> guests = guestService.getAllGuests();
		if (guests.isEmpty()) {
			System.out.println("Нет заселённых гостей.");
			return;
		}
		for (Guest g : guests) {
			System.out.println(g.getInfo());
		}
	}

	public void findGuestById() {
		System.out.print("Введите ID гостя: ");
		int id = sc.nextInt();
		sc.nextLine();

		Guest g = guestService.findGuestById(id);
		if (g != null) {
			System.out.println(g.getInfo());
		} else {
			System.out.println("Гость не найден.");
		}
	}

	public void showGuestCount() {
		System.out.println("Количество гостей: " + guestService.getGuestCount());
	}

	public void sortGuests() {
		System.out.print("Сортировать по ('name' или 'checkoutDate'): ");
		String sortBy = sc.nextLine();

		List<Guest> sorted = guestService.getGuestsSorted(sortBy);
		for (Guest g : sorted) {
			System.out.println(g.getInfo());
		}
	}
}
