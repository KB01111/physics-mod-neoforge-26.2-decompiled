package net.diebuddies.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanRippleImpulse;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.RippleUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class OceanRippleRenderer {
   private static final int RIPPLE_RESOLUTION = 2048;
   private static final float SIMULATION_STEP_SECONDS = 0.016666668F;
   private static final int MAX_SIMULATION_STEPS_PER_FRAME = 5;
   private static final int ACTIVE_SIMULATION_STEPS_AFTER_IMPULSE = 480;
   private static final int ACTIVE_RIPPLE_LAYER_COUNT = 3;
   private static final float SIMULATION_DAMPING = 0.991F;
   private static final float SIMULATION_WAVE_SPEED_BLOCKS_PER_SECOND = 5.0F;
   private static final float SIMULATION_MAX_PROPAGATION = 0.48F;
   private static final float SIMULATION_BORDER_DAMPING = 0.045F;
   private final MainRenderer mainRenderer;
   @Nullable
   private SimpleColorRenderTarget rippleImpulseTarget;
   private OceanRippleRenderer.RippleSimulationSlot[] rippleSimulationSlots;
   @Nullable
   private InstancedRenderer rippleImpulseInstances;
   private int slotUseCounter;
   private final ShortSet selectedRippleLayers = new ShortOpenHashSet(3);
   private final short[] selectedLayerCandidates = new short[3];
   private final double[] selectedLayerDistances = new double[3];
   private final float rippleRange = 32.0F;
   private final Matrix4f projectionMatrix;
   private final Matrix4f viewMatrix;
   private final Vector3d camera = new Vector3d(0.0, 0.0, 0.0);

   public OceanRippleRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.projectionMatrix = new Matrix4f().ortho(-32.0F, 32.0F, 32.0F, -32.0F, -100.0F, 100.0F);
      this.viewMatrix = new Matrix4f().rotateX((float)Math.toRadians(90.0));
   }

   public void selectActiveLayers(OceanWorld oceanWorld, ShortList visibleLayerPositions, Vec3 cameraPos) {
      this.selectedRippleLayers.clear();
      if (ConfigClient.oceanRipples && !visibleLayerPositions.isEmpty()) {
         int selectedCount = 0;
         int farthestIndex = -1;
         double farthestDistance = -1.0;

         for (int i = 0; i < visibleLayerPositions.size(); i++) {
            ProxyOceanLayer layer = oceanWorld.getOceanLayer(visibleLayerPositions.getShort(i));
            if (this.isRippleLayerActive(layer)) {
               double distance = distanceToCameraSqr(layer, cameraPos);
               if (selectedCount < 3) {
                  this.selectedLayerCandidates[selectedCount] = layer.getLayerPosY();
                  this.selectedLayerDistances[selectedCount] = distance;
                  if (distance > farthestDistance) {
                     farthestDistance = distance;
                     farthestIndex = selectedCount;
                  }

                  selectedCount++;
               } else if (distance < farthestDistance && farthestIndex >= 0) {
                  this.selectedLayerCandidates[farthestIndex] = layer.getLayerPosY();
                  this.selectedLayerDistances[farthestIndex] = distance;
                  farthestIndex = this.farthestCandidateIndex(selectedCount);
                  farthestDistance = this.selectedLayerDistances[farthestIndex];
               }
            }
         }

         for (int ix = 0; ix < selectedCount; ix++) {
            this.selectedRippleLayers.add(this.selectedLayerCandidates[ix]);
         }

         this.releaseUnselectedSlots();
      } else {
         this.releaseUnselectedSlots();
      }
   }

   private int farthestCandidateIndex(int selectedCount) {
      int farthestIndex = 0;
      double farthestDistance = this.selectedLayerDistances[0];

      for (int i = 1; i < selectedCount; i++) {
         if (this.selectedLayerDistances[i] > farthestDistance) {
            farthestDistance = this.selectedLayerDistances[i];
            farthestIndex = i;
         }
      }

      return farthestIndex;
   }

   private boolean isRippleLayerActive(@Nullable ProxyOceanLayer layer) {
      return layer != null && (layer.needsRippleUpdate() || layer.getRippleCount() > 0 || this.hasActiveSimulation(layer));
   }

   private static double distanceToCameraSqr(ProxyOceanLayer layer, Vec3 cameraPos) {
      double surfaceY = (double)layer.getLayerPosY() + 0.8888888;
      double dy = cameraPos.y - surfaceY;
      return dy * dy;
   }

   private boolean isLayerSelected(ProxyOceanLayer layer) {
      return this.selectedRippleLayers.contains(layer.getLayerPosY());
   }

   private void releaseUnselectedSlots() {
      if (this.rippleSimulationSlots != null) {
         for (OceanRippleRenderer.RippleSimulationSlot slot : this.rippleSimulationSlots) {
            if (slot != null && slot.layer != null && !this.selectedRippleLayers.contains(slot.layer.getLayerPosY())) {
               slot.release();
            }
         }
      }
   }

   public boolean renderSmallWaves(PhysicsWorld physics, ProxyOceanLayer layer, ClientLevel level, Matrix4fStack modelView, Vec3 cameraPos) {
      if (ConfigClient.oceanRipples && this.isLayerSelected(layer)) {
         boolean optifineShaders = StarterClient.optifabric && Optifine.isUsingShadersNoInternal();
         if (optifineShaders) {
            Optifine.storeFBOAndViewport();
         }

         int resolution = 2048;
         this.ensureRenderResources(resolution);
         this.camera.set(cameraPos.x, cameraPos.y, cameraPos.z);
         OceanWorld oceanWorld = physics.getOceanWorld();
         float globalTime = oceanWorld.getGlobalTime();
         CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
         OceanRippleRenderer.RippleSimulationSlot slot = this.acquireSimulationSlot(encoder, layer, globalTime, cameraPos);
         if (slot == null) {
            if (optifineShaders) {
               Optifine.restoreFBOAndViewport();
            }

            return false;
         } else {
            if (slot.resourcesNeedClear) {
               this.clearSimulationTargets(encoder, slot);
               slot.resourcesNeedClear = false;
            }

            List<OceanRippleImpulse> impulses = layer.consumeRippleImpulses();
            boolean hasImpulse = this.renderImpulseTexture(encoder, slot, impulses, (float)physics.getRenderPercent());
            if (hasImpulse) {
               slot.activeSimulationSteps = 480;
               layer.setRippleCount(slot.activeSimulationSteps);
            }

            if (Float.isNaN(slot.lastSimulationTime)) {
               slot.lastSimulationTime = globalTime;
            }

            float delta = Math.max(0.0F, globalTime - slot.lastSimulationTime);
            slot.lastSimulationTime = globalTime;
            slot.simulationAccumulator = slot.simulationAccumulator + Math.min(delta, 0.083333336F);
            int simulatedSteps = 0;

            boolean consumedImpulse;
            for (consumedImpulse = false; slot.simulationAccumulator >= 0.016666668F && simulatedSteps < 5; simulatedSteps++) {
               slot.simulationAccumulator -= 0.016666668F;
               this.simulateOneStep(encoder, slot, cameraPos, hasImpulse && !consumedImpulse, 1.0F);
               consumedImpulse = consumedImpulse || hasImpulse;
            }

            if (hasImpulse && !consumedImpulse) {
               this.simulateOneStep(encoder, slot, cameraPos, true, 1.0F);
               simulatedSteps++;
               consumedImpulse = true;
            } else if (simulatedSteps == 0 && this.hasCenterMoved(slot, cameraPos)) {
               this.simulateOneStep(encoder, slot, cameraPos, false, 0.0F);
            }

            if (simulatedSteps > 0 && slot.activeSimulationSteps > 0) {
               slot.activeSimulationSteps = Math.max(0, slot.activeSimulationSteps - simulatedSteps);
               layer.setRippleCount(slot.activeSimulationSteps);
            }

            if (slot.activeSimulationSteps <= 0 && !layer.hasRippleImpulses()) {
               layer.setRippleCount(0);
            }

            if (optifineShaders) {
               Optifine.restoreFBOAndViewport();
            }

            return this.isSlotRenderable(slot, layer);
         }
      } else {
         return false;
      }
   }

   private void ensureRenderResources(int resolution) {
      if (this.rippleImpulseTarget == null) {
         this.rippleImpulseTarget = new SimpleColorRenderTarget("Physics Ocean Ripple Impulses", PhysicsShaders.OCEAN_RIPPLE_IMPULSE_FORMAT);
      }

      this.rippleImpulseTarget.ensureSize(resolution, resolution);
      if (this.rippleSimulationSlots == null || this.rippleSimulationSlots.length != 3) {
         this.destroySimulationSlots();
         this.rippleSimulationSlots = new OceanRippleRenderer.RippleSimulationSlot[3];

         for (int i = 0; i < this.rippleSimulationSlots.length; i++) {
            this.rippleSimulationSlots[i] = new OceanRippleRenderer.RippleSimulationSlot(i);
         }
      }

      for (OceanRippleRenderer.RippleSimulationSlot slot : this.rippleSimulationSlots) {
         slot.ensureSize(resolution);
      }
   }

   private void destroySimulationSlots() {
      if (this.rippleSimulationSlots != null) {
         for (OceanRippleRenderer.RippleSimulationSlot slot : this.rippleSimulationSlots) {
            if (slot != null) {
               slot.destroy();
            }
         }

         this.rippleSimulationSlots = null;
      }
   }

   private OceanRippleRenderer.RippleSimulationSlot acquireSimulationSlot(CommandEncoder encoder, ProxyOceanLayer layer, float globalTime, Vec3 cameraPos) {
      if (!this.isLayerSelected(layer)) {
         return null;
      } else {
         OceanRippleRenderer.RippleSimulationSlot existing = this.findSimulationSlot(layer);
         if (existing != null) {
            existing.lastUsed = ++this.slotUseCounter;
            return existing;
         } else if (layer.getPendingRippleImpulseCount() > 0 && this.rippleSimulationSlots != null) {
            OceanRippleRenderer.RippleSimulationSlot selected = null;

            for (OceanRippleRenderer.RippleSimulationSlot slot : this.rippleSimulationSlots) {
               if (!slot.isAlive() || slot.layer == null || !this.isLayerSelected(slot.layer)) {
                  selected = slot;
                  break;
               }
            }

            if (selected == null) {
               return null;
            } else {
               selected.assign(layer, globalTime, cameraPos, ++this.slotUseCounter);
               this.clearSimulationTargets(encoder, selected);
               selected.resourcesNeedClear = false;
               return selected;
            }
         } else {
            return null;
         }
      }
   }

   @Nullable
   private RippleSimulationSlot findSimulationSlot(@Nullable ProxyOceanLayer layer) {
      if (layer != null && this.rippleSimulationSlots != null) {
         for (OceanRippleRenderer.RippleSimulationSlot slot : this.rippleSimulationSlots) {
            if (slot != null && slot.layer == layer) {
               return slot;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private void clearSimulationTargets(CommandEncoder encoder, OceanRippleRenderer.RippleSimulationSlot slot) {
      clearTarget(encoder, slot.rippleStateTargetA);
      clearTarget(encoder, slot.rippleStateTargetB);
   }

   private static void clearTarget(CommandEncoder encoder, @Nullable SimpleColorRenderTarget target) {
      if (target != null) {
         RenderPass pass = encoder.createRenderPass(
            () -> "Physics Mod Clear Ocean Ripple Target", target.textureView(), Optional.of(new Vector4f(0.0F)), null, OptionalDouble.empty()
         );
         if (pass != null) {
            pass.close();
         }
      }
   }

   private boolean renderImpulseTexture(
      CommandEncoder encoder, OceanRippleRenderer.RippleSimulationSlot slot, List<OceanRippleImpulse> impulses, float renderPercent
   ) {
      if (this.rippleImpulseTarget == null) {
         return false;
      } else {
         RenderPass pass = encoder.createRenderPass(
            () -> "Physics Mod Ocean Ripple Impulses", this.rippleImpulseTarget.textureView(), Optional.of(new Vector4f(0.0F)), null, OptionalDouble.empty()
         );

         boolean var10;
         label68: {
            boolean var11;
            label69: {
               try {
                  if (impulses.isEmpty()) {
                     var10 = false;
                     break label68;
                  }

                  GpuBufferSlice rippleUniformBuffer = this.uploadRippleUniform(slot, renderPercent, 0.0F, false);
                  if (rippleUniformBuffer == null) {
                     var11 = false;
                     break label69;
                  }

                  if (this.rippleImpulseInstances == null) {
                     this.rippleImpulseInstances = this.createRippleInstancedRenderer();
                  }

                  this.rippleImpulseInstances.writeInstanceData(new OceanRippleRenderer.RippleImpulseVertexWriter(impulses), impulses.size());
                  pass.setUniform("PhysicsRipple", rippleUniformBuffer);
                  pass.setPipeline(PhysicsShaders.PHYSICS_OCEAN_RIPPLE_PIPELINE);
                  this.rippleImpulseInstances.render(pass, "PhysicsRippleInstances");
                  var11 = this.rippleImpulseInstances.getRenderCount() > 0;
               } catch (Throwable var9) {
                  if (pass != null) {
                     try {
                        pass.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (pass != null) {
                  pass.close();
               }

               return var11;
            }

            if (pass != null) {
               pass.close();
            }

            return var11;
         }

         if (pass != null) {
            pass.close();
         }

         return var10;
      }
   }

   private void simulateOneStep(CommandEncoder encoder, OceanRippleRenderer.RippleSimulationSlot slot, Vec3 cameraPos, boolean useImpulse, float stepAmount) {
      SimpleColorRenderTarget readTarget = slot.getReadStateTarget();
      SimpleColorRenderTarget writeTarget = slot.getWriteStateTarget();
      if (readTarget != null && writeTarget != null) {
         GpuBufferSlice rippleUniformBuffer = this.uploadRippleUniform(slot, 0.0F, stepAmount, true);
         if (rippleUniformBuffer != null) {
            GpuTextureView impulseTexture = useImpulse && this.rippleImpulseTarget != null
               ? this.rippleImpulseTarget.textureView()
               : Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.BLACK_TEXTURE).getTextureView();
            RenderPass pass = encoder.createRenderPass(
               () -> "Physics Mod Ocean Ripple Simulation", writeTarget.textureView(), Optional.empty(), null, OptionalDouble.empty()
            );

            try {
               pass.setUniform("PhysicsRipple", rippleUniformBuffer);
               pass.bindTexture("physics_ripple_state", readTarget.textureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, false));
               pass.bindTexture("physics_ripple_impulse", impulseTexture, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, false));
               pass.setPipeline(PhysicsShaders.PHYSICS_OCEAN_RIPPLE_SIMULATION_PIPELINE);
               pass.draw(3, 1, 0, 0);
            } catch (Throwable var14) {
               if (pass != null) {
                  try {
                     pass.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (pass != null) {
               pass.close();
            }

            slot.readTargetIsA = !slot.readTargetIsA;
            slot.simulationInitialized = true;
            slot.previousSimulationCenter.set(cameraPos.x, 0.0, cameraPos.z);
         }
      }
   }

   private boolean hasCenterMoved(OceanRippleRenderer.RippleSimulationSlot slot, Vec3 cameraPos) {
      if (!Double.isNaN(slot.previousSimulationCenter.x) && !Double.isNaN(slot.previousSimulationCenter.z)) {
         double dx = cameraPos.x - slot.previousSimulationCenter.x;
         double dz = cameraPos.z - slot.previousSimulationCenter.z;
         return dx * dx + dz * dz > 1.0E-6;
      } else {
         return true;
      }
   }

   private static float computeResolutionIndependentPropagation(float texelWorldSize) {
      float courant = 0.083333336F / Math.max(texelWorldSize, 1.0E-4F);
      return Math.min(0.48F, courant * courant);
   }

   @Nullable
   private GpuBufferSlice uploadRippleUniform(OceanRippleRenderer.RippleSimulationSlot slot, float renderPercent, float simulationStep, boolean simulationPass) {
      DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
      int resolution = slot.rippleStateTargetA != null ? Math.max(1, slot.rippleStateTargetA.getWidth()) : Math.max(1, 2048);
      float texelWorldSize = 64.0F / (float)resolution;
      float simulationPropagation = computeResolutionIndependentPropagation(texelWorldSize);
      RippleUniform rippleUniform = new RippleUniform(
         this.viewMatrix,
         this.projectionMatrix,
         (float)this.camera.x,
         (float)this.camera.y,
         (float)this.camera.z,
         renderPercent,
         (float)this.camera.x,
         (float)this.camera.z,
         (float)slot.previousSimulationCenter.x,
         (float)slot.previousSimulationCenter.z,
         texelWorldSize,
         0.991F,
         simulationPropagation,
         simulationPass ? simulationStep : 0.0F,
         32.0F,
         0.045F,
         1.35F,
         (float)slot.index
      );
      GpuBufferSlice[] slices = dynamicUniforms.physicsmod$writeRippleUniforms(rippleUniform);
      return slices.length > 0 ? slices[0] : null;
   }

   private InstancedRenderer createRippleInstancedRenderer() {
      return new InstancedRenderer(
         "Physics Ocean Ripple Impulses Instanced",
         PhysicsShaders.OCEAN_RIPPLE_FORMAT,
         PhysicsShaders.OCEAN_RIPPLE_INSTANCE_FORMAT,
         new OceanRippleRenderer.RipplesMeshVertexWriter()
      );
   }

   public void updateRippleInstances(OceanWorld oceanWorld, ProxyOceanLayer layer, Vec3 cameraPos) {
      OceanRippleRenderer.RippleSimulationSlot slot = this.findSimulationSlot(layer);
      int activeSteps = slot != null && slot.isRenderableFor(layer) ? slot.activeSimulationSteps : 0;
      layer.setRippleCount(Math.max(layer.getPendingRippleImpulseCount(), activeSteps));
   }

   public boolean hasActiveSimulation(@Nullable ProxyOceanLayer layer) {
      if (!ConfigClient.oceanRipples || layer == null) {
         return false;
      } else if (layer.hasRippleImpulses()) {
         return true;
      } else {
         OceanRippleRenderer.RippleSimulationSlot slot = this.findSimulationSlot(layer);
         return slot != null && slot.isRenderableFor(layer);
      }
   }

   private boolean isSlotRenderable(OceanRippleRenderer.RippleSimulationSlot slot, ProxyOceanLayer layer) {
      return ConfigClient.oceanRipples && slot.isRenderableFor(layer) && slot.getReadStateTarget() != null;
   }

   public GpuTextureView getRippleTextureView(@Nullable ProxyOceanLayer layer) {
      OceanRippleRenderer.RippleSimulationSlot slot = this.findSimulationSlot(layer);
      if (ConfigClient.oceanRipples && layer != null && this.isLayerSelected(layer) && slot != null && slot.isRenderableFor(layer)) {
         SimpleColorRenderTarget readTarget = slot.getReadStateTarget();
         if (readTarget != null) {
            return readTarget.textureView();
         }
      }

      return Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.BLACK_TEXTURE).getTextureView();
   }

   public double getRippleRange() {
      return 32.0;
   }

   public static int getActiveRippleLayerCount() {
      return 3;
   }

   public void destroy() {
      if (this.rippleImpulseInstances != null) {
         this.rippleImpulseInstances.destroy();
         this.rippleImpulseInstances = null;
      }

      if (this.rippleImpulseTarget != null) {
         this.rippleImpulseTarget.destroy();
         this.rippleImpulseTarget = null;
      }

      this.destroySimulationSlots();
      this.slotUseCounter = 0;
   }

   private static final class RippleImpulseVertexWriter implements VertexWriter {
      private final List<OceanRippleImpulse> impulses;
      private int instanceCount;

      private RippleImpulseVertexWriter(List<OceanRippleImpulse> impulses) {
         this.impulses = impulses;
      }

      @Override
      public void write(ByteBuffer buffer) {
         this.instanceCount = 0;
         long address = MemoryUtil.memAddress(buffer);

         for (OceanRippleImpulse impulse : this.impulses) {
            MemoryUtil.memPutFloat(address, (float)impulse.x);
            MemoryUtil.memPutFloat(address + 4L, (float)impulse.y);
            MemoryUtil.memPutFloat(address + 8L, (float)impulse.z);
            MemoryUtil.memPutFloat(address + 12L, impulse.strength);
            MemoryUtil.memPutFloat(address + 16L, impulse.radius);
            MemoryUtil.memPutFloat(address + 20L, impulse.mode);
            MemoryUtil.memPutFloat(address + 24L, impulse.softness);
            MemoryUtil.memPutFloat(address + 28L, impulse.width);
            address += (long)PhysicsShaders.OCEAN_RIPPLE_INSTANCE_FORMAT.getVertexSize();
            this.instanceCount++;
         }
      }

      @Override
      public int count() {
         return this.instanceCount;
      }
   }

   private static final class RippleSimulationSlot {
      private final int index;
      private final SimpleColorRenderTarget rippleStateTargetA;
      private final SimpleColorRenderTarget rippleStateTargetB;
      @Nullable
      private ProxyOceanLayer layer;
      private boolean readTargetIsA = true;
      private boolean simulationInitialized;
      private boolean resourcesNeedClear = true;
      private int activeSimulationSteps;
      private float simulationAccumulator;
      private float lastSimulationTime = Float.NaN;
      private int lastUsed;
      private final Vector3d previousSimulationCenter = new Vector3d(Double.NaN, 0.0, Double.NaN);

      private RippleSimulationSlot(int index) {
         this.index = index;
         this.rippleStateTargetA = new SimpleColorRenderTarget("Physics Ocean Ripple State " + index + " A", PhysicsShaders.OCEAN_RIPPLE_SIMULATION_FORMAT);
         this.rippleStateTargetB = new SimpleColorRenderTarget("Physics Ocean Ripple State " + index + " B", PhysicsShaders.OCEAN_RIPPLE_SIMULATION_FORMAT);
      }

      private void ensureSize(int resolution) {
         boolean changed = this.rippleStateTargetA.getWidth() != resolution || this.rippleStateTargetA.getHeight() != resolution;
         this.rippleStateTargetA.ensureSize(resolution, resolution);
         this.rippleStateTargetB.ensureSize(resolution, resolution);
         if (changed) {
            this.resourcesNeedClear = true;
            this.simulationInitialized = false;
         }
      }

      private void assign(ProxyOceanLayer newLayer, float globalTime, Vec3 cameraPos, int useCounter) {
         if (this.layer != null && this.layer != newLayer) {
            this.layer.setRippleCount(0);
         }

         this.layer = newLayer;
         this.readTargetIsA = true;
         this.simulationInitialized = false;
         this.resourcesNeedClear = true;
         this.activeSimulationSteps = 0;
         this.simulationAccumulator = 0.0F;
         this.lastSimulationTime = globalTime;
         this.lastUsed = useCounter;
         this.previousSimulationCenter.set(cameraPos.x, 0.0, cameraPos.z);
         newLayer.setRippleCount(Math.max(newLayer.getRippleCount(), newLayer.getPendingRippleImpulseCount()));
      }

      private boolean isAlive() {
         return this.layer != null && (this.activeSimulationSteps > 0 || this.layer.hasRippleImpulses());
      }

      private boolean isRenderableFor(ProxyOceanLayer testLayer) {
         return this.layer == testLayer && this.activeSimulationSteps > 0 && this.simulationInitialized;
      }

      @Nullable
      private SimpleColorRenderTarget getReadStateTarget() {
         return this.readTargetIsA ? this.rippleStateTargetA : this.rippleStateTargetB;
      }

      @Nullable
      private SimpleColorRenderTarget getWriteStateTarget() {
         return this.readTargetIsA ? this.rippleStateTargetB : this.rippleStateTargetA;
      }

      private void release() {
         if (this.layer != null) {
            this.layer.setRippleCount(0);
         }

         this.layer = null;
         this.readTargetIsA = true;
         this.simulationInitialized = false;
         this.resourcesNeedClear = true;
         this.activeSimulationSteps = 0;
         this.simulationAccumulator = 0.0F;
         this.lastSimulationTime = Float.NaN;
         this.previousSimulationCenter.set(Double.NaN, 0.0, Double.NaN);
      }

      private void destroy() {
         this.release();
         this.rippleStateTargetA.destroy();
         this.rippleStateTargetB.destroy();
      }
   }

   private static final class RipplesMeshVertexWriter implements VertexWriter {
      @Override
      public void write(ByteBuffer buffer) {
         long address = MemoryUtil.memAddress(buffer);
         MemoryUtil.memPutFloat(address, -1.0F);
         MemoryUtil.memPutFloat(address + 4L, 0.0F);
         MemoryUtil.memPutFloat(address + 8L, -1.0F);
         MemoryUtil.memPutFloat(address + 12L, 0.0F);
         MemoryUtil.memPutFloat(address + 16L, 0.0F);
         address += (long)PhysicsShaders.OCEAN_RIPPLE_FORMAT.getVertexSize();
         MemoryUtil.memPutFloat(address, 1.0F);
         MemoryUtil.memPutFloat(address + 4L, 0.0F);
         MemoryUtil.memPutFloat(address + 8L, -1.0F);
         MemoryUtil.memPutFloat(address + 12L, 1.0F);
         MemoryUtil.memPutFloat(address + 16L, 0.0F);
         address += (long)PhysicsShaders.OCEAN_RIPPLE_FORMAT.getVertexSize();
         MemoryUtil.memPutFloat(address, 1.0F);
         MemoryUtil.memPutFloat(address + 4L, 0.0F);
         MemoryUtil.memPutFloat(address + 8L, 1.0F);
         MemoryUtil.memPutFloat(address + 12L, 1.0F);
         MemoryUtil.memPutFloat(address + 16L, 1.0F);
         address += (long)PhysicsShaders.OCEAN_RIPPLE_FORMAT.getVertexSize();
         MemoryUtil.memPutFloat(address, -1.0F);
         MemoryUtil.memPutFloat(address + 4L, 0.0F);
         MemoryUtil.memPutFloat(address + 8L, -1.0F);
         MemoryUtil.memPutFloat(address + 12L, 0.0F);
         MemoryUtil.memPutFloat(address + 16L, 0.0F);
         address += (long)PhysicsShaders.OCEAN_RIPPLE_FORMAT.getVertexSize();
         MemoryUtil.memPutFloat(address, 1.0F);
         MemoryUtil.memPutFloat(address + 4L, 0.0F);
         MemoryUtil.memPutFloat(address + 8L, 1.0F);
         MemoryUtil.memPutFloat(address + 12L, 1.0F);
         MemoryUtil.memPutFloat(address + 16L, 1.0F);
         address += (long)PhysicsShaders.OCEAN_RIPPLE_FORMAT.getVertexSize();
         MemoryUtil.memPutFloat(address, -1.0F);
         MemoryUtil.memPutFloat(address + 4L, 0.0F);
         MemoryUtil.memPutFloat(address + 8L, 1.0F);
         MemoryUtil.memPutFloat(address + 12L, 0.0F);
         MemoryUtil.memPutFloat(address + 16L, 1.0F);
      }

      @Override
      public int count() {
         return 6;
      }
   }
}
