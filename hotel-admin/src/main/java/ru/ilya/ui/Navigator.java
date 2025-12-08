package ru.ilya.ui;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;


public class Navigator {
	private static Navigator instance;
	private final Stack<Menu> stack = new Stack<>();
	private final Scanner sc = new Scanner(System.in);

	private Navigator(Menu root) {
		stack.push(root);
	}

	public static Navigator getInstance(Menu root) {
		if (instance == null) {
			instance = new Navigator(root);
		}
		return instance;
	}

	private void printCurrent() {
		Menu cur = stack.peek();
		System.out.println("\n=== " + cur.getName() + " ===");
		List<MenuItem> items = cur.getItems();
		for (int i = 0; i < items.size(); i++) {
			System.out.println((i + 1) + ". " + items.get(i).getTitle());
		}

		if (stack.size() > 1) {
			System.out.println("0. Назад");
		} else {
			System.out.println("0. Выход");
		}

		System.out.print("Выбор: ");
	}

	public void start() {
		while (!stack.isEmpty()) {
			printCurrent();
			int choice;
			try {
				choice = Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Неверный ввод.");
				continue;
			}

			if (choice == 0) {
				if (stack.size() == 1) {
					System.out.println("Выход из программы...");
					break; 
				} else {
					stack.pop(); // возврат в предыдущее меню
					continue;
				}
			}

			Menu cur = stack.peek();
			if (choice < 1 || choice > cur.getItems().size()) {
				System.out.println("Неверный пункт.");
				continue;
			}

			MenuItem item = cur.getItems().get(choice - 1);
			if (item.getNextMenu() != null) {
				stack.push(item.getNextMenu());
			} else if (item.getAction() != null) {
				item.getAction().execute();
			}
		}
		System.out.println("Выход из меню.");
	}
}
