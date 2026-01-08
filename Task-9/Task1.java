public class Task1 {
   public static void main(String[] args) throws InterruptedException {
      final Object lock = new Object();

      Thread thread = new Thread(() -> {
         try {
            synchronized (lock) {
               Thread.sleep(200);
               lock.wait();
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      });

      System.out.println("NEW: " + thread.getState());

      synchronized (lock) { // main занимает lock
         thread.start();

         System.out.println("RUNNABLE: " + thread.getState());
         Thread.sleep(50); // Даем потоку время дойти до synchronized
         System.out.println("BLOCKED: " + thread.getState());
      }

      Thread.sleep(50); // Даём время main выйти из lock
      System.out.println("TIMED_WAITING: " + thread.getState()); // После выхода из synchronized, поток сможет войти и
                                                                 // перейти в TIMED_WAITING

      Thread.sleep(200); // Ждем пока поток выйдет из sleep и перейдет в WAITING
      System.out.println("WAITING: " + thread.getState()); // поток wait и освобождает lock

      synchronized (lock) {
         lock.notify(); // main заходит в lock и будит поток
      }

      thread.join();
      System.out.println("TERMINATED: " + thread.getState());
   }
}