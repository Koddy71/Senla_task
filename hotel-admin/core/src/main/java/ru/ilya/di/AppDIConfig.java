package ru.ilya.di;

import ru.ilya.autoconfig.ApplicationContext;
import ru.ilya.service.GuestService;
import ru.ilya.service.PriceService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.StateRestoreService;
import ru.ilya.service.impl.GuestServiceImpl;
import ru.ilya.service.impl.PriceServiceImpl;
import ru.ilya.service.impl.RoomServiceImpl;
import ru.ilya.service.impl.ServiceManagerImpl;
import ru.ilya.service.impl.StateRestoreServiceImpl;


public class AppDIConfig {

    public static void configure(ApplicationContext ctx) {

        ctx.bind(RoomService.class, RoomServiceImpl.class);
        ctx.bind(GuestService.class, GuestServiceImpl.class);
        ctx.bind(ServiceManager.class, ServiceManagerImpl.class);
        ctx.bind(PriceService.class, PriceServiceImpl.class);
        ctx.bind(StateRestoreService.class, StateRestoreServiceImpl.class);
    }
}
