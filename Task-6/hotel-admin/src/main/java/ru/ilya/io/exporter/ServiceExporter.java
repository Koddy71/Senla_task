package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

public class ServiceExporter {
   private static ServiceExporter instance;

   private final ServiceManager serviceManager;

   public ServiceExporter(ServiceManager serviceManager) {
      this.serviceManager = serviceManager;
   }
   
   public static ServiceExporter getInstance(ServiceManager serviceManager) {
      if (instance == null) {
         instance = new ServiceExporter(serviceManager);
      }
      return instance;
   }
   
   public void exportCsv(String path) throws IOException {
      List<String> lines = new ArrayList<>();

      lines.add("id,name,price");

      for (Service s : serviceManager.getAllServices()) {
         lines.add(
               s.getId() + "," +
               s.getName() + "," +
               s.getPrice());
      }

      CsvUtil.write(path, lines);
   }
}
