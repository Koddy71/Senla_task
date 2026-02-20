package ru.ilya.io.importer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

@Component
public class GuestImporter {
    private static final Logger logger = LoggerFactory.getLogger(GuestImporter.class);

    @Autowired
    private GuestService guestService;

    public GuestImporter() {
    }

    public int importCsv(String path) throws IOException {
        int count = 0;
        List<String[]> rows = CsvUtil.read(path);
        for (String[] r : rows) {
            if (r.length < 5) {
                System.out.println("Ошибка: недостаточно данных в строке: " + String.join(",", r));
                logger.error("Недостаточно данных в строке: {}", String.join(",", r));
                continue;
            }

            try {
                int id = Integer.parseInt(r[0].trim());
                String name = r[1].trim();
                int roomId = Integer.parseInt(r[2].trim());
                LocalDate checkInDate = LocalDate.parse(r[3].trim());
                LocalDate checkOutDate = LocalDate.parse(r[4].trim());

                Guest existing = guestService.findGuestById(id);
                if (existing == null) {
                    Guest g = guestService.checkIn(name, roomId, checkInDate, checkOutDate);
                    if (g != null) {
                        count++;

                        if (r.length == 6 && !r[5].trim().isEmpty()) {
                            String[] serviceIds = r[5].trim().split("\\|");
                            for (String serviceIdStr : serviceIds) {
                                try {
                                    int serviceId = Integer.parseInt(serviceIdStr.trim());
                                    guestService.addServiceToGuest(g.getId(), serviceId);
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка формата ID услуги: " + serviceIdStr);
                                    logger.error("Ошибка формата ID услуги: {}", serviceIdStr, e);
                                }
                            }
                        }

                    } else {
                        System.out.println("Не удалось заселить гостя: " + String.join(",", r));
                        logger.error("Не удалось заселить гостя: {}", String.join(",", r));
                    }
                }

            } catch (NumberFormatException e) {
                System.out.println("Ошибка формата числовых данных: " + String.join(",", r));
                logger.error("Ошибка формата числовых данных в строке: {}", String.join(",", r), e);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка формата даты: " + String.join(",", r));
                logger.error("Ошибка формата даты в строке: {}", String.join(",", r), e);
            } catch (Exception e) {
                System.out.println("Ошибка при регистрации гостя: " + e.getMessage());
                logger.error("Исключение при регистрации гостя из строки: {}", String.join(",", r), e);
            }
        }
        return count;
    }
}