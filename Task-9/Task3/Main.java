package Task3;

import java.util.LinkedList;
import java.util.Queue;

public class Main {
   public static void main(String[] args){
      int BUFFER_SIZE = 5;
      Queue<Integer> buffer = new LinkedList<>();
      
      Thread producer = new Thread(new Producer(buffer, BUFFER_SIZE));
      Thread consume = new Thread(new Consume(buffer));

      producer.start();
      consume.start();
   }
}
