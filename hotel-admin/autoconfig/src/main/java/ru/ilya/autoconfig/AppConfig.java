package ru.ilya.autoconfig;

import ru.ilya.autodi.ConfigProperty;

public class AppConfig {
   @ConfigProperty
   private boolean roomStatusChangeEnable;

   @ConfigProperty
   private int roomHistoryLimit;

   @ConfigProperty(propertyName = "storage.type")
   private String storageType;

   public boolean isRoomStatusChangeEnable(){
      return roomStatusChangeEnable;
   }

   public int getRoomHistoryLimit() {
      return roomHistoryLimit;
   }

   public String getStorageType(){
      return storageType;
   }
}
