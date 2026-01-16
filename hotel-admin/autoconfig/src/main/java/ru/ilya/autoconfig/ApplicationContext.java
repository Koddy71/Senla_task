package ru.ilya.autoconfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ru.ilya.autodi.Inject;

public class ApplicationContext {

   private final Map<Class<?>, Object> instances = new HashMap<>();
   private final Map<Class<?>, Class<?>> bindings = new HashMap<>();

   public <T> void bind(Class<T> abstraction, Class<? extends T> impl) {
      bindings.put(abstraction, impl);
   }

   public void addSingleton(Object obj) {
      instances.put(obj.getClass(), obj);
   }

   @SuppressWarnings("unchecked")
   public <T> T getInstance(Class<T> type) {
      try {
         Class<?> impl = bindings.getOrDefault(type, type);

         if (instances.containsKey(impl)) {
            return (T) instances.get(impl);
         }

         Constructor<?>[] constructors = impl.getDeclaredConstructors();
         Constructor<?> constructor = constructors[0];

         constructor.setAccessible(true);

         Class<?>[] paramTypes = constructor.getParameterTypes();
         Object[] params = new Object[paramTypes.length];

         for (int i = 0; i < paramTypes.length; i++) {
            params[i] = getInstance(paramTypes[i]);
         }

         Object obj = constructor.newInstance(params);

         instances.put(impl, obj);

         injectFields(obj);

         return (T) obj;

      } catch (Exception e) {
         throw new RuntimeException(
               "DI ошибка для класса: " + type.getName(), e);
      }
   }

   private void injectFields(Object obj) throws IllegalAccessException {
      for (Field field : obj.getClass().getDeclaredFields()) {
         if (field.isAnnotationPresent(Inject.class)) {
            field.setAccessible(true);
            Object dependency = getInstance(field.getType());
            field.set(obj, dependency);
         }
      }
   }
}
