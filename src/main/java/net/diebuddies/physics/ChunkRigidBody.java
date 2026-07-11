package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class ChunkRigidBody {
   private PxRigidActor chunk;
   private Set<Vector3i> fullBlocks;
   private List<PxShape> blocks = new ObjectArrayList();
   private PxShapeFlags shapeFlags = new PxShapeFlags((byte)(PxShapeFlagEnum.eSIMULATION_SHAPE.value | PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value));
   private PxFilterData filterData = new PxFilterData(1, 23, 0, 0);
   private boolean destroyed;

   public ChunkRigidBody(double x, double y, double z) {
      this.fullBlocks = new ObjectOpenHashSet();
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)x, (float)y, (float)z);
         PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F);
         PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
         this.chunk = StarterClient.physics.createRigidStatic(tmpPose);
      } catch (Throwable var12) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (mem != null) {
         mem.close();
      }
   }

   public void attachFullBlock(int x, int y, int z) {
      this.fullBlocks.add(new Vector3i(x, y, z));
   }

   public void compileChunk() {
      int SIZE = 4;
      if (!this.fullBlocks.isEmpty()) {
         boolean[] voxels = new boolean[64];

         for (Vector3i v : this.fullBlocks) {
            if ((v.x | v.y | v.z) >= 0 && v.x < 4 && v.y < 4 && v.z < 4) {
               voxels[v.x + v.y * 4 + v.z * 4 * 4] = true;
            }
         }

         boolean[] visited = new boolean[voxels.length];

         for (int z = 0; z < 4; z++) {
            for (int y = 0; y < 4; y++) {
               for (int x = 0; x < 4; x++) {
                  int base = x + y * 4 + z * 4 * 4;
                  if (voxels[base] && !visited[base]) {
                     int maxX = x;

                     while (maxX + 1 < 4 && voxels[maxX + 1 + y * 4 + z * 4 * 4] && !visited[maxX + 1 + y * 4 + z * 4 * 4]) {
                        maxX++;
                     }

                     int maxY;
                     label129:
                     for (maxY = y; maxY + 1 < 4; maxY++) {
                        for (int ix = x; ix <= maxX; ix++) {
                           int idx = ix + (maxY + 1) * 4 + z * 4 * 4;
                           if (!voxels[idx] || visited[idx]) {
                              break label129;
                           }
                        }
                     }

                     int maxZ;
                     label115:
                     for (maxZ = z; maxZ + 1 < 4; maxZ++) {
                        for (int iy = y; iy <= maxY; iy++) {
                           for (int ixx = x; ixx <= maxX; ixx++) {
                              int idx = ixx + iy * 4 + (maxZ + 1) * 4 * 4;
                              if (!voxels[idx] || visited[idx]) {
                                 break label115;
                              }
                           }
                        }
                     }

                     for (int zz = z; zz <= maxZ; zz++) {
                        for (int yy = y; yy <= maxY; yy++) {
                           int row = yy * 4 + zz * 4 * 4;

                           for (int xx = x; xx <= maxX; xx++) {
                              visited[xx + row] = true;
                           }
                        }
                     }

                     float w = (float)(maxX - x + 1);
                     float h = (float)(maxY - y + 1);
                     float d = (float)(maxZ - z + 1);
                     float cx = (float)x + w * 0.5F;
                     float cy = (float)y + h * 0.5F;
                     float cz = (float)z + d * 0.5F;
                     this.attachBox(cx, cy, cz, w, h, d);
                  }
               }
            }
         }

         this.fullBlocks.clear();
      }
   }

   public void attachBox(float x, float y, float z, float width, float height, float depth) {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, width * 0.5F, height * 0.5F, depth * 0.5F);
         PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, this.shapeFlags);
         boxShape.setLocalPose(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, x, y, z),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            )
         );
         boxShape.setSimulationFilterData(this.filterData);
         this.chunk.attachShape(boxShape);
         this.blocks.add(boxShape);
      } catch (Throwable var11) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (mem != null) {
         mem.close();
      }
   }

   public PxRigidActor getActor() {
      return this.chunk;
   }

   public List<PxShape> getBlocks() {
      return this.blocks;
   }

   public void destroy() {
      if (!this.destroyed) {
         for (PxShape shape : this.blocks) {
            shape.release();
         }

         this.chunk.release();
         this.shapeFlags.destroy();
         this.filterData.destroy();
         this.destroyed = true;
      }
   }
}
