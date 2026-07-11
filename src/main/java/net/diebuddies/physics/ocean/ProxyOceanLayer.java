package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.util.Mth;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class ProxyOceanLayer {
   private OceanWorld world;
   private final short layerPosY;
   private Long2ObjectMap<ProxyOceanStorage> oceanStorage;
   private OceanSurface oceanSurface;
   private LinkedList<OceanRippleImpulse> rippleImpulses;
   private boolean needsRippleUpdate;
   private int rippleCount;
   private Vector3d rippleOffset = new Vector3d();
   private Vector2d tmpVec2 = new Vector2d();

   public ProxyOceanLayer(OceanWorld world, short layerPosY) {
      this.world = world;
      this.oceanStorage = new Long2ObjectOpenHashMap();
      this.rippleImpulses = new LinkedList<>();
      this.layerPosY = layerPosY;
   }

   public void update(double diff) {
      if (!this.rippleImpulses.isEmpty()) {
         this.needsRippleUpdate = true;
      }
   }

   public double calculateYOffset(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return 0.0;
      } else {
         Vector2d position = this.tmpVec2.set(x - (double)this.world.getWaveAnchorX(), z - (double)this.world.getWaveAnchorZ());
         float factor = this.bilinearInterpolationTexture(x, y, z);
         double waveHeight = WaveFunction.waveHeight(position, 13, (double)factor, (double)oceanWorld.getOceanHeight(), (double)oceanWorld.getOceanTime());
         return this.warpFunction(oceanWorld, waveHeight, y);
      }
   }

   public boolean isInsideOceanWater(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return false;
      } else {
         Vector2d position = this.tmpVec2.set(x - (double)this.world.getWaveAnchorX(), z - (double)this.world.getWaveAnchorZ());
         float factor = this.bilinearInterpolationTexture(x, y, z);
         double waveHeight = WaveFunction.waveHeight(position, 13, (double)factor, (double)oceanWorld.getOceanHeight(), (double)oceanWorld.getOceanTime());
         double vanillaSurface = (double)this.layerPosY + 0.8888888;
         double surface = vanillaSurface + waveHeight;
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         double distance = y - (double)this.layerPosY;
         if (distance >= 0.0) {
            if ((double)this.getOceanHeight(ix, iz) <= distance) {
               return false;
            }
         } else if ((double)this.getOceanDepth(ix, iz) <= -distance) {
            return false;
         }

         return y < surface;
      }
   }

   public boolean isInsideTextureOceanRange(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return false;
      } else {
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         double distance = y - (double)this.layerPosY;
         if (distance >= 0.0) {
            if ((double)this.getOceanHeight(ix, iz) <= distance) {
               return false;
            }
         } else if ((double)this.getOceanDepth(ix, iz) <= -distance) {
            return false;
         }

         return true;
      }
   }

   public boolean isInsideTextureOceanRangeNoWarp(OceanWorld oceanWorld, double x, double y, double z) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z, 2.0 + (double)oceanWorld.getOceanHeight() * 0.5)) {
         return false;
      } else {
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         double distance = y - (double)this.layerPosY;
         if (distance >= 0.0) {
            if ((double)this.getOceanHeight(ix, iz) <= distance) {
               return false;
            }
         } else if ((double)this.getOceanDepth(ix, iz) <= -distance) {
            return false;
         }

         return true;
      }
   }

   public ProxyOceanLayer.WaveForceResult calculateWaveNormal(OceanWorld oceanWorld, double x, double y, double z, Vector3d waveForce) {
      if (!this.isInsideOceanRange(oceanWorld, x, y, z)) {
         return ProxyOceanLayer.WaveForceResult.OUTSIDE;
      } else {
         Vector2d position = this.tmpVec2.set(x - (double)this.world.getWaveAnchorX(), z - (double)this.world.getWaveAnchorZ());
         float factor = this.bilinearInterpolationTexture(x, y, z);
         double waveHeight = WaveFunction.waveHeight(position, 13, (double)factor, (double)oceanWorld.getOceanHeight(), (double)oceanWorld.getOceanTime());
         double vanillaSurface = (double)this.layerPosY + 0.8888888;
         double surface = vanillaSurface + waveHeight;
         if (y > surface && y > vanillaSurface) {
            return ProxyOceanLayer.WaveForceResult.INSIDE_BUT_AIR;
         } else {
            Vector3d waveNormal = WaveFunction.waveNormal(
               position, (double)factor, (double)oceanWorld.getOceanHeight(), (double)oceanWorld.getOceanTime(), waveForce
            );
            waveNormal.y = 1.0;
            if (y < vanillaSurface) {
               if (y > surface) {
                  waveNormal.y = -waveNormal.y;
               } else {
                  waveNormal.y = 0.0;
               }
            }

            double warpFunction = this.warpFunction(oceanWorld, 1.0, y);
            waveNormal.x *= warpFunction;
            waveNormal.z *= warpFunction;
            return ProxyOceanLayer.WaveForceResult.INSIDE;
         }
      }
   }

   public boolean isInsideOceanRange(OceanWorld oceanWorld, double x, double y, double z) {
      return this.isInsideOceanRange(oceanWorld, x, y, z, (double)oceanWorld.getOceanHeight() * 0.5);
   }

   public boolean isInsideOceanRange(OceanWorld oceanWorld, double x, double y, double z, double oceanHeight) {
      if (this.world.getWaveAnchorX() != Integer.MAX_VALUE && this.oceanSurface != null && this.oceanSurface.textureData != null) {
         double distance = y - (double)this.layerPosY;
         int ix = Mth.floor(x);
         int iz = Mth.floor(z);
         return !(Math.abs(distance) > oceanHeight) && this.getOcean(ix, iz) != 0;
      } else {
         return false;
      }
   }

   private double warpFunction(OceanWorld oceanWorld, double waveHeight, double y) {
      double distance = y - (double)this.layerPosY;
      double halfHeight = (double)oceanWorld.getOceanHeight() * 0.5 - 1.0;
      double warp = 1.0 - net.diebuddies.math.Math.clamp(Math.abs(distance) - 1.0, 0.0, halfHeight) / halfHeight;
      return waveHeight * warp;
   }

   public float bilinearInterpolationTexture(double worldX, double worldY, double worldZ) {
      double distance = worldY - (double)this.layerPosY;
      int ix = Mth.floor(worldX);
      int iz = Mth.floor(worldZ);
      int properOffsetX = this.oceanSurface.offsetX;
      int properOffsetZ = this.oceanSurface.offsetZ;
      int uvX = ix - properOffsetX;
      int uvY = iz - properOffsetZ;
      float fractUVX = (float)(worldX - (double)properOffsetX - (double)uvX);
      float fractUVY = (float)(worldZ - (double)properOffsetZ - (double)uvY);
      float data0 = this.textureData(uvX, uvY);
      float data1 = this.textureData(uvX + 1, uvY);
      float data2 = this.textureData(uvX, uvY + 1);
      float data3 = this.textureData(uvX + 1, uvY + 1);
      if (distance >= 0.0) {
         if ((double)this.getOceanHeight(ix, iz) <= distance) {
            data0 = 0.0F;
            data1 = 0.0F;
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix + 1, iz) <= distance) {
            data1 = 0.0F;
            data3 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix, iz + 1) <= distance) {
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix + 1, iz + 1) <= distance) {
            data3 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix - 1, iz) <= distance) {
            data0 = 0.0F;
            data2 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix, iz - 1) <= distance) {
            data0 = 0.0F;
            data1 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix - 1, iz - 1) <= distance) {
            data0 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix - 1, iz + 1) <= distance) {
            data2 = 0.0F;
         }

         if ((double)this.getOceanHeight(ix + 1, iz - 1) <= distance) {
            data1 = 0.0F;
         }
      } else {
         double depth = -distance;
         if ((double)this.getOceanDepth(ix, iz) <= depth) {
            data0 = 0.0F;
            data1 = 0.0F;
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix + 1, iz) <= depth) {
            data1 = 0.0F;
            data3 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix, iz + 1) <= depth) {
            data2 = 0.0F;
            data3 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix + 1, iz + 1) <= depth) {
            data3 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix - 1, iz) <= depth) {
            data0 = 0.0F;
            data2 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix, iz - 1) <= depth) {
            data0 = 0.0F;
            data1 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix - 1, iz - 1) <= depth) {
            data0 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix - 1, iz + 1) <= depth) {
            data2 = 0.0F;
         }

         if ((double)this.getOceanDepth(ix + 1, iz - 1) <= depth) {
            data1 = 0.0F;
         }
      }

      float data4 = org.joml.Math.lerp(data0, data1, fractUVX);
      float data5 = org.joml.Math.lerp(data2, data3, fractUVX);
      return org.joml.Math.lerp(data4, data5, fractUVY);
   }

   public float textureData(int x, int y) {
      if (this.oceanSurface.textureData.length == 1) {
         return (float)(this.oceanSurface.textureData[0] & 255) / 255.0F;
      } else {
         x = net.diebuddies.math.Math.clamp(x, 0, this.oceanSurface.width - 1);
         y = net.diebuddies.math.Math.clamp(y, 0, this.oceanSurface.height - 1);
         return (float)(this.oceanSurface.textureData[y * this.oceanSurface.width + x] & 255) / 255.0F;
      }
   }

   public Long2ObjectMap<ProxyOceanStorage> getOceanStorage() {
      return this.oceanStorage;
   }

   public int getOceanDepth(int x, int z) {
      ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : Index.getWaterDepth(storage.depths.getData(Index.chunkStorage(x & 15, z & 15)));
   }

   public int getOceanHeight(int x, int z) {
      ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : Index.getWaterHeight(storage.depths.getData(Index.chunkStorage(x & 15, z & 15)));
   }

   public int getOcean(int x, int z) {
      ProxyOceanStorage storage = (ProxyOceanStorage)this.oceanStorage.get(OceanLayer.oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : storage.blocks.getData(Index.chunkStorage(x & 15, z & 15));
   }

   public short getLayerPosY() {
      return this.layerPosY;
   }

   public void setOceanSurface(OceanSurface oceanSurface) {
      this.oceanSurface = oceanSurface;
   }

   public OceanSurface getOceanSurface() {
      return this.oceanSurface;
   }

   public void addRippleImpulse(OceanRippleImpulse impulse) {
      this.rippleImpulses.addFirst(impulse);
      this.rippleCount = Math.max(this.rippleCount, this.rippleImpulses.size());
      this.needsRippleUpdate = true;
   }

   public List<OceanRippleImpulse> consumeRippleImpulses() {
      if (this.rippleImpulses.isEmpty()) {
         return List.of();
      } else {
         List<OceanRippleImpulse> impulses = new ArrayList<>(this.rippleImpulses);
         this.rippleImpulses.clear();
         return impulses;
      }
   }

   public boolean hasRippleImpulses() {
      return !this.rippleImpulses.isEmpty();
   }

   public int getPendingRippleImpulseCount() {
      return this.rippleImpulses.size();
   }

   public Vector3d getRippleOffset() {
      return this.rippleOffset;
   }

   public void setRippleCount(int rippleCount) {
      this.rippleCount = rippleCount;
   }

   public int getRippleCount() {
      return this.rippleCount;
   }

   public boolean needsRippleUpdate() {
      return this.needsRippleUpdate || !this.rippleImpulses.isEmpty();
   }

   public void setNeedsRippleUpdate(boolean needsRippleUpdate) {
      this.needsRippleUpdate = needsRippleUpdate;
   }

   public void destroy() {
      this.rippleImpulses.clear();
   }

   public static enum WaveForceResult {
      OUTSIDE,
      INSIDE_BUT_AIR,
      INSIDE;
   }
}
