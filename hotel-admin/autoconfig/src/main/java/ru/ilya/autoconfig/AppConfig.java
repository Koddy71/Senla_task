package ru.ilya.autoconfig;

import ru.ilya.autodi.ConfigProperty;

public class AppConfig {
   @ConfigProperty
   private boolean roomStatusChangeEnable;

   @ConfigProperty
   private int roomHistoryLimit;

   public boolean isRoomStatusChangeEnable(){
      return roomStatusChangeEnable;
   }

   public int getRoomHistoryLimit() {
      return roomHistoryLimit;
   }
}
