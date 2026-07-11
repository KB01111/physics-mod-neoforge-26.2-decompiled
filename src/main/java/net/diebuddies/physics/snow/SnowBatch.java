package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class SnowBatch {
   public static final int BUCKET_SHIFTS = 3;
   private Map<Vector3i, SnowBatch.SnowChunkBucket> buckets = new Object2ObjectOpenHashMap();
   private Vector3i tmp = new Vector3i();

   public void add(ChunkEntity entity) {
      Vector3i bucketPos = this.getBucketPos(entity);
      SnowBatch.SnowChunkBucket bucket;
      if (this.buckets.containsKey(bucketPos)) {
         bucket = this.buckets.get(bucketPos);
      } else {
         this.buckets.put(new Vector3i(bucketPos), bucket = new SnowBatch.SnowChunkBucket());
      }

      bucket.add(entity);
   }

   public void remove(ChunkEntity entity) {
      SnowBatch.SnowChunkBucket bucket = this.buckets.get(this.getBucketPos(entity));
      bucket.remove(entity);
      if (bucket.isEmpty()) {
         this.buckets.remove(this.tmp);
      }
   }

   public void update() {
      for (SnowBatch.SnowChunkBucket bucket : this.buckets.values()) {
         bucket.update();
      }
   }

   public Map<Vector3i, SnowBatch.SnowChunkBucket> getBuckets() {
      return this.buckets;
   }

   private Vector3i getBucketPos(ChunkEntity entity) {
      Vector3i position = entity.batchPosition;
      return this.tmp.set(position.x, position.y, position.z);
   }

   public static int shiftPos(int pos) {
      return pos >> 3 << 3;
   }

   public class SnowChunkBucket {
      private List<ChunkEntity> entities;
      private AABB3D aabb;
      private boolean aabbNeedsUpdate;

      public SnowChunkBucket() {
         Objects.requireNonNull(SnowBatch.this);
         super();
         this.entities = new ObjectArrayList();
         this.aabb = new AABB3D(new Vector3d(Double.MAX_VALUE), new Vector3d(-Double.MAX_VALUE));
      }

      public void update() {
         if (this.aabbNeedsUpdate) {
            this.aabb.start.set(Double.MAX_VALUE);
            this.aabb.end.set(-Double.MAX_VALUE);

            for (int i = 0; i < this.entities.size(); i++) {
               this.aabb.include(this.entities.get(i).aabb);
            }

            this.aabbNeedsUpdate = false;
         }
      }

      public void add(ChunkEntity entity) {
         this.entities.add(entity);
         this.aabb.include(entity.aabb);
      }

      public void remove(ChunkEntity entity) {
         this.entities.remove(entity);
         this.aabbNeedsUpdate = true;
      }

      public boolean isEmpty() {
         return this.entities.isEmpty();
      }

      public AABB3D getAABB() {
         return this.aabb;
      }

      public List<ChunkEntity> getEntities() {
         return this.entities;
      }
   }
}
