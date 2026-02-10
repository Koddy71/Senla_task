package ru.ilya.service.impl;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ServiceManagerImpl implements ServiceManager {

   private Map<Integer, Service> services = new HashMap<>();

   public ServiceManagerImpl() {
   }

   @Override
   public boolean addService(Service service) {
      if (service == null || services.containsKey(service.getId())) {
         return false;
      }
      services.put(service.getId(), service);
      return true;
   }

   @Override
   public boolean removeService(int id) {
      return services.remove(id) != null;
   }

   @Override
   public Service findService(int id) {
      return services.get(id);
   }

   @Override
   public boolean changePrice(int id, int newPrice) {
      Service service = findService(id);
      if (service != null && newPrice > 0) {
         service.setPrice(newPrice);
         return true;
      }
      return false;
   }

   @Override
   public List<Service> getAllServices() {
      return new ArrayList<>(services.values());
   }

}
