package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortAVLTreeSet;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import java.util.ArrayDeque;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class LayeredSurfaceQueue {
   private final Short2ObjectOpenHashMap<ArrayDeque<OceanSurface>> perLayer = new Short2ObjectOpenHashMap();
   private final ShortAVLTreeSet activeLayers = new ShortAVLTreeSet();
   private int totalSize = 0;

   public void addAll(List<OceanSurface> surfaces) {
      if (surfaces != null && !surfaces.isEmpty()) {
         for (OceanSurface s : surfaces) {
            this.addUnsafe(s);
         }
      }
   }

   @Nullable
   public OceanSurface pollClosestTo(double cameraY) {
      if (this.totalSize == 0) {
         return null;
      } else {
         short bestLayer = 0;
         double bestDist = Double.POSITIVE_INFINITY;
         ShortBidirectionalIterator it = this.activeLayers.iterator();

         while (it.hasNext()) {
            short layerY = it.nextShort();
            double dist = Math.abs(cameraY - (double)layerY);
            if (dist < bestDist || dist == bestDist && layerY < bestLayer) {
               bestDist = dist;
               bestLayer = layerY;
            }
         }

         ArrayDeque<OceanSurface> q = (ArrayDeque<OceanSurface>)this.perLayer.get(bestLayer);
         if (q == null) {
            this.activeLayers.remove(bestLayer);
            return null;
         } else {
            OceanSurface s = q.pollFirst();
            if (s == null) {
               this.perLayer.remove(bestLayer);
               this.activeLayers.remove(bestLayer);
               return null;
            } else {
               this.totalSize--;
               if (q.isEmpty()) {
                  this.perLayer.remove(bestLayer);
                  this.activeLayers.remove(bestLayer);
               }

               return s;
            }
         }
      }
   }

   public boolean isEmpty() {
      return this.totalSize == 0;
   }

   public int size() {
      return this.totalSize;
   }

   public List<OceanSurface> drainAll() {
      List<OceanSurface> out = new ObjectArrayList(this.totalSize);
      ShortBidirectionalIterator it = this.activeLayers.iterator();

      while (it.hasNext()) {
         short layerY = it.nextShort();
         ArrayDeque<OceanSurface> q = (ArrayDeque<OceanSurface>)this.perLayer.get(layerY);
         if (q != null && !q.isEmpty()) {
            out.addAll(q);
         }
      }

      this.perLayer.clear();
      this.activeLayers.clear();
      this.totalSize = 0;
      return out;
   }

   private void addUnsafe(@Nullable OceanSurface s) {
      if (s != null) {
         short layerY = s.oceanLayer.getLayerPosY();
         ArrayDeque<OceanSurface> q = (ArrayDeque<OceanSurface>)this.perLayer.get(layerY);
         if (q == null) {
            q = new ArrayDeque<>();
            this.perLayer.put(layerY, q);
            this.activeLayers.add(layerY);
         }

         q.addLast(s);
         this.totalSize++;
      }
   }
}
