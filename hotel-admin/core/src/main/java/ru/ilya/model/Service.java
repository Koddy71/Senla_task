package ru.ilya.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // для неизвсетного поля
public class Service extends Priceable {
    private int id;
    private String name;
    private int price;
    private static int idCounter = 1;

    // для сериализации
    public Service() {
    }

    public Service(String name, int price) {
        this.id = idCounter++;
        this.name = name;
        this.price = price;
    }

    public Service(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;

        if (id >= idCounter) {
            idCounter = id + 1;
        }
    }

    public static void setIdCounter(int next) {
        if (next > idCounter) {
            idCounter = next;
        }
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        if (price <= 0) {
            System.out.println("Цена должна быть больше нуля!");
            return;
        }
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getInfo() {
        return "ID: " + id + ", услуга " + name + " - " + price + " руб.";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Service))
            return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
