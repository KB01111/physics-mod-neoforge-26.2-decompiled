package net.diebuddies.physics.sound;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SoundType;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxJoint;
import physx.extensions.PxJointActorIndexEnum;
import physx.physics.PxActor;
import physx.physics.PxConstraintInfo;
import physx.physics.PxContactPair;
import physx.physics.PxContactPairHeader;
import physx.physics.PxContactPairPoint;
import physx.physics.PxPairFlagEnum;
import physx.physics.PxPairFlags;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxSimulationEventCallbackImpl;
import physx.support.Vector_PxContactPairPoint;

public class ContactSimulationCallback extends PxSimulationEventCallbackImpl {
   public static final float FORCE_THRESHOLD = 0.25F;
   public static final int REPORT_CONTACT_FLAGS = PxPairFlagEnum.eNOTIFY_THRESHOLD_FORCE_FOUND.value | PxPairFlagEnum.eNOTIFY_CONTACT_POINTS.value;
   public static final long OBJECT_SOUND_DELAY = 500000000L;
   public static final int MAX_SOUNDS_PER_TICK = 1;
   public static int RESET_SOUNDS_PER_TICK_EVERY_X_TICKS = 1;
   private PhysicsWorld physics;
   private ClientLevel level;
   private Vector_PxContactPairPoint contacts = new Vector_PxContactPairPoint(64);
   public int soundCount;
   public int soundTickCount;

   public ContactSimulationCallback(PhysicsWorld physics, ClientLevel level) {
      this.physics = physics;
      this.level = level;
   }

   @Override
   public void onConstraintBreak(PxConstraintInfo constraints, int count) {
      super.onConstraintBreak(constraints, count);
      if (!(ConfigClient.jointBlood <= 0.0F)) {
         for (int i = 0; i < count; i++) {
            PxConstraintInfo info = PxConstraintInfo.arrayGet(constraints.getAddress(), i);
            PxJoint joint = PxJoint.wrapPointer(info.getExternalReference().getAddress());
            PhysicsWorld.Tuple<IRigidBody, IRigidBody> parents = this.physics.getJointParents(joint);
            if (parents != null) {
               PxTransform transform = joint.getLocalPose(PxJointActorIndexEnum.eACTOR0);
               IRigidBody rigidBody = parents.getA();
               PhysicsRenderable renderable = rigidBody.getEntity();
               Object entityInfo = renderable.info;
               if (entityInfo instanceof EntityType<?> entityType) {
                  if (renderable.type == PhysicsEntity.Type.MOB) {
                     MobSetting mobSetting = ConfigMobs.getMobSetting(entityType);
                     if (mobSetting.getType() != MobPhysicsType.RAGDOLL_BREAK_BLOOD) {
                        return;
                     }
                  }
               }

               PxRigidActor body = rigidBody.getRigidBody();
               if (body != null) {
                  PxTransform globalPose = body.getGlobalPose();
                  PxVec3 globalPos = globalPose.getP();
                  PxQuat globalRot = globalPose.getQ();
                  Matrix4d globalMatrix = new Matrix4d()
                     .translationRotate(
                        (double)globalPos.getX(),
                        (double)globalPos.getY(),
                        (double)globalPos.getZ(),
                        (double)globalRot.getX(),
                        (double)globalRot.getY(),
                        (double)globalRot.getZ(),
                        (double)globalRot.getW()
                     );
                  Vector3d offset = this.physics.getOffset();
                  int amount = (int)((float)(8 + Math.randomInt(12)) * ConfigClient.jointBlood);
                  PxVec3 pos = transform.getP();
                  Vector3d finalPos = globalMatrix.transformPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new Vector3d());

                  for (int j = 0; j < amount; j++) {
                     ParticleSpawner.spawnBloodPhysicsParticle(this.level, offset.x + finalPos.x, offset.y + finalPos.y, offset.z + finalPos.z);
                  }
               }
            }
         }
      }
   }

   @Override
   public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
      super.onContact(pairHeader, pairs, nbPairs);
      if (this.soundCount < 1 && !(ConfigClient.impactVolume < 0.01F)) {
         PxActor actor0 = pairHeader.getActors(0);
         PxActor actor1 = pairHeader.getActors(1);

         for (int i = 0; i < nbPairs; i++) {
            PxContactPair pair = PxContactPair.arrayGet(pairs.getAddress(), i);
            PxPairFlags events = pair.getEvents();
            if (events.isSet(PxPairFlagEnum.eNOTIFY_THRESHOLD_FORCE_FOUND)) {
               double largestImpulse = 0.0;
               PxVec3 impulsePos = null;
               int contactPoints = pair.extractContacts(this.contacts.data(), 64);

               for (int j = 0; j < contactPoints; j++) {
                  PxContactPairPoint cp = this.contacts.at(j);
                  PxVec3 impulse = cp.getImpulse();
                  PxVec3 position = cp.getPosition();
                  float x = MemoryUtil.memGetFloat(impulse.getAddress());
                  float y = MemoryUtil.memGetFloat(impulse.getAddress() + 4L);
                  float z = MemoryUtil.memGetFloat(impulse.getAddress() + 8L);
                  double impulseMagnitude = (double)(x * x + y * y + z * z);
                  if (largestImpulse < impulseMagnitude) {
                     largestImpulse = impulseMagnitude;
                     impulsePos = position;
                  }
               }

               double volume = Math.remapClamp(largestImpulse, 2.0E-5, 0.0015, 0.0, 1.0) * (double)ConfigClient.impactVolume;
               if (!(volume <= 0.0)) {
                  PhysicsMod mod = PhysicsMod.getInstance(this.level);
                  long time = mod.time;
                  PhysicsWorld physics = mod.getPhysicsWorld();
                  IRigidBody body0 = physics.getBody(actor0);
                  SoundType sound = null;
                  if (body0 != null && body0.getEntity().lastSoundTime > 500000000L) {
                     sound = body0.getEntity().sound;
                     body0.getEntity().lastSoundTime = time;
                  }

                  IRigidBody body1 = physics.getBody(actor1);
                  if (body1 != null && time - body1.getEntity().lastSoundTime > 500000000L) {
                     if (sound == null) {
                        sound = body1.getEntity().sound;
                     }

                     body1.getEntity().lastSoundTime = time;
                  }

                  if (sound != null) {
                     Vector3d offset = physics.getOffset();
                     SoundEvent soundEvent = sound.getHitSound();
                     if (soundEvent != null) {
                        float pitch = 0.85F + Math.random() * 0.3F;
                        float ix = MemoryUtil.memGetFloat(impulsePos.getAddress());
                        float iy = MemoryUtil.memGetFloat(impulsePos.getAddress() + 4L);
                        float iz = MemoryUtil.memGetFloat(impulsePos.getAddress() + 8L);
                        this.level
                           .playLocalSound(
                              (double)ix + offset.x, (double)iy + offset.y, (double)iz + offset.z, soundEvent, SoundSource.BLOCKS, (float)volume, pitch, true
                           );
                        this.soundCount++;
                     }
                  }
               }
            }
         }
      }
   }

   public void tick(float fixedTimeStep) {
      this.soundTickCount++;
      if (this.soundTickCount >= RESET_SOUNDS_PER_TICK_EVERY_X_TICKS) {
         this.soundTickCount = 0;
         this.soundCount = 0;
      }
   }
}
