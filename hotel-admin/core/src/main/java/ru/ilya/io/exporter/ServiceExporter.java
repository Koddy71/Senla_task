package ru.ilya.io.exporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ilya.autodi.Inject;
import ru.ilya.io.CsvUtil;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

public class ServiceExporter {
    private static final Logger logger = LoggerFactory.getLogger(ServiceExporter.class);

    @Inject
    private ServiceManager serviceManager;

    public ServiceExporter() {
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
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при экспорте услуг", e);
            throw new RuntimeException("Ошибка экспорта услуг", e);
        }
    }
}