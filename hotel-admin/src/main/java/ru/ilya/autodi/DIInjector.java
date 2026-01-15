package ru.ilya.autodi;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ru.ilya.service.*;
import ru.ilya.service.impl.*;
import ru.ilya.controller.*;
import ru.ilya.ui.*;

public class DIInjector {

   private final Map<Class<?>, Object> instances = new HashMap<>();
   private final Map<Class<?>, Class<?>> bindings = new HashMap<>();

   public DIInjector() {
      bindings.put(RoomService.class, RoomServiceImpl.class);
      bindings.put(GuestService.class, GuestServiceImpl.class);
      bindings.put(ServiceManager.class, ServiceManagerImpl.class);
      bindings.put(PriceService.class, PriceServiceImpl.class);

      bindings.put(GuestController.class, GuestController.class);
      bindings.put(RoomController.class, RoomController.class);
      bindings.put(ServiceController.class, ServiceController.class);
      bindings.put(ImportExportController.class, ImportExportController.class);

      bindings.put(MenuController.class, MenuController.class);
      bindings.put(Navigator.class, Navigator.class);
      bindings.put(Menu.class, Menu.class);
   }

   public void inject(Object rootConfig) {
      instances.put(rootConfig.getClass(), rootConfig);
   }

   @SuppressWarnings("unchecked")
   public <T> T getInstance(Class<T> type) {
      try {
         Class<?> impl = bindings.getOrDefault(type, type);

         if (instances.containsKey(impl)) {
            return (T) instances.get(impl);
         }

         Object obj = impl.getDeclaredConstructor().newInstance();
         instances.put(impl, obj);

         injectFields(obj);
         return (T) obj;

      } catch (Exception e) {
         throw new RuntimeException("DI ошибка для класса: " + type.getName(), e);
      }
   }

   private void injectFields(Object obj) throws IllegalAccessException {
      for (Field field : obj.getClass().getDeclaredFields()) {
         if (field.isAnnotationPresent(Inject.class)) {
            field.setAccessible(true);
            Object dep = getInstance(field.getType());
            field.set(obj, dep);
         }
      }
   }
}
