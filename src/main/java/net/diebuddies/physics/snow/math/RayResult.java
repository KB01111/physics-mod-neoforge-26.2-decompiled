package net.diebuddies.physics.snow.math;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.joml.Vector3d;

public class RayResult {
   private boolean hit;
   private List<RayHit> rayHits = new ObjectArrayList();

   public RayResult() {
      this.hit = false;
   }

   public void addRayHit(RayHit rayHit) {
      this.rayHits.add(rayHit);
      this.hit = true;
   }

   public boolean hasHit() {
      return this.hit;
   }

   public List<RayHit> getRayHits() {
      return this.rayHits;
   }

   public void sortByDistance(final Vector3d start) {
      Collections.sort(this.rayHits, new Comparator<RayHit>() {
         {
            Objects.requireNonNull(RayResult.this);
         }

         public int compare(RayHit o1, RayHit o2) {
            return o1.point.distance(start) < o2.point.distance(start) ? -1 : 1;
         }
      });
   }
}
