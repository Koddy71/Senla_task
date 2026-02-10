package ru.ilya.di;

import ru.ilya.autoconfig.ApplicationContext;
import ru.ilya.service.*;
import ru.ilya.service.impl.*;

public class AppDIConfig {

   public static void configure(ApplicationContext ctx) {

      ctx.bind(RoomService.class, RoomServiceImpl.class);
      ctx.bind(GuestService.class, GuestServiceImpl.class);
      ctx.bind(ServiceManager.class, ServiceManagerImpl.class);
      ctx.bind(PriceService.class, PriceServiceImpl.class);
      ctx.bind(StateRestoreService.class, StateRestoreServiceImpl.class);
   }
}
