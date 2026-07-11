package net.diebuddies.physics.ragdoll;

import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import org.joml.Quaternionf;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6AxisEnum;
import physx.extensions.PxD6DriveEnum;
import physx.extensions.PxD6Joint;
import physx.extensions.PxD6JointDrive;
import physx.extensions.PxD6MotionEnum;
import physx.physics.PxFilterData;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class DoorRagdoll extends DynamicRagdoll {
   private static final int DOOR_CLOSE_TIMER = 80;
   public float stiffness = 10.0F;
   public float damping = 60.0F;
   public float currentTarget = 0.0F;
   public long tickDelay = 0L;
   public int switchDir = 80;
   public float direction = 0.0F;

   @Override
   public void updatePhysics(PhysicsWorld physics) {
      super.updatePhysics(physics);
      if (this.hookJoint != null && !this.isFrozen()) {
         float currentAngle = (float)Math.toDegrees((double)this.hookJoint.getSwingYAngle());
         if (Math.abs(currentAngle - this.currentTarget) > 35.0F && this.btBodies.size() >= 2 && this.tickDelay <= 0L) {
            IRigidBody body = this.btBodies.get(1).rigid();
            PxRigidDynamic dynamicBody = (PxRigidDynamic)body.getRigidBody();
            if (this.tickDelay == 0L) {
               this.direction = Math.signum(this.currentTarget - currentAngle);
            }

            MemoryStack mem = MemoryStack.stackPush();

            try {
               dynamicBody.addTorque(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, this.direction * 20.0F, 0.0F), PxForceModeEnum.eVELOCITY_CHANGE);
            } catch (Throwable var9) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (mem != null) {
               mem.close();
            }

            this.switchDir--;
            if (this.switchDir == 0) {
               this.switchDir = 80;
               this.direction = -this.direction;
            }
         } else {
            this.switchDir = 80;
         }

         this.tickDelay--;
      }
   }

   @Override
   public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
      if (this.bodiesPos.size() == 0) {
         return false;
      } else {
         BlockPos start = this.bodiesPos.get(0);
         if (start.getX() == pos.getX() && start.getZ() == pos.getZ() && start.getY() == pos.getY()) {
            if (state.getBlock() instanceof DoorBlock) {
               MemoryStack mem = MemoryStack.stackPush();

               try {
                  BlockState initial = this.bodiesState.get(0);
                  boolean wasOpen = (Boolean)initial.getValue(DoorBlock.OPEN);
                  boolean isOpen = (Boolean)state.getValue(DoorBlock.OPEN);
                  if (wasOpen == isOpen) {
                     this.setTargetAngle(0.0F);
                  } else {
                     float degree = 90.0F;
                     boolean hinge = this.bodiesState.get(0).getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
                     if (wasOpen) {
                        degree = -degree;
                     }

                     if (hinge) {
                        degree = -degree;
                     }

                     this.setTargetAngle(degree);
                  }

                  for (Ragdoll.LinkedBody body : this.btBodies) {
                     PxRigidActor actor = body.rigid().getRigidBody();
                     if (actor instanceof PxRigidDynamic) {
                        ((PxRigidDynamic)actor).wakeUp();
                     }
                  }
               } catch (Throwable var15) {
                  if (mem != null) {
                     try {
                        mem.close();
                     } catch (Throwable var13) {
                        var15.addSuppressed(var13);
                     }
                  }

                  throw var15;
               }

               if (mem != null) {
                  mem.close();
               }

               return true;
            } else {
               this.pxJoints.remove(this.hookJoint);
               this.hookJoint.release();
               this.hookJoint = null;
               physics.getDynamicsWorld().removeActor(this.hookBody.rigid().getRigidBody());
               this.btBodies.remove(this.hookBody);
               this.hookBody.rigid().destroy();
               this.hookBody = null;

               for (Ragdoll.LinkedBody link : this.btBodies) {
                  PhysicsRenderable entity = link.rigid().getEntity();
                  IRigidBody bodyx = link.rigid();
                  entity.time = PhysicsWorld.calculateLifetime(link.entity());
                  if (!this.linkedPhysics) {
                     entity.time = 0.0F;
                  }

                  if (!this.collision) {
                     MemoryStack stack = MemoryStack.stackPush();

                     try {
                        PxFilterData filterData = PxFilterData.createAt(stack, MemoryStack::nmalloc, 2, 23, ContactSimulationCallback.REPORT_CONTACT_FLAGS, 0);
                        bodyx.getShape().setSimulationFilterData(filterData);
                     } catch (Throwable var14) {
                        if (stack != null) {
                           try {
                              stack.close();
                           } catch (Throwable var12) {
                              var14.addSuppressed(var12);
                           }
                        }

                        throw var14;
                     }

                     if (stack != null) {
                        stack.close();
                     }
                  }

                  entity.type = PhysicsEntity.Type.BLOCK;
                  bodyx.applyRandomSpawnForces();
               }

               this.setFrozen(false);
               this.bodiesPos.clear();
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private void setTargetAngle(float degree) {
      Quaternionf rot = new Quaternionf();
      this.currentTarget = degree;
      this.tickDelay = 20L;
      this.switchDir = 80;
      rot.rotateLocalY((float)Math.toRadians((double)degree));
      MemoryStack mem = MemoryStack.stackPush();

      try {
         this.hookJoint
            .setDrivePosition(
               PxTransform.createAt(
                  mem,
                  MemoryStack::nmalloc,
                  PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
                  PxQuat.createAt(mem, MemoryStack::nmalloc, rot.x, rot.y, rot.z, rot.w)
               )
            );
      } catch (Throwable var7) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (mem != null) {
         mem.close();
      }
   }

   @Override
   protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
      PxD6Joint joint = null;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
         PxD6JointDrive drive = new PxD6JointDrive(this.stiffness, this.damping, 100000.0F, true);
         joint.setDrivePosition(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            )
         );
         joint.setDriveVelocity(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F), PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F));
         joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLOCKED);
         joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eFREE);
         joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLOCKED);
         joint.setDrive(PxD6DriveEnum.eSWING, drive);
         drive.destroy();
      } catch (Throwable var10) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (mem != null) {
         mem.close();
      }

      return joint;
   }
}
