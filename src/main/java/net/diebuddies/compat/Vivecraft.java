package net.diebuddies.compat;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.diebuddies.physics.BoxRigidBody;
import net.diebuddies.physics.GrabHand;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.provider.openvr_lwjgl.VRInputAction;
import org.vivecraft.client_vr.render.helpers.RenderHelper;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxHitFlagEnum;
import physx.physics.PxHitFlags;
import physx.physics.PxQueryFilterData;
import physx.physics.PxQueryFlagEnum;
import physx.physics.PxQueryFlags;
import physx.physics.PxRaycastHit;
import physx.physics.PxRaycastResult;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;

public class Vivecraft {
   private static final int OFFSET = -2147483638;
   private final GrabHand[] hands = new GrabHand[]{new GrabHand(), new GrabHand()};

   public void performVrHandsSupport(PhysicsWorld physicsWorld, IntSet active, IntSet activeLastFrame, Int2ObjectMap<IRigidBody> worldEntities) {
      physicsWorld.getDynamicsWorld().getScene().sceneQueriesUpdate();
      physicsWorld.getDynamicsWorld().getScene().fetchQueries(true);
      this.addHandBody(physicsWorld, 0, active, activeLastFrame, worldEntities);
      this.addHandBody(physicsWorld, 1, active, activeLastFrame, worldEntities);
      IRigidBody rightHandBody = (IRigidBody)worldEntities.get(-2147483638);
      if (rightHandBody != null && !rightHandBody.isDestroyed()) {
         VRInputAction action = ClientDataHolderVR.getInstance().vr.getInputActionByName("/actions/ingame/in/key.attack");
         this.overrideActionWhenHovered(physicsWorld, rightHandBody, action, 0);
         this.isHandTriggered(physicsWorld, rightHandBody, action, 0);
      }

      IRigidBody leftHandBody = (IRigidBody)worldEntities.get(-2147483637);
      if (leftHandBody != null && !leftHandBody.isDestroyed()) {
         VRInputAction action = ClientDataHolderVR.getInstance().vr.getInputActionByName("/actions/ingame/in/vivecraft.key.teleport");
         this.overrideActionWhenHovered(physicsWorld, rightHandBody, action, 1);
         this.isHandTriggered(physicsWorld, leftHandBody, action, 1);
      }
   }

   private void overrideActionWhenHovered(PhysicsWorld physicsWorld, IRigidBody handBody, VRInputAction action, int index) {
      if (!action.isButtonPressed()) {
         action.setEnabled(true);

         try {
            MemoryStack mem = MemoryStack.stackPush();

            try {
               Vec3 pos = this.getControllerPosition(index);
               Vector3f dir = this.getControllerDirection(index);
               Vector3d offset = physicsWorld.getOffset();
               if (dir.lengthSquared() == 0.0F) {
                  dir.set(1.0F, 0.0F, 0.0F);
               } else {
                  dir.normalize();
               }

               PxVec3 origin = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)(pos.x - offset.x), (float)(pos.y - offset.y), (float)(pos.z - offset.z));
               PxVec3 unitDir = PxVec3.createAt(mem, MemoryStack::nmalloc, dir.x, dir.y, dir.z);
               float distance = 5.0F;
               float closestTerrainHit = Float.MAX_VALUE;
               PxRaycastResult terrainResult = new PxRaycastResult();
               PxHitFlags terrainHitFlags = PxHitFlags.createAt(mem, MemoryStack::nmalloc, (short)((byte)PxHitFlagEnum.eDEFAULT.value));
               PxQueryFlags terrainQueryFlags = PxQueryFlags.createAt(
                  mem, MemoryStack::nmalloc, (short)((byte)(PxQueryFlagEnum.eSTATIC.value | PxQueryFlagEnum.eANY_HIT.value))
               );
               PxQueryFilterData terrainQueryFilter = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, terrainQueryFlags);
               if (physicsWorld.getDynamicsWorld().getScene().raycast(origin, unitDir, distance, terrainResult, terrainHitFlags, terrainQueryFilter)) {
                  for (int i = 0; i < terrainResult.getNbAnyHits(); i++) {
                     PxRaycastHit hit = terrainResult.getAnyHit(i);
                     float curDistance = hit.getDistance();
                     if (curDistance < closestTerrainHit) {
                        closestTerrainHit = curDistance;
                     }
                  }
               }

               PxRaycastResult result = new PxRaycastResult();
               PxHitFlags hitFlags = PxHitFlags.createAt(mem, MemoryStack::nmalloc, (short)((byte)PxHitFlagEnum.eDEFAULT.value));
               PxQueryFlags queryFlags = PxQueryFlags.createAt(
                  mem, MemoryStack::nmalloc, (short)((byte)(PxQueryFlagEnum.eDYNAMIC.value | PxQueryFlagEnum.eNO_BLOCK.value))
               );
               PxQueryFilterData queryFilter = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, queryFlags);
               if (physicsWorld.getDynamicsWorld().getScene().raycast(origin, unitDir, distance, result, hitFlags, queryFilter)) {
                  for (int ix = 0; ix < result.getNbAnyHits(); ix++) {
                     PxRaycastHit hit = result.getAnyHit(ix);
                     PxRigidDynamic dynamic = PxRigidDynamic.wrapPointer(hit.getActor().getAddress());
                     if (!dynamic.getRigidBodyFlags().isSet(PxRigidBodyFlagEnum.eKINEMATIC) && hit.getDistance() < closestTerrainHit) {
                        action.setEnabled(false);
                        break;
                     }
                  }
               }

