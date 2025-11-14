package ru.ilya.ui;

import ru.ilya.controller.GuestController;
import ru.ilya.controller.RoomController;
import ru.ilya.controller.PriceController;
import ru.ilya.controller.ServiceController;

public class MenuBuilder {
	private final GuestController guestController;
	private final RoomController roomController;
	private final ServiceController serviceController;
	private final PriceController priceController;

	public MenuBuilder(GuestController guestController,
			RoomController roomController,
			ServiceController serviceController,
			PriceController priceController) {

		this.guestController = guestController;
		this.roomController = roomController;
		this.serviceController = serviceController;
		this.priceController = priceController;
	}

	public Menu buildMainMenu() {
		Menu main = new Menu("Главное меню");

		main.addItem(new MenuItem("Управление гостями", buildGuestMenu()));
		main.addItem(new MenuItem("Управление номерами", buildRoomMenu()));
		main.addItem(new MenuItem("Управление услугами", buildServiceMenu()));
		main.addItem(new MenuItem("Просмотр комнат и услуг", () -> priceController.showRoomsAndService()));
		return main;
	}

	private Menu buildGuestMenu() {
		Menu m = new Menu("Меню гостей");
		m.addItem(new MenuItem("Заселить гостя", () -> guestController.checkInGuest()));
		m.addItem(new MenuItem("Выселить гостя (по ID)", () -> guestController.checkOutGuest()));
		m.addItem(new MenuItem("Показать всех гостей", () -> guestController.showAllGuests()));
		m.addItem(new MenuItem("Найти гостя по ID", () -> guestController.findGuestById()));
		m.addItem(new MenuItem("Показать количество гостей", () -> guestController.showGuestCount()));
		m.addItem(new MenuItem("Сортировать гостей", () -> guestController.sortGuests()));
		return m;
	}

	private Menu buildRoomMenu() {
		Menu m = new Menu("Меню номеров");
		m.addItem(new MenuItem("Добавить комнату", () -> roomController.addRoom()));
		m.addItem(new MenuItem("Удалить комнату", () -> roomController.removeRoom()));
		m.addItem(new MenuItem("Показать все комнаты", () -> roomController.showAllRooms()));
		m.addItem(new MenuItem("Найти комнату по номеру", () -> roomController.findRoomByNumber()));
		m.addItem(new MenuItem("Изменить статус комнаты", () -> roomController.changeRoomStatus()));
		m.addItem(new MenuItem("Показать доступные комнаты на дату", () -> roomController.getRoomsFreeByDate()));
		m.addItem(new MenuItem("Сортировать комнаты", () -> roomController.sortRooms()));
		return m;
	}

	private Menu buildServiceMenu() {
		Menu m = new Menu("Меню услуг");
		m.addItem(new MenuItem("Добавить услугу", () -> serviceController.addService()));
		m.addItem(new MenuItem("Удалить услугу", () -> serviceController.removeService()));
		m.addItem(new MenuItem("Найти услугу", () -> serviceController.findService()));
		m.addItem(new MenuItem("Изменить цену услуги", () -> serviceController.changePrice()));
		m.addItem(new MenuItem("Показать все услуги", () -> serviceController.printAllServices()));
		return m;
	}
}
