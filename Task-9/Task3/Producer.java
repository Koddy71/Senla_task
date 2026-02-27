package Task3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Producer implements Runnable {
   private final Random random= new Random();
   private Queue<Integer> buffer = new LinkedList<>();
   private int BUFFER_SIZE;

   Producer(Queue<Integer> buffer, int BUFFER_SIZE){
      this.BUFFER_SIZE=BUFFER_SIZE;
      this.buffer=buffer;
   }

   @Override
   public void run(){
      try{
         while(true){
            produce();
            Thread.sleep(500);
         }
      } catch (InterruptedException e){
         Thread.currentThread().interrupt();
      }
   }

   private void produce() throws InterruptedException{
      synchronized (buffer){
         while(buffer.size()==BUFFER_SIZE){
            System.out.println("Буфер полон. Производитель ждёт");
            buffer.wait();
         }

         int value = random.nextInt(100);
         buffer.add(value);
         System.out.println("Произведено: "+ value);

         buffer.notifyAll();
      }
   }
}
