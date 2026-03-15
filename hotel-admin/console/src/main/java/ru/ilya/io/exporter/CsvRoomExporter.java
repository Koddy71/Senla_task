package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.model.Room;
import ru.ilya.service.RoomService;
import ru.ilya.exceptions.ExportException;
import ru.ilya.io.CsvUtil;

@Component
public class CsvRoomExporter {
    private static final Logger logger = LoggerFactory.getLogger(CsvRoomExporter.class);

    private final RoomService roomService;

    @Autowired
    public CsvRoomExporter(RoomService roomService) {
        this.roomService=roomService;
    }

    public void exportCsv(String path) throws IOException {
        try{
            List<String> lines = new ArrayList<>();

            lines.add("number,price,capacity,stars,status");

            for (Room r : roomService.getAllRooms()) {
                String line = String.format("d,d,d,d,s",
                    r.getNumber(),
                    r.getPrice(),
                    r.getCapacity(),
                    r.getStars(),
                    r.getStatus().name());
                lines.add(line);
            }

            CsvUtil.write(path, lines);
        } catch(IOException e){
            logger.error("Ошибка при записи файла экспорта комнат: {}", path, e);
            throw new ExportException("Ошибка экспорта комнат", e);
        } 
    }
}
