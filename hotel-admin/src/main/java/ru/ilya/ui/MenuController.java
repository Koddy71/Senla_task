package ru.ilya.ui;


public class MenuController {
	private final Navigator navigator;
   private final Builder builder;

	public MenuController(Builder builder) {
		this.builder=builder;
      this.builder.buildConsoleMenu();
      this.navigator=Navigator.getInstance(builder.getRootMenu());
	}

	public void run() {
		navigator.start();
	}
}
