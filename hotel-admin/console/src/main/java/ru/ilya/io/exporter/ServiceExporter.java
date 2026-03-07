package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.io.CsvUtil;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;
import ru.ilya.exceptions.ExportException;

@Component
public class ServiceExporter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExporter.class);

    private final ServiceManager serviceManager;

    @Autowired
    public ServiceExporter(ServiceManager serviceManager) {
        this.serviceManager=serviceManager;
    }

    public void exportCsv(String path) throws IOException {
        try {
            List<String> lines = new ArrayList<>();

            lines.add("id,name,price");

            for (Service s : serviceManager.getAllServices()) {
                String line = String.format("%d,%s,%d",
                        s.getId(),
                        s.getName(),
                        s.getPrice());
                lines.add(line);
            }

            CsvUtil.write(path, lines);
        } catch (IOException e) {
            logger.error("Ошибка при записи файла экспорта услуг: {}", path, e);
            throw new ExportException("Ошибка экспорта услуг", e);
        } 
    }
}