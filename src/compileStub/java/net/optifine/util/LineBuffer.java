package net.optifine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineBuffer implements Iterable<String> {
   private final List<String> lines = new ArrayList<>();

   public void add(String line) {
      this.lines.add(line);
   }

   public String get(int index) {
      return this.lines.get(index);
   }

   public int size() {
      return this.lines.size();
   }

   @Override
   public Iterator<String> iterator() {
      return this.lines.iterator();
   }
}
