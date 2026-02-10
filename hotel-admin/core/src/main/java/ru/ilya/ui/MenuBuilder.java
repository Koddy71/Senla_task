package ru.ilya.ui;

import ru.ilya.controller.GuestController;
import ru.ilya.controller.CsvFileController;
import ru.ilya.controller.RoomController;
import ru.ilya.controller.PriceController;
import ru.ilya.controller.ServiceController;

public class MenuBuilder {
   private final GuestController guestController;
   private final RoomController roomController;
   private final ServiceController serviceController;
   private final PriceController priceController;
   private final CsvFileController csvFileController;

   public MenuBuilder(GuestController guestController,
         RoomController roomController,
         ServiceController serviceController,
         PriceController priceController,
         CsvFileController csvFileController) {

      this.guestController = guestController;
      this.roomController = roomController;
      this.serviceController = serviceController;
      this.priceController = priceController;
      this.csvFileController = csvFileController;
   }

   private Menu buildMainMenu() {
      Menu root = new Menu("Главное меню");
      root.addItem(new MenuItem("Гости", build(GuestController.class)));
      root.addItem(new MenuItem("Комнаты", build(RoomController.class)));
      root.addItem(new MenuItem("Сервисы", build(ServiceController.class)));
      root.addItem(new MenuItem("Просмотр комнат и услуг", () -> priceController.showRoomsAndService()));
      root.addItem(new MenuItem("Импорт / Экспорт данных", build(CsvFileController.class)));
      return root;
   }

   private Menu buildGuestMenu() {
      Menu m = new Menu("Меню гостей");
      m.addItem(new MenuItem("Заселить гостя", () -> guestController.checkInGuest()));
      m.addItem(new MenuItem("Выселить гостя (по ID)", () -> guestController.checkOutGuest()));
      m.addItem(new MenuItem("Добавить услугу", ()-> guestController.addService()));
      m.addItem(new MenuItem("Удалить услугу", () -> guestController.removeService()));
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
      m.addItem(new MenuItem("Найти комнату", () -> roomController.findRoomByNumber()));
      m.addItem(new MenuItem("Изменить статус комнаты", () -> roomController.changeRoomStatus()));
      m.addItem(new MenuItem("Показать доступные комнаты на дату", () -> roomController.getRoomsFreeByDate()));
      m.addItem(new MenuItem("Сортировать комнаты", () -> roomController.sortRooms()));
      return m;
   }

   private Menu buildServiceMenu() {
      Menu m = new Menu("Меню услуг");
      m.addItem(new MenuItem("Добавить услугу", () -> serviceController.addService()));
      m.addItem(new MenuItem("Удалить услугу", () -> serviceController.removeService()));
      m.addItem(new MenuItem("Показать все услуги", () -> serviceController.printAllServices()));
      m.addItem(new MenuItem("Найти услугу по ID", () -> serviceController.findService()));
      m.addItem(new MenuItem("Изменить цену услуги", () -> serviceController.changePrice()));
      return m;
   }

   private Menu buildImportExportMenu() {
      Menu menu = new Menu("Импорт / Экспорт данных");

      menu.addItem(new MenuItem("Импорт гостей", () -> csvFileController.importGuests()));
      menu.addItem(new MenuItem("Импорт комнат", () -> csvFileController.importRooms()));
      menu.addItem(new MenuItem("Импорт услуг", () -> csvFileController.importServices()));

      menu.addItem(new MenuItem("Экспорт гостей", () -> csvFileController.exportGuests()));
      menu.addItem(new MenuItem("Экспорт комнат", () -> csvFileController.exportRooms()));
      menu.addItem(new MenuItem("Экспорт услуг", () -> csvFileController.exportServices()));

      return menu;
   }

   public Menu build(Class<?> type) {
      if (type == GuestController.class) {
         return buildGuestMenu();
      }

      if (type == RoomController.class) {
         return buildRoomMenu();
      }

      if (type == ServiceController.class) {
         return buildServiceMenu();
      }

      if (type == CsvFileController.class){
         return buildImportExportMenu();
      }

      return buildMainMenu();
   }
}
