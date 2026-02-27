package ru.ilya.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.ilya.service.StateRestoreService;

@Component
public class MenuController {
    @Autowired
    private Builder builder;

    @Autowired
    private StateRestoreService stateRestoreService;

    private Navigator navigator;

    public MenuController(Builder builder, StateRestoreService stateRestoreService) {
        this.builder = builder;
        this.stateRestoreService = stateRestoreService;
    }

    public void run() {
        builder.buildConsoleMenu();
        navigator = Navigator.getInstance(builder.getRootMenu());
        stateRestoreService.restore();
        navigator.start();
        stateRestoreService.save();
    }
}
