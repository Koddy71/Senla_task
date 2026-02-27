package ru.ilya;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.ilya.ui.MenuController;
import ru.ilya.di.SpringAppConfig;

public class HotelAdmin {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringAppConfig.class);
        MenuController menuController = context.getBean(MenuController.class);
        menuController.run();
        context.close();
    }
}
