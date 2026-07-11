package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.ocean.storage.FullStorageType2DBit;
import net.diebuddies.physics.ocean.storage.FullStorageType2DByte;
import net.diebuddies.physics.ocean.storage.FullStorageType2DShort;
import net.minecraft.util.Mth;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class OceanLayer {
   private static int counter;
   private int hashCode;
   private static final int CHUNK_GROUPING = 6;
   public static byte RANGE = 32;
   public static byte RANGE_SOLID = (byte)(RANGE + 1);
   public static float SCALE_COLOR_RANGE = 256.0F / (float)RANGE;
   public static final int CHUNK_SIZE = 16;
   public static final int CHUNK_SIZE_BITS = 15;
   public static final int CHUNK_VOLUME = 256;
   public static final int CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(15);
   public static final byte SOLID = 0;
   private OceanProcessor processor;
   private final ProxyOceanLayer proxyLayer;
   private final short layerPosY;
   private Long2ObjectMap<OceanLayer.OceanStorage> oceanStorage;
   private LongSet surfaceUpdatesNeeded;
   private static final int BUCKET_SHIFTS = 12;

   public static void updateRange(byte range) {
      RANGE = range;
      RANGE_SOLID = (byte)(RANGE + 1);
      SCALE_COLOR_RANGE = 256.0F / (float)RANGE;
   }

   public OceanLayer(OceanProcessor processor, short layerPosY) {
      this.hashCode = counter++;
      this.processor = processor;
      this.layerPosY = layerPosY;
      this.oceanStorage = new Long2ObjectOpenHashMap();
      this.proxyLayer = new ProxyOceanLayer(processor.getOceanWorld(), layerPosY);
      this.surfaceUpdatesNeeded = new LongOpenHashSet();
      processor.proxyEvents.add(() -> processor.getOceanWorld().getOceanLayers().put(layerPosY, this.proxyLayer));
   }

   @Nullable
   public OceanSurface generateMesh() {
      if (this.oceanStorage.isEmpty()) {
         this.surfaceUpdatesNeeded.clear();
         return new OceanSurface(this.proxyLayer, true);
      } else {
         int minX = Integer.MAX_VALUE;
         int maxX = Integer.MIN_VALUE;
         int minZ = Integer.MAX_VALUE;
         int maxZ = Integer.MIN_VALUE;
         ObjectIterator width = this.oceanStorage.long2ObjectEntrySet().iterator();

         while (width.hasNext()) {
            Entry<OceanLayer.OceanStorage> entry = (Entry<OceanLayer.OceanStorage>)width.next();
            long layerIndex = entry.getLongKey();
            OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)entry.getValue();
            if (storage.lights == null || !storage.areBiomesLoaded(this.processor, this.layerPosY)) {
               return null;
            }

            FullStorageType2DBit blocks = storage.blocks;
            int chunkX = Index.getXFromOceanLayer(layerIndex) * 16;
            int chunkZ = Index.getZFromOceanLayer(layerIndex) * 16;

            for (int xo = 0; xo < 16; xo++) {
               for (int zo = 0; zo < 16; zo++) {
                  int data = blocks.getData(Index.chunkStorage(xo, zo));
                  if (data != 0) {
                     int fx = chunkX + xo;
                     int fz = chunkZ + zo;
                     if (fx < minX) {
                        minX = fx;
                     }

                     if (fx > maxX) {
                        maxX = fx;
                     }

                     if (fz < minZ) {
                        minZ = fz;
                     }

                     if (fz > maxZ) {
                        maxZ = fz;
                     }
                  }
               }
            }
         }

         if (minX == Integer.MAX_VALUE) {
            this.surfaceUpdatesNeeded.clear();
            return new OceanSurface(this.proxyLayer, true);
         } else {
            int widthx = maxX - minX + 1;
            int height = maxZ - minZ + 1;
            Dynamic2DArray waves = this.processor.waves;
            Dynamic2DArray depth = this.processor.depth;
            waves.request(widthx, height);
            depth.request(widthx, height);
            boolean allZeroDepth = true;

            for (int x = minX; x <= maxX; x++) {
               for (int z = minZ; z <= maxZ; z++) {
                  int oceanData = this.getOcean(x, z);
                  int index = waves.index(x - minX, z - minZ);
                  if (oceanData == 0) {
                     waves.set(index, RANGE_SOLID);
                  } else {
                     waves.set(index, (byte)0);
                  }

                  byte depthData = this.getMaxDepth(x, z);
                  if (depthData > 1) {
                     allZeroDepth = false;
                  }

                  depth.set(index, (byte)Math.max(0, RANGE - Math.max(0, depthData - 1)));
               }
            }

            int maxInfluence = 0;
            byte[] texture;
            if (allZeroDepth) {
               texture = new byte[1];
            } else {
               this.blur(waves, RANGE_SOLID);
               this.blur(depth, RANGE);
               texture = new byte[widthx * height];

               for (int x = 0; x < widthx; x++) {
                  for (int z = 0; z < height; z++) {
                     int val = 255
                        - net.diebuddies.math.Math.clamp((int)((float)Math.max(waves.get(x, z) + 1, depth.get(x, z) + 1) * SCALE_COLOR_RANGE), 0, 255);
                     texture[z * widthx + x] = (byte)val;
                     maxInfluence = Math.max(maxInfluence, val);
                  }
               }
            }

            OceanSurface surface = new OceanSurface(texture, widthx, height, minX, minZ, this.proxyLayer);
            LongIterator it = this.surfaceUpdatesNeeded.iterator();

            while (it.hasNext()) {
               long indexx = it.nextLong();
               int chunkX = Index.getXFromOceanLayer(indexx);
               int chunkZ = Index.getZFromOceanLayer(indexx);
               int worldPosX = chunkX * 16 * 6;
               int worldPosZ = chunkZ * 16 * 6;
               int xOffset = shiftPos(worldPosX);
               int zOffset = shiftPos(worldPosZ);
               int internalXOffset = xOffset - minX;
               int internalZOffset = zOffset - minZ;
               int chunkSize = 96;
               int startX = worldPosX - minX;
               int startZ = worldPosZ - minZ;
               int endX = startX + chunkSize;
               int endZ = startZ + chunkSize;
               int waveOffsetX = -xOffset;
               int waveOffsetZ = -zOffset;
               Matrix4d transformation = new Matrix4d().translation((double)xOffset, 0.0, (double)zOffset);
               RawMesh mesh = this.generateMesh(waves, depth, minX, minZ, startX, startZ, endX, endZ, internalXOffset, internalZOffset, this.layerPosY);
               long meshIndex = Index.oceanLayerChunk(xOffset, zOffset);
               int textureSize = chunkSize + 1;
               OceanMesh oceanMesh = new OceanMesh(
                  mesh,
                  transformation,
                  (float)maxInfluence / 255.0F,
                  this.layerPosY,
                  textureSize,
                  textureSize,
                  waveOffsetX,
                  waveOffsetZ,
                  startX - internalXOffset,
                  startZ - internalZOffset,
                  meshIndex
               );
               surface.addOceanMesh(chunkX, this.layerPosY, chunkZ, oceanMesh);
            }

            this.surfaceUpdatesNeeded.clear();
            return surface;
         }
      }
   }

   public static int shiftPos(int pos) {
      return pos >> 12 << 12;
   }

   private RawMesh generateMesh(
      Dynamic2DArray waves, Dynamic2DArray depth, int offsetX, int offsetZ, int startX, int startZ, int endX, int endZ, int xOffset, int zOffset, int layer
   ) {
      LongList points = new LongArrayList();
      float size = 1.0F;
      startX = Math.max(0, startX);
      startZ = Math.max(0, startZ);
      endX = Math.min(waves.getWidth(), endX);
      endZ = Math.min(waves.getHeight(), endZ);

      for (int x = startX; x < endX; x++) {
         for (int z = startZ; z < endZ; z++) {
            if (waves.get(x, z) != RANGE_SOLID) {
               points.add((long)x << 32 | (long)z & 4294967295L);
            }
         }
      }

      if (points.isEmpty()) {
         return null;
      } else {
         int positionSize = points.size() * 3 * 4 * 4;
         int colorSize = points.size() * 4 * 4;
         int uvSize = points.size() * 2 * 4 * 4;
         int lightSize = points.size() * 4 * 4;
         int wavinessSize = points.size() * 4 * 4;
         int vertexSize = positionSize + colorSize + uvSize + lightSize + wavinessSize;
         RawMesh mesh = new RawMesh();
         ByteBuffer data = MemoryUtil.memAlloc(vertexSize);
         long pointer = MemoryUtil.memAddress(data);
         ByteBuffer indexData = MemoryUtil.memAlloc(points.size() * 6 * 2);
         long indexPointer = MemoryUtil.memAddress(indexData);
         Vector4f waterUVs = this.processor.getWaterUVOffsets();
         Vector2f midCoord = this.processor.getWaterMidCoord();
         int pointsSize = points.size();
         int indexCount = 0;

         for (int i = 0; i < pointsSize; i++) {
            long localIndex = points.getLong(i);
            int localX = (int)(localIndex >> 32);
            int localZ = (int)localIndex;
            int worldX = offsetX + localX;
            int worldZ = offsetZ + localZ;
            float height = this.processor.getHeight(worldX, this.layerPosY, worldZ);
            float north = this.processor.getHeight(worldX, this.layerPosY, worldZ - 1);
            float south = this.processor.getHeight(worldX, this.layerPosY, worldZ + 1);
            float east = this.processor.getHeight(worldX + 1, this.layerPosY, worldZ);
            float west = this.processor.getHeight(worldX - 1, this.layerPosY, worldZ);
            float h1 = this.processor.calculateAverageHeight(worldX + 1, this.layerPosY, worldZ - 1, height, north, east) - 0.001F;
            float h2 = this.processor.calculateAverageHeight(worldX - 1, this.layerPosY, worldZ - 1, height, north, west) - 0.001F;
            float h3 = this.processor.calculateAverageHeight(worldX + 1, this.layerPosY, worldZ + 1, height, south, east) - 0.001F;
            float h4 = this.processor.calculateAverageHeight(worldX - 1, this.layerPosY, worldZ + 1, height, south, west) - 0.001F;
            MemoryUtil.memPutFloat(pointer, (float)localX + size - (float)xOffset);
            MemoryUtil.memPutFloat(pointer + 4L, h3 + (float)layer);
            MemoryUtil.memPutFloat(pointer + 8L, (float)localZ + size - (float)zOffset);
            MemoryUtil.memPutInt(pointer + 12L, this.calculateBiomeColor(worldX, layer, worldZ));
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.y);
            MemoryUtil.memPutFloat(pointer + 20L, waterUVs.w);
            MemoryUtil.memPutInt(pointer + 24L, this.calculateLight(worldX, worldZ));
            MemoryUtil.memPutFloat(
               pointer + 28L,
               1.0F
                  - net.diebuddies.math.Math.clamp(
                     (float)Math.max(waves.getClamp(localX + 1, localZ + 1) + 1, depth.getClamp(localX + 1, localZ + 1) + 1) / (float)RANGE, 0.0F, 1.0F
                  )
            );
            pointer += 32L;
            MemoryUtil.memPutFloat(pointer, (float)localX + size - (float)xOffset);
            MemoryUtil.memPutFloat(pointer + 4L, h1 + (float)layer);
            MemoryUtil.memPutFloat(pointer + 8L, (float)(localZ - zOffset));
            MemoryUtil.memPutInt(pointer + 12L, this.calculateBiomeColor(worldX, layer, worldZ - 1));
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.y);
            MemoryUtil.memPutFloat(pointer + 20L, waterUVs.z);
            MemoryUtil.memPutInt(pointer + 24L, this.calculateLight(worldX, worldZ - 1));
            MemoryUtil.memPutFloat(
               pointer + 28L,
               1.0F
                  - net.diebuddies.math.Math.clamp(
                     (float)Math.max(waves.getClamp(localX + 1, localZ) + 1, depth.getClamp(localX + 1, localZ) + 1) / (float)RANGE, 0.0F, 1.0F
                  )
            );
            pointer += 32L;
            MemoryUtil.memPutFloat(pointer, (float)(localX - xOffset));
            MemoryUtil.memPutFloat(pointer + 4L, h2 + (float)layer);
            MemoryUtil.memPutFloat(pointer + 8L, (float)(localZ - zOffset));
            MemoryUtil.memPutInt(pointer + 12L, this.calculateBiomeColor(worldX - 1, layer, worldZ - 1));
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.x);
            MemoryUtil.memPutFloat(pointer + 20L, waterUVs.z);
            MemoryUtil.memPutInt(pointer + 24L, this.calculateLight(worldX - 1, worldZ - 1));
            MemoryUtil.memPutFloat(
               pointer + 28L,
               1.0F
                  - net.diebuddies.math.Math.clamp(
                     (float)Math.max(waves.getClamp(localX, localZ) + 1, depth.getClamp(localX, localZ) + 1) / (float)RANGE, 0.0F, 1.0F
                  )
            );
            pointer += 32L;
            MemoryUtil.memPutFloat(pointer, (float)(localX - xOffset));
            MemoryUtil.memPutFloat(pointer + 4L, h4 + (float)layer);
            MemoryUtil.memPutFloat(pointer + 8L, (float)localZ + size - (float)zOffset);
            MemoryUtil.memPutInt(pointer + 12L, this.calculateBiomeColor(worldX - 1, layer, worldZ));
            MemoryUtil.memPutFloat(pointer + 16L, waterUVs.x);
            MemoryUtil.memPutFloat(pointer + 20L, waterUVs.w);
            MemoryUtil.memPutInt(pointer + 24L, this.calculateLight(worldX - 1, worldZ));
            MemoryUtil.memPutFloat(
               pointer + 28L,
               1.0F
                  - net.diebuddies.math.Math.clamp(
                     (float)Math.max(waves.getClamp(localX, localZ + 1) + 1, depth.getClamp(localX, localZ + 1) + 1) / (float)RANGE, 0.0F, 1.0F
                  )
            );
            pointer += 32L;
            MemoryUtil.memPutShort(indexPointer, (short)indexCount);
            MemoryUtil.memPutShort(indexPointer + 2L, (short)(indexCount + 1));
            MemoryUtil.memPutShort(indexPointer + 4L, (short)(indexCount + 2));
            MemoryUtil.memPutShort(indexPointer + 6L, (short)(indexCount + 2));
            MemoryUtil.memPutShort(indexPointer + 8L, (short)(indexCount + 3));
            MemoryUtil.memPutShort(indexPointer + 10L, (short)indexCount);
            indexCount += 4;
            indexPointer += 12L;
         }

         mesh.data = data;
         mesh.indexData = indexData;
         return mesh;
      }
   }

   private int calculateLight(int worldX, int worldZ) {
      int count = 0;
      int sky = 0;
      int block = 0;

      for (int x = 0; x <= 1; x++) {
         int fx = worldX + x;

         for (int z = 0; z <= 1; z++) {
            int fz = worldZ + z;
            OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(oceanLayerChunkFromWorldPos(fx, fz));
            if (storage != null && storage.blocks.getData(Index.chunkStorage(fx & 15, fz & 15)) != 0) {
               byte lightData = storage.lights.getData(Index.chunkStorage(fx & 15, fz & 15));
               sky += lightData >> 4 & 15;
               block += lightData & 15;
               count++;
            }
         }
      }

      if (count == 0) {
         return 0;
      } else {
         sky /= count;
         block /= count;
         return sky << 20 | block << 4;
      }
   }

   private int calculateBiomeColor(int worldX, int layerY, int worldZ) {
      int count = 0;
      int r = 0;
      int g = 0;
      int b = 0;

      for (int x = 0; x <= 1; x++) {
         int fx = worldX + x;

         for (int z = 0; z <= 1; z++) {
            int fz = worldZ + z;
            int color = this.processor.getBiomeColor(fx, (short)layerY, fz);
            if (color != 0) {
               b += color & 0xFF;
               g += (color & 0xFF00) >> 8;
               r += (color & 0xFF0000) >> 16;
               count++;
            }
         }
      }

      if (count == 0) {
         return 0;
      } else {
         r /= count;
         g /= count;
         b /= count;
         return r | g << 8 | b << 16 | 0xFF000000;
      }
   }

   public int getOcean(int x, int z) {
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(oceanLayerChunkFromWorldPos(x, z));
      return storage == null ? 0 : storage.blocks.getData(Index.chunkStorage(x & 15, z & 15));
   }

   public byte getMaxDepth(int x, int z) {
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(oceanLayerChunkFromWorldPos(x, z));
      if (storage == null) {
         return 1;
      } else {
         short data = storage.depths.getData(Index.chunkStorage(x & 15, z & 15));
         byte depth = Index.getWaterDepth(data);
         byte height = Index.getWaterHeight(data);
         return depth < height ? depth : height;
      }
   }

   private void blur(Dynamic2DArray arr, byte borderValue) {
      int w = arr.getWidth();
      int h = arr.getHeight();
      int w1 = w - 1;
      int h1 = h - 1;
      byte borderMinus1 = (byte)(borderValue - 1);

      for (int y = 0; y < h; y++) {
         byte right = arr.get(w1, y);
         if (right < borderMinus1) {
            arr.set(w1, y, borderMinus1);
         }

         byte left = arr.get(0, y);
         if (left < borderMinus1) {
            arr.set(0, y, borderMinus1);
         }

         byte prev = arr.get(0, y);

         for (int x = 1; x < w; x++) {
            byte val = arr.get(x, y);
            byte candidate = (byte)(prev - 1);
            if (candidate > val) {
               arr.set(x, y, candidate);
               val = candidate;
            }

            prev = val;
         }

         prev = arr.get(w1, y);

         for (int x = w1 - 1; x >= 0; x--) {
            byte val = arr.get(x, y);
            byte candidate = (byte)(prev - 1);
            if (candidate > val) {
               arr.set(x, y, candidate);
               val = candidate;
            }

            prev = val;
         }
      }

      for (int x = 0; x < w; x++) {
         byte bottom = arr.get(x, h1);
         if (bottom < borderMinus1) {
            arr.set(x, h1, borderMinus1);
         }

         byte top = arr.get(x, 0);
         if (top < borderMinus1) {
            arr.set(x, 0, borderMinus1);
         }

         byte prev = arr.get(x, 0);

         for (int y = 1; y < h; y++) {
            byte val = arr.get(x, y);
            byte candidate = (byte)(prev - 1);
            if (candidate > val) {
               arr.set(x, y, candidate);
               val = candidate;
            }

            prev = val;
         }

         prev = arr.get(x, h1);

         for (int y = h1 - 1; y >= 0; y--) {
            byte val = arr.get(x, y);
            byte candidate = (byte)(prev - 1);
            if (candidate > val) {
               arr.set(x, y, candidate);
               val = candidate;
            }

            prev = val;
         }
      }
   }

   public boolean remove(int chunkX, int chunkZ) {
      long index = Index.oceanLayerChunk(chunkX, chunkZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.remove(index);
      if (storage != null) {
         this.processor.layerUpdates.add(this);
         this.causeOceanSurfaceUpdate(chunkX * 16, chunkZ * 16);
         this.causeOceanSurfaceUpdate(chunkX * 16 + 15, chunkZ * 16);
         this.causeOceanSurfaceUpdate(chunkX * 16 + 15, chunkZ * 16 + 15);
         this.causeOceanSurfaceUpdate(chunkX * 16, chunkZ * 16 + 15);
         this.processor.proxyEvents.add(() -> {
            this.proxyLayer.getOceanStorage().remove(index);
            if (this.proxyLayer.getOceanStorage().isEmpty()) {
               this.processor.getOceanWorld().removeOceanLayer(this.layerPosY);
            }
         });
      }

      return this.oceanStorage.isEmpty();
   }

   public void clear() {
      this.oceanStorage.clear();
      this.processor.layerUpdates.add(this);
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   public void setWaterAndDepthAndHeight(int worldX, int worldZ, short depthAndHeight) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage
         .computeIfAbsent(index, key -> new OceanLayer.OceanStorage(key, this.processor, this.proxyLayer, this.layerPosY));
      int fx = worldX & 15;
      int fz = worldZ & 15;
      boolean changed = false;
      int dataIndex = Index.chunkStorage(fx, fz);
      changed |= storage.blocks.setAndCompareData(dataIndex);
      changed |= storage.depths.setAndCompareData(dataIndex, depthAndHeight);
      if (changed) {
         this.processor.layerUpdates.add(this);
         this.causeOceanSurfaceUpdate(worldX, worldZ);
         this.processor.proxyEvents.add(() -> {
            ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
            proxyStorage.blocks.setData(dataIndex);
            proxyStorage.depths.setData(dataIndex, depthAndHeight);
         });
      }
   }

   public void causeLayerUpdate(int worldX, int worldZ) {
      this.processor.layerUpdates.add(this);
      this.causeOceanSurfaceUpdate(worldX, worldZ);
   }

   private void causeOceanSurfaceUpdate(int worldX, int worldZ) {
      int grouping = 6;
      int startX = Mth.floor((double)calculateChunkPosX(worldX - RANGE_SOLID) / (double)grouping);
      int endX = Mth.floor((double)calculateChunkPosX(worldX + RANGE_SOLID) / (double)grouping);
      int startZ = Mth.floor((double)calculateChunkPosZ(worldZ - RANGE_SOLID) / (double)grouping);
      int endZ = Mth.floor((double)calculateChunkPosZ(worldZ + RANGE_SOLID) / (double)grouping);

      for (int x = startX; x <= endX; x++) {
         for (int z = startZ; z <= endZ; z++) {
            this.surfaceUpdatesNeeded.add(Index.oceanLayerChunk(x, z));
         }
      }
   }

   public void unsetWater(int worldX, int worldZ) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         int fx = worldX & 15;
         int fz = worldZ & 15;
         boolean changed = storage.blocks.unsetAndCompareData(Index.chunkStorage(fx, fz));
         if (changed) {
            this.processor.layerUpdates.add(this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
            this.processor.proxyEvents.add(() -> {
               ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
               proxyStorage.blocks.unsetData(Index.chunkStorage(fx, fz));
            });
         }
      }
   }

   public boolean isWater(int worldX, int worldZ) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         int fx = worldX & 15;
         int fz = worldZ & 15;
         return storage.blocks.getData(Index.chunkStorage(fx, fz)) > 0;
      } else {
         return false;
      }
   }

   public void updateDepthAndHeight(int worldX, short layerY, int worldZ) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         int fx = worldX & 15;
         int fz = worldZ & 15;
         if (storage.blocks.getData(Index.chunkStorage(fx, fz)) != 0) {
            short depthAndHeight = this.processor.getWaterDepthAndHeight(worldX, layerY, worldZ);
            int dataIndex = Index.chunkStorage(fx, fz);
            boolean changed = storage.depths.setAndCompareData(dataIndex, depthAndHeight);
            if (changed) {
               this.processor.layerUpdates.add(this);
               this.causeOceanSurfaceUpdate(worldX, worldZ);
               this.processor.proxyEvents.add(() -> {
                  ProxyOceanStorage proxyStorage = (ProxyOceanStorage)this.proxyLayer.getOceanStorage().get(index);
                  proxyStorage.depths.setData(dataIndex, depthAndHeight);
               });
            }
         }
      }
   }

   public void setLight(int worldX, int worldZ, byte light) {
      long index = oceanLayerChunkFromWorldPos(worldX, worldZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null && storage.lights != null) {
         int dataIndex = Index.chunkStorage(worldX & 15, worldZ & 15);
         boolean changed = storage.lights.setAndCompareData(dataIndex, light);
         if (changed) {
            this.processor.layerUpdates.add(this);
            this.causeOceanSurfaceUpdate(worldX, worldZ);
         }
      }
   }

   public void setLayerLight(int chunkX, int chunkZ, byte[] light) {
      long index = Index.oceanLayerChunk(chunkX, chunkZ);
      OceanLayer.OceanStorage storage = (OceanLayer.OceanStorage)this.oceanStorage.get(index);
      if (storage != null) {
         storage.lights = new FullStorageType2DByte(light);
      }
   }

   public static int calculateChunkPosX(int worldX) {
      return worldX >> CHUNK_SIZE_USED_BITS;
   }

   public static int calculateChunkPosZ(int worldZ) {
      return worldZ >> CHUNK_SIZE_USED_BITS;
   }

   public static long oceanLayerChunkFromWorldPos(int worldX, int worldZ) {
      return Index.oceanLayerChunk(calculateChunkPosX(worldX), calculateChunkPosZ(worldZ));
   }

   public short getLayerPosY() {
      return this.layerPosY;
   }

   private static class OceanStorage {
      public final int x;
      public final int z;
      public final int size = 256;
      public final FullStorageType2DBit blocks;
      public final FullStorageType2DShort depths;
      @Nullable
      public FullStorageType2DByte lights;

      public OceanStorage(long index, OceanProcessor processor, ProxyOceanLayer proxyLayer, short layerPosY) {
         this.x = Index.getXFromOceanLayer(index);
         this.z = Index.getZFromOceanLayer(index);
         this.blocks = new FullStorageType2DBit(this.size);
         this.depths = new FullStorageType2DShort(this.size);
         processor.loadOceanBiomes(this.x, layerPosY, this.z);
         processor.getOceanWorld().queueEvent(() -> processor.getOceanWorld().loadOceanLayerLights(this.x, layerPosY, this.z));
         processor.proxyEvents.add(() -> {
            proxyLayer.getOceanStorage().put(index, new ProxyOceanStorage(index));
            processor.getOceanWorld().getOceanLayers().put(layerPosY, proxyLayer);
         });
      }

      public boolean areBiomesLoaded(OceanProcessor processor, short layerPosY) {
         if (!processor.areOceanBiomesLoaded(this.x, layerPosY, this.z)) {
            processor.loadOceanBiomes(this.x, layerPosY, this.z);
            return false;
         } else {
            return true;
         }
      }
   }
}
