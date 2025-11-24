package ru.ilya.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
   public static List<String[]> read(String path) throws IOException {
      List<String[]> list = new ArrayList<>();
      try (BufferedReader br = new BufferedReader(new FileReader(path))) {
         br.readLine(); //для заголовка
         String line;
         while ((line = br.readLine()) != null) {
            list.add(line.split(","));
         }
      }
      return list;
   }

   public static void write(String path, List<String> lines) throws IOException {
      try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
         for (String l : lines)
            bw.write(l + "\n");
      }
   }
}
