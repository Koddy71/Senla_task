package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Guest;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;

public class GuestExporter {
    private static final Logger logger = LoggerFactory.getLogger(GuestExporter.class);

    @Inject
    private GuestService guestService;

    public GuestExporter() {
    }

    public void exportCsv(String path) throws IOException {
        try {
            List<String> lines = new ArrayList<>();

            lines.add("id,name,roomNumber,checkInDate,checkOutDate,serviceID");

            for (Guest g : guestService.getAllGuests()) {
                int roomNumber = g.getRoom().getNumber();

                StringBuilder serviceIDs = new StringBuilder();
                List<Service> services = g.getServices();

                for (int i = 0; i < services.size(); i++) {
                    Service service = services.get(i);
                    serviceIDs.append(service.getId());
                    if (i < services.size() - 1) {
                        serviceIDs.append("|");
                    }
                }

                String line = String.format("%d,%s,%d,%s,%s,%s",
                        g.getId(),
                        g.getName(),
                        roomNumber,
                        g.getCheckInDate(),
                        g.getCheckOutDate(),
                        serviceIDs.toString());
                lines.add(line);
            }

            CsvUtil.write(path, lines);
        } catch (IOException e) {
            logger.error("Ошибка при записи файла экспорта гостей: {}", path, e);
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при экспорте гостей", e);
            throw new RuntimeException("Ошибка экспорта гостей", e);
        }
    }
}