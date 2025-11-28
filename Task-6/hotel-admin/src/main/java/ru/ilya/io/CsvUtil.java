package ru.ilya.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
   public static List<String[]> read(String path) throws IOException {
      File file = new File(path);

      if (!file.exists()){
         throw new IOException("Файл не найден: " + path);
      }
      if(!file.isFile()){
         throw new IOException("Указанный путь не является файлом: "+ path);
      }

      List<String[]> list = new ArrayList<>();
      try(BufferedReader br = new BufferedReader(new FileReader(file))){
         br.readLine();
         String line;
         while((line=br.readLine())!=null){
            list.add(line.split(","));
         }
      }
      return list;
   }


   public static void write(String path, List<String> lines) throws IOException {
      File file = new File(path);

      if(!file.exists()){
         File parent = file.getParentFile();
         if (parent!=null && !parent.exists()){
            if(!parent.mkdirs()){
               throw new IOException("Не удалось создать директорию: "+path);
            }
         }
         if (!file.createNewFile()){
            throw new IOException("Не удалось создать файл: "+path);
         }
      }

      try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
         for (String l : lines){
            bw.write(l);
            bw.newLine();
         }
      }
   }
}
