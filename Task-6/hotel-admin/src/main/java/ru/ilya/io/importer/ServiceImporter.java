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

   public int importCsv(String path) throws IOException {
      int count =0;
      List<String[]> rows = CsvUtil.read(path);

      for (String[] r : rows) {
         if (r.length < 3) {
            System.out.println("Ошибка: некорректные значения в строке: " + String.join(",", r));
            continue;
         }

         try {
            int id = Integer.parseInt(r[0].trim());
            String name = r[1].trim();
            int price = Integer.parseInt(r[2].trim());

            if (name.isEmpty()) {
               System.out.println("Ошибка: название услуги пустое: " + String.join(",", r));
               continue;
            }

            if (price < 0) {
               System.out.println("Ошибка: цена не может быть отрицательной: " + String.join(",", r));
               continue;
            }

            Service service = new Service(id, name, price);
            boolean ok = serviceManager.addService(service);
            if (ok) {
               count++; // ← успешно импортировано
            } else {
               System.out.println("Услуга не добавлена (возможно, ID уже существует): " + String.join(",", r));
            }

         } catch (NumberFormatException e) {
            System.out.println("Ошибка формата числовых данных: " + String.join(",", r));
         } catch (Exception e) {
            System.out.println("Ошибка при добавлении услуги: " + e.getMessage());
         }
      }
      return count;
   }
}
