package ru.ilya.ui;


import ru.ilya.service.*;
import ru.ilya.autodi.Inject;
import ru.ilya.controller.ImportExportController;

public class MenuController {

   @Inject
   private Builder builder;

   @Inject
   private RoomService roomService;

   @Inject
   private GuestService guestService;

   @Inject
   private ServiceManager serviceManager;

   @Inject
   private ImportExportController importExportController;

   @Inject
   private StateRestoreService stateRestoreService;

   private Navigator navigator;

   public MenuController() {}

   public void run() {
      builder.buildConsoleMenu(); 
      navigator = Navigator.getInstance(builder.getRootMenu());
      stateRestoreService.restore();
      navigator.start();
      stateRestoreService.saveFromJson();
   }
}
