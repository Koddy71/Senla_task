package ru.ilya.service;

import ru.ilya.model.Service;
import java.util.List;

public interface ServiceManager {

   boolean addService(Service service);

   boolean removeService(int id);

   Service findService(int id);

   boolean changePrice(int id, int newPrice);

   List<Service> getAllServices();
}
