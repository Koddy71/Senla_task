package ru.ilya.controller;

import ru.ilya.service.PriceService;
import ru.ilya.model.Priceable;

import java.util.List;
import java.util.Scanner;

public class PriceController {
	private final PriceService priceService;
	private Scanner sc = new Scanner(System.in);

	public PriceController(PriceService priceService) {
		this.priceService = priceService;
	}

	public void showRoomsAndService() {
		System.out.print("Что вывести сначала ('room', 'service'): ");
		String sortBy = sc.nextLine();

		List<Priceable> sorted = priceService.getRoomsAndServices(null);
		for (Priceable r : sorted) {
			System.out.println(r.getInfo());
		}
	}
} 