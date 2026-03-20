package ru.ilya.service.impl;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;
import ru.ilya.dao.jpa.ServiceDaoJpa;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class ServiceManagerImpl implements ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManagerImpl.class);

    private final ServiceDaoJpa serviceDao;

    @Autowired
    public ServiceManagerImpl(ServiceDaoJpa serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Override
    public boolean addService(Service service) {
        logger.info("Начало добавления услуги");
        if (service == null) {
            logger.info("Добавление услуги не выполнено: service == null");
            return false;
        }

        serviceDao.create(service);
        logger.info("Добавление услуги завершено успешно (id={})", service.getId());
        return true;
    }

    @Override
    public boolean removeService(int id) {
        logger.info("Начало удаления услуги с ID {}", id);
        boolean result = serviceDao.delete(id);
        logger.info("Удаление услуги завершено. Результат: {}", result);
        return result;
    }

    @Override
    public Service findService(int id) {
        logger.info("Поиск услуги с ID {}", id);
        Service service = serviceDao.findById(id);
        logger.info("Поиск услуги завершен");
        return service;
    }

    @Override
    public boolean changePrice(int id, int newPrice) {
        logger.info("Начало изменения цены услуги с ID {}", id);
        Service service = serviceDao.findById(id);
        if (service != null && newPrice > 0) {
            service.setPrice(newPrice);
            serviceDao.update(service);
            logger.info("Изменение цены завершено успешно");
            return true;
        }
        logger.info("Изменение цены не выполнено");
        return false;
    }

    @Override
    public List<Service> getAllServices() {
        logger.info("Получение списка всех услуг");
        return serviceDao.findAll();
    }
}
