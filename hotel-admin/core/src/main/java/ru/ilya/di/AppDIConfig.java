package ru.ilya.di;

import ru.ilya.autoconfig.ApplicationContext;
import ru.ilya.service.*;
import ru.ilya.service.impl.*;
import ru.ilya.controller.*;
import ru.ilya.ui.*;

public class AppDIConfig {

   public static void configure(ApplicationContext ctx) {

      ctx.bind(RoomService.class, RoomServiceImpl.class);
      ctx.bind(GuestService.class, GuestServiceImpl.class);
      ctx.bind(ServiceManager.class, ServiceManagerImpl.class);
      ctx.bind(PriceService.class, PriceServiceImpl.class);
      ctx.bind(StateRestoreService.class, StateRestoreServiceImpl.class);

      ctx.bind(GuestController.class, GuestController.class);
      ctx.bind(RoomController.class, RoomController.class);
      ctx.bind(ServiceController.class, ServiceController.class);
      ctx.bind(CsvFileController.class, CsvFileController.class);

      ctx.bind(MenuBuilder.class, MenuBuilder.class);
      ctx.bind(Builder.class, Builder.class);
      ctx.bind(MenuController.class, MenuController.class);
   }
}
