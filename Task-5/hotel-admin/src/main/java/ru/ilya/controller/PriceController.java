package ru.ilya.controller;

import ru.ilya.service.PriceService;
import ru.ilya.model.Priceable;

import java.util.List;
import java.util.Scanner;

public class PriceController {
   private static PriceController instance;
	private final PriceService priceService;
	private Scanner sc = new Scanner(System.in);

	private PriceController(PriceService priceService) {
		this.priceService = priceService;
	}

	public void showRoomsAndService() {
		System.out.print("Что вывести сначала ('room', 'service'): ");
		String sortBy = sc.nextLine();

		List<Priceable> sorted = priceService.getRoomsAndServices(sortBy);
		for (Priceable r : sorted) {
			System.out.println(r.getInfo());
		}
	}

   public static PriceController getInstance(PriceService priceService) {
      if (instance == null) {
         instance = new PriceController(priceService);
      }
      return instance;
   }
} 