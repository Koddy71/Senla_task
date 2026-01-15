package ru.ilya;

import ru.ilya.autoconfig.ApplicationContext;
import ru.ilya.autoconfig.AppConfig;
import ru.ilya.autoconfig.ConfigInjector;
import ru.ilya.ui.MenuController;
import ru.ilya.di.AppDIConfig;

public class HotelAdmin {

   public static void main(String[] args) {

      AppConfig config = new AppConfig();

      ConfigInjector configInjector =new ConfigInjector("config.properties");
      configInjector.configure(config);

      ApplicationContext context = new ApplicationContext();
      context.addSingleton(config);

      AppDIConfig.configure(context);

      MenuController menuController = context.getInstance(MenuController.class);

      menuController.run();
   }
}
