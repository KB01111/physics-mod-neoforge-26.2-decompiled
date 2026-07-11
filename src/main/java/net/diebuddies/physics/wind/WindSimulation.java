package net.diebuddies.physics.wind;

import java.util.ArrayDeque;
import java.util.Queue;
import org.joml.Vector3i;

public class WindSimulation {
   public static final Vector3i[] NEIGHBOUR_VOXEL_OFFSETS = new Vector3i[]{
      new Vector3i(1, 0, 0), new Vector3i(-1, 0, 0), new Vector3i(0, 1, 0), new Vector3i(0, -1, 0)
   };
   public static final int SKY_LIGHT = 60;
   public static final int SOLID = 1;
   public int[][] light = new int[70][40];
   public int[][] map = new int[70][40];
   private Queue<Vector3i> lightAddQueue = new ArrayDeque<>(0);
   private Queue<Vector3i> lightRemoveQueue = new ArrayDeque<>(0);

   public void update(double delta) {
      this.updateLightValues();
   }

   public void updateLightValues() {
      Vector3i currentLight = null;
      Vector3i tmp = new Vector3i();

      while ((currentLight = this.lightRemoveQueue.poll()) != null) {
         Vector3i pos = currentLight;
         int lightValue = this.getLightData(currentLight.x, currentLight.y);
         this.setLightData(currentLight.x, currentLight.y, 0);

         for (int i = 0; i < NEIGHBOUR_VOXEL_OFFSETS.length; i++) {
            Vector3i offset = tmp.set(pos.x + NEIGHBOUR_VOXEL_OFFSETS[i].x, pos.y + NEIGHBOUR_VOXEL_OFFSETS[i].y, pos.z + NEIGHBOUR_VOXEL_OFFSETS[i].z);
            int lightValueOff = this.getLightData(offset.x, offset.y);
            if (lightValue == 60 && i == 2 && lightValueOff == lightValue) {
               this.lightRemoveQueue.add(new Vector3i(offset));
            } else if (lightValueOff != 0 && lightValueOff < lightValue) {
               this.lightRemoveQueue.add(new Vector3i(offset));
            } else if (lightValueOff >= lightValue) {
               this.lightAddQueue.add(new Vector3i(offset));
            }
         }
      }

      while ((currentLight = this.lightAddQueue.poll()) != null) {
         Vector3i pos = currentLight;
         int lightValue = this.getLightData(currentLight.x, currentLight.y);
         int lightValueDec = lightValue - 1;

         for (int ix = 0; ix < NEIGHBOUR_VOXEL_OFFSETS.length; ix++) {
            Vector3i offset = tmp.set(pos.x + NEIGHBOUR_VOXEL_OFFSETS[ix].x, pos.y + NEIGHBOUR_VOXEL_OFFSETS[ix].y, pos.z + NEIGHBOUR_VOXEL_OFFSETS[ix].z);
            int lightValueOff = this.getLightData(offset.x, offset.y);
            boolean solid = this.isSolid(offset.x, offset.y);
            if (!solid) {
               if (lightValue == 60 && ix == 2 && lightValueOff < lightValue) {
                  this.setLightData(offset.x, offset.y, 60);
                  this.lightAddQueue.add(new Vector3i(offset));
               } else if (lightValueOff < lightValueDec) {
                  this.setLightData(offset.x, offset.y, lightValueDec);
                  this.lightAddQueue.add(new Vector3i(offset));
               }
            }
         }
      }
   }

   private void setLightData(int x, int y, int lightData) {
      if (x >= 0 && y >= 0 && x < this.light.length && y < this.light[0].length) {
         this.light[x][y] = lightData;
      }
   }

   public int getLightData(int x, int y) {
      return x >= 0 && y >= 0 && x < this.light.length && y < this.light[0].length ? this.light[x][y] : 0;
   }

   public void setSkyLight(int x, int y) {
      if (x >= 0 && y >= 0 && x < this.light.length && y < this.light[0].length) {
         this.light[x][y] = 60;
         this.lightAddQueue.add(new Vector3i(x, y, 0));
      }
   }

   public void removeSkyLight(int x, int y) {
      if (x >= 0 && y >= 0 && x < this.light.length && y < this.light[0].length) {
         if (this.light[x][y] == 60) {
            this.lightRemoveQueue.add(new Vector3i(x, y, 0));
         }
      }
   }

   public boolean isSolid(int x, int y) {
      return x >= 0 && y >= 0 && x < this.map.length && y < this.map[0].length ? this.map[x][y] == 1 : true;
   }

   public void setSolid(int x, int y) {
      if (x >= 0 && y >= 0 && x < this.map.length && y < this.map[0].length) {
         if (this.map[x][y] == 0) {
            this.lightRemoveQueue.add(new Vector3i(x, y, 0));
            this.map[x][y] = 1;
         }
      }
   }

   public void removeSolid(int x, int y) {
      if (x >= 0 && y >= 0 && x < this.map.length && y < this.map[0].length) {
         if (this.map[x][y] == 1) {
            this.map[x][y] = 0;
            int lightData = this.getLightData(x, y);

            for (int i = 0; i < NEIGHBOUR_VOXEL_OFFSETS.length; i++) {
               Vector3i offset = NEIGHBOUR_VOXEL_OFFSETS[i];
               int offLightData = this.getLightData(x + offset.x, y + offset.y);
               if (offLightData > lightData) {
                  this.lightAddQueue.add(new Vector3i(x + offset.x, y + offset.y, 0));
               }
            }
         }
      }
   }
}
