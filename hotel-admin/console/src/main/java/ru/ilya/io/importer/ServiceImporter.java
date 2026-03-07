package ru.ilya.io.importer;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

@Component
public class ServiceImporter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceImporter.class);

    private final ServiceManager serviceManager;

    @Autowired
    public ServiceImporter(ServiceManager serviceManager) {
        this.serviceManager=serviceManager;
    }

    public int importCsv(String path) throws IOException {
        int count = 0;
        List<String[]> rows = CsvUtil.read(path);

        for (String[] r : rows) {
            if (r.length < 3) {
                System.out.println("Ошибка: некорректные значения в строке: " + String.join(",", r));
                logger.error("Некорректные значения в строке: {}", String.join(",", r));
                continue;
            }

            try {
                int id = Integer.parseInt(r[0].trim());
                String name = r[1].trim();
                int price = Integer.parseInt(r[2].trim());

                if (name.isEmpty()) {
                    System.out.println("Ошибка: название услуги пустое: " + String.join(",", r));
                    logger.error("Пустое название услуги в строке: {}", String.join(",", r));
                    continue;
                }

                if (price < 0) {
                    System.out.println("Ошибка: цена не может быть отрицательной: " + String.join(",", r));
                    logger.error("Отрицательная цена в строке: {}", String.join(",", r));
                    continue;
                }

                Service service = new Service(id, name, price);
                boolean ok = serviceManager.addService(service);
                if (ok) {
                    count++;
                } else {
                    System.out.println("Услуга не добавлена (возможно, ID уже существует): " + String.join(",", r));
                    logger.error("Не удалось добавить услугу (возможно, дубликат ID): {}", String.join(",", r));
                }

            } catch (NumberFormatException e) {
                System.out.println("Ошибка формата числовых данных: " + String.join(",", r));
                logger.error("Ошибка парсинга чисел в строке: {}", String.join(",", r), e);
            } catch (Exception e) {
                System.out.println("Ошибка при добавлении услуги: " + e.getMessage());
                logger.error("Исключение при добавлении услуги из строки: {}", String.join(",", r), e);
            }
        }
        return count;
    }
}