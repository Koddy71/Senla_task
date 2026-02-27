package ru.ilya.io.importer;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Room;
import ru.ilya.service.RoomService;

@Component
public class RoomImporter {
    private static final Logger logger = LoggerFactory.getLogger(RoomImporter.class);

    private final RoomService roomService;
    
    @Autowired
    public RoomImporter(RoomService roomService) {
        this.roomService=roomService;
    }

    public int importCsv(String path) throws IOException {
        int count = 0;
        List<String[]> rows = CsvUtil.read(path);
        for (String[] r : rows) {
            if (r.length < 4) {
                System.out.println("Недостаточно данных в строке: " + String.join(",", r));
                logger.error("Недостаточно данных в строке: {}", String.join(",", r));
                continue;
            }
            try {
                int number = Integer.parseInt(r[0].trim());
                int price = Integer.parseInt(r[1].trim());
                int capacity = Integer.parseInt(r[2].trim());
                int stars = Integer.parseInt(r[3].trim());

                if (number <= 0 || price < 0 || capacity <= 0 || stars < 0) {
                    System.out.println("Ошибка: некорректные значения в строке: " + String.join(",", r));
                    logger.error("Некорректные значения в строке: {}", String.join(",", r));
                    continue;
                }

                Room room = new Room(number, price, capacity, stars);
                if (roomService.addRoom(room)) {
                    count++;
                } else {
                    System.out.println(
                            "Комната не добавлена (возможно, такой номер уже существует): " + String.join(",", r));
                    logger.error("Не удалось добавить комнату (возможно, дубликат номера): {}", String.join(",", r));
                }

            } catch (NumberFormatException e) {
                System.out.println("Ошибка формата данных: " + String.join(",", r));
                logger.error("Ошибка парсинга чисел в строке: {}", String.join(",", r), e);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка статуса комнаты: " + String.join(",", r));
                logger.error("Некорректный статус комнаты в строке: {}", String.join(",", r), e);
            }
        }
        return count;
    }
}