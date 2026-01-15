import java.time.LocalTime;

public class Task4 {
   public static void main(String[] args) throws InterruptedException {
      int n = 2;

      Thread thread = new Thread(() -> {
         try {
            while (true) {
               System.out.println("Время: " + LocalTime.now());
               Thread.sleep(n * 1000);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      });

      thread.setDaemon(true); 
      thread.start();

      Thread.sleep(6000);
      System.out.println("Main завершён");
   }
}
