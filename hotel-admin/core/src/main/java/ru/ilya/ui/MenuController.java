package ru.ilya.ui;

import ru.ilya.autodi.Inject;
import ru.ilya.controller.CsvFileController;
import ru.ilya.service.GuestService;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;
import ru.ilya.service.StateRestoreService;

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
    private CsvFileController csvFileController;

    @Inject
    private StateRestoreService stateRestoreService;

    private Navigator navigator;

    public MenuController() {
    }

    public void run() {
        builder.buildConsoleMenu();
        navigator = Navigator.getInstance(builder.getRootMenu());
        stateRestoreService.restore();
        navigator.start();
        stateRestoreService.save();
    }
}
