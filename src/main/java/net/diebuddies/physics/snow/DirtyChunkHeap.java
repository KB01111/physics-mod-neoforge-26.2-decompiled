package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class DirtyChunkHeap {
   private ChunkContouring[] heap;
   private double[] key;
   private int size;
   private final Reference2IntOpenHashMap<ChunkContouring> index = new Reference2IntOpenHashMap();

   public DirtyChunkHeap(int initialCapacity) {
      this.heap = new ChunkContouring[initialCapacity + 1];
      this.key = new double[initialCapacity + 1];
      this.index.defaultReturnValue(0);
   }

   int size() {
      return this.size;
   }

   boolean isEmpty() {
      return this.size == 0;
   }

   ChunkContouring peekChunk() {
      return this.size == 0 ? null : this.heap[1];
   }

   double peekKey() {
      return this.size == 0 ? Double.POSITIVE_INFINITY : this.key[1];
   }

   void clear() {
      for (int i = 1; i <= this.size; i++) {
         this.heap[i] = null;
      }

      this.size = 0;
      this.index.clear();
   }

   void addOrUpdate(ChunkContouring c, double k) {
      int i = this.index.getInt(c);
      if (i == 0) {
         this.ensureCapacity(this.size + 1);
         i = ++this.size;
         this.heap[i] = c;
         this.key[i] = k;
         this.index.put(c, i);
         this.upHeap(i);
      } else {
         double old = this.key[i];
         this.key[i] = k;
         if (k < old) {
            this.upHeap(i);
         } else if (k > old) {
            this.downHeap(i);
         }
      }
   }

   ChunkContouring pollChunk() {
      if (this.size == 0) {
         return null;
      } else {
         ChunkContouring root = this.heap[1];
         this.removeAt(1);
         return root;
      }
   }

   boolean remove(ChunkContouring c) {
      int i = this.index.getInt(c);
      if (i == 0) {
         return false;
      } else {
         this.removeAt(i);
         return true;
      }
   }

   void rekeyAll(double px, double py, double pz) {
      for (int i = 1; i <= this.size; i++) {
         ChunkContouring c = this.heap[i];
         this.key[i] = distSqToChunkCenter(c, px, py, pz);
      }

      for (int i = this.size >>> 1; i >= 1; i--) {
         this.downHeap(i);
      }
   }

   private void removeAt(int i) {
      ChunkContouring removed = this.heap[i];
      this.index.removeInt(removed);
      if (i == this.size) {
         this.heap[this.size] = null;
         this.size--;
      } else {
         ChunkContouring moved = this.heap[this.size];
         double movedKey = this.key[this.size];
         this.heap[i] = moved;
         this.key[i] = movedKey;
         this.index.put(moved, i);
         this.heap[this.size] = null;
         this.size--;
         this.downHeap(i);
         this.upHeap(i);
      }
   }

   private void upHeap(int i) {
      ChunkContouring c = this.heap[i];
      double k = this.key[i];

      while (i > 1) {
         int p = i >>> 1;
         if (k >= this.key[p]) {
            break;
         }

         this.heap[i] = this.heap[p];
         this.key[i] = this.key[p];
         this.index.put(this.heap[i], i);
         i = p;
      }

      this.heap[i] = c;
      this.key[i] = k;
      this.index.put(c, i);
   }

   private void downHeap(int i) {
      ChunkContouring c = this.heap[i];
      double k = this.key[i];

      while (true) {
         int l = i << 1;
         if (l > this.size) {
            break;
         }

         int r = l + 1;
         int smallest = r <= this.size && this.key[r] < this.key[l] ? r : l;
         if (this.key[smallest] >= k) {
            break;
         }

         this.heap[i] = this.heap[smallest];
         this.key[i] = this.key[smallest];
         this.index.put(this.heap[i], i);
         i = smallest;
      }

      this.heap[i] = c;
      this.key[i] = k;
      this.index.put(c, i);
   }

   private void ensureCapacity(int needed) {
      if (needed >= this.heap.length) {
         int newCap = this.heap.length + (this.heap.length >>> 1);
         if (newCap <= needed) {
            newCap = needed + 1;
         }

         ChunkContouring[] newHeap = new ChunkContouring[newCap];
         double[] newKey = new double[newCap];
         System.arraycopy(this.heap, 0, newHeap, 0, this.heap.length);
         System.arraycopy(this.key, 0, newKey, 0, this.key.length);
         this.heap = newHeap;
         this.key = newKey;
      }
   }

   public static double distSqToChunkCenter(ChunkContouring chunk, double px, double py, double pz) {
      double cx = (double)(chunk.xVoxel() + IChunk.CHUNK_SIZE_HALF);
      double cy = (double)(chunk.yVoxel() + IChunk.CHUNK_SIZE_HALF);
      double cz = (double)(chunk.zVoxel() + IChunk.CHUNK_SIZE_HALF);
      double dx = px - cx;
      double dy = py - cy;
      double dz = pz - cz;
      return dx * dx + dy * dy + dz * dz;
   }
}
