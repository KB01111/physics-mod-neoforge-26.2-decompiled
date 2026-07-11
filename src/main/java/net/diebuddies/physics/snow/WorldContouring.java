package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import net.diebuddies.math.Math;
import net.diebuddies.math.PerlinNoise;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.ocean.DynamicFrustumBVH;
import net.diebuddies.physics.ocean.storage.FullStorageType;
import net.diebuddies.physics.snow.contouring.DualContouring;
import net.diebuddies.physics.snow.contouring.Vertex;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.snow.math.SDF;
import net.diebuddies.physics.snow.thread.ChunkFreeMesh;
import net.diebuddies.physics.snow.thread.ChunkUploadMesh;
import net.diebuddies.physics.snow.thread.MultipleEvent;
import net.minecraft.world.phys.Vec3;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class WorldContouring extends IWorld<ChunkContouring> implements Runnable {
   private static final int CONTOUR_STACK_SIZE = 8;
   private static final int LOD_UPDATE_EVERY_X_BLOCKS = 4;
   private ConcurrentLinkedQueue<Runnable> events;
   private FrustumIntersection frustum;
   private Vector3d playerPosition;
   private Vector3i lastPlayerPosition;
   private Matrix4f viewProjectionMatrix;
   private Set<ChunkContouring> needsVisualUpdate;
   public Set<ChunkContouring> seamUpdates;
   private Set<ChunkRenderUpdate> chunkRenderUpdates;
   private List<Runnable> tasks;
   private SnowWorld snowWorld;
   private MultipleEvent updateMeshesEvent = new MultipleEvent();
   public final DualContouring dualContouring;
   public List<Vertex> vertices = new ObjectArrayList();
   public IntList indices = new IntArrayList();
   private Thread thread;
   private final Semaphore wake = new Semaphore(0);
   private volatile boolean shutdown;
   private static final double FRUSTUM_KEY_SCALE = 0.1;
   private final DirtyChunkHeap dirtyHeap = new DirtyChunkHeap(8192);
   private final ObjectArrayList<ChunkContouring> deleteLaterBuf = new ObjectArrayList(512);
   private final ObjectArrayList<ChunkContouring> urgentBuf = new ObjectArrayList(64);
   private final ChunkContouring[] selChunks = new ChunkContouring[8];
   private final double[] selKeys = new double[8];
   private int selCap;
   private int selCount;
   private double selWorstKey;
   private double selPx;
   private double selPy;
   private double selPz;
   private int heapKeyX = Integer.MIN_VALUE;
   private int heapKeyZ = Integer.MIN_VALUE;
   private final ObjectArrayList<Runnable> priorityTaskBuf = new ObjectArrayList(8);
   private final ObjectArrayList<Runnable> dataChangedTaskBuf = new ObjectArrayList(8);
   private final ObjectArrayList<Runnable> normalTaskBuf = new ObjectArrayList(8);
   private final DynamicFrustumBVH<ChunkContouring> dynamicFrustum = new DynamicFrustumBVH<>();

   public WorldContouring(SnowWorld snowWorld, SnowConfiguration configuration, int minChunkY, int maxChunkY) {
      super(minChunkY, maxChunkY);
      this.dualContouring = new DualContouring(configuration);
      this.events = new ConcurrentLinkedQueue<>();
      this.snowWorld = snowWorld;
      this.needsVisualUpdate = new ObjectOpenHashSet();
      this.seamUpdates = new ObjectOpenHashSet();
      this.chunkRenderUpdates = new ObjectOpenHashSet();
      this.tasks = new ObjectArrayList();
      Vec3 pos = snowWorld.getCameraTranslation();
      this.playerPosition = new Vector3d(pos.x * (double)IChunk.CHUNK_MULTIPLE, pos.y * (double)IChunk.CHUNK_MULTIPLE, pos.z * (double)IChunk.CHUNK_MULTIPLE);
      this.lastPlayerPosition = new Vector3i((int)(pos.x / 4.0), (int)(pos.y / 4.0), (int)(pos.z / 4.0));
      this.viewProjectionMatrix = new Matrix4f(snowWorld.getCameraViewProjectionMatrix());
      this.frustum = new FrustumIntersection(this.viewProjectionMatrix, true);
      this.thread = new Thread(this, "Snow Contouring Thread");
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
      }
   }

   @Override
   public void run() {
      this.createModulationLayer();

      while (!this.shutdown) {
         boolean didWork = false;
         Runnable event = null;

         while ((event = this.events.poll()) != null) {
            didWork = true;
            event.run();
         }

         if (this.seamUpdates.size() > 0) {
            MultipleEvent seamEvents = new MultipleEvent();

            for (ChunkContouring chunk : this.seamUpdates) {
               chunk.updateSeamMesh(seamEvents, this);
            }

            seamEvents.run();
            this.seamUpdates.clear();
         }

         int ix = (int)(this.playerPosition.x / 4.0);
         int iy = (int)(this.playerPosition.y / 4.0);
         int iz = (int)(this.playerPosition.z / 4.0);
         if (ix != this.lastPlayerPosition.x || iy != this.lastPlayerPosition.y || iz != this.lastPlayerPosition.z) {
            ObjectIterator chunkX = this.loadedChunks.values().iterator();

            while (chunkX.hasNext()) {
               ChunkContouring chunk = (ChunkContouring)chunkX.next();
               chunk.checkLOD(this, this.playerPosition);
            }

            this.lastPlayerPosition.set(ix, iy, iz);
         }

         int chunkX = WorldUtil.calculateChunkPosX((int)this.playerPosition.x);
         int chunkZ = WorldUtil.calculateChunkPosX((int)this.playerPosition.z);
         this.updateChangedChunks(chunkX, chunkZ);

         for (Runnable runnable : this.tasks) {
            runnable.run();
         }

         this.tasks.clear();
         if (!this.updateMeshesEvent.isEmpty()) {
            this.cleanupUploadEvents(this.updateMeshesEvent);
            this.snowWorld.queueEvent(this.updateMeshesEvent);
            this.updateMeshesEvent = new MultipleEvent();
         }

         if (!didWork && this.needsVisualUpdate.isEmpty()) {
            try {
               this.wake.tryAcquire(1L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException var10) {
               var10.printStackTrace();
            }
         }
      }
   }

   private void cleanupUploadEvents(MultipleEvent updateMeshesEvent) {
      List<List<Runnable>> ordered = new ObjectArrayList();
      boolean gatherUploads = false;

      while (updateMeshesEvent.size() > 0) {
         List<Runnable> collected = this.gatherEvents(updateMeshesEvent, gatherUploads);
         gatherUploads = !gatherUploads;
         if (!collected.isEmpty()) {
            ordered.add(collected);
         }
      }

      updateMeshesEvent.getEvents().clear();
      updateMeshesEvent.addEvent(() -> {
         for (List<Runnable> batched : ordered) {
            boolean batchedUploads = batched.get(0) instanceof ChunkUploadMesh;
            if (!batchedUploads) {
               for (Runnable event : batched) {
                  event.run();
               }
            } else {
               this.snowWorld.runBatchedUpload(batched);
            }
         }
      });
   }

   private List<Runnable> gatherEvents(MultipleEvent updateMeshesEvent, boolean uploadEvents) {
      Map<Vector3i, Runnable> gather = new Object2ObjectOpenHashMap();
      Iterator<Runnable> it = updateMeshesEvent.getEvents().iterator();

      while (it.hasNext()) {
         Runnable event = it.next();
         if (event instanceof ChunkFreeMesh) {
            ChunkFreeMesh mesh = (ChunkFreeMesh)event;
            Vector3i position = new Vector3i(mesh.position).add(0, mesh.yOffset, 0);
            if (!gather.containsKey(position)) {
               if (uploadEvents) {
                  gather.put(position, null);
               } else {
                  gather.put(position, mesh);
                  it.remove();
               }
            }
         } else if (event instanceof ChunkUploadMesh) {
            ChunkUploadMesh mesh = (ChunkUploadMesh)event;
            Vector3i position = new Vector3i(mesh.position).add(0, mesh.yOffset, 0);
            if (!gather.containsKey(position)) {
               if (!uploadEvents) {
                  gather.put(position, null);
               } else {
                  gather.put(position, mesh);
                  it.remove();
               }
            }
         }
      }

      List<Runnable> collected = new ObjectArrayList();

      for (Runnable runnable : gather.values()) {
         if (runnable != null) {
            collected.add(runnable);
         }
      }

      return collected;
   }

   private void createModulationLayer() {
      this.modulationLayer = new FullStorageType(new byte[IChunk.CHUNK_VOLUME]);
      this.modulationLayerRaw = new FullStorageType(new byte[IChunk.CHUNK_VOLUME]);
      int repeatableAfter = IChunk.CHUNK_SIZE;
      int tilesLarge = IChunk.CHUNK_SIZE / 16;
      double dividerLarge = (double)repeatableAfter / (double)tilesLarge;
      PerlinNoise perlinLarge = new PerlinNoise(new Random(0L), tilesLarge);
      int tilesSmall = IChunk.CHUNK_SIZE / 2;
      double dividerSmall = (double)repeatableAfter / (double)tilesSmall;
      PerlinNoise perlinSmall = new PerlinNoise(new Random(1L), tilesSmall);

      for (int x = 0; x < IChunk.CHUNK_SIZE; x++) {
         for (int y = 0; y < IChunk.CHUNK_SIZE; y++) {
            for (int z = 0; z < IChunk.CHUNK_SIZE; z++) {
               double noiseLarge = perlinLarge.noise((double)x / dividerLarge, (double)y / dividerLarge, (double)z / dividerLarge) * 0.5 + 0.5;
               double noiseSmall = perlinSmall.noise((double)x / dividerSmall, (double)y / dividerSmall, (double)z / dividerSmall);
               double finalNoise = noiseLarge * 3.5 + noiseSmall * 3.5;
               byte rawData = Math.clamp((int)(finalNoise * 0.5 * 127.0), (byte)-107, (byte)107);
               byte data = Math.clamp(-127 + rawData, (byte)-126, (byte)127);
               this.modulationLayer.setData(null, Index.chunkStorage(x, y, z), data);
               this.modulationLayerRaw.setData(null, Index.chunkStorage(x, y, z), rawData);
            }
         }
      }
   }

   private void updateChangedChunks(int chunkX, int chunkZ) {
      int placeSize = java.lang.Math.max(0, 8 - this.chunkRenderUpdates.size());
      this.selPx = this.playerPosition.x;
      this.selPy = this.playerPosition.y;
      this.selPz = this.playerPosition.z;
      int keyX = (int)(this.selPx / 4.0);
      int keyZ = (int)(this.selPz / 4.0);
      if (keyX != this.heapKeyX || keyZ != this.heapKeyZ) {
         this.heapKeyX = keyX;
         this.heapKeyZ = keyZ;
         this.dirtyHeap.rekeyAll(this.selPx, this.selPy, this.selPz);
      }

      this.deleteLaterBuf.clear();
      this.urgentBuf.clear();
      this.selCap = placeSize;
      this.selCount = 0;
      this.selWorstKey = Double.POSITIVE_INFINITY;
      this.dynamicFrustum.query(this.frustum, this.selPx, this.selPy, this.selPz, this::onVisibleDirty);
      int i = 0;

      for (int n = this.deleteLaterBuf.size(); i < n; i++) {
         this.removeQueuedVisualUpdate((ChunkContouring)this.deleteLaterBuf.get(i));
      }

      this.deleteLaterBuf.clear();
      if (this.selCount == this.selCap && this.selCap > 0) {
         this.selWorstKey = this.selKeys[this.selCap - 1];
      }

      i = 4096;
      int pops = 0;

      while (this.selCount < this.selCap && !this.dirtyHeap.isEmpty() && pops++ < 4096) {
         ChunkContouring ch = this.dirtyHeap.pollChunk();
         if (ch == null) {
            break;
         }

         if (!ch.needsUpdate()) {
            this.removeQueuedVisualUpdate(ch);
         } else if (ch.needsUrgentUpdate()) {
            this.urgentBuf.add(ch);
         } else if (!isSelected(ch, this.selChunks, this.selCount)) {
            double key = DirtyChunkHeap.distSqToChunkCenter(ch, this.selPx, this.selPy, this.selPz);
            this.selCount = insertSorted(this.selChunks, this.selKeys, this.selCount, ch, key);
            if (this.selCount == this.selCap && this.selCap > 0) {
               this.selWorstKey = this.selKeys[this.selCap - 1];
            }
         }
      }

      while (this.selCap > 0 && this.selCount == this.selCap && !this.dirtyHeap.isEmpty() && pops++ < 4096) {
         double heapMin = this.dirtyHeap.peekKey();
         if (heapMin >= this.selWorstKey) {
            break;
         }

         ChunkContouring chx = this.dirtyHeap.pollChunk();
         if (chx == null) {
            break;
         }

         if (!chx.needsUpdate()) {
            this.removeQueuedVisualUpdate(chx);
         } else if (chx.needsUrgentUpdate()) {
            this.urgentBuf.add(chx);
         } else if (!isSelected(chx, this.selChunks, this.selCount)) {
            double key = DirtyChunkHeap.distSqToChunkCenter(chx, this.selPx, this.selPy, this.selPz);
            if (key >= this.selWorstKey) {
               this.dirtyHeap.addOrUpdate(chx, key);
               break;
            }

            ChunkContouring dropped = this.selChunks[this.selCap - 1];
            insertSortedDropLast(this.selChunks, this.selKeys, this.selCap, chx, key);
            this.selWorstKey = this.selKeys[this.selCap - 1];
            if (dropped != null && this.needsVisualUpdate.contains(dropped) && dropped.needsUpdate()) {
               double dk = DirtyChunkHeap.distSqToChunkCenter(dropped, this.selPx, this.selPy, this.selPz);
               this.dirtyHeap.addOrUpdate(dropped, dk);
            }
         }
      }

      for (int ix = 0; ix < this.selCount; ix++) {
         this.checkChunkForUpdate(this.selChunks[ix]);
      }

      int ix = 0;

      for (int n = this.urgentBuf.size(); ix < n; ix++) {
         this.checkChunkForUpdate((ChunkContouring)this.urgentBuf.get(ix));
      }

      this.urgentBuf.clear();
      this.priorityTaskBuf.clear();
      this.dataChangedTaskBuf.clear();
      this.normalTaskBuf.clear();
      ix = 0;
      Iterator<ChunkRenderUpdate> it = this.chunkRenderUpdates.iterator();

      while (it.hasNext()) {
         ChunkRenderUpdate cru = it.next();
         ChunkContouring chunk = cru.chunk;
         if (!this.loadedChunks.containsKey(Index.chunk(chunk.x, chunk.y, chunk.z))) {
            it.remove();
         } else if (chunk.hasPriority()) {
            this.priorityTaskBuf.add(cru.event);
            chunk.setPriority(false);
            it.remove();
         } else if (cru.dataChanged) {
            this.dataChangedTaskBuf.add(cru.event);
            ix++;
            it.remove();
         } else if (ix <= 8) {
            this.normalTaskBuf.add(cru.event);
            ix++;
            it.remove();
         }
      }

      for (int ixx = this.priorityTaskBuf.size() - 1; ixx >= 0; ixx--) {
         this.tasks.add((Runnable)this.priorityTaskBuf.get(ixx));
      }

      for (int ixx = this.dataChangedTaskBuf.size() - 1; ixx >= 0; ixx--) {
         this.tasks.add((Runnable)this.dataChangedTaskBuf.get(ixx));
      }

      int ixx = 0;

      for (int n = this.normalTaskBuf.size(); ixx < n; ixx++) {
         this.tasks.add((Runnable)this.normalTaskBuf.get(ixx));
      }
   }

   private void checkChunkForUpdate(ChunkContouring chunk) {
      boolean dataChanged = !chunk.areVoxelsUpdated();
      MultipleEvent updateMeshes = new MultipleEvent();
      chunk.renderUpdate(this, this.playerPosition, updateMeshes);
      if (!updateMeshes.isEmpty()) {
         this.chunkRenderUpdates.add(new ChunkRenderUpdate(chunk, updateMeshes, dataChanged));
      }

      this.removeQueuedVisualUpdate(chunk);
   }

   private boolean onVisibleDirty(int id, ChunkContouring chunk) {
      if (chunk == null) {
         return true;
      } else if (!chunk.needsUpdate()) {
         this.deleteLaterBuf.add(chunk);
         return true;
      } else if (chunk.needsUrgentUpdate()) {
         this.urgentBuf.add(chunk);
         return true;
      } else if (this.selCap == 0) {
         return true;
      } else {
         double key = DirtyChunkHeap.distSqToChunkCenter(chunk, this.selPx, this.selPy, this.selPz) * 0.1;
         if (this.selCount < this.selCap) {
            this.selCount = insertSorted(this.selChunks, this.selKeys, this.selCount, chunk, key);
            if (this.selCount == this.selCap) {
               this.selWorstKey = this.selKeys[this.selCap - 1];
            }
         } else if (key < this.selWorstKey) {
            insertSortedDropLast(this.selChunks, this.selKeys, this.selCap, chunk, key);
            this.selWorstKey = this.selKeys[this.selCap - 1];
         }

         return true;
      }
   }

   public void changeDensity(SDF sdf, byte strength, byte action) {
      int x = (int)sdf.getX();
      int y = (int)sdf.getY();
      int z = (int)sdf.getZ();
      int adjRadius = (int)java.lang.Math.ceil(sdf.getBounds()) + 2;

      for (int i = x - adjRadius; i < x + adjRadius; i++) {
         for (int j = y - adjRadius; j < y + adjRadius; j++) {
            for (int k = z - adjRadius; k < z + adjRadius; k++) {
               ChunkContouring c = this.getChunkWorldPos(i, j, k);
               if (c != null) {
                  double distance = Math.clamp(sdf.getDistance((double)i, (double)j, (double)k), -1.0, 1.0);
                  byte calcData = (byte)((int)((double)strength * distance));
                  int voxelX = WorldUtil.calculateVoxelPosX(i);
                  int voxelY = WorldUtil.calculateVoxelPosY(j);
                  int voxelZ = WorldUtil.calculateVoxelPosZ(k);
                  byte oldData = c.getDataByteFast(this, voxelX, voxelY, voxelZ);
                  if (oldData > calcData) {
                     if (sdf.hasPriority()) {
                        c.setPriority(true);
                     }

                     c.setDataSnowTrack(this, voxelX, voxelY, voxelZ, calcData);
                  }
               }
            }
         }
      }
   }

   public void uploadInformation(Matrix4f viewProjection, double camX, double camY, double camZ) {
      this.frustum.set(this.viewProjectionMatrix.set(viewProjection), true);
      this.playerPosition.set(camX * (double)IChunk.CHUNK_MULTIPLE, camY * (double)IChunk.CHUNK_MULTIPLE, camZ * (double)IChunk.CHUNK_MULTIPLE);
   }

   public ChunkContouring removeChunk(long index) {
      ChunkContouring chunk = (ChunkContouring)this.loadedChunks.get(index);
      this.removeQueuedVisualUpdate(chunk);
      if (chunk != null) {
         this.removeChunkMesh(new Vector3i(chunk.x, chunk.y, chunk.z), true);
         this.removeChunkMesh(new Vector3i(chunk.x, chunk.y, chunk.z), false);
      }

      return (ChunkContouring)super.removeChunk(index);
   }

   public void removeChunkMesh(Vector3i chunkPosition, boolean seam) {
      this.updateMeshesEvent.addEvent(new ChunkFreeMesh(this.snowWorld, chunkPosition, seam ? this.heightChunks : 0));
   }

   public void addChunkMesh(Vector3i chunkPosition, RawMesh mesh, boolean seam) {
      this.updateMeshesEvent.addEvent(new ChunkUploadMesh(this.snowWorld, chunkPosition, mesh, seam ? this.heightChunks : 0));
   }

   public void queueEvent(Runnable runnable) {
      this.events.add(runnable);
      this.wake.release();
   }

   public void queueVisualUpdate(ChunkContouring chunk) {
      boolean added = this.needsVisualUpdate.add(chunk);
      if (added) {
         this.dynamicFrustum
            .add(
               new AABB3D(
                  (double)(chunk.xVoxel() - 2),
                  (double)(chunk.yVoxel() - 2),
                  (double)(chunk.zVoxel() - 2),
                  (double)(chunk.xVoxel() + IChunk.CHUNK_SIZE + 2),
                  (double)(chunk.yVoxel() + IChunk.CHUNK_SIZE + 2),
                  (double)(chunk.zVoxel() + IChunk.CHUNK_SIZE + 2)
               ),
               chunk
            );
         double d = DirtyChunkHeap.distSqToChunkCenter(chunk, this.playerPosition.x, this.playerPosition.y, this.playerPosition.z);
         this.dirtyHeap.addOrUpdate(chunk, d);
      }
   }

   public void removeQueuedVisualUpdate(ChunkContouring chunk) {
      boolean removed = this.needsVisualUpdate.remove(chunk);
      if (removed) {
         this.dynamicFrustum.remove(chunk);
         this.dirtyHeap.remove(chunk);
      }
   }

   public void shutdown() {
      this.shutdown = true;
   }

   public SnowWorld getSnowWorld() {
      return this.snowWorld;
   }

   public Vector3d getPlayerPosition() {
      return this.playerPosition;
   }

   private static int insertSorted(ChunkContouring[] chunks, double[] keys, int size, ChunkContouring c, double key) {
      int i;
      for (i = size; i > 0 && key < keys[i - 1]; i--) {
         chunks[i] = chunks[i - 1];
         keys[i] = keys[i - 1];
      }

      chunks[i] = c;
      keys[i] = key;
      return size + 1;
   }

   private static void insertSortedDropLast(ChunkContouring[] chunks, double[] keys, int size, ChunkContouring c, double key) {
      int i;
      for (i = size - 1; i > 0 && key < keys[i - 1]; i--) {
         chunks[i] = chunks[i - 1];
         keys[i] = keys[i - 1];
      }

      chunks[i] = c;
      keys[i] = key;
   }

   private static boolean isSelected(ChunkContouring c, ChunkContouring[] arr, int count) {
      for (int i = 0; i < count; i++) {
         if (arr[i] == c) {
            return true;
         }
      }

      return false;
   }
}
