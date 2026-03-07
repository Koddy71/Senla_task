package ru.ilya.ui;

import org.springframework.stereotype.Component;

@Component
public class Builder {
    private final MenuBuilder factory;
    private Menu rootMenu;

    public Builder(MenuBuilder factory) {
        this.factory = factory;
    }

    public void buildConsoleMenu() {
        rootMenu = factory.build(null);
    }

    public Menu getRootMenu() {
        return rootMenu;
    }
}
