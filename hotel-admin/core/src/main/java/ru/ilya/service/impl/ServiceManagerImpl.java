package ru.ilya.service.impl;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManagerImpl implements ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManagerImpl.class);

    private Map<Integer, Service> services = new HashMap<>();

    public ServiceManagerImpl() {
        logger.info("ServiceManagerImpl инициализирован");
    }

    @Override
    public boolean addService(Service service) {
        logger.info("Начало добавления услуги");

        if (service == null || services.containsKey(service.getId())) {
            logger.info("Добавление услуги не выполнено");
            return false;
        }

        services.put(service.getId(), service);
        logger.info("Добавление услуги завершено успешно");
        return true;
    }

    @Override
    public boolean removeService(int id) {
        logger.info("Начало удаления услуги с ID {}", id);

        boolean result = services.remove(id) != null;

        logger.info("Удаление услуги завершено. Результат: {}", result);
        return result;
    }

    @Override
    public Service findService(int id) {
        logger.info("Поиск услуги с ID {}", id);

        Service service = services.get(id);

        logger.info("Поиск услуги завершен");
        return service;
    }

    @Override
    public boolean changePrice(int id, int newPrice) {
        logger.info("Начало изменения цены услуги с ID {}", id);

        Service service = services.get(id);
        if (service != null && newPrice > 0) {
            service.setPrice(newPrice);
            logger.info("Изменение цены завершено успешно");
            return true;
        }

        logger.info("Изменение цены не выполнено");
        return false;
    }

    @Override
    public List<Service> getAllServices() {
        logger.info("Получение списка всех услуг");
        return new ArrayList<>(services.values());
    }
}
