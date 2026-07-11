package net.diebuddies.physics.ocean;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.StagingBuffer;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.StagingBuffer.Uploader;
import com.mojang.blaze3d.vertex.TlsfAllocator.Allocation;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.ValkyrienSkies;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.BasicRigidBody;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.thread.OceanChunkCreator;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.OceanUniform;
import net.diebuddies.util.ObjectOpenHashSetReplace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LayerLightEventListener.DummyLightLayerEventListener;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class OceanWorld {
   private static final float BASE_OCEAN_HEIGHT = 13.0F;
   private static final float RAIN_RIPPLE_STRENGTH = 0.22F;
   private static final float GENERIC_RIPPLE_STRENGTH = 0.52F;
   private static final float GENERIC_RIPPLE_SPEED_STRENGTH = 0.12F;
   private static final float GENERIC_RIPPLE_SPEED_RADIUS_SCALE = 3.0F;
   private static final float BOAT_RIPPLE_STRENGTH = 0.52F;
   private static final float BOAT_RIPPLE_SPEED_STRENGTH = 0.12F;
   private static final float BOAT_RIPPLE_MIN_RADIUS = 1.75F;
   private static final float BOAT_RIPPLE_SPEED_RADIUS_SCALE = 3.0F;
   private int waveAnchorX = Integer.MAX_VALUE;
   private int waveAnchorZ = Integer.MAX_VALUE;
   private PhysicsWorld world;
   private final Level level;
   private ConcurrentLinkedQueue<Runnable> queue;
   private OceanProcessor processor;
   private ObjectOpenHashSetReplace<OceanBlockUpdate> blockUpdates;
   private final LayeredSurfaceQueue queuedMeshes = new LayeredSurfaceQueue();
   public Long2ObjectMap<ShortSet> lightUpdates;
   private Set<Vector3i> oceanLayerLightUpdates;
   private double rippleTime;
   private static final int OCEAN_INDEX_ALIGNMENT = 8;
   private OceanWorld.OceanUberBuffers oceanBuffers;
   private StagingBuffer stagingBuffer;
   private int oceanVertexHeapSize;
   private int oceanIndexHeapSize;
   private Short2ObjectMap<ProxyOceanLayer> oceanLayers;
   private double oceanTime;
   private double globalTime;
   private float oceanHeightMultiplier;
   private float weatherSpeedMultiplier;
   private Vector2f waterMidCoord;
   private Vector4f waterCoord;
   private Long2ObjectMap<OceanMesh> oceanMeshes;
   public DynamicFrustumBVH<OceanMesh> bvh;
   private MutableBlockPos tmp = new MutableBlockPos();
   private static final long BUDGET_NS = 1000000L;

   public OceanWorld(PhysicsWorld world, Level level) {
      this.world = world;
      this.level = level;
      this.queue = new ConcurrentLinkedQueue<>();
      this.blockUpdates = new ObjectOpenHashSetReplace<>();
      this.oceanMeshes = new Long2ObjectOpenHashMap();
      this.lightUpdates = new Long2ObjectOpenHashMap();
      this.oceanLayerLightUpdates = new ObjectOpenHashSet();
      this.oceanLayers = new Short2ObjectOpenHashMap();
      this.bvh = new DynamicFrustumBVH<>(2048);
      this.weatherSpeedMultiplier = 1.0F;
      this.oceanHeightMultiplier = 1.0F;
      TextureAtlasSprite waterTexture = Minecraft.getInstance()
         .getModelManager()
         .getBlockStateModelSet()
         .getParticleMaterial(Blocks.WATER.defaultBlockState())
         .sprite();
      this.waterCoord = new Vector4f(waterTexture.getU0(), waterTexture.getU1(), waterTexture.getV0(), waterTexture.getV1());
      this.waterMidCoord = new Vector2f(this.waterCoord.x + this.waterCoord.y, this.waterCoord.z + this.waterCoord.w).mul(0.5F);
      this.processor = new OceanProcessor(this, level.getMinSectionY(), level.getMaxSectionY(), this.waterCoord);
      this.processor.start();
   }

   public void update(double diff) {
      Runnable event = null;

      while ((event = this.queue.poll()) != null) {
         event.run();
      }

      Minecraft minecraft = Minecraft.getInstance();
      double cameraY = minecraft.gameRenderer.mainCamera().position().y();
      long start = System.nanoTime();

      do {
         OceanSurface s = this.queuedMeshes.pollClosestTo(cameraY);
         if (s == null) {
            break;
         }

         this.processNewSurface(s);
      } while (!this.queuedMeshes.isEmpty() && System.nanoTime() - start < 1000000L);
   }

   public void updateRipple(double diff) {
      this.rippleTime += diff;
      double tick = 0.025;
      if (this.rippleTime >= tick * 5.0) {
         this.rippleTime = tick * 5.0;
      }

      while (this.rippleTime >= tick) {
         this.rippleTime -= tick;
         this.updateParticles(tick);
      }
   }

   private void updateParticles(double diff) {
      ObjectIterator var3 = this.oceanLayers.values().iterator();

      while (var3.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var3.next();
         layer.update(diff);
      }
   }

   public void spawnRainRipple(int lifetime, float scale, double x, double y, double z) {
      this.queueRippleImpulse(OceanRippleImpulse.rain(x, y, z, scale, 0.22F));
   }

   public void spawnRipple(float scale, double x, double y, double z, double speed) {
      float speedFactor = (float)Math.min(Math.max(speed, 0.0), 1.5);
      float radius = Math.max(scale, speedFactor * 3.0F);
      float strength = 0.52F + speedFactor * 0.12F;
      this.queueRippleImpulse(OceanRippleImpulse.radial(x, y, z, radius, strength));
   }

   public void spawnBoatRipple(double x, double y, double z, double speed) {
      float speedFactor = (float)Math.min(Math.max(speed, 0.0), 2.0);
      float radius = Math.max(1.75F, speedFactor * 3.0F);
      float strength = 0.52F + speedFactor * 0.12F;
      this.queueRippleImpulse(OceanRippleImpulse.boat(x, y, z, radius, strength));
   }

   public void queueRippleImpulse(OceanRippleImpulse impulse) {
      ProxyOceanLayer layer = this.getOceanLayer(impulse.x, impulse.y, impulse.z);
      if (layer != null) {
         layer.addRippleImpulse(impulse);
      }
   }

   public float getOceanTime() {
      return (float)this.oceanTime;
   }

   public float getGlobalTime() {
      return (float)this.globalTime;
   }

   public int getOceanNormalIterationCount() {
      return 13 + (int)(ConfigClient.oceanDetail * 35.0F);
   }

   public OceanUniform createRenderUniform(float rippleRange) {
      return new OceanUniform(
         this.getOceanTime(),
         this.getGlobalTime(),
         this.getOceanNormalIterationCount(),
         this.getOceanHeight(),
         ConfigClient.oceanHorizontalWaveScale,
         rippleRange,
         ConfigClient.oceanFoamAmount,
         ConfigClient.oceanFoamOpacity
      );
   }

   private void applyBlockUpdates(List<Runnable> events) {
      if (!this.blockUpdates.isEmpty()) {
         List<OceanBlockUpdate> updates = new ObjectArrayList(this.blockUpdates);
         events.add(() -> {
            for (OceanBlockUpdate update : updates) {
               BlockPos pos = update.pos;
               byte state = update.state;
               int rx = pos.getX();
               int ry = pos.getY();
               int rz = pos.getZ();
               IChunk<?> chunk = this.processor.getChunkWorldPos(rx, ry, rz);
               if (chunk != null) {
                  int lx = rx & 15;
                  int ly = ry & 15;
                  int lz = rz & 15;
                  byte data = chunk.getData(this.processor, lx, ly, lz);
                  if (data != state) {
                     chunk.setData(lx, ly, lz, state);
                     this.processor.blockChanged(rx, ry, rz, data, state);
                  }
               }
            }
         });
         this.blockUpdates.clear();
      }
   }

   private void applyLightUpdates(List<Runnable> events) {
      if (!this.lightUpdates.isEmpty()) {
         List<OceanWorld.LightUpdate> asyncUpdates = new ObjectArrayList();
         Iterator<Entry<ShortSet>> it = this.lightUpdates.long2ObjectEntrySet().iterator();
         LevelLightEngine levelLightEngine = this.level.getLightEngine();

         while (it.hasNext()) {
            Entry<ShortSet> entry = it.next();
            long chunkIndex = entry.getLongKey();
            int x = SectionPos.x(chunkIndex);
            int y = SectionPos.y(chunkIndex);
            int z = SectionPos.z(chunkIndex);
            ShortSet positions = (ShortSet)entry.getValue();
            LevelChunk chunk = this.level.getChunk(x, z);
            if (positions.isEmpty() || chunk == null) {
               it.remove();
            } else if (levelLightEngine.lightOnInColumn(SectionPos.getZeroNode(chunk.getPos().x(), chunk.getPos().z()))) {
               ShortIterator blockIt = positions.iterator();

               while (blockIt.hasNext()) {
                  short localPos = blockIt.nextShort();
                  byte lx = (byte)(localPos >> 8 & 15);
                  byte ly = (byte)(localPos >> 4 & 15);
                  byte lz = (byte)(localPos & 15);
                  int wx = x * 16 + lx;
                  int wy = y * 16 + ly;
                  int wz = z * 16 + lz;
                  if (wy >= this.level.getMinY() && wy < this.level.getMaxY()) {
                     this.tmp.set(wx, wy, wz);
                     int sky = net.diebuddies.math.Math.clamp(this.level.getBrightness(LightLayer.SKY, this.tmp), 0, 15);
                     int block = net.diebuddies.math.Math.clamp(this.level.getBrightness(LightLayer.BLOCK, this.tmp), 0, 15);
                     OceanWorld.LightUpdate update = new OceanWorld.LightUpdate();
                     update.posX = wx;
                     update.posY = wy;
                     update.posZ = wz;
                     update.lightData = (byte)(sky << 4 | block);
                     asyncUpdates.add(update);
                     blockIt.remove();
                  } else {
                     blockIt.remove();
                  }
               }

               if (positions.isEmpty()) {
                  it.remove();
               }
            }
         }

         if (!asyncUpdates.isEmpty()) {
            events.add(() -> {
               for (OceanWorld.LightUpdate updatex : asyncUpdates) {
                  this.processor.updateLight(updatex.posX, updatex.posY, updatex.posZ, updatex.lightData);
               }
            });
         }
      }
   }

   public double calculateYOffset(double x, double y, double z) {
      double maxOffset = 0.0;
      double maxMagnitude = 0.0;
      ObjectIterator var11 = this.oceanLayers.values().iterator();

      while (var11.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var11.next();
         double offset = layer.calculateYOffset(this, x, y, z);
         double magnitude = Math.abs(offset);
         if (magnitude > maxMagnitude) {
            maxMagnitude = magnitude;
            maxOffset = offset;
         }
      }

      return maxOffset;
   }

   @Nullable
   public Vector3d calculateWaveForce(double x, double y, double z, @Nullable BasicRigidBody body, boolean cacheUpdate) {
      Vector3d scratch = new Vector3d();
      if (body == null) {
         return this.findMaxWaveForce(x, y, z, this.oceanLayers.values(), scratch, null);
      } else if (!cacheUpdate) {
         return this.findMaxWaveForce(x, y, z, body.getCachedLayers(), scratch, null);
      } else {
         body.clearCachedLayers();
         return this.findMaxWaveForce(x, y, z, this.oceanLayers.values(), scratch, layer -> body.addCachedLayer(layer));
      }
   }

   @Nullable
   public Vector3d calculateWaveForce(double x, double y, double z) {
      return this.calculateWaveForce(x, y, z, null, false);
   }

   @Nullable
   private Vector3d findMaxWaveForce(
      double x, double y, double z, Iterable<ProxyOceanLayer> layers, Vector3d scratch, @Nullable LayerCacheHook cacheHook
   ) {
      Vector3d best = null;
      double bestMagnitude = 0.0;

      for (ProxyOceanLayer layer : layers) {
         ProxyOceanLayer.WaveForceResult result = layer.calculateWaveNormal(this, x, y, z, scratch);
         if (cacheHook != null && result != ProxyOceanLayer.WaveForceResult.OUTSIDE) {
            cacheHook.onMaybeCache(layer);
         }

         if (result == ProxyOceanLayer.WaveForceResult.INSIDE) {
            double magnitude = scratch.lengthSquared();
            if (best == null || magnitude > bestMagnitude) {
               bestMagnitude = magnitude;
               if (best == null) {
                  best = new Vector3d();
               }

               best.set(scratch);
            }
         }
      }

      return best;
   }

   public boolean isInsideOceanWater(double x, double y, double z) {
      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         if (layer.isInsideOceanWater(this, x, y, z)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInsideOceanRange(double x, double y, double z) {
      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         if (layer.isInsideTextureOceanRange(this, x, y, z)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public ProxyOceanLayer getOceanLayer(double x, double y, double z) {
      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         if (layer.isInsideTextureOceanRangeNoWarp(this, x, y, z)) {
            return layer;
         }
      }

      return null;
   }

   @Nullable
   public ProxyOceanLayer getOceanLayer(short layerY) {
      return (ProxyOceanLayer)this.oceanLayers.get(layerY);
   }

   private void applyOceanLayerLightUpdates(List<Runnable> events) {
      if (!this.oceanLayerLightUpdates.isEmpty()) {
         Iterator<Vector3i> it = this.oceanLayerLightUpdates.iterator();
         List<OceanWorld.LayerLightUpdate> asyncUpdates = new ObjectArrayList();
         LevelLightEngine lightEngine = this.level.getLightEngine();
         LayerLightEventListener blockLightListener = lightEngine.getLayerListener(LightLayer.BLOCK);
         LayerLightEventListener skyLightListener = lightEngine.getLayerListener(LightLayer.SKY);
         LevelLightEngine levelLightEngine = this.level.getLightEngine();

         while (it.hasNext()) {
            Vector3i chunkPosExceptYWorldPos = it.next();
            LevelChunk chunk = this.level.getChunk(chunkPosExceptYWorldPos.x, chunkPosExceptYWorldPos.z);
            if (chunk == null) {
               it.remove();
            } else if (levelLightEngine.lightOnInColumn(SectionPos.getZeroNode(chunk.getPos().x(), chunk.getPos().z()))) {
               int worldX = chunkPosExceptYWorldPos.x * 16;
               int worldZ = chunkPosExceptYWorldPos.z * 16;
               SectionPos pos = SectionPos.of(
                  SectionPos.blockToSectionCoord(worldX), SectionPos.blockToSectionCoord(chunkPosExceptYWorldPos.y + 1), SectionPos.blockToSectionCoord(worldZ)
               );
               DataLayer layerBlock = blockLightListener.getDataLayerData(pos);
               DataLayer layerSky = skyLightListener.getDataLayerData(pos);
               int topSectionY = this.level.getMaxSectionY() - 1;
               if (layerBlock != null) {
                  while (layerSky == null && pos.y() + 1 < topSectionY) {
                     pos = SectionPos.of(pos.x(), pos.y() + 1, pos.z());
                     layerSky = skyLightListener.getDataLayerData(pos);
                  }

                  if (layerSky != null || skyLightListener instanceof DummyLightLayerEventListener) {
                     int relativeY = SectionPos.sectionRelative(chunkPosExceptYWorldPos.y + 1);
                     OceanWorld.LayerLightUpdate layerUpdate = new OceanWorld.LayerLightUpdate();
                     layerUpdate.chunkX = chunkPosExceptYWorldPos.x;
                     layerUpdate.chunkZ = chunkPosExceptYWorldPos.z;
                     layerUpdate.layerY = (short)chunkPosExceptYWorldPos.y;
                     if (layerSky == null) {
                        int sky = 0;

                        for (int zo = 0; zo < 16; zo++) {
                           int zindex = zo << 4;

                           for (int xo = 0; xo < 16; xo++) {
                              int block = Math.min(15, layerBlock.get(xo, relativeY, zo));
                              layerUpdate.lightData[zindex + xo] = (byte)(sky << 4 | block);
                           }
                        }
                     } else {
                        for (int zo = 0; zo < 16; zo++) {
                           int zindex = zo << 4;

                           for (int xo = 0; xo < 16; xo++) {
                              int sky = Math.min(15, layerSky.get(xo, relativeY, zo));
                              int block = Math.min(15, layerBlock.get(xo, relativeY, zo));
                              layerUpdate.lightData[zindex + xo] = (byte)(sky << 4 | block);
                           }
                        }
                     }

                     asyncUpdates.add(layerUpdate);
                     it.remove();
                  }
               }
            }
         }

         if (!asyncUpdates.isEmpty()) {
            events.add(() -> {
               for (OceanWorld.LayerLightUpdate update : asyncUpdates) {
                  this.processor.updateLayerLight(update.chunkX, update.layerY, update.chunkZ, update.lightData);
               }
            });
         }
      }
   }

   public void computeEntityOffset(
      Matrix4f transformation,
      @Nullable Matrix3f normal,
      Level level,
      Entity entity,
      double x,
      double y,
      double z,
      double offsetX,
      double offsetY,
      double offsetZ,
      float renderPercent
   ) {
      Entity vehicle = entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null && ValkyrienSkies.hasShipMount(entity) != null) {
         ValkyrienSkies.doEntityOnShipTransformation(transformation, entity, renderPercent);
      } else {
         EntityOcean entityOcean = (EntityOcean)entity;
         double yOffset = ((EntityOcean)entity).getPhysicsYOffset(renderPercent);
         transformation.translate(0.0F, (float)yOffset, 0.0F);
         if (entity instanceof AbstractBoat || vehicle != null && vehicle instanceof AbstractBoat) {
            float actualYRot = 0.0F;
            if (entity instanceof LivingEntity living) {
               actualYRot = Mth.rotLerp(renderPercent, living.yBodyRotO, living.yBodyRot);
            } else {
               actualYRot = entity.getViewYRot(renderPercent);
            }

            float currentYRot = (float)(-Math.toRadians((double)(actualYRot - (float) Math.PI)));
            double forwardZ = Math.cos((double)currentYRot);
            double forwardX = Math.sin((double)currentYRot);
            double leftZ = -forwardX;
            double roll = entityOcean.getPhysicsRoll(renderPercent);
            double pitch = entityOcean.getPhysicsPitch(renderPercent);
            float diffX = 0.0F;
            float diffY = 0.375F;
            float diffZ = 0.0F;
            float diffRot = 0.0F;
            if (!(entity instanceof AbstractBoat)) {
               double ex = Mth.lerp((double)renderPercent, entity.xo, entity.getX());
               double ey = Mth.lerp((double)renderPercent, entity.yo, entity.getY());
               double ez = Mth.lerp((double)renderPercent, entity.zo, entity.getZ());
               double bx = Mth.lerp((double)renderPercent, vehicle.xo, vehicle.getX());
               double by = Mth.lerp((double)renderPercent, vehicle.yo, vehicle.getY());
               double bz = Mth.lerp((double)renderPercent, vehicle.zo, vehicle.getZ());
               diffX = (float)((double)diffX + (bx - ex));
               diffY = (float)((double)diffY + (by - ey));
               diffZ = (float)((double)diffZ + (bz - ez));
            }

            if (vehicle != null && vehicle instanceof AbstractBoat) {
               diffRot = vehicle.getViewYRot(renderPercent) - actualYRot;
            }

            float ox = (float)(x - offsetX) + diffX;
            float oy = (float)(y - offsetY) + diffY;
            float oz = (float)(z - offsetZ) + diffZ;
            transformation.translate(ox, oy, oz);
            transformation.rotate(Axis.YP.rotationDegrees(-diffRot));
            transformation.rotate(Axis.of(new Vector3f((float)forwardX, 0.0F, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
            transformation.rotate(Axis.of(new Vector3f((float)forwardZ, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
            if (normal != null) {
               normal.rotate(Axis.of(new Vector3f((float)forwardX, 0.0F, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
               normal.rotate(Axis.of(new Vector3f((float)forwardZ, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
            }

            transformation.rotate(Axis.YP.rotationDegrees(diffRot));
            transformation.translate(-ox, -oy, -oz);
         }
      }
   }

   public void extractEntityOffset(OceanRenderState renderState, Level level, Entity entity, float renderPercent) {
      Entity vehicle = entity.getVehicle();
      renderState.valkyrienPatch = false;
      renderState.isInBoat = false;
      if (StarterClient.valkyrienSkies && vehicle == null && ValkyrienSkies.hasShipMount(entity) != null) {
         ValkyrienSkies.extractEntityOnShipTransformation(renderState, entity, renderPercent);
      } else {
         EntityOcean entityOcean = (EntityOcean)entity;
         double yOffset = ((EntityOcean)entity).getPhysicsYOffset(renderPercent);
         renderState.yOffset = (float)yOffset;
         if (entity instanceof AbstractBoat || vehicle != null && vehicle instanceof AbstractBoat) {
            float actualYRot = 0.0F;
            if (entity instanceof LivingEntity living) {
               actualYRot = Mth.rotLerp(renderPercent, living.yBodyRotO, living.yBodyRot);
            } else {
               actualYRot = entity.getViewYRot(renderPercent);
            }

            float currentYRot = (float)(-Math.toRadians((double)(actualYRot - (float) Math.PI)));
            double forwardZ = Math.cos((double)currentYRot);
            double forwardX = Math.sin((double)currentYRot);
            double roll = entityOcean.getPhysicsRoll(renderPercent);
            double pitch = entityOcean.getPhysicsPitch(renderPercent);
            float diffX = 0.0F;
            float diffY = 0.375F;
            float diffZ = 0.0F;
            float diffRot = 0.0F;
            if (!(entity instanceof AbstractBoat)) {
               double ex = Mth.lerp((double)renderPercent, entity.xo, entity.getX());
               double ey = Mth.lerp((double)renderPercent, entity.yo, entity.getY());
               double ez = Mth.lerp((double)renderPercent, entity.zo, entity.getZ());
               double bx = Mth.lerp((double)renderPercent, vehicle.xo, vehicle.getX());
               double by = Mth.lerp((double)renderPercent, vehicle.yo, vehicle.getY());
               double bz = Mth.lerp((double)renderPercent, vehicle.zo, vehicle.getZ());
               diffX = (float)((double)diffX + (bx - ex));
               diffY = (float)((double)diffY + (by - ey));
               diffZ = (float)((double)diffZ + (bz - ez));
            }

            if (vehicle != null && vehicle instanceof AbstractBoat) {
               diffRot = vehicle.getViewYRot(renderPercent) - actualYRot;
            }

            renderState.ox = diffX;
            renderState.oy = diffY;
            renderState.oz = diffZ;
            renderState.diffRot = diffRot;
            renderState.forwardZ = forwardZ;
            renderState.forwardX = forwardX;
            renderState.roll = roll;
            renderState.pitch = pitch;
            renderState.isInBoat = true;
         }
      }
   }

   public double computeYOffset(Level level, Entity entity, float renderPercent) {
      Entity vehicle = entity.getVehicle();
      double wx;
      double wy;
      double wz;
      if (vehicle != null) {
         wx = Mth.lerp((double)renderPercent, vehicle.xOld, vehicle.getX());
         wy = Mth.lerp((double)renderPercent, vehicle.yOld, vehicle.getY());
         wz = Mth.lerp((double)renderPercent, vehicle.zOld, vehicle.getZ());
      } else {
         wx = Mth.lerp((double)renderPercent, entity.xOld, entity.getX());
         wy = Mth.lerp((double)renderPercent, entity.yOld, entity.getY());
         wz = Mth.lerp((double)renderPercent, entity.zOld, entity.getZ());
      }

      return this.calculateYOffset(wx, wy, wz);
   }

   public double computeYOffset(Level level, Entity entity) {
      return this.calculateYOffset(entity.getX(), entity.getY(), entity.getZ());
   }

   public void loadOceanLayerLights(int x, short layerPosY, int z) {
      this.oceanLayerLightUpdates.add(new Vector3i(x, layerPosY, z));
   }

   public void replaceOceanMeshes(List<OceanSurface> generatedMeshes) {
      this.queuedMeshes.addAll(generatedMeshes);
   }

   private void processNewSurface(OceanSurface oceanSurface) {
      OceanSurface oldSurface = oceanSurface.oceanLayer.getOceanSurface();
      boolean initializingSurface = oldSurface == null;
      if (initializingSurface) {
         oceanSurface.oceanLayer.setOceanSurface(oceanSurface);
      }

      OceanSurface usedSurface = oceanSurface.oceanLayer.getOceanSurface();
      if (oceanSurface.removeAllMeshes) {
         ObjectIterator var15 = usedSurface.meshes.long2ObjectEntrySet().iterator();

         while (var15.hasNext()) {
            Entry<OceanMesh> entry = (Entry<OceanMesh>)var15.next();
            long index = entry.getLongKey();
            OceanMesh mesh = this.removeOceanMesh(index);
            if (mesh != null) {
               this.removeOceanMeshBuffers(mesh);
               mesh.destroy();
            }
         }

         usedSurface.meshes.clear();
         usedSurface.set(oceanSurface);
      } else {
         Long2ObjectMap<OceanMesh> stagedAdds = new Long2ObjectOpenHashMap();
         LongSet removeLater = new LongOpenHashSet();
         ObjectIterator stagedIterator = oceanSurface.meshes.long2ObjectEntrySet().iterator();

         while (stagedIterator.hasNext()) {
            Entry<OceanMesh> entry = (Entry<OceanMesh>)stagedIterator.next();
            long index = entry.getLongKey();
            OceanMesh newMesh = (OceanMesh)entry.getValue();
            if (newMesh.mesh == null) {
               removeLater.add(index);
            } else if (this.stageOceanMeshUpload(newMesh)) {
               stagedAdds.put(index, newMesh);
            } else {
               newMesh.destroy();
               if (initializingSurface) {
                  usedSurface.removeOceanMesh(index);
               }
            }
         }

         if (!stagedAdds.isEmpty()) {
            this.uploadGlobalGeomBuffersToGPU();
         }

         LongIterator stagedIteratorx = stagedAdds.keySet().iterator();

         while (stagedIteratorx.hasNext()) {
            long index = stagedIteratorx.nextLong();
            OceanMesh newMesh = (OceanMesh)stagedAdds.get(index);
            if (!this.finalizeOceanMeshUpload(newMesh)) {
               this.cleanupStagedOceanMesh(newMesh);
               newMesh.destroy();
               stagedIteratorx.remove();
               if (initializingSurface) {
                  usedSurface.removeOceanMesh(index);
               }
            }
         }

         LongIterator removeIterator = removeLater.longIterator();

         while (removeIterator.hasNext()) {
            long index = removeIterator.nextLong();
            OceanMesh oldMesh = this.removeOceanMesh(index);
            if (oldMesh != null) {
               this.removeOceanMeshBuffers(oldMesh);
               oldMesh.destroy();
            }

            usedSurface.removeOceanMesh(index);
         }

         ObjectIterator var22 = stagedAdds.long2ObjectEntrySet().iterator();

         while (var22.hasNext()) {
            Entry<OceanMesh> entry = (Entry<OceanMesh>)var22.next();
            long index = entry.getLongKey();
            OceanMesh newMesh = (OceanMesh)entry.getValue();
            OceanMesh oldMesh = this.replaceOceanMesh(index, newMesh);
            if (oldMesh != null) {
               this.removeOceanMeshBuffers(oldMesh);
               oldMesh.destroy();
            }

            usedSurface.addOceanMesh(index, newMesh);
         }

         usedSurface.set(oceanSurface);
      }
   }

   private OceanMesh replaceOceanMesh(long index, OceanMesh mesh) {
      AABB3D modelBoundingBox = mesh.aabb;
      Vector3d start = modelBoundingBox.start;
      Vector3d end = modelBoundingBox.end;
      float halfHeight = Math.max(0.5F, this.getOceanHeight() * 0.5F * mesh.maxInfluence);
      this.bvh.add(new AABB3D(new Vector3d(start).add(0.0, (double)(-halfHeight), 0.0), new Vector3d(end).add(0.0, (double)halfHeight, 0.0)), mesh);
      OceanMesh oldMesh = (OceanMesh)this.oceanMeshes.put(index, mesh);
      if (oldMesh != null) {
         this.bvh.remove(oldMesh);
      }

      return oldMesh;
   }

   private OceanMesh removeOceanMesh(long index) {
      OceanMesh mesh = (OceanMesh)this.oceanMeshes.remove(index);
      if (mesh != null) {
         this.bvh.remove(mesh);
      }

      return mesh;
   }

   private boolean stageOceanMeshUpload(OceanMesh oceanMesh) {
      this.createRenderObjects();
      RawMesh rawMesh = oceanMesh.mesh;
      if (rawMesh == null) {
         return false;
      } else {
         oceanMesh.uploadedIndexCount = rawMesh.indexData.capacity() / 2;
         boolean success = this.addOceanMeshToUberBuffer(oceanMesh, rawMesh.data, rawMesh.indexData);
         if (!success && RenderSystem.isOnRenderThread()) {
            this.uploadGlobalGeomBuffersToGPU();
            success = this.addOceanMeshToUberBuffer(oceanMesh, rawMesh.data, rawMesh.indexData);
         }

         if (!success) {
            StarterClient.logger.info("messed up upload");
            this.cleanupStagedOceanMesh(oceanMesh);
         }

         return success;
      }
   }

   private boolean finalizeOceanMeshUpload(OceanMesh oceanMesh) {
      if (!oceanMesh.hasRenderBuffers()) {
         return false;
      } else {
         oceanMesh.discardCpuMesh();
         return true;
      }
   }

   private void cleanupStagedOceanMesh(OceanMesh oceanMesh) {
      oceanMesh.clearRenderBuffers();
      if (this.oceanBuffers != null) {
         this.oceanBuffers.vertexBuffer.removeAllocation(oceanMesh);
         this.oceanBuffers.indexBuffer.removeAllocation(oceanMesh);
      }
   }

   private boolean addOceanMeshToUberBuffer(OceanMesh oceanMesh, @Nullable ByteBuffer vertexBuffer, @Nullable ByteBuffer indexBuffer) {
      boolean success = true;
      OceanWorld.OceanUberBuffers buffers = this.getOceanBuffers();
      if (vertexBuffer != null
         && vertexBuffer.remaining() > 0
         && !buffers.vertexBuffer.addAllocation(oceanMesh, mesh -> this.cacheVertexRenderBuffer(buffers, mesh), vertexBuffer)) {
         this.uploadGlobalGeomBuffersToGPU();
         success &= buffers.vertexBuffer.addAllocation(oceanMesh, mesh -> this.cacheVertexRenderBuffer(buffers, mesh), vertexBuffer);
      }

      if (indexBuffer != null
         && indexBuffer.remaining() > 0
         && !buffers.indexBuffer.addAllocation(oceanMesh, mesh -> this.cacheIndexRenderBuffer(buffers, mesh), indexBuffer)) {
         this.uploadGlobalGeomBuffersToGPU();
         success &= buffers.indexBuffer.addAllocation(oceanMesh, mesh -> this.cacheIndexRenderBuffer(buffers, mesh), indexBuffer);
      }

      if (!success) {
         this.cleanupStagedOceanMesh(oceanMesh);
      }

      return success;
   }

   private void cacheVertexRenderBuffer(OceanWorld.OceanUberBuffers buffers, OceanMesh oceanMesh) {
      Allocation allocation = buffers.vertexBuffer.getAllocation(oceanMesh);
      if (allocation == null) {
         oceanMesh.vertexBuffer = null;
         oceanMesh.vertexBufferOffset = 0L;
      } else {
         oceanMesh.vertexBuffer = buffers.vertexBuffer.getGpuBuffer(allocation);
         oceanMesh.vertexBufferOffset = allocation.getOffsetFromHeap();
      }
   }

   private void cacheIndexRenderBuffer(OceanWorld.OceanUberBuffers buffers, OceanMesh oceanMesh) {
      Allocation allocation = buffers.indexBuffer.getAllocation(oceanMesh);
      if (allocation == null) {
         oceanMesh.indexBuffer = null;
         oceanMesh.indexBufferOffset = 0L;
      } else {
         oceanMesh.indexBuffer = buffers.indexBuffer.getGpuBuffer(allocation);
         oceanMesh.indexBufferOffset = allocation.getOffsetFromHeap();
      }
   }

   private void removeOceanMeshBuffers(OceanMesh oceanMesh) {
      oceanMesh.clearRenderBuffers();
      if (this.oceanBuffers != null) {
         this.oceanBuffers.vertexBuffer.removeAllocation(oceanMesh);
         this.oceanBuffers.indexBuffer.removeAllocation(oceanMesh);
      }
   }

   private OceanWorld.OceanUberBuffers getOceanBuffers() {
      this.createRenderObjects();
      return this.oceanBuffers;
   }

   public void uploadGlobalGeomBuffersToGPU() {
      if (this.oceanBuffers != null && this.stagingBuffer != null) {
         GpuDevice device = RenderSystem.getDevice();
         Uploader uploader = this.stagingBuffer.startUploading(device.createCommandEncoder());

         try {
            this.oceanBuffers.vertexBuffer.uploadStagedAllocations(device, uploader);
            this.oceanBuffers.indexBuffer.uploadStagedAllocations(device, uploader);
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

   public ShortSet getLightUpdates(long chunkIndex) {
      ShortSet lightUpdates = (ShortSet)this.lightUpdates.get(chunkIndex);
      if (lightUpdates == null) {
         lightUpdates = new ShortOpenHashSet();
         this.lightUpdates.put(chunkIndex, lightUpdates);
      }

      return lightUpdates;
   }

   public void queueEvent(Runnable runnable) {
      this.queue.add(runnable);
   }

   public Short2ObjectMap<ProxyOceanLayer> getOceanLayers() {
      return this.oceanLayers;
   }

   public Long2ObjectMap<OceanMesh> getOceanMeshes() {
      return this.oceanMeshes;
   }

   public ObjectOpenHashSetReplace<OceanBlockUpdate> getBlockUpdates() {
      return this.blockUpdates;
   }

   public float getOceanHeight() {
      return 13.0F * ConfigClient.oceanWaveHeightMultiplier * this.oceanHeightMultiplier;
   }

   public static float getMaxOceanHeight() {
      float storminess = ConfigClient.oceanWeatherClear;
      storminess += ConfigClient.oceanWeatherRain;
      storminess += ConfigClient.oceanWeatherThunder;
      float oceanHeightMultiplier = 0.7F + storminess * 0.4F;
      return 13.0F * ConfigClient.oceanWaveHeightMultiplier * oceanHeightMultiplier;
   }

   public void addChunkColumn(List<OceanChunkCreator> asyncChunkCreation, int chunkX, int chunkZ) {
      int worldPosX = chunkX * 16;
      int worldPosZ = chunkZ * 16;
      if (Math.abs(this.waveAnchorX - worldPosX) > 36000 || Math.abs(this.waveAnchorZ - worldPosZ) > 36000) {
         this.waveAnchorX = worldPosX;
         this.waveAnchorZ = worldPosZ;
      }

      this.processor.queueEvent(() -> {
         List<OceanChunk> chunkColumn = new ObjectArrayList();

         for (int i = 0; i < asyncChunkCreation.size(); i++) {
            chunkColumn.add(asyncChunkCreation.get(i).create());
         }

         this.processor.addChunkColumn(chunkColumn);
      });
   }

   public void removeChunkColumn(int chunkX, int chunkZ) {
      this.processor.queueEvent(() -> this.processor.removeChunkColumn(chunkX, chunkZ));
   }

   public void removeAll() {
      this.processor.queueEvent(() -> this.processor.removeAll());
   }

   public int getWaveAnchorX() {
      return this.waveAnchorX;
   }

   public int getWaveAnchorZ() {
      return this.waveAnchorZ;
   }

   public Vector2f getWaterMidCoord() {
      return this.waterMidCoord;
   }

   public Vector4f getWaterUVCoord() {
      return this.waterCoord;
   }

   public Level getLevel() {
      return this.level;
   }

   public void removeOceanLayer(short layerPosY) {
      ProxyOceanLayer layer = (ProxyOceanLayer)this.oceanLayers.remove(layerPosY);
      if (layer != null) {
         layer.destroy();
      }
   }

   public void clearOceanLayers() {
      ObjectIterator var1 = this.oceanLayers.values().iterator();

      while (var1.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var1.next();
         layer.destroy();
      }

      this.oceanLayers.clear();
   }

   public static void createWaterSplash(
      Level level, SimpleParticleType type, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount
   ) {
      for (int i = 0; i < amount; i++) {
         double angle = (Math.PI * 2) * (double)net.diebuddies.math.Math.random();
         double radius = 1.0;
         double x = radius * Math.cos(angle);
         double z = radius * Math.sin(angle);
         level.addAlwaysVisibleParticle(
            type,
            true,
            wx + x * randomOffset + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
            wy + (double)net.diebuddies.math.Math.random() * 0.4,
            wz + z * randomOffset + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
            x * 0.2 * intensity + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3 + vx,
            0.181 + vy,
            z * 0.2 * intensity + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3 + vz
         );
      }
   }

   public static void createWaterSplash(
      Level level, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount
   ) {
      createWaterSplash(level, WeatherEffects.PHYSICS_SPLASH, wx, wy, wz, vx, vy, vz, randomOffset, intensity, amount);
   }

   public static void createExplosionWaterSplash(
      Level level, double wx, double wy, double wz, double vx, double vy, double vz, double randomOffset, double intensity, int amount
   ) {
      createWaterSplash(level, WeatherEffects.PHYSICS_SPLASH_EXPLOSION, wx, wy, wz, vx, vy, vz, randomOffset, intensity, amount);
   }

   private void createRenderObjects() {
      if (this.oceanBuffers == null && this.stagingBuffer == null) {
         VertexFormat format = this.getVertexFormat();
         this.oceanVertexHeapSize = 1048576 * format.getVertexSize() * 2 / 2;
         this.oceanIndexHeapSize = 3145728;
         int stagingBufferSize = Math.min(this.oceanVertexHeapSize + this.oceanIndexHeapSize, 6291456);
         GpuDevice gpuDevice = RenderSystem.getDevice();
         this.stagingBuffer = StagingBuffer.create("Ocean", gpuDevice, stagingBufferSize);
         this.oceanBuffers = new OceanWorld.OceanUberBuffers(
            new UberGpuBuffer("Ocean Vertex", 32, this.oceanVertexHeapSize, format.getVertexSize(), this.stagingBuffer),
            new UberGpuBuffer("Ocean Index", 64, this.oceanIndexHeapSize, 8, this.stagingBuffer)
         );
      }
   }

   public VertexFormat getVertexFormat() {
      return StarterClient.iris() ? Iris.remapFormat(PhysicsShaders.OCEAN_FORMAT) : PhysicsShaders.OCEAN_FORMAT;
   }

   public void destroy() {
      this.processor.shutdown();
      this.processor.join();
      Runnable event = null;

      while ((event = this.queue.poll()) != null) {
         event.run();
      }

      for (OceanSurface surface : this.queuedMeshes.drainAll()) {
         ObjectIterator var4 = surface.getMeshes().values().iterator();

         while (var4.hasNext()) {
            OceanMesh mesh = (OceanMesh)var4.next();
            mesh.destroy();
         }
      }

      ObjectIterator var7 = this.oceanLayers.values().iterator();

      while (var7.hasNext()) {
         ProxyOceanLayer layer = (ProxyOceanLayer)var7.next();
         layer.destroy();
      }

      var7 = this.oceanMeshes.values().iterator();

      while (var7.hasNext()) {
         OceanMesh oceanMesh = (OceanMesh)var7.next();
         this.removeOceanMeshBuffers(oceanMesh);
         oceanMesh.destroy();
      }

      if (this.oceanBuffers != null) {
         this.oceanBuffers.vertexBuffer.close();
         this.oceanBuffers.indexBuffer.close();
      }

      if (this.stagingBuffer != null) {
         this.stagingBuffer.close();
      }

      this.oceanMeshes.clear();
   }

   @FunctionalInterface
   private interface LayerCacheHook {
      void onMaybeCache(ProxyOceanLayer var1);
   }

   private class LayerLightUpdate {
      int chunkX;
      int chunkZ;
      short layerY;
      byte[] lightData;

      public LayerLightUpdate() {
         Objects.requireNonNull(OceanWorld.this);
         super();
         this.lightData = new byte[256];
      }
   }

   private class LightUpdate {
      int posX;
      int posY;
      int posZ;
      byte lightData;

      private LightUpdate() {
         Objects.requireNonNull(OceanWorld.this);
         super();
      }
   }

   private static record OceanUberBuffers(UberGpuBuffer<OceanMesh> vertexBuffer, UberGpuBuffer<OceanMesh> indexBuffer) {
   }
}
