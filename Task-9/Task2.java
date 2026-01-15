public class Task2 {
   private static Object lock = new Object();
   private static boolean flag = true;

   public static void main(String[] args){
      Thread t1 = new Thread(() -> print("Thread 1", true));
      Thread t2 = new Thread(() -> print("Thread 2", false));

      t1.start();
      t2.start();
   }

   private static void print(String name, boolean turn){
      while (true) {
         synchronized(lock){
            while(flag!=turn){
               try{
                  lock.wait();
               } catch (InterruptedException e){
                  Thread.currentThread().interrupt();
                  return;
               }
            }

            System.out.println(name);
            flag= !flag;
            lock.notifyAll();
         }
      }
   }
}