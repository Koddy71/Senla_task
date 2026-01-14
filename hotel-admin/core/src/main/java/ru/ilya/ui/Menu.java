package ru.ilya.ui;

import java.util.ArrayList;
import java.util.List;

public class Menu {
	private String name;
	private List<MenuItem> items = new ArrayList<>();

	public Menu(String name) {
		this.name = name;
	}

	public void addItem(MenuItem item) {
		items.add(item);
	}

	public String getName() {
		return name;
	}

	public List<MenuItem> getItems() {
		return items;
	}
}
