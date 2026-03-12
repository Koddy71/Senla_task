package ru.ilya.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuController {
    @Autowired
    private Builder builder;

    private Navigator navigator;

    public MenuController(Builder builder) {
        this.builder = builder;
    }

    public void run() {
        builder.buildConsoleMenu();
        navigator = Navigator.getInstance(builder.getRootMenu());
        navigator.start();
    }
}
