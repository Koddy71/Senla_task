package ru.ilya.io.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ilya.exceptions.ServiceException;
import ru.ilya.io.JsonUtil;
import ru.ilya.model.Service;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class JsonServiceExporter {
    private static final Logger logger = LoggerFactory.getLogger(JsonServiceExporter.class);
    private static final String FILE_PATH = "core/src/main/resources/services.json";

    private final ObjectMapper mapper;
    private final JsonUtil jsonUtil;

    public JsonServiceExporter(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
        this.mapper = jsonUtil.getMapper();
    }

    public void exportJson(List<Service> services) {
        logger.info("Экспорт услуг в JSON");
        try {
            File file = new File(FILE_PATH);
            jsonUtil.createFileIfNeeded(file);
            mapper.writeValue(file, services);
            logger.info("Экспортировано {} услуг", services.size());
        } catch (IOException e) {
            logger.error("Ошибка при экспорте услуг", e);
            throw new ServiceException("Ошибка при сохранении данных услуг.", e);
        }
    }
}
