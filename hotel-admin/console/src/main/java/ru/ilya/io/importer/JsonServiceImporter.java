package ru.ilya.io.importer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ilya.exceptions.ServiceException;
import ru.ilya.io.JsonUtil;
import ru.ilya.model.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonServiceImporter {
    private static final Logger logger = LoggerFactory.getLogger(JsonServiceImporter.class);
    private static final String FILE_PATH = "core/src/main/resources/services.json";

    private final ObjectMapper mapper;
    private final JsonUtil jsonUtil;

    public JsonServiceImporter(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
        this.mapper = jsonUtil.getMapper();
    }

    public List<Service> importJson() {
        logger.info("Импорт услуг из JSON");
        try {
            File file = new File(FILE_PATH);
            boolean wasCreated = !file.exists();
            jsonUtil.createFileIfNeeded(file);

            if (wasCreated || file.length() == 0) {
                logger.info("Файл услуг пуст или не существовал, возвращаем пустой список");
                return new ArrayList<>();
            }

            List<Service> services = mapper.readValue(file, new TypeReference<List<Service>>() {
            });
            // Устанавливаем счётчик ID для новых услуг
            int maxId = services.stream().mapToInt(Service::getId).max().orElse(0);
            Service.setIdCounter(maxId + 1);
            logger.info("Импортировано {} услуг", services.size());
            return services;
        } catch (IOException e) {
            logger.error("Ошибка при импорте услуг", e);
            throw new ServiceException("Ошибка при загрузке данных услуг.", e);
        }
    }
}
