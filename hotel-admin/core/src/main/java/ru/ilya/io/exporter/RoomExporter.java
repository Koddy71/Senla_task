package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Room;
import ru.ilya.service.RoomService;
import ru.ilya.exceptions.ExportException;

@Component
public class RoomExporter {
    private static final Logger logger = LoggerFactory.getLogger(GuestExporter.class);

    private final RoomService roomService;

    @Autowired
    public RoomExporter(RoomService roomService) {
        this.roomService=roomService;
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
            logger.error("Ошибка при записи файла экспорта комнат: {}", path, e);
            throw new ExportException("Ошибка экспорта комнат", e);
        } 
    }
}
