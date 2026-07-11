package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.ocean.OceanRippleImpulse;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxArticulationLink;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;

public class BasicRigidBody extends IRigidBody {
   private static final int CACHE_UPDATE_EVERY_X = 6;
   private BlockState blockState;
   private Vector3d fluidVelocity;
   private float fluidHeight;
   private boolean inWater;
   private boolean wasSleeping = false;
   private boolean isSleeping = false;
   private boolean changedTransformation = false;
   private boolean gravityBefore = true;
   private List<ProxyOceanLayer> cachedLayers = new ObjectArrayList(1);
   private int cacheFrame = -1;
   private Vector3f changedTranslation = new Vector3f();
   private Quaternionf changedRotation = new Quaternionf();
   private static final MutableBlockPos tmpPos = new MutableBlockPos();
   private MutableBlockPos blockPos = new MutableBlockPos();

   public BasicRigidBody() {
      this.fluidVelocity = new Vector3d();
   }

   @Override
   public void updateTransformations(PhysicsWorld physics, double diff) {
      super.updateTransformations(physics, diff);
      if (this.changedTransformation) {
         this.entity.oldPosition.set(this.entity.position);
         this.entity.oldRotation.set(this.entity.rotation);
         double yPos = (double)this.changedTranslation.y + physics.getOffset().y;
         if (yPos < (double)(physics.getWorld().getMinY() - 10)) {
            this.entity.startDespawnAnimation(physics.getWorld());
         }

         this.entity.position.set(this.changedTranslation);
         this.entity.rotation.set(this.changedRotation);
      }

      this.changedTransformation = false;
   }

