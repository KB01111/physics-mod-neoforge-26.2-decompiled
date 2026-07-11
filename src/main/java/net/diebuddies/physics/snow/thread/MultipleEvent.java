package net.diebuddies.physics.snow.thread;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

public class MultipleEvent implements Runnable {
   private List<Runnable> events = new ObjectArrayList();

   @Override
   public void run() {
      for (Runnable event : this.events) {
         event.run();
      }

      this.events.clear();
   }

   public boolean isEmpty() {
      return this.events.isEmpty();
   }

   public int size() {
      return this.events.size();
   }

   public List<Runnable> getEvents() {
      return this.events;
   }

   public void addEvent(Runnable event) {
      if (this.events == null) {
         this.events = new ObjectArrayList();
      }

      this.events.add(event);
   }

   public void removeEvent(Runnable event) {
      if (this.events == null) {
         this.events = new ObjectArrayList();
      }

      this.events.remove(event);
   }
}