               result.destroy();
            } catch (Throwable var25) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var24) {
                     var25.addSuppressed(var24);
                  }
               }

               throw var25;
            }

            if (mem != null) {
               mem.close();
            }
         } catch (Exception var26) {
            var26.printStackTrace();
         }
      }
   }

   public void isHandTriggered(PhysicsWorld physicsWorld, IRigidBody handBody, VRInputAction action, int index) {
      GrabHand hand = this.hands[index];
      boolean triggered = action.isButtonPressed();
      if (triggered && !hand.isPreviousTriggered()) {
         if (hand.getJoint() != null) {
            hand.getJoint().release();
            hand.setJoint(null);
         }

         try {
            MemoryStack mem = MemoryStack.stackPush();

            try {
               Vec3 pos = this.getControllerPosition(index);
               Vector3f dir = this.getControllerDirection(index);
               Vector3d offset = physicsWorld.getOffset();
               if (dir.lengthSquared() == 0.0F) {
                  dir.set(1.0F, 0.0F, 0.0F);
               } else {
                  dir.normalize();
               }

               PxVec3 origin = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)(pos.x - offset.x), (float)(pos.y - offset.y), (float)(pos.z - offset.z));
               PxVec3 unitDir = PxVec3.createAt(mem, MemoryStack::nmalloc, dir.x, dir.y, dir.z);
               float distance = 5.0F;
               float closestTerrainHit = Float.MAX_VALUE;
               PxRaycastResult terrainResult = new PxRaycastResult();
               PxHitFlags terrainHitFlags = PxHitFlags.createAt(mem, MemoryStack::nmalloc, (short)((byte)PxHitFlagEnum.eDEFAULT.value));
               PxQueryFlags terrainQueryFlags = PxQueryFlags.createAt(
                  mem, MemoryStack::nmalloc, (short)((byte)(PxQueryFlagEnum.eSTATIC.value | PxQueryFlagEnum.eANY_HIT.value))
               );
               PxQueryFilterData terrainQueryFilter = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, terrainQueryFlags);
               if (physicsWorld.getDynamicsWorld().getScene().raycast(origin, unitDir, distance, terrainResult, terrainHitFlags, terrainQueryFilter)) {
                  for (int i = 0; i < terrainResult.getNbAnyHits(); i++) {
                     PxRaycastHit hit = terrainResult.getAnyHit(i);
                     float curDistance = hit.getDistance();
                     if (curDistance < closestTerrainHit) {
                        closestTerrainHit = curDistance;
                     }
                  }
               }

               PxRaycastResult result = new PxRaycastResult();
               PxHitFlags hitFlags = PxHitFlags.createAt(mem, MemoryStack::nmalloc, (short)((byte)PxHitFlagEnum.eDEFAULT.value));
               PxQueryFlags queryFlags = PxQueryFlags.createAt(
                  mem, MemoryStack::nmalloc, (short)((byte)(PxQueryFlagEnum.eDYNAMIC.value | PxQueryFlagEnum.eNO_BLOCK.value))
               );
               PxQueryFilterData queryFilter = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, queryFlags);
               if (physicsWorld.getDynamicsWorld().getScene().raycast(origin, unitDir, distance, result, hitFlags, queryFilter)) {
                  float closest = Float.MAX_VALUE;
                  PxRigidDynamic closestActor = null;
                  PxVec3 closestPos = null;

                  for (int ix = 0; ix < result.getNbAnyHits(); ix++) {
                     PxRaycastHit hit = result.getAnyHit(ix);
                     PxRigidDynamic dynamic = PxRigidDynamic.wrapPointer(hit.getActor().getAddress());
                     if (!dynamic.getRigidBodyFlags().isSet(PxRigidBodyFlagEnum.eKINEMATIC)) {
                        float curDistance = hit.getDistance();
                        if (curDistance < closest && curDistance < closestTerrainHit) {
                           closest = curDistance;
                           closestActor = dynamic;
                           closestPos = hit.getPosition();
                        }
                     }
                  }

                  if (closestActor != null) {
                     hand.setJoint(physicsWorld.createGrabJoint(handBody.getRigidBody(), closestActor, closestPos));
                  }
               }

               result.destroy();
            } catch (Throwable var31) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var30) {
                     var31.addSuppressed(var30);
                  }
               }

               throw var31;
            }

            if (mem != null) {
               mem.close();
            }
         } catch (Exception var32) {
            var32.printStackTrace();
         }
      } else if (!triggered && hand.isPreviousTriggered() && hand.getJoint() != null) {
         hand.getJoint().release();
         hand.setJoint(null);
      }

      hand.setPreviousTriggered(triggered);
   }

   public void addHandBody(PhysicsWorld physicsWorld, int index, IntSet active, IntSet activeLastFrame, Int2ObjectMap<IRigidBody> worldEntities) {
      try {
         Vector3d offset = physicsWorld.getOffset();
         Matrix4f pos = new Matrix4f();
         this.setupRenderingAtController(index, pos);
         int controllerId = -2147483638 + index;
         double width = 0.2;
         double height = 0.5;
         double depth = 0.5;
         Vector3f translation = pos.getTranslation(new Vector3f());
         Quaternionf rotation = pos.getNormalizedRotation(new Quaternionf());
         if (!activeLastFrame.contains(controllerId)) {
            PhysicsEntity physicsEntity = new PhysicsEntity(PhysicsEntity.Type.MOB, null);
            physicsEntity.physicsGroup = 4;
            physicsEntity.physicsMask = 7;
            physicsEntity.getTransformation().set(pos);
            IRigidBody body = null;
            IRigidBody var26 = BoxRigidBody.create(physicsEntity, (float)width, (float)height, (float)depth, 0.0F, 0.0F, 0.0F, true);
            var26.setKinematic(true);
            var26.setGravity(false);
            physicsWorld.getDynamicsWorld().addActor(var26.getRigidBody());
            worldEntities.put(controllerId, var26);
         }

         IRigidBody ibody = (IRigidBody)worldEntities.get(controllerId);
         if (!ibody.isDestroyed()) {
            PxRigidDynamic body = (PxRigidDynamic)ibody.getRigidBody();
            MemoryStack mem = MemoryStack.stackPush();

            try {
               body.setKinematicTarget(
                  PxTransform.createAt(
                     mem,
                     MemoryStack::nmalloc,
                     PxVec3.createAt(
                        mem,
                        MemoryStack::nmalloc,
                        (float)((double)translation.x - offset.x),
                        (float)((double)translation.y - offset.y),
                        (float)((double)translation.z - offset.z)
                     ),
                     PxQuat.createAt(mem, MemoryStack::nmalloc, rotation.x, rotation.y, rotation.z, rotation.w)
                  )
               );
            } catch (Throwable var23) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var22) {
                     var23.addSuppressed(var22);
                  }
               }

               throw var23;
            }

            if (mem != null) {
               mem.close();
            }
         }

         active.add(controllerId);
      } catch (Exception var24) {
      }
   }

   public void setupRenderingAtController(int c, Matrix4f matrix) {
      Vec3 aimSource = this.getControllerPosition(c);
      matrix.translate((float)aimSource.x, (float)aimSource.y, (float)aimSource.z);
      ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();
      float sc = DATA_HOLDER.vrPlayer.vrdata_world_render.worldScale;
      matrix.mul(DATA_HOLDER.vrPlayer.vrdata_world_render.getController(c).getMatrix().invert().transpose());
      matrix.scale(sc, sc, sc);
   }

   public Vec3 getControllerPosition(int c) {
      return RenderHelper.getControllerRenderPos(c);
   }

   public Vector3f getControllerDirection(int c) {
      ClientDataHolderVR DATA_HOLDER = ClientDataHolderVR.getInstance();
      return DATA_HOLDER.vrPlayer.vrdata_world_render.getController(c).getDirection();
   }

   public void destroy() {
      for (GrabHand hand : this.hands) {
         if (hand.getJoint() != null) {
            hand.getJoint().release();
         }
      }
   }
}
