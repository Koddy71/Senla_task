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
        logger.info("Start processing command: addService");
        try {
            System.out.print("Введите название услуги: ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Название услуги не может быть пустым.");
                logger.error("addService aborted: empty service name");
                return;
            }

            System.out.print("Введите цену услуги (целое число): ");
            int price;
            try {
                price = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат цены.");
                logger.error("addService aborted: invalid price format");
                return;
            }

            Service s = new Service(name, price);

            boolean ok = serviceManager.addService(s);
            if (ok) {
                System.out.println("Услуга добавлена. ID: " + s.getId());
                logger.info("addService processed successfully: service '{}' added with id {}", name, s.getId());
            } else {
                System.out.println("Услуга с таким ID уже существует.");
                logger.error("addService failed: duplicate id for service '{}'", name);
            }

        } catch (Exception e) {
            logger.error("Error processing addService", e);
            System.out.println("Произошла ошибка при добавлении услуги: " + e.getMessage());
        }
    }

    public void removeService() {
        logger.info("Start processing command: removeService");
        try {
            System.out.print("Введите ID услуги для удаления: ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ID.");
                logger.error("removeService aborted: invalid id format");
                return;
            }

            boolean ok = serviceManager.removeService(id);
            if (ok) {
                System.out.println("Услуга удалена.");
                logger.info("removeService processed successfully: removed service {}", id);
            } else {
                System.out.println("Услуга не найдена.");
                logger.error("removeService failed: service {} not found", id);
            }

        } catch (Exception e) {
            logger.error("Error processing removeService", e);
            System.out.println("Произошла ошибка при удалении услуги: " + e.getMessage());
        }
    }

    public void findService() {
        logger.info("Start processing command: findService");
        try {
            System.out.print("Введите ID услуги для поиска: ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ID.");
                logger.error("findService aborted: invalid id format");
                return;
            }

            Service s = serviceManager.findService(id);
            if (s != null) {
                System.out.println("Найдена услуга: " + s.getInfo());
                logger.info("findService processed successfully: found service {}", id);
            } else {
                System.out.println("Услуга не найдена.");
                logger.error("findService failed: service {} not found", id);
            }

        } catch (Exception e) {
            logger.error("Error processing findService", e);
            System.out.println("Произошла ошибка при поиске услуги: " + e.getMessage());
        }
    }

    public void changePrice() {
        logger.info("Start processing command: changePrice");
        try {
            System.out.print("Введите ID услуги для изменения цены: ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат ID.");
                logger.error("changePrice aborted: invalid id format");
                return;
            }

            System.out.print("Введите новую цену (целое число): ");
            int newPrice;
            try {
                newPrice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат цены.");
                logger.error("changePrice aborted: invalid price format");
                return;
            }

            boolean ok = serviceManager.changePrice(id, newPrice);
            if (ok) {
                System.out.println("Цена изменена.");
                logger.info("changePrice processed successfully: service {} new price {}", id, newPrice);
            } else {
                System.out.println("Услуга не найдена.");
                logger.error("changePrice failed: service {} not found", id);
            }

        } catch (Exception e) {
            logger.error("Error processing changePrice", e);
            System.out.println("Произошла ошибка при изменении цены: " + e.getMessage());
        }
    }

    public void printAllServices() {
        logger.info("Start processing command: printAllServices");
        try {
            List<Service> services = serviceManager.getAllServices();

            if (services == null || services.isEmpty()) {
                System.out.println("Услуг нет.");
                logger.info("printAllServices processed successfully: no services found");
                return;
            }

            System.out.println("\nСписок услуг:");
            for (Service s : services) {
                System.out.println(s.getInfo());
            }

            logger.info("printAllServices processed successfully: displayed {} services", services.size());

        } catch (Exception e) {
            logger.error("Error processing printAllServices", e);
            System.out.println("Произошла ошибка при выводе списка услуг: " + e.getMessage());
        }
    }
}
