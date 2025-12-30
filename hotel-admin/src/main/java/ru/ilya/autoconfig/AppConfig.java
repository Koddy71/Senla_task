package ru.ilya.autoconfig;

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
