package ru.ilya;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.dao.jdbc.GuestDaoJdbc;
import ru.ilya.dao.jdbc.RoomDaoJdbc;
import ru.ilya.dao.jdbc.ServiceDaoJdbc;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

/**
 * При старте веб-приложения (в Tomcat) подгружает данные из базы через DAO
 * и раскладывает их по in-memory коллекциям в сервисах (RoomService,
 * ServiceManager, GuestService). Без этого они останутся пустыми.
 *
 * Раньше в консольной версии загрузку делал StateRestoreService через
 * MenuController.run(), но в веб-версии мы не запускаем консольный flow,
 * поэтому нужен отдельный компонент, который срабатывает автоматически
 * при старте Spring-контекста.
 *
 * Работает как с jdbc, так и с jpa — тип хранилища берётся из AppConfig.getStorageType().
 */

@Component
public class StartupStateLoader implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(StartupStateLoader.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RoomDaoJdbc roomDaoJdbc;

    @Autowired
    private ServiceDaoJdbc serviceDaoJdbc;

    @Autowired
    private GuestDaoJdbc guestDaoJdbc;

    @Autowired
    private RoomService roomService;

    @Autowired
    private ServiceManager serviceManager;

    @Autowired
    private GuestService guestService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String storageType = Optional.ofNullable(appConfig).map(AppConfig::getStorageType).orElse("").trim()
                    .toLowerCase();
            if (!("jdbc".equals(storageType) || "jpa".equals(storageType))) {
                logger.info(
                        "StartupStateLoader: storageType = '{}'. Пропускаем автоматическую загрузку (требуется jdbc/jpa).",
                        storageType);
                return;
            }

            logger.info("StartupStateLoader: начинаем загрузку состояния из БД (storageType = {}).", storageType);

            List<Room> rooms = roomDaoJdbc.findAll();
            if (rooms != null && !rooms.isEmpty()) {
                logger.info("StartupStateLoader: загружено {} комнат из БД. Заполняем RoomService.", rooms.size());
                for (Room room : rooms) {
                    try {
                        roomService.addRoom(room);
                    } catch (Exception ex) {
                        logger.warn("Не удалось добавить комнату в RoomService: {}", room, ex);
                    }
                }
            } else {
                logger.info("StartupStateLoader: комнаты не найдены в БД.");
            }

            List<Service> services = serviceDaoJdbc.findAll();
            if (services != null && !services.isEmpty()) {
                logger.info("StartupStateLoader: загружено {} услуг из БД. Заполняем ServiceManager.", services.size());
                for (Service svc : services) {
                    try {
                        serviceManager.addService(svc);
                    } catch (Exception ex) {
                        logger.warn("Не удалось добавить услугу в ServiceManager: {}", svc, ex);
                    }
                }

                try {
                    int maxServiceId = services.stream().mapToInt(Service::getId).max().orElse(0);
                    Service.setIdCounter(maxServiceId + 1);
                    logger.info("StartupStateLoader: Service.idCounter установлен в {}", Service.getIdCounter());
                } catch (Throwable t) {
                    logger.debug("StartupStateLoader: не удалось установить Service.idCounter автоматически", t);
                }

            } else {
                logger.info("StartupStateLoader: услуги не найдены в БД.");
            }

            List<Guest> guests = guestDaoJdbc.findAll();
            if (guests != null && !guests.isEmpty()) {
                logger.info("StartupStateLoader: загружено {} гостей из БД. Регистрируем через GuestService.",
                        guests.size());
                for (Guest g : guests) {
                    try {
                        Guest newGuest = guestService.checkIn(
                                g.getName(),
                                g.getRoom() != null ? g.getRoom().getNumber() : null,
                                g.getCheckInDate(),
                                g.getCheckOutDate());

                        if (g.getServices() != null) {
                            for (Service s : g.getServices()) {
                                try {
                                    guestService.addServiceToGuest(newGuest.getId(), s.getId());
                                } catch (Exception ex) {
                                    logger.warn("Не удалось добавить услугу (id={}) гостю (id={}): {}",
                                            s != null ? s.getId() : null, newGuest.getId(), ex.getMessage());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logger.warn("Не удалось восстановить гостя из БД: {} (пропускаем).", g, ex);
                    }
                }
            } else {
                logger.info("StartupStateLoader: гости не найдены в БД.");
            }

            logger.info("StartupStateLoader: загрузка состояния завершена.");
        } catch (Throwable t) {
            logger.error("StartupStateLoader: ошибка при загрузке состояния при старте приложения", t);
        }
    }
}