   @Override
   public void updatePhysics(PhysicsWorld physics, double diff, boolean blocksChanged) {
      super.updatePhysics(physics, diff, blocksChanged);
      this.isSleeping = false;
      if (this.rigidBody instanceof PxRigidDynamic dynamicBody) {
         this.isSleeping = dynamicBody.isSleeping();
      }

      Vector3d waveForce = null;
      if (!this.wasSleeping || !this.isSleeping) {
         this.changedTransformation = true;
         this.entity.oldPosition.set(this.entity.position);
         this.entity.oldRotation.set(this.entity.rotation);
         PxTransform transform = this.rigidBody.getGlobalPose();
         float rotX = MemoryUtil.memGetFloat(transform.getAddress());
         float rotY = MemoryUtil.memGetFloat(transform.getAddress() + 4L);
         float rotZ = MemoryUtil.memGetFloat(transform.getAddress() + 8L);
         float rotW = MemoryUtil.memGetFloat(transform.getAddress() + 12L);
         float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
         float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
         float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
         Vector3d offset = physics.getOffset();
         if (this.cachedLayers.size() > 0) {
            this.cachedLayers.clear();
         }

         this.changedTranslation.set(posX, posY, posZ);
         this.changedRotation.set(rotX, rotY, rotZ, rotW).normalize();
         this.loadChunkPhysics(posX, posY, posZ);
         tmpPos.set((double)posX + offset.x, (double)posY + offset.y, (double)posZ + offset.z);
         if (this.blockState == null || !tmpPos.equals(this.blockPos) || blocksChanged) {
            this.blockPos.set(tmpPos);
            this.blockState = physics.getWorld().getBlockState(this.blockPos);
            if (!(this.blockState.getFluidState().getType() instanceof EmptyFluid)) {
               this.fluidHeight = this.blockState.getFluidState().getOwnHeight();
               if (this.blockState.getFluidState().getType().isSame(physics.getWorld().getFluidState(tmpPos.set(this.blockPos).move(0, 1, 0)).getType())) {
                  this.fluidHeight = 1.0F;
               }

               Vec3 fv = this.blockState.getFluidState().getFlow(physics.getWorld(), this.blockPos);
               this.fluidVelocity.set(fv.x(), fv.y(), fv.z());
               boolean bubble = false;
               if (this.blockState.getBlock() == Blocks.BUBBLE_COLUMN) {
                  bubble = true;
                  boolean drag = (Boolean)this.blockState.getValue(BubbleColumnBlock.DRAG_DOWN);
                  if (drag) {
                     this.fluidVelocity.y = -2.0;
                  } else {
                     this.fluidVelocity.y = 8.0;
                  }
               }

               this.fluidVelocity.y = bubble ? this.fluidVelocity.y : Math.max(this.fluidVelocity.y, 0.0);
            } else {
               this.fluidHeight = -1.0F;
               this.fluidVelocity.set(0.0);
            }
         }
      }

      this.wasSleeping = this.isSleeping;
      float height = this.getFluidHeight();
      boolean oceanWaveForce = waveForce != null && waveForce.y >= 0.0;
      boolean waterBlockForce = height >= 0.0F && ((double)this.getEntity().position.y + physics.getOffset().y) % 1.0 < (double)height;
      boolean isInsideWater = oceanWaveForce || waterBlockForce && (waveForce == null || waveForce.y >= 0.0);
      if (isInsideWater && this.getRigidBody() instanceof PxRigidBody rigidBody) {
         if (!this.inWater) {
            this.inWater = true;
            this.gravityBefore = this.hasGravity();
            this.setGravity(false);
            if (ConfigClient.areOceanPhysicsEnabled() && !this.isKinematicOrFrozen() && (ConfigClient.oceanRipples || ConfigClient.oceanParticles)) {
               float vy = rigidBody.getLinearVelocity().getY();
               if ((double)Math.abs(vy) > 4.52) {
                  Vector3d offsetx = physics.getOffset();
                  double worldX = (double)this.entity.position.x + offsetx.x;
                  double worldY = (double)this.entity.position.y + offsetx.y;
                  double worldZ = (double)this.entity.position.z + offsetx.z;
                  double objectSize = (double)this.entity.getBoundingSphereRadius();
                  if (ConfigClient.oceanRipples) {
                     float diameter = Math.max(0.1F, (float)objectSize * 2.0F);
                     OceanWorld oceanWorld = physics.getOceanWorld();
                     oceanWorld.queueRippleImpulse(OceanRippleImpulse.radial(worldX, worldY, worldZ, diameter, 1.0F));
                  }

                  if (objectSize > 0.4) {
                     int splashamount = (int)net.diebuddies.math.Math.remapClamp(objectSize, 0.4, 2.0, 30.0, 75.0);
                     double intensity = net.diebuddies.math.Math.remapClamp(objectSize, 0.4, 2.0, 0.25, 0.7);
                     float volume = (float)intensity * ConfigClient.oceanSplashVolume;
                     float pitch = net.diebuddies.math.Math.random() * 0.4F + 0.7F;
                     Level level = physics.getLevel();
                     level.playLocalSound(worldX, worldY, worldZ, WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true);
                     if (ConfigClient.oceanParticles) {
                        OceanWorld.createWaterSplash(level, worldX, worldY, worldZ, 0.0, 0.0, 0.0, 0.25, intensity, splashamount);
                     }
                  }
               }
            }
         }

         float mass = this.getMass();
         float flowStrength = 4.6F;
         this.setAngularDamping(2.355F);
         this.setLinearDamping(2.355F);
         if (!this.isKinematicOrFrozen()) {
            MemoryStack mem = MemoryStack.stackPush();

            try {
               Vector3d direction = this.getFluidVelocity();
               Vector3f buoyancy = physics.getDynamicsWorld().getBuoyancy();
               if (rigidBody instanceof PxArticulationLink) {
                  PxVec3 v = rigidBody.getLinearVelocity();
                  PxVec3 a = rigidBody.getAngularVelocity();
                  float damping = 0.885F;
                  float aDamping = 0.885F;
                  float vx = MemoryUtil.memGetFloat(v.getAddress());
                  float vy = MemoryUtil.memGetFloat(v.getAddress() + 4L);
                  float vz = MemoryUtil.memGetFloat(v.getAddress() + 8L);
                  float ax = MemoryUtil.memGetFloat(a.getAddress());
                  float ay = MemoryUtil.memGetFloat(a.getAddress() + 4L);
                  float az = MemoryUtil.memGetFloat(a.getAddress() + 8L);
                  PxVec3 flowForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -vx * (1.0F - damping), -vy * (1.0F - damping), -vz * (1.0F - damping));
                  rigidBody.addForce(flowForce, PxForceModeEnum.eVELOCITY_CHANGE);
                  PxVec3 angularForce = PxVec3.createAt(mem, MemoryStack::nmalloc, -ax * (1.0F - aDamping), -ay * (1.0F - aDamping), -az * (1.0F - aDamping));
                  rigidBody.addTorque(angularForce, PxForceModeEnum.eVELOCITY_CHANGE);
               }

               float forceX = buoyancy.x + (float)direction.x * flowStrength;
               float forceY = buoyancy.y + (float)direction.y * flowStrength;
               float forceZ = buoyancy.z + (float)direction.z * flowStrength;
               if (waveForce != null) {
                  forceX = (float)((double)forceX + (double)buoyancy.x * waveForce.y + -waveForce.x * 3.0);
                  forceY = (float)((double)forceY + (double)buoyancy.y * waveForce.y);
                  forceZ = (float)((double)forceZ + (double)buoyancy.z * waveForce.y + -waveForce.z * 3.0);
               }

               PxVec3 counterForce = PxVec3.createAt(mem, MemoryStack::nmalloc, forceX * mass, forceY * mass, forceZ * mass);
               rigidBody.addForce(counterForce, PxForceModeEnum.eFORCE);
            } catch (Throwable var29) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var28) {
                     var29.addSuppressed(var28);
                  }
               }

               throw var29;
            }

            if (mem != null) {
               mem.close();
            }
         }
      } else {
         if (this.inWater) {
            this.setGravity(this.gravityBefore);
            this.inWater = false;
         }

         this.setAngularDamping(0.0F);
         this.setLinearDamping(0.0F);
      }
   }

   @Override
   public boolean hasTransformationChanged() {
      return this.wasSleeping && this.isSleeping ? true : super.hasTransformationChanged();
   }

   public boolean isInWater() {
      return this.inWater;
   }

   public float getFluidHeight() {
      return this.fluidHeight;
   }

   public Vector3d getFluidVelocity() {
      return this.fluidVelocity;
   }

   public List<ProxyOceanLayer> getCachedLayers() {
      return this.cachedLayers;
   }

   public void addCachedLayer(ProxyOceanLayer layer) {
      this.cachedLayers.add(layer);
   }

   public void clearCachedLayers() {
      this.cachedLayers.clear();
   }
}
