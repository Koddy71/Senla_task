package ru.ilya.autoconfig;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigInjector {
   private final String defaultConfigFileName;
   private final Map<String, Properties> cache = new HashMap<>();
   
   public ConfigInjector(String defaultConfigFileName){
      this.defaultConfigFileName=defaultConfigFileName;
   }

   public void configure(Object target){
      if (target ==null){
         return;
      }

      Class<?> clazz = target.getClass();
      for(Field field : clazz.getDeclaredFields()){
         ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
         if (annotation == null){
            continue;
         }

         String configFileName = annotation.configFilename();
         if (configFileName == null || configFileName.isEmpty()){
            configFileName = defaultConfigFileName;
         }

         Properties props = loadProperties(configFileName);

         String propertyName =  annotation.propertyName();
         if (propertyName == null || propertyName.isEmpty()){
            propertyName = clazz.getSimpleName() + "."  + field.getName();
         }

         String rawValue = props.getProperty(propertyName);
         if (rawValue == null) {
            continue;
         }

         Object converted = convertValue(rawValue, field.getType(), annotation.type());
         try {
            field.setAccessible(true);
            field.set(target, converted);
         } catch (IllegalAccessException e) {
            throw new RuntimeException("Не удалось установить значение для поля " + field.getName() + " в классе " + clazz.getName(), e);
         }
      }
   }

   private Properties loadProperties(String fileName){
      if (cache.containsKey(fileName)){
         return cache.get(fileName);
      }

      Properties props = new Properties();
      try(FileReader reader = new FileReader(fileName)){
         props.load(reader);
      } catch (IOException e){
         throw new RuntimeException("Не удалось загрузить файл конфигурации: " + fileName, e);
      }
      cache.put(fileName, props);
      return props;
   }

   private Object convertValue(String raw, Class<?> fieldType, ValueType valueType) {
      ValueType typeToUse = valueType;

      if (typeToUse == ValueType.AUTO) {
         if (fieldType == int.class || fieldType == Integer.class) {
            typeToUse = ValueType.INT;
         } else if (fieldType == long.class || fieldType == Long.class) {
            typeToUse = ValueType.LONG;
         } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            typeToUse = ValueType.BOOLEAN;
         } else if (fieldType == double.class || fieldType == Double.class) {
            typeToUse = ValueType.DOUBLE;
         } else {
            typeToUse = ValueType.STRING;
         }
      }

      String trimmed = raw.trim();

      if (typeToUse == ValueType.INT) {
         return Integer.parseInt(trimmed);
      }
      if (typeToUse == ValueType.LONG) {
         return Long.parseLong(trimmed);
      }
      if (typeToUse == ValueType.BOOLEAN) {
         return Boolean.parseBoolean(trimmed);
      }
      if (typeToUse == ValueType.DOUBLE) {
         return Double.parseDouble(trimmed);
      }
      return trimmed;
   }
   
   
}
