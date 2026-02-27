package Task3;

import java.util.LinkedList;
import java.util.Queue;

public class Consume implements Runnable{
   private Queue<Integer> buffer = new LinkedList<>();

   Consume(Queue<Integer> buffer){
      this.buffer=buffer;
   }

   @Override
   public void run(){
      try{
         while(true){
            consume();
            Thread.sleep(500);
         }
      } catch (InterruptedException e){
         Thread.currentThread().interrupt();
      }
   }

   private void consume() throws InterruptedException{
      synchronized (buffer){
         while(buffer.isEmpty()){
            System.out.println("Буфер пуст. Потребитель ждёт");
            buffer.wait();
         }

         int value = buffer.poll();
         System.out.println("Потреблено: " + value);

         buffer.notifyAll();
      }
   }

}
