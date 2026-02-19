package ru.ilya.controller;

import ru.ilya.service.PriceService;
import ru.ilya.autodi.Inject;
import ru.ilya.model.Priceable;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceController {

    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    @Inject
    private PriceService priceService;

    private Scanner sc = new Scanner(System.in);

    public PriceController() {
    }

    public void showRoomsAndService() {
        logger.info("Начало обработки команды: showRoomsAndService");
        try {
            System.out.print("Что вывести сначала ('room', 'service'): ");
            String sortBy = sc.nextLine().trim();

            List<Priceable> sorted = priceService.getRoomsAndServices(sortBy);

            if (sorted == null || sorted.isEmpty()) {
                System.out.println("Нет данных для отображения.");
                logger.info("showRoomsAndService успешно выполнен: нет данных для отображения");
                return;
            }

            for (Priceable r : sorted) {
                System.out.println(r.getInfo());
            }

            logger.info("showRoomsAndService успешно выполнен: отображено {} элементов", sorted.size());

        } catch (Exception e) {
            logger.error("Ошибка при выполнении showRoomsAndService", e);
            System.out.println("Произошла ошибка при выводе данных: " + e.getMessage());
        }
    }
}