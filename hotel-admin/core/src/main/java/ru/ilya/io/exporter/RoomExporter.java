package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Room;
import ru.ilya.service.RoomService;

public class RoomExporter {
    private static final Logger logger = LoggerFactory.getLogger(GuestExporter.class);

    @Inject
    private RoomService roomService;

    public RoomExporter() {
    }

    public void exportCsv(String path) throws IOException {
        try{
            List<String> lines = new ArrayList<>();

            lines.add("number,price,capacity,stars");

            for (Room r : roomService.getAllRooms()) {
                String line = String.format("d,d,d,d",
                    r.getNumber(),
                    r.getPrice(),
                    r.getCapacity(),
                    r.getStars());
                lines.add(line);
            }

            CsvUtil.write(path, lines);
        } catch(IOException e){
            logger.error("Ошибка при записи файла экспорта гостей: {}", path, e);
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при экспорте гостей", e);
            throw new RuntimeException("Ошибка экспорта гостей", e);
        }
    }
}
