package ru.ilya.ui;

public class MenuItem {
   private String title;
   private IAction action;
   private Menu nextMenu;

   public MenuItem(String title, IAction action) {
      this.title = title;
      this.action = action;
   }

   public MenuItem(String title, Menu nextMenu) {
      this.title = title;
      this.nextMenu = nextMenu;
   }

   public String getTitle() {
      return title;
   }

   public IAction getAction() {
      return action;
   }

   public Menu getNextMenu() {
      return nextMenu;
   }

}
