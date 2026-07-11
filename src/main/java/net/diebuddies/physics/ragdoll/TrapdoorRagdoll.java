package net.diebuddies.physics.ragdoll;

import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
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
import physx.extensions.PxJointLinearLimit;
import physx.extensions.PxSpring;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class TrapdoorRagdoll extends DynamicRagdoll {
   public float stiffness = 10.0F;
   public float damping = 60.0F;
   public boolean west = false;

   @Override
   public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
      if (this.bodiesPos.size() == 0) {
         return false;
      } else {
         BlockPos start = this.bodiesPos.get(0);
         if (start.getX() == pos.getX() && start.getZ() == pos.getZ() && start.getY() == pos.getY()) {
            if (state.getBlock() instanceof TrapDoorBlock) {
               MemoryStack mem = MemoryStack.stackPush();

               try {
                  BlockState initial = this.bodiesState.get(0);
                  boolean wasOpen = (Boolean)initial.getValue(TrapDoorBlock.OPEN);
                  boolean isOpen = (Boolean)state.getValue(TrapDoorBlock.OPEN);
                  boolean isTop = initial.getValue(TrapDoorBlock.HALF) == Half.TOP;
                  if (wasOpen == isOpen) {
                     this.hookJoint
                        .setDrivePosition(
                           PxTransform.createAt(
                              mem,
                              MemoryStack::nmalloc,
                              PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
                              PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
                           )
                        );
                  } else {
                     Quaternionf rot = new Quaternionf();
                     float degree = isTop ? -90.0F : 90.0F;
                     if (wasOpen) {
                        degree = -degree;
                     }

                     switch ((Direction)initial.getValue(TrapDoorBlock.FACING)) {
                        case SOUTH:
                           rot.rotateLocalX((float)Math.toRadians((double)(-degree)));
                           break;
                        case WEST:
                           rot.rotateLocalZ((float)Math.toRadians((double)(-degree)));
                           break;
                        case EAST:
                           rot.rotateLocalZ((float)Math.toRadians((double)degree));
                           break;
                        default:
                           rot.rotateLocalX((float)Math.toRadians((double)degree));
                     }

                     this.hookJoint
                        .setDrivePosition(
                           PxTransform.createAt(
                              mem,
                              MemoryStack::nmalloc,
                              PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
                              PxQuat.createAt(mem, MemoryStack::nmalloc, rot.x, rot.y, rot.z, rot.w)
                           )
                        );
                  }

                  for (Ragdoll.LinkedBody body : this.btBodies) {
                     if (body.rigid().getRigidBody() instanceof PxRigidDynamic dynamic) {
                        dynamic.wakeUp();
                     }
                  }
               } catch (Throwable var17) {
                  if (mem != null) {
                     try {
                        mem.close();
                     } catch (Throwable var15) {
                        var17.addSuppressed(var15);
                     }
                  }

                  throw var17;
               }

               if (mem != null) {
                  mem.close();
               }

               return true;
            } else {
               this.hookJoint.release();
               this.hookJoint = null;
               this.pxJoints.clear();
               physics.getDynamicsWorld().removeActor(this.hookBody.rigid().getRigidBody());
               this.btBodies.remove(this.hookBody);
               this.hookBody.rigid().destroy();
               this.hookBody = null;

               for (Ragdoll.LinkedBody link : this.btBodies) {
                  IRigidBody bodyx = link.rigid();
                  PhysicsRenderable entity = bodyx.getEntity();
                  entity.time = PhysicsWorld.calculateLifetime(link.entity());
                  if (!this.linkedPhysics) {
                     entity.time = 0.0F;
                  }

                  if (!this.collision) {
                     MemoryStack stack = MemoryStack.stackPush();

                     try {
                        PxFilterData filterData = PxFilterData.createAt(stack, MemoryStack::nmalloc, 2, 23, ContactSimulationCallback.REPORT_CONTACT_FLAGS, 0);
                        bodyx.getShape().setSimulationFilterData(filterData);
                     } catch (Throwable var16) {
                        if (stack != null) {
                           try {
                              stack.close();
                           } catch (Throwable var14) {
                              var16.addSuppressed(var14);
                           }
                        }

                        throw var16;
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
         if (this.west) {
            joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLOCKED);
            joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLOCKED);
            joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eFREE);
            joint.setDrive(PxD6DriveEnum.eSWING, drive);
         } else {
            joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eFREE);
            joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLOCKED);
            joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLOCKED);
            joint.setDrive(PxD6DriveEnum.eTWIST, drive);
         }

         PxSpring spring = new PxSpring(900.0F, 20.0F);
         PxJointLinearLimit distanceLimit = new PxJointLinearLimit(0.003F, spring);
         joint.setDistanceLimit(distanceLimit);
         drive.destroy();
         spring.destroy();
         distanceLimit.destroy();
      } catch (Throwable var11) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (mem != null) {
         mem.close();
      }

      return joint;
   }
}
