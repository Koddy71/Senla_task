package ru.ilya.service.impl;

import ru.ilya.autodi.Inject;
import ru.ilya.model.Priceable;
import ru.ilya.model.Room;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceServiceImpl implements PriceService {

    private static final Logger logger = LoggerFactory.getLogger(PriceServiceImpl.class);

    @Inject
    private RoomService roomService;

    @Inject
    private ServiceManager serviceManager;

    public PriceServiceImpl() {
        logger.info("PriceServiceImpl инициализирован");
    }

    public List<Priceable> getRoomsAndServices(String orderBy) {
        logger.info("Начало получения списка комнат и услуг с сортировкой по '{}'", orderBy);

        List<Priceable> result = new ArrayList<>();

        List<Room> roomList = new ArrayList<>(roomService.getAllRooms());
        List<Service> serviceList = new ArrayList<>(serviceManager.getAllServices());

        roomList.sort(Comparator.comparingInt(Room::getPrice));
        serviceList.sort(Comparator.comparingInt(Service::getPrice));

        if ("service".equalsIgnoreCase(orderBy)) {
            result.addAll(serviceList);
            result.addAll(roomList);
        } else {
            result.addAll(roomList);
            result.addAll(serviceList);
        }

        logger.info("Получение списка завершено. Всего элементов: {}", result.size());
        return result;
    }
}