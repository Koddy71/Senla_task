package ru.ilya.controller;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.List;
import java.util.Scanner;

public class ServiceController {
   private static ServiceController instance;
	private final ServiceManager serviceManager;
	private final Scanner sc = new Scanner(System.in);

	private ServiceController(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public void addService() {
		System.out.print("Введите название услуги: ");
		String name = sc.nextLine().trim();
		System.out.print("Введите цену услуги (целое число): ");
		int price;
		try {
			price = Integer.parseInt(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Неверный формат цены.");
			return;
		}

		Service s = new Service(name, price);
		boolean ok = serviceManager.addService(s);
		System.out.println(ok ? "Услуга добавлена." : "Услуга с таким именем уже существует.");
	}

	public void removeService() {
		System.out.print("Введите название услуги для удаления: ");
		String name = sc.nextLine().trim();
		boolean ok = serviceManager.removeService(name);
		System.out.println(ok ? "Услуга удалена." : "Услуга не найдена.");
	}

	public void findService() {
		System.out.print("Введите название услуги для поиска: ");
		String name = sc.nextLine().trim();
		Service s = serviceManager.findService(name);
		if (s != null) {
			System.out.println("Найдена услуга: " + s.getName() + " — " + s.getPrice());
		} else {
			System.out.println("Услуга не найдена.");
		}
	}

	public void changePrice() {
		System.out.print("Введите название услуги для изменения цены: ");
		String name = sc.nextLine().trim();
		System.out.print("Введите новую цену (целое число): ");
		int newPrice;
		try {
			newPrice = Integer.parseInt(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Неверный формат цены.");
			return;
		}
		boolean ok = serviceManager.changePrice(name, newPrice);
		System.out.println(ok ? "Цена изменена." : "Услуга не найдена.");
	}

	public void printAllServices() {
		List<Service> services = serviceManager.getAllServices();
		if (services == null || services.isEmpty()) {
			System.out.println("Услуг нет.");
			return;
		}
		System.out.println("\nСписок услуг:");
		for (Service s : services) {
			System.out.println("- " + s.getName() + " | Цена: " + s.getPrice());
		}
	}

   public static ServiceController getInstance(ServiceManager serviceManager) {
      if (instance == null) {
         instance = new ServiceController(serviceManager);
      }
      return instance;
   }
}
