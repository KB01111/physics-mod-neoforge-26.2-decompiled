package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap.Entry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.diebuddies.physics.ocean.storage.EqualStorageType;
import net.diebuddies.physics.ocean.storage.EqualStorageType2DInt;
import net.diebuddies.physics.ocean.storage.FullStorageType2DInt;
import net.diebuddies.physics.ocean.storage.ImmutableStorageTypeInt;
import net.diebuddies.physics.ocean.storage.StorageContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class OceanProcessor extends IWorld<OceanChunk> implements Runnable {
   private static final int BIOME_LOADER_THREAD_COUNT = Math.max(2, Math.min(4, Runtime.getRuntime().availableProcessors()));
   private ConcurrentLinkedQueue<Runnable> events;
   public final Dynamic2DArray waves = new Dynamic2DArray(50, 50);
   public final Dynamic2DArray depth = new Dynamic2DArray(50, 50);
   public final Long2ObjectMap<Short2ObjectMap<ImmutableStorageTypeInt>> loadedBiomeChunks = new Long2ObjectOpenHashMap(400);
   private final Object biomeDataLock = new Object();
   private final ExecutorService biomeExecutor;
   private final ConcurrentHashMap<OceanProcessor.BiomeLoadKey, Long> loadingBiomeJobs = new ConcurrentHashMap<>();
   private final ConcurrentLinkedQueue<OceanProcessor.BiomeLoadResult> completedBiomeLoads = new ConcurrentLinkedQueue<>();
   private final AtomicLong biomeLoadTickets = new AtomicLong();
   private Thread thread;
   private volatile boolean shutdown;
   private OceanWorld oceanWorld;
   private Short2ObjectMap<OceanLayer> oceanLayers;
   private Vector4f waterUVOffsets;
   private Vector2f waterMidCoord;
   protected LongSet processChunkColumns;
   public List<Runnable> proxyEvents;
   public ObjectSet<OceanLayer> layerUpdates;
   private float[] weights = new float[2];
   private static final ThreadLocal<OceanProcessor.PrefixBuffers> BIOME_PREFIX_BUFFERS = ThreadLocal.withInitial(OceanProcessor.PrefixBuffers::new);

   public OceanProcessor(OceanWorld oceanWorld, int minChunkY, int maxChunkY, Vector4f waterUVOffsets) {
      super(minChunkY, maxChunkY);
      this.oceanWorld = oceanWorld;
      this.layerUpdates = new ObjectOpenHashSet();
      this.processChunkColumns = new LongOpenHashSet();
      this.oceanLayers = new Short2ObjectOpenHashMap();
      this.events = new ConcurrentLinkedQueue<>();
      this.proxyEvents = new ObjectArrayList();
      this.waterUVOffsets = waterUVOffsets;
      this.waterMidCoord = new Vector2f(waterUVOffsets.x + waterUVOffsets.y, waterUVOffsets.z + waterUVOffsets.w).mul(0.5F);
      AtomicInteger biomeThreadCounter = new AtomicInteger();
      ThreadFactory biomeThreadFactory = task -> {
         Thread worker = new Thread(task, "Ocean Biome Loader Thread " + biomeThreadCounter.incrementAndGet());
         worker.setDaemon(true);
         return worker;
      };
      this.biomeExecutor = Executors.newFixedThreadPool(BIOME_LOADER_THREAD_COUNT, biomeThreadFactory);
      this.thread = new Thread(this, "Ocean Processor Thread");
      this.thread.setDaemon(true);
   }

   public void start() {
      this.thread.start();
   }

   public void join() {
      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
         Thread.currentThread().interrupt();
      }
   }

   @Override
   public void run() {
      while (!this.shutdown) {
         Runnable event = null;

         while ((event = this.events.poll()) != null) {
            event.run();
         }

         this.drainCompletedBiomeLoads();
         LongIterator it = this.processChunkColumns.iterator();

         while (it.hasNext()) {
            long index = it.nextLong();
            this.processChunkColumn(index);
         }

         this.processChunkColumns.clear();
         this.drainCompletedBiomeLoads();
         if (!this.layerUpdates.isEmpty()) {
            List<OceanSurface> generatedSurfaces = new ObjectArrayList();
            Iterator<OceanLayer> layerIterator = this.layerUpdates.iterator();

            while (layerIterator.hasNext()) {
               OceanLayer oceanLayer = layerIterator.next();
               OceanSurface oceanSurface = oceanLayer.generateMesh();
               if (oceanSurface != null) {
                  generatedSurfaces.add(oceanSurface);
                  layerIterator.remove();
               }
            }

            this.oceanWorld.queueEvent(() -> this.oceanWorld.replaceOceanMeshes(generatedSurfaces));
         }

         if (!this.proxyEvents.isEmpty()) {
            List<Runnable> multipleEventsCopy = new ObjectArrayList(this.proxyEvents);
            this.oceanWorld.queueEvent(() -> {
               for (Runnable task : multipleEventsCopy) {
                  task.run();
               }
            });
            this.proxyEvents.clear();
         }

         try {
            Thread.sleep(1L);
         } catch (InterruptedException var7) {
            var7.printStackTrace();
            Thread.currentThread().interrupt();
         }
      }

      this.drainCompletedBiomeLoads();
   }

   private void processChunkColumn(long index) {
      int x = Index.getXFromOceanLayer(index);
      int z = Index.getZFromOceanLayer(index);
      if (this.areSurroundingsLoaded(x, 0, z)) {
         int voxelX = x * 16;
         int voxelZ = z * 16;

         for (int y = this.minChunkY; y <= this.maxChunkY; y++) {
            OceanChunk chunk = this.getChunk(x, y, z);
            int voxelY = y * 16;
            if (chunk != null) {
               StorageContainer dataStorage = chunk.dataStorage;
               if (dataStorage.getStorageType() instanceof EqualStorageType) {
                  byte data = dataStorage.getData(0);
                  if (data > 0) {
                     int fy = voxelY + 16 - 1;

                     for (int xo = 0; xo < 16; xo++) {
                        for (int zo = 0; zo < 16; zo++) {
                           int fx = voxelX + xo;
                           int fz = voxelZ + zo;
                           if (this.isOceanSurface(data, fx, fy, fz)) {
                              this.setToSurface(data, fx, fy, fz);
                           }
                        }
                     }
                  }
               } else {
                  for (int xo = 0; xo < 16; xo++) {
                     for (int yo = 0; yo < 16; yo++) {
                        for (int zox = 0; zox < 16; zox++) {
                           byte data = dataStorage.getData(Index.chunkStorage(xo, yo, zox));
                           int fx = voxelX + xo;
                           int fy = voxelY + yo;
                           int fz = voxelZ + zox;
                           if (this.isOceanSurface(data, fx, fy, fz)) {
                              this.setToSurface(data, fx, fy, fz);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void setToSurface(byte state, int x, int y, int z) {
      OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)y);
      if (oceanLayer == null) {
         oceanLayer = new OceanLayer(this, (short)y);
         this.oceanLayers.put((short)y, oceanLayer);
      }

      if (oceanLayer != null) {
         short depth = this.getWaterDepthAndHeight(x, y, z);
         oceanLayer.setWaterAndDepthAndHeight(x, z, depth);
      }
   }

   public void blockChanged(int x, int y, int z, byte previousState, byte state) {
      if (this.areSurroundingsLoaded(WorldUtil.calculateChunkPosX(x), 0, WorldUtil.calculateChunkPosZ(z))) {
         OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)y);
         if (oceanLayer == null && this.isOceanSurface(state, x, y, z)) {
            oceanLayer = new OceanLayer(this, (short)y);
            this.oceanLayers.put((short)y, oceanLayer);
         }

         if (oceanLayer != null) {
            this.updateSurface(oceanLayer, state, x, y, z);
            this.updateSurface(oceanLayer, x + 1, y, z);
            this.updateSurface(oceanLayer, x - 1, y, z);
            this.updateSurface(oceanLayer, x, y, z + 1);
            this.updateSurface(oceanLayer, x, y, z - 1);
            if (previousState > 0 || state > 0) {
               boolean hasSurfaceNeighbour = false;

               for (int xo = -1; xo <= 1 && !hasSurfaceNeighbour; xo++) {
                  for (int zo = -1; zo <= 1 && !hasSurfaceNeighbour; zo++) {
                     if (xo != 0 || zo != 0) {
                        hasSurfaceNeighbour = oceanLayer.isWater(x + xo, z + zo);
                     }
                  }
               }

               if (hasSurfaceNeighbour) {
                  oceanLayer.causeLayerUpdate(x, z);
               }
            }

            short belowPos = (short)(y - 1);
            OceanLayer belowOceanLayer = (OceanLayer)this.oceanLayers.get(belowPos);
            if (belowOceanLayer == null) {
               if (this.isOceanSurface(x, belowPos, z)) {
                  belowOceanLayer = new OceanLayer(this, belowPos);
                  this.oceanLayers.put(belowPos, belowOceanLayer);
               }
            } else {
               this.updateSurface(belowOceanLayer, x, belowPos, z);
            }
         }

         ObjectIterator var12 = this.oceanLayers.short2ObjectEntrySet().iterator();

         while (var12.hasNext()) {
            Entry<OceanLayer> entry = (Entry<OceanLayer>)var12.next();
            oceanLayer = (OceanLayer)entry.getValue();
            short layerY = entry.getShortKey();
            oceanLayer.updateDepthAndHeight(x, layerY, z);
         }
      }
   }

   private void updateSurface(OceanLayer oceanLayer, byte state, int x, int y, int z) {
      if (this.isOceanSurface(state, x, y, z)) {
         short depth = this.getWaterDepthAndHeight(x, y, z);
         oceanLayer.setWaterAndDepthAndHeight(x, z, depth);
      } else {
         oceanLayer.unsetWater(x, z);
      }
   }

   private void updateSurface(OceanLayer oceanLayer, int x, int y, int z) {
      this.updateSurface(oceanLayer, this.getData(x, y, z), x, y, z);
   }

   private boolean isOceanSurface(byte state, int x, int y, int z) {
      return state > 0 && !this.isWater(x, y + 1, z) && !this.hasWaterFlow(x, y, z);
   }

   private boolean isOceanSurface(int x, int y, int z) {
      return this.isOceanSurface(this.getData(x, y, z), x, y, z);
   }

   public short getWaterDepthAndHeight(int x, int y, int z) {
      byte height = OceanLayer.RANGE;
      byte depth = OceanLayer.RANGE;

      for (byte offset = 1; offset <= OceanLayer.RANGE; offset++) {
         if (!this.isAir(x, y + offset, z)) {
            height = offset;
            break;
         }
      }

      for (byte offsetx = 1; offsetx <= OceanLayer.RANGE; offsetx++) {
         if (!this.isWater(x, y - offsetx, z)) {
            depth = offsetx;
            break;
         }
      }

      return Index.getWaterDepthAndHeight(depth, height);
   }

   @Override
   public void removeChunkColumn(int x, int z) {
      super.removeChunkColumn(x, z);
      long biomeIndex = Index.chunk(x, 0, z);
      synchronized (this.biomeDataLock) {
         this.loadedBiomeChunks.remove(biomeIndex);
      }

      this.cancelBiomeLoadsForColumn(x, z);
      Iterator<Entry<OceanLayer>> it = this.oceanLayers.short2ObjectEntrySet().iterator();

      while (it.hasNext()) {
         Entry<OceanLayer> entry = it.next();
         OceanLayer oceanLayer = (OceanLayer)entry.getValue();
         boolean empty = oceanLayer.remove(x, z);
         if (empty) {
            it.remove();
         }
      }
   }

   @Override
   public void removeAll() {
      super.removeAll();
      synchronized (this.biomeDataLock) {
         this.loadedBiomeChunks.clear();
      }

      this.loadingBiomeJobs.clear();
      this.completedBiomeLoads.clear();
      ObjectIterator var4 = this.oceanLayers.values().iterator();

      while (var4.hasNext()) {
         OceanLayer layer = (OceanLayer)var4.next();
         layer.clear();
      }

      this.oceanLayers.clear();
      this.proxyEvents.add(() -> this.oceanWorld.clearOceanLayers());
   }

   public boolean isWater(int x, int y, int z) {
      byte data = this.getData(x, y, z);
      return data != -1 && data != 0;
   }

   public boolean isAir(int x, int y, int z) {
      return this.getData(x, y, z) == 0;
   }

   public boolean hasWaterFlow(int x, int y, int z) {
      int vx = 0;
      int vz = 0;
      byte currentState = this.getData(x, y, z);

      for (Direction direction : Plane.HORIZONTAL) {
         int nx = x + direction.getStepX();
         int ny = y + direction.getStepY();
         int nz = z + direction.getStepZ();
         byte neighbourState = this.getData(nx, ny, nz);
         if (this.affectsFlow(neighbourState)) {
            int neighbourHeight = this.getOwnHeight(neighbourState);
            int magnitude = 0;
            if (neighbourHeight == 0) {
               byte belowNeighbourState = this.getData(nx, ny - 1, nz);
               boolean affectsFlow = this.affectsFlow(belowNeighbourState);
               neighbourHeight = this.getOwnHeight(belowNeighbourState);
               if (neighbourState > -1 && affectsFlow && neighbourHeight > 0) {
                  magnitude = this.getOwnHeight(currentState) - (neighbourHeight - 8);
               }
            } else if (neighbourHeight > 0) {
               magnitude = this.getOwnHeight(currentState) - neighbourHeight;
            }

            if (magnitude != 0) {
               vx += direction.getStepX() * magnitude;
               vz += direction.getStepZ() * magnitude;
            }
         }
      }

      return vx != 0 || vz != 0;
   }

   private boolean affectsFlow(byte neighbourState) {
      return neighbourState > 0;
   }

   private int getOwnHeight(byte neighbourState) {
      return Math.max(0, neighbourState);
   }

   public void updateLight(int worldX, int worldY, int worldZ, byte lightData) {
      IChunk<?> chunk = this.getChunkWorldPos(worldX, --worldY, worldZ);
      if (chunk != null) {
         OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get((short)worldY);
         if (oceanLayer != null) {
            oceanLayer.setLight(worldX, worldZ, lightData);
         }
      }
   }

   public void updateLayerLight(int chunkX, short layerY, int chunkZ, byte[] lightData) {
      IChunk<?> chunk = this.getChunkWorldPos(chunkX * 16, layerY, chunkZ * 16);
      if (chunk != null) {
         OceanLayer oceanLayer = (OceanLayer)this.oceanLayers.get(layerY);
         if (oceanLayer != null) {
            oceanLayer.setLayerLight(chunkX, chunkZ, lightData);
         }
      }
   }

   public float getHeight(int worldX, int worldY, int worldZ) {
      IChunk c = this.getChunkWorldPos(worldX, worldY, worldZ);
      if (c == null) {
         return 0.0F;
      } else {
         int vx = WorldUtil.calculateVoxelPosX(worldX);
         int vy = WorldUtil.calculateVoxelPosY(worldY);
         int vz = WorldUtil.calculateVoxelPosZ(worldZ);
         byte data = c.getDataFast(vx, vy, vz);
         if (data > 0) {
            if (vy < 15) {
               if (c.getDataFast(vx, vy + 1, vz) > 0) {
                  return 1.0F;
               }
            } else if (this.getData(worldX, worldY + 1, worldZ) > 0) {
               return 1.0F;
            }

            return (float)data / 9.0F;
         } else {
            return data == 0 ? 0.0F : -1.0F;
         }
      }
   }

   public float calculateAverageHeight(int worldX, int worldY, int worldZ, float height, float side1, float side2) {
      if (!(side2 >= 1.0F) && !(side1 >= 1.0F)) {
         this.weights[0] = 0.0F;
         this.weights[1] = 0.0F;
         if (side2 > 0.0F || side1 > 0.0F) {
            float currentHeight = this.getHeight(worldX, worldY, worldZ);
            if (currentHeight >= 1.0F) {
               return 1.0F;
            }

            this.addWeightedHeight(this.weights, currentHeight);
         }

         this.addWeightedHeight(this.weights, height);
         this.addWeightedHeight(this.weights, side2);
         this.addWeightedHeight(this.weights, side1);
         return this.weights[0] / this.weights[1];
      } else {
         return 1.0F;
      }
   }

   private void addWeightedHeight(float[] weights, float height) {
      if (height >= 0.8F) {
         weights[0] += height * 10.0F;
         weights[1] += 10.0F;
      } else if (height >= 0.0F) {
         weights[0] += height;
         weights[1]++;
      }
   }

   public void loadOceanBiomes(int chunkX, short layerPosY, int chunkZ) {
      IChunk<?> chunk = this.getChunkWorldPos(chunkX * 16, 0, chunkZ * 16);
      if (chunk != null) {
         long biomeIndex = Index.chunk(chunkX, 0, chunkZ);
         synchronized (this.biomeDataLock) {
            Short2ObjectMap<ImmutableStorageTypeInt> loadedBiomeColumns = (Short2ObjectMap<ImmutableStorageTypeInt>)this.loadedBiomeChunks.get(biomeIndex);
            if (loadedBiomeColumns != null && loadedBiomeColumns.containsKey(layerPosY)) {
               return;
            }
         }

         OceanProcessor.BiomeLoadKey key = new OceanProcessor.BiomeLoadKey(chunkX, layerPosY, chunkZ);
         long ticket = this.biomeLoadTickets.incrementAndGet();
         if (this.loadingBiomeJobs.putIfAbsent(key, ticket) == null) {
            Level level = this.oceanWorld.getLevel();
            int worldX = chunkX * 16;
            int worldZ = chunkZ * 16;
            int biomeBlendRadius = (Integer)Minecraft.getInstance().options.biomeBlendRadius().get();

            try {
               this.biomeExecutor.execute(() -> {
                  try {
                     int[] rgb = new int[256];
                     this.calculateTint16x16Fast(level, worldX, layerPosY, worldZ, BiomeColors.WATER_COLOR_RESOLVER, rgb, biomeBlendRadius);
                     ImmutableStorageTypeInt storage = this.createBiomeStorage(rgb);
                     this.completedBiomeLoads.add(new OceanProcessor.BiomeLoadResult(key, ticket, biomeIndex, storage, null));
                  } catch (Throwable var13x) {
                     this.completedBiomeLoads.add(new OceanProcessor.BiomeLoadResult(key, ticket, biomeIndex, null, var13x));
                  }
               });
            } catch (RejectedExecutionException var15) {
               this.loadingBiomeJobs.remove(key, ticket);
            }
         }
      }
   }

   private ImmutableStorageTypeInt createBiomeStorage(int[] rgb) {
      int start = rgb[0];

      for (int i = 1; i < rgb.length; i++) {
         if (rgb[i] != start) {
            return new FullStorageType2DInt(rgb);
         }
      }

      return new EqualStorageType2DInt(start);
   }

   private void drainCompletedBiomeLoads() {
      OceanProcessor.BiomeLoadResult result = null;

      while ((result = this.completedBiomeLoads.poll()) != null) {
         this.applyBiomeLoadResult(result);
      }
   }

   private void applyBiomeLoadResult(OceanProcessor.BiomeLoadResult result) {
      Long activeTicket = this.loadingBiomeJobs.get(result.key);
      if (activeTicket != null && activeTicket == result.ticket) {
         this.loadingBiomeJobs.remove(result.key, result.ticket);
         if (result.error != null) {
            result.error.printStackTrace();
         } else if (result.storage != null) {
            IChunk<?> chunk = this.getChunkWorldPos(result.key.chunkX * 16, 0, result.key.chunkZ * 16);
            if (chunk != null) {
               synchronized (this.biomeDataLock) {
                  Short2ObjectMap<ImmutableStorageTypeInt> loadedBiomeColumns = (Short2ObjectMap<ImmutableStorageTypeInt>)this.loadedBiomeChunks
                     .computeIfAbsent(result.biomeIndex, key -> new Short2ObjectOpenHashMap());
                  loadedBiomeColumns.put(result.key.layerPosY, result.storage);
               }
            }
         }
      }
   }

   private void cancelBiomeLoadsForColumn(int chunkX, int chunkZ) {
      for (OceanProcessor.BiomeLoadKey key : this.loadingBiomeJobs.keySet()) {
         if (key.chunkX == chunkX && key.chunkZ == chunkZ) {
            this.loadingBiomeJobs.remove(key);
         }
      }
   }

   public boolean areOceanBiomesLoaded(int chunkX, short layerPosY, int chunkZ) {
      IChunk<?> chunk = this.getChunkWorldPos(chunkX * 16, 0, chunkZ * 16);
      if (chunk == null) {
         return true;
      } else {
         long biomeIndex = Index.chunk(chunkX, 0, chunkZ);
         synchronized (this.biomeDataLock) {
            Short2ObjectMap<ImmutableStorageTypeInt> loadedBiomeColumns = (Short2ObjectMap<ImmutableStorageTypeInt>)this.loadedBiomeChunks.get(biomeIndex);
            return loadedBiomeColumns != null && loadedBiomeColumns.containsKey(layerPosY);
         }
      }
   }

   private void calculateTint16x16Fast(Level level, int baseX, int y, int baseZ, ColorResolver resolver, int[] outColors, int biomeBlendRadius) {
      int r = biomeBlendRadius;
      if (biomeBlendRadius == 0) {
         MutableBlockPos pos = new MutableBlockPos();

         for (int zo = 0; zo < 16; zo++) {
            int z = baseZ + zo;

            for (int xo = 0; xo < 16; xo++) {
               int x = baseX + xo;
               pos.set(x, y, z);
               int c = resolver.getColor((Biome)level.getBiome(pos).value(), (double)x, (double)z);
               outColors[zo << 4 | xo] = c;
            }
         }
      } else {
         int w = 16 + 2 * biomeBlendRadius;
         int h = 16 + 2 * biomeBlendRadius;
         int stride = w + 1;
         int needed = (w + 1) * (h + 1);
         OceanProcessor.PrefixBuffers prefixBuffers = BIOME_PREFIX_BUFFERS.get();
         prefixBuffers.ensureCapacity(needed);
         int[] prefixR = prefixBuffers.prefixR;
         int[] prefixG = prefixBuffers.prefixG;
         int[] prefixB = prefixBuffers.prefixB;
         Arrays.fill(prefixR, 0, needed, 0);
         Arrays.fill(prefixG, 0, needed, 0);
         Arrays.fill(prefixB, 0, needed, 0);
         int minX = baseX - biomeBlendRadius;
         int minZ = baseZ - biomeBlendRadius;
         MutableBlockPos pos = new MutableBlockPos();

         for (int dz = 0; dz < h; dz++) {
            int z = minZ + dz;
            int rowBase = (dz + 1) * stride;

            for (int dx = 0; dx < w; dx++) {
               int x = minX + dx;
               pos.set(x, y, z);
               int c = resolver.getColor((Biome)level.getBiome(pos).value(), (double)x, (double)z);
               int rr = c >> 16 & 0xFF;
               int gg = c >> 8 & 0xFF;
               int bb = c & 0xFF;
               int idx = rowBase + dx + 1;
               int left = idx - 1;
               int up = idx - stride;
               int upLeft = up - 1;
               prefixR[idx] = rr + prefixR[left] + prefixR[up] - prefixR[upLeft];
               prefixG[idx] = gg + prefixG[left] + prefixG[up] - prefixG[upLeft];
               prefixB[idx] = bb + prefixB[left] + prefixB[up] - prefixB[upLeft];
            }
         }

         int win = 2 * biomeBlendRadius + 1;
         int area = win * win;

         for (int zo = 0; zo < 16; zo++) {
            int z1 = zo;
            int z2 = zo + 2 * r;

            for (int xo = 0; xo < 16; xo++) {
               int x2 = xo + 2 * r;
               int sumR = rectSum(prefixR, stride, xo, z1, x2, z2);
               int sumG = rectSum(prefixG, stride, xo, z1, x2, z2);
               int sumB = rectSum(prefixB, stride, xo, z1, x2, z2);
               int rAvg = sumR / area & 0xFF;
               int gAvg = sumG / area & 0xFF;
               int bAvg = sumB / area & 0xFF;
               outColors[zo << 4 | xo] = rAvg << 16 | gAvg << 8 | bAvg;
            }
         }
      }
   }

   private static int rectSum(int[] prefix, int stride, int x1, int z1, int x2, int z2) {
      int a = z1 * stride + x1;
      int b = z1 * stride + x2 + 1;
      int c = (z2 + 1) * stride + x1;
      int d = (z2 + 1) * stride + x2 + 1;
      return prefix[d] - prefix[b] - prefix[c] + prefix[a];
   }

   public int getBiomeColor(int x, short layerPosY, int z) {
      ImmutableStorageTypeInt c = this.getBiomeChunkWorldPos(x, layerPosY, z);
      return c != null ? c.getData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosZ(z)) : 0;
   }

   @Nullable
   public ImmutableStorageTypeInt getBiomeChunkWorldPos(int x, short layerPosY, int z) {
      int chunkX = WorldUtil.calculateChunkPosX(x);
      int chunkZ = WorldUtil.calculateChunkPosZ(z);
      synchronized (this.biomeDataLock) {
         Short2ObjectMap<ImmutableStorageTypeInt> loadedBiomeColumns = (Short2ObjectMap<ImmutableStorageTypeInt>)this.loadedBiomeChunks
            .get(Index.chunk(chunkX, 0, chunkZ));
         return loadedBiomeColumns != null ? (ImmutableStorageTypeInt)loadedBiomeColumns.get(layerPosY) : null;
      }
   }

   public OceanWorld getOceanWorld() {
      return this.oceanWorld;
   }

   public Vector4f getWaterUVOffsets() {
      return this.waterUVOffsets;
   }

   public Vector2f getWaterMidCoord() {
      return this.waterMidCoord;
   }

   public Short2ObjectMap<OceanLayer> getOceanLayers() {
      return this.oceanLayers;
   }

   public void queueEvent(Runnable runnable) {
      this.events.add(runnable);
   }

   public void shutdown() {
      this.shutdown = true;
      this.biomeExecutor.shutdownNow();
   }

   private static final class BiomeLoadKey {
      private final int chunkX;
      private final short layerPosY;
      private final int chunkZ;

      private BiomeLoadKey(int chunkX, short layerPosY, int chunkZ) {
         this.chunkX = chunkX;
         this.layerPosY = layerPosY;
         this.chunkZ = chunkZ;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            return !(obj instanceof OceanProcessor.BiomeLoadKey other)
               ? false
               : this.chunkX == other.chunkX && this.layerPosY == other.layerPosY && this.chunkZ == other.chunkZ;
         }
      }

      @Override
      public int hashCode() {
         int result = Integer.hashCode(this.chunkX);
         result = 31 * result + Short.hashCode(this.layerPosY);
         return 31 * result + Integer.hashCode(this.chunkZ);
      }
   }

   private static final class BiomeLoadResult {
      private final OceanProcessor.BiomeLoadKey key;
      private final long ticket;
      private final long biomeIndex;
      @Nullable
      private final ImmutableStorageTypeInt storage;
      @Nullable
      private final Throwable error;

      private BiomeLoadResult(
         OceanProcessor.BiomeLoadKey key, long ticket, long biomeIndex, @Nullable ImmutableStorageTypeInt storage, @Nullable Throwable error
      ) {
         this.key = key;
         this.ticket = ticket;
         this.biomeIndex = biomeIndex;
         this.storage = storage;
         this.error = error;
      }
   }

   private static final class PrefixBuffers {
      private int[] prefixR = new int[0];
      private int[] prefixG = new int[0];
      private int[] prefixB = new int[0];

      private void ensureCapacity(int needed) {
         if (this.prefixR.length < needed) {
            this.prefixR = new int[needed];
            this.prefixG = new int[needed];
            this.prefixB = new int[needed];
         }
      }
   }
}
