package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

public class ServiceExporter {
   @Inject
   private ServiceManager serviceManager;

   public ServiceExporter(){}
   
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
