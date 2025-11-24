package ru.ilya.io.importer;

import java.io.IOException;
import java.util.List;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

public class ServiceImporter {
   private static ServiceImporter instance;

   private final ServiceManager serviceManager;

   public ServiceImporter(ServiceManager serviceManager) {
      this.serviceManager = serviceManager;
   }

   public static ServiceImporter getInstance(ServiceManager serviceManager) {
      if (instance == null) {
         instance = new ServiceImporter(serviceManager);
      }
      return instance;
   }

   public void importCsv(String path) throws IOException {
      List<String[]> rows = CsvUtil.read(path);

      for (String[] r : rows) {
         int id = Integer.parseInt(r[0]);
         String name = r[1];
         int price = Integer.parseInt(r[2]);

         Service service = new Service(id, name, price);

         serviceManager.addService(service);
      }
   }
}
