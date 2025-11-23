package ru.ilya.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Service extends Priceable{
   private int id;
	private String name;
	private int price;
   private static AtomicInteger idGenerator = new AtomicInteger(1);

	public Service(String name, int price) {
      this.id = idGenerator.incrementAndGet();
		this.name = name;
		this.price = price;
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
		return "ID: " + id + "| услуга "+ name + " - " + price + " руб.";
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
