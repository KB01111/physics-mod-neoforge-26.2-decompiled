package net.diebuddies.physics.snow;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.StagingBuffer;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.StagingBuffer.Uploader;
import com.mojang.blaze3d.vertex.TlsfAllocator.Allocation;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.compat.Iris;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.snow.math.SDF;
import net.diebuddies.physics.snow.math.SDFBoxRound;
import net.diebuddies.physics.snow.thread.ChunkCreator;
import net.diebuddies.physics.snow.thread.ChunkUploadMesh;
import net.diebuddies.physics.snow.thread.SnowChunkCreator;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.util.ObjectOpenHashSetReplace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.jspecify.annotations.Nullable;

public class SnowWorld {
   private static final Vector3d Y_AXIS = new Vector3d(0.0, 1.0, 0.0);
   public static final int SNOW_TRACK_UPDATES_PER_SECOND = 10;
   public Level level;
   public Long2ObjectMap<ChunkEntity> chunks;
   public ConcurrentLinkedQueue<Runnable> queue;
   public WorldContouring contouring;
   public Long2ObjectMap<ShortSet> fetchLightUpdates;
   private Long2ObjectMap<LongList> loadedColumns;
   public double trackTime;
   public Matrix4f viewProjection;
   public int trackUpdateCount;
   private ObjectOpenHashSetReplace<BlockUpdate> blockUpdates;
   private static final int SNOW_INDEX_ALIGNMENT = 8;
   @Nullable
   private SnowUberBuffers snowBuffers;
   @Nullable
   private StagingBuffer stagingBuffer;
   private int snowVertexHeapSize;
   private int snowIndexHeapSize;
   public SnowBatch snowBatch = new SnowBatch();
   public Matrix4d tmp = new Matrix4d();
   private MutableBlockPos pos = new MutableBlockPos();
   private SnowConfiguration configuration;

   public SnowWorld(Level level) {
      this.level = level;
      this.blockUpdates = new ObjectOpenHashSetReplace<>();
      this.viewProjection = new Matrix4f();
      this.chunks = new Long2ObjectOpenHashMap();
      this.queue = new ConcurrentLinkedQueue<>();
      this.contouring = new WorldContouring(this, this.configuration = new SnowConfiguration(), level.getMinSectionY(), level.getMaxSectionY());
      this.fetchLightUpdates = new Long2ObjectOpenHashMap();
      this.loadedColumns = new Long2ObjectOpenHashMap();
      this.contouring.start();
   }

   private void createRenderObjects() {
      if (this.snowBuffers == null && this.stagingBuffer == null) {
         this.snowVertexHeapSize = 1048576 * this.getModelVertexFormat().getVertexSize();
         this.snowIndexHeapSize = 4194304;
         int stagingBufferSize = Math.min(this.snowVertexHeapSize + this.snowIndexHeapSize, 6291456);
         GpuDevice gpuDevice = RenderSystem.getDevice();
         this.stagingBuffer = StagingBuffer.create("Physics Snow Staging", gpuDevice, stagingBufferSize);
         this.snowBuffers = new SnowWorld.SnowUberBuffers(
            new UberGpuBuffer("Physics Snow Vertex", 32, this.snowVertexHeapSize, this.getModelVertexFormat().getVertexSize(), this.stagingBuffer),
            new UberGpuBuffer("Physics Snow Index", 64, this.snowIndexHeapSize, 8, this.stagingBuffer)
         );
      }
   }

