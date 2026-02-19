package ru.ilya.controller;

import ru.ilya.autodi.Inject;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Inject
    private ServiceManager serviceManager;

    private final Scanner sc = new Scanner(System.in);

    public ServiceController() {
    }

    public void addService() {
        logger.info("Начало обработки команды: addService");
        try {
            System.out.print("Введите название услуги: ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Название услуги не может быть пустым.");
                logger.error("addService прервана: пустое название услуги");
                return;
            }

            System.out.print("Введите цену услуги (целое число): ");
            int price;
            try {
                price = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат цены.");
                logger.error("addService прервана: неверный формат цены");
                return;
            }

            Service s = new Service(name, price);

            boolean ok = serviceManager.addService(s);
            if (ok) {
                System.out.println("Услуга добавлена. ID: " + s.getId());
                logger.info("addService успешно выполнена: услуга '{}' добавлена с id {}", name, s.getId());
            } else {
                System.out.println("Услуга с таким ID уже существует.");
                logger.error("addService не удалась: дубликат id для услуги '{}'", name);
            }

        } catch (Exception e) {
            logger.error("Ошибка при выполнении addService", e);
            System.out.println("Произошла ошибка при добавлении услуги: " + e.getMessage());
        }
    }

    public void removeService() {
        logger.info("Начало обработки команды: removeService");
        try {
            System.out.print("Введите ID услуги для удаления: ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ID.");
                logger.error("removeService прервана: неверный формат ID");
                return;
            }

            boolean ok = serviceManager.removeService(id);
            if (ok) {
                System.out.println("Услуга удалена.");
                logger.info("removeService успешно выполнена: удалена услуга {}", id);
            } else {
                System.out.println("Услуга не найдена.");
                logger.error("removeService не удалась: услуга {} не найдена", id);
            }

        } catch (Exception e) {
            logger.error("Ошибка при выполнении removeService", e);
            System.out.println("Произошла ошибка при удалении услуги: " + e.getMessage());
        }
    }

    public void findService() {
        logger.info("Начало обработки команды: findService");
        try {
            System.out.print("Введите ID услуги для поиска: ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ID.");
                logger.error("findService прервана: неверный формат ID");
                return;
            }

            Service s = serviceManager.findService(id);
            if (s != null) {
                System.out.println("Найдена услуга: " + s.getInfo());
                logger.info("findService успешно выполнена: найдена услуга {}", id);
            } else {
                System.out.println("Услуга не найдена.");
                logger.error("findService не удалась: услуга {} не найдена", id);
            }

        } catch (Exception e) {
            logger.error("Ошибка при выполнении findService", e);
            System.out.println("Произошла ошибка при поиске услуги: " + e.getMessage());
        }
    }

    public void changePrice() {
        logger.info("Начало обработки команды: changePrice");
        try {
            System.out.print("Введите ID услуги для изменения цены: ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ID.");
                logger.error("changePrice прервана: неверный формат ID");
                return;
            }

            System.out.print("Введите новую цену (целое число): ");
            int newPrice;
            try {
                newPrice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат цены.");
                logger.error("changePrice прервана: неверный формат цены");
                return;
            }

            boolean ok = serviceManager.changePrice(id, newPrice);
            if (ok) {
                System.out.println("Цена изменена.");
                logger.info("changePrice успешно выполнена: услуга {} новая цена {}", id, newPrice);
            } else {
                System.out.println("Услуга не найдена.");
                logger.error("changePrice не удалась: услуга {} не найдена", id);
            }

        } catch (Exception e) {
            logger.error("Ошибка при выполнении changePrice", e);
            System.out.println("Произошла ошибка при изменении цены: " + e.getMessage());
        }
    }

    public void printAllServices() {
        logger.info("Начало обработки команды: printAllServices");
        try {
            List<Service> services = serviceManager.getAllServices();

            if (services == null || services.isEmpty()) {
                System.out.println("Услуг нет.");
                logger.info("printAllServices успешно выполнена: услуги не найдены");
                return;
            }

            System.out.println("\nСписок услуг:");
            for (Service s : services) {
                System.out.println(s.getInfo());
            }

            logger.info("printAllServices успешно выполнена: отображено {} услуг", services.size());

        } catch (Exception e) {
            logger.error("Ошибка при выполнении printAllServices", e);
            System.out.println("Произошла ошибка при выводе списка услуг: " + e.getMessage());
        }
    }
}