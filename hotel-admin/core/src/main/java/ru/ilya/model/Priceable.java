package ru.ilya.model;

public abstract class Priceable {
    protected int price;

    public int getPrice() {
        return price;
    }

    public abstract String getInfo();
}
