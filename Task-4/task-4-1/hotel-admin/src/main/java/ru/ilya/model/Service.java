package ru.ilya.model;

public class Service {
	private String name;
	private int price;

	public Service(String name, int price) {
		this.name = name;
		this.price = price;
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
		return name + " - " + price + " руб.";
	}
}