   public void uploadGlobalGeomBuffersToGPU() {
      if (this.snowBuffers != null && this.stagingBuffer != null) {
         GpuDevice device = RenderSystem.getDevice();
         Uploader uploader = this.stagingBuffer.startUploading(device.createCommandEncoder());

         try {
            this.snowBuffers.vertexBuffer.uploadStagedAllocations(device, uploader);
            this.snowBuffers.indexBuffer.uploadStagedAllocations(device, uploader);
         } catch (Throwable var6) {
            if (uploader != null) {
               try {
                  uploader.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (uploader != null) {
            uploader.close();
         }
      }
   }

   public void update(double diff) {
      Runnable event = null;

      while ((event = this.queue.poll()) != null) {
         event.run();
      }

      this.loadedColumns.clear();
      this.fetchLightUpdates.clear();
   }

   private void applyBlockUpdates(List<Runnable> events) {
      if (!this.blockUpdates.isEmpty()) {
         List<BlockUpdate> updates = new ObjectArrayList(this.blockUpdates);

         for (BlockUpdate update : updates) {
            BlockPos pos = update.pos;
            int cx = SectionPos.blockToSectionCoord(pos.getX());
            int cy = SectionPos.blockToSectionCoord(pos.getY());
            int cz = SectionPos.blockToSectionCoord(pos.getZ());
            SnowSearcher.queueLightUpdates(this, this.getLightUpdates(SectionPos.asLong(cx, cy, cz)), pos.getX(), pos.getY(), pos.getZ());
         }

         events.add(
            () -> {
               for (BlockUpdate updatex : updates) {
                  BlockPos posx = updatex.pos;
                  BlockState state = updatex.state;
                  int rx = posx.getX() * IChunk.CHUNK_MULTIPLE;
                  int ry = posx.getY() * IChunk.CHUNK_MULTIPLE;
                  int rz = posx.getZ() * IChunk.CHUNK_MULTIPLE;
                  ChunkContouring chunk = this.contouring.getChunkWorldPos(rx, ry, rz);
                  if (chunk != null) {
                     SnowChunkCreator.updateBlock(
                        this.contouring, chunk, rx & IChunk.CHUNK_SIZE_BITS, ry & IChunk.CHUNK_SIZE_BITS, rz & IChunk.CHUNK_SIZE_BITS, state
                     );
                  }
               }
            }
         );
         this.blockUpdates.clear();
      }
   }

   private void applyLightUpdates(List<Runnable> events) {
      if (!this.fetchLightUpdates.isEmpty()) {
         Iterator<Entry<ShortSet>> it = this.fetchLightUpdates.long2ObjectEntrySet().iterator();
         LevelLightEngine levelLightEngine = this.level.getLightEngine();

         while (it.hasNext()) {
            Entry<ShortSet> entry = it.next();
            long chunkIndex = entry.getLongKey();
            int x = SectionPos.x(chunkIndex);
            int y = SectionPos.y(chunkIndex);
            int z = SectionPos.z(chunkIndex);
            ShortSet positions = (ShortSet)entry.getValue();
            if (positions.isEmpty() || !this.areSurroundingsLoaded(this.level, x, y, z)) {
               it.remove();
            } else if (levelLightEngine.lightOnInColumn(SectionPos.getZeroNode(x, z))) {
               ShortIterator blockIt = positions.iterator();
               List<SnowWorld.LightUpdate> updates = new ObjectArrayList();

               while (blockIt.hasNext()) {
                  short localPos = blockIt.nextShort();
                  byte lx = (byte)(localPos >> 8 & 15);
                  byte ly = (byte)(localPos >> 4 & 15);
                  byte lz = (byte)(localPos & 15);
                  int bx = x * 16 + lx;
                  int by = y * 16 + ly;
                  int bz = z * 16 + lz;
                  if (by >= this.level.getMinY() && by < this.level.getMaxY()) {
                     this.pos.set(bx, by, bz);
                     int sky = Math.min(this.level.getBrightness(LightLayer.SKY, this.pos), 15);
                     int block = Math.min(this.level.getBrightness(LightLayer.BLOCK, this.pos), 15);
                     SnowWorld.LightUpdate update = new SnowWorld.LightUpdate();
                     update.posX = lx;
                     update.posY = ly;
                     update.posZ = lz;
                     update.lightData = (byte)(sky << 4 | block);
                     updates.add(update);
                     blockIt.remove();
                  } else {
                     blockIt.remove();
                  }
               }

               if (positions.isEmpty()) {
                  it.remove();
               }

               if (!updates.isEmpty()) {
                  events.add(
                     () -> {
                        ChunkContouring chunk = this.contouring.getChunk(x, y, z);
                        if (chunk != null) {
                           boolean xupdate = false;
                           boolean yupdate = false;
                           boolean zupdate = false;

                           for (int i = 0; i < updates.size(); i++) {
                              SnowWorld.LightUpdate updatex = updates.get(i);
                              xupdate |= updatex.posX == 0;
                              yupdate |= updatex.posY == 0;
                              zupdate |= updatex.posZ == 0;
                              chunk.setLightDataFast(
                                 updatex.posX << IChunk.CHUNK_MULTIPLE_BITS,
                                 updatex.posY << IChunk.CHUNK_MULTIPLE_BITS,
                                 updatex.posZ << IChunk.CHUNK_MULTIPLE_BITS,
                                 updatex.lightData
                              );
                           }

                           chunk.setLightsUpdated(this.contouring, false);
                           if (xupdate) {
                              ChunkContouring neighbour = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, -1, 0, 0);
                              if (neighbour != null) {
                                 neighbour.setLightsUpdated(this.contouring, false);
                              }

                              if (yupdate) {
                                 neighbour = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, -1, -1, 0);
                                 if (neighbour != null) {
                                    neighbour.setLightsUpdated(this.contouring, false);
                                 }

                                 if (zupdate) {
                                    neighbour = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, -1, -1, -1);
                                    if (neighbour != null) {
                                       neighbour.setLightsUpdated(this.contouring, false);
                                    }
                                 }
                              } else if (zupdate) {
                                 neighbour = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, -1, 0, -1);
                                 if (neighbour != null) {
                                    neighbour.setLightsUpdated(this.contouring, false);
                                 }
                              }
                           }

                           if (yupdate) {
                              ChunkContouring neighbourx = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, 0, -1, 0);
                              if (neighbourx != null) {
                                 neighbourx.setLightsUpdated(this.contouring, false);
                              }

                              if (zupdate) {
                                 neighbourx = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, 0, -1, -1);
                                 if (neighbourx != null) {
                                    neighbourx.setLightsUpdated(this.contouring, false);
                                 }
                              }
                           }

                           if (zupdate) {
                              ChunkContouring neighbourxx = (ChunkContouring)chunk.getNeighbourChunk(this.contouring, 0, 0, -1);
                              if (neighbourxx != null) {
                                 neighbourxx.setLightsUpdated(this.contouring, false);
                              }
                           }
                        }
                     }
                  );
               }
            }
         }
      }
   }

   private boolean areSurroundingsLoaded(Level level, int x, int y, int z) {
      for (int xo = -1; xo <= 1; xo++) {
         for (int zo = -1; zo <= 1; zo++) {
            if (level.getChunkSource().hasChunk(x + xo, z + zo)) {
               return true;
            }
         }
      }

      return false;
   }

   private void applyEntitySnowTracks(double diff, List<Runnable> events) {
      this.trackTime += diff;
      if (this.trackTime >= 0.1) {
         this.trackTime = 0.0;
         ClientLevel clientLevel = (ClientLevel)this.level;
         ObjectArrayList sdfs = new ObjectArrayList();
         double maxTrackDistanceSquared = ConfigClient.snowTrackDistance * ConfigClient.snowTrackDistance * 3.0;
         Vec3 cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
         Iterator var9 = clientLevel.entitiesForRendering().iterator();

         while (true) {
            AABB boundingBox;
            LivingEntity trackedLiving = null;
            while (true) {
               if (!var9.hasNext()) {
                  events.add(
                     () -> {
                        Collections.sort(
                           sdfs,
                           new Comparator<SDF>() {
                              {
                                 Objects.requireNonNull(SnowWorld.this);
                              }

                              public int compare(SDF o1, SDF o2) {
                                 return Double.compare(
                                    Vector3d.distanceSquared(
                                       o1.getX(),
                                       o1.getY(),
                                       o1.getZ(),
                                       SnowWorld.this.contouring.getPlayerPosition().x,
                                       SnowWorld.this.contouring.getPlayerPosition().y,
                                       SnowWorld.this.contouring.getPlayerPosition().z
                                    ),
                                    Vector3d.distanceSquared(
                                       o2.getX(),
                                       o2.getY(),
                                       o2.getZ(),
                                       SnowWorld.this.contouring.getPlayerPosition().x,
                                       SnowWorld.this.contouring.getPlayerPosition().y,
                                       SnowWorld.this.contouring.getPlayerPosition().z
                                    )
                                 );
                              }
                           }
                        );

                        for (int i = 0; i < sdfs.size() && i < ConfigClient.snowTrackEntities; i++) {
                           this.contouring.changeDensity((SDF)sdfs.get(i), (byte)127, (byte)2);
                        }
                     }
                  );
                  return;
               }

               Entity entity = (Entity)var9.next();
               if (entity instanceof LivingEntity living && !(living.distanceToSqr(cameraPos) > maxTrackDistanceSquared)) {
                  trackedLiving = living;
                  boundingBox = living.getBoundingBox();

                  try {
                     if (boundingBox != null && !boundingBox.hasNaN() && !living.isSpectator()) {
                        break;
                     }
                  } catch (Exception var26) {
                     break;
                  }
               }
            }

            double halfWidth = (boundingBox.maxX - boundingBox.minX) * 0.5 * (double)IChunk.CHUNK_MULTIPLE;
            double halfHeight = (boundingBox.maxY - boundingBox.minY) * 0.5 * (double)IChunk.CHUNK_MULTIPLE;
            double halfDepth = (boundingBox.maxZ - boundingBox.minZ) * 0.5 * (double)IChunk.CHUNK_MULTIPLE;
            double centerX = ((boundingBox.maxX + boundingBox.minX) * 0.5 - 0.5 / (double)IChunk.CHUNK_MULTIPLE) * (double)IChunk.CHUNK_MULTIPLE;
            double centerY = ((boundingBox.maxY + boundingBox.minY) * 0.5 - 0.5 / (double)IChunk.CHUNK_MULTIPLE) * (double)IChunk.CHUNK_MULTIPLE;
            double centerZ = ((boundingBox.maxZ + boundingBox.minZ) * 0.5 - 0.5 / (double)IChunk.CHUNK_MULTIPLE) * (double)IChunk.CHUNK_MULTIPLE;
            SDFBoxRound sdf = new SDFBoxRound(halfWidth, halfHeight, halfDepth, 0.1 * (double)IChunk.CHUNK_MULTIPLE);
            sdf.setTransformation(this.tmp.identity().translate(centerX, centerY, centerZ).rotate((double)trackedLiving.yBodyRot, Y_AXIS));
            if (trackedLiving instanceof Player) {
               sdf.setPriority(true);
            }

            sdfs.add(sdf);
         }
      }
   }

   public void runBatchedUpload(List<Runnable> batched) {
      List<SnowWorld.PreparedUpload> preparedUploads = new ObjectArrayList();

      for (Runnable runnable : batched) {
         ChunkUploadMesh upload = (ChunkUploadMesh)runnable;
         if (upload.mesh != null && upload.mesh.data != null && upload.mesh.indexData != null) {
            SnowWorld.ChunkCoords coords = this.resolveChunkCoords(upload);
            ChunkEntity entity = this.createChunkEntity(upload);
            if (coords != null && entity != null) {
               int vertexCount = upload.mesh.data.capacity() / this.getModelVertexFormat().getVertexSize();
               int indexCount = upload.mesh.indexData.capacity() / 4;
               if (vertexCount > 0 && indexCount > 0) {
                  preparedUploads.add(new SnowWorld.PreparedUpload(upload, entity, coords, vertexCount, indexCount));
               } else {
                  upload.mesh.destroy();
               }
            } else {
               upload.mesh.destroy();
            }
         } else if (upload.mesh != null) {
            upload.mesh.destroy();
         }
      }

      if (!preparedUploads.isEmpty()) {
         this.createRenderObjects();

         for (SnowWorld.PreparedUpload prepared : preparedUploads) {
            ChunkUploadMesh upload = prepared.upload();

            try {
               if (this.stageRenderBuffers(prepared.entity(), upload.mesh.data, upload.mesh.indexData, prepared.vertexCount(), prepared.indexCount())) {
                  SnowWorld.ChunkCoords coords = prepared.coords();
                  this.attachChunkEntity(prepared.entity(), coords.chunkX(), coords.chunkY(), coords.chunkZ());
               }
            } finally {
               upload.mesh.destroy();
            }
         }
      }
   }

   public VertexFormat getModelVertexFormat() {
      return StarterClient.iris() ? Iris.remapFormat(PhysicsShaders.SNOW_FORMAT) : PhysicsShaders.SNOW_FORMAT;
   }

   private boolean stageRenderBuffers(ChunkEntity entity, ByteBuffer vertexData, ByteBuffer indexData, int vertexCount, int indexCount) {
      SnowWorld.SnowUberBuffers buffers = this.getSnowBuffers();
      boolean success = true;
      if (!buffers.vertexBuffer.addAllocation(entity, uploaded -> this.cacheVertexRenderBuffer(buffers, uploaded), vertexData)) {
         this.uploadGlobalGeomBuffersToGPU();
         success &= buffers.vertexBuffer.addAllocation(entity, uploaded -> this.cacheVertexRenderBuffer(buffers, uploaded), vertexData);
      }

      if (!buffers.indexBuffer.addAllocation(entity, uploaded -> this.cacheIndexRenderBuffer(buffers, uploaded), indexData)) {
         this.uploadGlobalGeomBuffersToGPU();
         success &= buffers.indexBuffer.addAllocation(entity, uploaded -> this.cacheIndexRenderBuffer(buffers, uploaded), indexData);
      }

      if (success) {
         entity.uploadedIndexCount = indexCount;
         this.uploadGlobalGeomBuffersToGPU();
         return true;
      } else {
         this.removeRenderBuffers(entity);
         return false;
      }
   }

   private void cacheVertexRenderBuffer(SnowWorld.SnowUberBuffers buffers, ChunkEntity entity) {
      Allocation allocation = buffers.vertexBuffer.getAllocation(entity);
      if (allocation == null) {
         entity.vertexBuffer = null;
         entity.vertexBufferOffset = 0L;
      } else {
         entity.vertexBuffer = buffers.vertexBuffer.getGpuBuffer(allocation);
         entity.vertexBufferOffset = allocation.getOffsetFromHeap();
      }
   }

   private void cacheIndexRenderBuffer(SnowWorld.SnowUberBuffers buffers, ChunkEntity entity) {
      Allocation allocation = buffers.indexBuffer.getAllocation(entity);
      if (allocation == null) {
         entity.indexBuffer = null;
         entity.indexBufferOffset = 0L;
      } else {
         entity.indexBuffer = buffers.indexBuffer.getGpuBuffer(allocation);
         entity.indexBufferOffset = allocation.getOffsetFromHeap();
      }
   }

   private SnowWorld.SnowUberBuffers getSnowBuffers() {
      this.createRenderObjects();
      return this.snowBuffers;
   }

   public Vec3 getCameraTranslation() {
      return Minecraft.getInstance().gameRenderer.mainCamera().position();
   }

   public Matrix4f getCameraViewProjectionMatrix() {
      return this.viewProjection;
   }

   public void addChunkEntity(ChunkEntity entity, int chunkX, int chunkY, int chunkZ) {
      this.attachChunkEntity(entity, chunkX, chunkY, chunkZ);
   }

   private void attachChunkEntity(ChunkEntity entity, int chunkX, int chunkY, int chunkZ) {
      long chunkIndex = Index.chunk(chunkX, chunkY, chunkZ);
      ChunkEntity previous = (ChunkEntity)this.chunks.put(chunkIndex, entity);
      if (previous != null) {
         this.snowBatch.remove(previous);
         if (previous != entity) {
            this.removeRenderBuffers(previous);
         }
      }

      AABB3D modelBoundingBox = entity.aabb;
      if (modelBoundingBox != null) {
         this.snowBatch.add(entity);
      }
   }

   public void removeChunkEntity(int chunkX, int chunkY, int chunkZ) {
      ChunkEntity entity = (ChunkEntity)this.chunks.remove(Index.chunk(chunkX, chunkY, chunkZ));
      if (entity != null) {
         this.snowBatch.remove(entity);
         this.removeRenderBuffers(entity);
      }
   }

   private void removeRenderBuffers(ChunkEntity entity) {
      entity.clearRenderBuffers();
      if (this.snowBuffers != null) {
         this.snowBuffers.vertexBuffer.removeAllocation(entity);
         this.snowBuffers.indexBuffer.removeAllocation(entity);
      }
   }

   @Nullable
   private ChunkEntity createChunkEntity(ChunkUploadMesh upload) {
      Vector3i position = upload.position;
      if (position != null && upload.mesh != null && upload.mesh.boundingBox != null) {
         ChunkEntity entity = new ChunkEntity();
         entity.position = new Vector3i(position);
         entity.batchPosition = new Vector3i(SnowBatch.shiftPos(position.x), SnowBatch.shiftPos(position.y), SnowBatch.shiftPos(position.z));
         entity.aabb = upload.mesh.boundingBox;
         entity.center = new Vector3d(entity.aabb.start).add(entity.aabb.end).mul(0.5);
         entity.calculateTransformations();
         return entity;
      } else {
         return null;
      }
   }

   private SnowWorld.ChunkCoords resolveChunkCoords(ChunkUploadMesh upload) {
      Vector3i position = upload.position;
      return new SnowWorld.ChunkCoords(position.x, position.y + upload.yOffset, position.z);
   }

   public void queueEvent(Runnable runnable) {
      this.queue.add(runnable);
   }

   public void addChunkColumn(List<ChunkCreator> asyncChunkCreation, int chunkX, int chunkZ) {
      long index = Index.chunk(chunkX, 0, chunkZ);
      LongList columns = (LongList)this.loadedColumns.get(index);
      if (columns == null) {
         columns = new LongArrayList();
         this.loadedColumns.put(index, columns);
      }

      for (int i = 0; i < asyncChunkCreation.size(); i++) {
         columns.add(Index.chunk(chunkX, asyncChunkCreation.get(i).getY(), chunkZ));
      }

      this.contouring.queueEvent(() -> {
         for (int ix = 0; ix < asyncChunkCreation.size(); ix++) {
            this.contouring.addChunk(asyncChunkCreation.get(ix).create());
         }
      });
   }

   public void removeChunkColumn(int chunkX, int chunkZ) {
      long index = Index.chunk(chunkX, 0, chunkZ);
      LongList columns = (LongList)this.loadedColumns.remove(index);
      if (columns != null) {
         this.contouring.queueEvent(() -> {
            for (int i = 0; i < columns.size(); i++) {
               long currentIndex = columns.getLong(i);
               this.contouring.removeChunk(currentIndex);
            }
         });
      }
   }

   public void removeAll() {
      LongList toRemove = new LongArrayList();
      ObjectIterator var2 = this.loadedColumns.values().iterator();

      while (var2.hasNext()) {
         LongList column = (LongList)var2.next();

         for (int i = 0; i < column.size(); i++) {
            toRemove.add(column.getLong(i));
         }
      }

      if (toRemove.size() > 0) {
         this.contouring.queueEvent(() -> {
            for (int ix = 0; ix < toRemove.size(); ix++) {
               long currentIndex = toRemove.getLong(ix);
               this.contouring.removeChunk(currentIndex);
            }
         });
      }
   }

   public Collection<ChunkEntity> getChunks() {
      return this.chunks.values();
   }

   public SnowBatch getSnowBatch() {
      return this.snowBatch;
   }

   public void destroy() {
      this.contouring.shutdown();
      this.contouring.join();
      Runnable event = null;

      while ((event = this.queue.poll()) != null) {
         event.run();
      }

      if (this.snowBuffers != null) {
         this.snowBuffers.vertexBuffer.close();
         this.snowBuffers.indexBuffer.close();
      }

      if (this.stagingBuffer != null) {
         this.stagingBuffer.close();
      }

      ObjectIterator var2 = this.chunks.values().iterator();

      while (var2.hasNext()) {
         ChunkEntity entity = (ChunkEntity)var2.next();
         this.snowBatch.remove(entity);
         entity.clearRenderBuffers();
      }

      this.chunks.clear();
   }

   public ShortSet getLightUpdates(long chunkIndex) {
      ShortSet lightUpdates = (ShortSet)this.fetchLightUpdates.get(chunkIndex);
      if (lightUpdates == null) {
         lightUpdates = new ShortOpenHashSet();
         this.fetchLightUpdates.put(chunkIndex, lightUpdates);
      }

      return lightUpdates;
   }

   public Long2ObjectMap<LongList> getLoadedColumns() {
      return this.loadedColumns;
   }

   public ObjectOpenHashSetReplace<BlockUpdate> getBlockUpdates() {
      return this.blockUpdates;
   }

   private static record ChunkCoords(int chunkX, int chunkY, int chunkZ) {
   }

   class LightUpdate {
      byte posX;
      byte posY;
      byte posZ;
      byte lightData;

      LightUpdate() {
         Objects.requireNonNull(SnowWorld.this);
         super();
      }
   }

   private static record PreparedUpload(ChunkUploadMesh upload, ChunkEntity entity, @Nullable ChunkCoords coords, int vertexCount, int indexCount) {
   }

   private static record SnowUberBuffers(UberGpuBuffer<ChunkEntity> vertexBuffer, UberGpuBuffer<ChunkEntity> indexBuffer) {
   }
}
