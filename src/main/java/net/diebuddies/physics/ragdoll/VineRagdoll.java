package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.diebuddies.physics.BasicRigidBody;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Vector3f;
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
import physx.extensions.PxJoint;
import physx.extensions.PxJointAngularLimitPair;
import physx.extensions.PxSpring;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class VineRagdoll extends DynamicRagdoll {
   public List<VineRagdoll.Connector> connectors = new ObjectArrayList();
   public boolean bottomFixed;
   public float stiffness = 10.0F;
   public float damping = 60.0F;
   private boolean alwaysInWater;

   @Override
   public void updatePhysics(PhysicsWorld physics) {
      super.updatePhysics(physics);
      if (this.alwaysInWater) {
         for (Ragdoll.LinkedBody body : this.btBodies) {
            body.rigid().setGravity(false);
         }
      }
   }

   @Override
   public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
      if (this.bodiesPos.size() == 0) {
         return false;
      } else {
         BlockPos start = this.bodiesPos.get(0);
         if (start.getX() == pos.getX() && start.getZ() == pos.getZ()) {
            int index = this.bodiesPos.indexOf(pos);
            if (index != -1) {
               VineSetting setting = (VineSetting)VineHelper.getSetting(this.bodiesState.get(index));
               if (setting != null && !setting.canLink(this.bodiesState.get(index), state)) {
                  boolean sideConnection = setting.sideConnection;
                  int cutJoint = -1;
                  BlockPos connectionDir = this.bottomFixed ? pos.below() : pos.above();

                  for (int i = 0; i < this.connectors.size(); i++) {
                     if (this.connectors.get(i).connects(pos, connectionDir)) {
                        cutJoint = i;
                        break;
                     }
                  }

                  PxJoint releasedJoint = this.pxJoints.remove(cutJoint);
                  releasedJoint.release();
                  List<PhysicsEntity> bodiesNew = new ObjectArrayList();
                  List<Ragdoll.LinkedBody> btBodiesNew = new ObjectArrayList();
                  List<PxJoint> pxJointsNew = new ObjectArrayList();
                  List<VineRagdoll.Connector> connectorsNew = new ObjectArrayList();
                  VineRagdoll.Connector cutConnector = this.connectors.remove(cutJoint);
                  BlockPos pos1 = pos;
                  BlockPos pos2 = connectionDir;
                  Set<BlockPos> removedPositions = new ObjectOpenHashSet();

                  while (cutConnector != null) {
                     cutConnector = null;
                     pos1 = this.bottomFixed ? pos1.above() : pos1.below();
                     pos2 = this.bottomFixed ? pos2.above() : pos2.below();
                     removedPositions.add(pos1);
                     removedPositions.add(pos2);
                     this.moveBodiesIntoNewRagdoll(pos1, btBodiesNew, bodiesNew);
                     this.moveBodiesIntoNewRagdoll(pos2, btBodiesNew, bodiesNew);

                     for (int ix = 0; ix < this.connectors.size(); ix++) {
                        VineRagdoll.Connector connection = this.connectors.get(ix);
                        if (connection.connects(pos1, pos2)) {
                           cutConnector = connection;
                           connectorsNew.add(this.connectors.remove(ix));
                           pxJointsNew.add(this.pxJoints.remove(ix));
                           break;
                        }
                     }
                  }

                  this.validateHitbox();
                  removedPositions.remove(pos);
                  VineRagdoll vine = new VineRagdoll();
                  vine.hitboxScale.set(this.hitboxScale);
                  vine.bottomFixed = this.bottomFixed;
                  vine.bodies.addAll(bodiesNew);
                  vine.btBodies.addAll(btBodiesNew);
                  vine.pxJoints.addAll(pxJointsNew);
                  vine.connectors.addAll(connectorsNew);
                  vine.initFreeze = false;
                  vine.linkedPhysics = this.linkedPhysics;
                  vine.btBodies.get(0).rigid().applyRandomSpawnForces();
                  if (this.isFrozen()) {
                     vine.setFrozen(false);
                  }

                  for (Ragdoll.LinkedBody link : vine.btBodies) {
                     IRigidBody body = link.rigid();
                     if (body.getRigidBody() instanceof PxRigidDynamic) {
                        ((PxRigidDynamic)body.getRigidBody()).wakeUp();
                     }

                     if (body instanceof BasicRigidBody basic && !basic.isInWater()) {
                        body.setGravity(true);
                     }

                     MemoryStack mem = MemoryStack.stackPush();

                     try {
                        PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, 0, 0);
                        body.getShape().setSimulationFilterData(tmpFilterData);
                     } catch (Throwable var29) {
                        if (mem != null) {
                           try {
                              mem.close();
                           } catch (Throwable var27) {
                              var29.addSuppressed(var27);
                           }
                        }

                        throw var29;
                     }

                     if (mem != null) {
                        mem.close();
                     }
                  }

                  float rnd = vine.bodies.size() > 0 ? PhysicsWorld.calculateLifetime(vine.bodies.get(0)) : 0.0F;

                  for (Ragdoll.LinkedBody bodyx : vine.btBodies) {
                     PhysicsRenderable entity = bodyx.rigid().getEntity();
                     entity.type = PhysicsEntity.Type.BLOCK;
                     entity.time = rnd;
                     if (!this.linkedPhysics) {
                        entity.time = 0.0F;
                     }
                  }

                  if (this.hookJoint == releasedJoint) {
                     physics.getDynamicsWorld().removeActor(this.hookBody.rigid().getRigidBody());
                     this.btBodies.remove(this.hookBody);
                     this.hookBody.rigid().destroy();
                     this.hookBody = null;
                  }

                  this.setFrozen(false);
                  physics.getRagdolls().add(vine);
                  if (sideConnection) {
                     List<BlockPos> sorted = new ObjectArrayList(removedPositions);
                     Collections.sort(sorted, (a, b) -> -Integer.compare(a.getY(), b.getY()));

                     for (BlockPos removedPosition : sorted) {
                        physics.blockUpdate(removedPosition);
                     }
                  }

                  return true;
               } else {
                  BlockState before = this.bodiesState.get(index);
                  if (before != state) {
                     PhysicsEntity entity = PhysicsMod.getInstance(physics.getWorld())
                        .renderBlockIntoEntity(physics.getLevel(), PhysicsEntity.Type.VINE, state, pos, true);
                     if (entity == null) {
                        entity = new PhysicsEntity(PhysicsEntity.Type.VINE, state);
                        entity.getTransformation().set(new Matrix4d().translate((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
                        entity.models.get(0).mesh = new Mesh();
                        entity.models.get(0).mesh.offset = new Vector3f();
                     }

                     entity.enlargeHitbox.set(this.hitboxScale);
                     if (this.bodies.get(index).models.size() > 0) {
                        Vector3f diff = entity.models.get(0).mesh.offset.sub(this.bodies.get(index).models.get(0).mesh.offset, new Vector3f());
                        diff.y %= 1.0F;
                        entity.models.get(0).mesh.move(diff);
                        entity.models.get(0).mesh.offset.set(this.bodies.get(index).models.get(0).mesh.offset);
                     }

                     this.bodies.get(index).destroy();
                     this.bodies.get(index).models = entity.models;
                     this.bodiesState.set(index, state);

                     for (Ragdoll.LinkedBody link : this.btBodies) {
                        if (link.entity().equals(this.bodies.get(index))) {
                           link.rigid().getEntity().models = entity.models;
                           physics.getQueueForModelCreation().add(link.rigid().getEntity());
                        }
                     }
                  }

                  return true;
               }
            } else {
               if (this.bodiesPos.size() > 0) {
                  int highestY = this.bodiesPos.get(0).getY();
                  int highestIndex = 0;

                  for (int ixx = 1; ixx < this.bodiesPos.size(); ixx++) {
                     int y = this.bodiesPos.get(ixx).getY();
                     if (this.bottomFixed) {
                        if (y > highestY) {
                           highestY = y;
                           highestIndex = ixx;
                        }
                     } else if (y < highestY) {
                        highestY = y;
                        highestIndex = ixx;
                     }
                  }

                  BlockPos check = new BlockPos(pos.getX(), highestY, pos.getZ());
                  index = this.bodiesPos.indexOf(check);
                  VineSetting setting = (VineSetting)VineHelper.getSetting(this.bodiesState.get(index));
                  DynamicSetting other = VineHelper.getSetting(state);
                  if (setting != null
                     && index != -1
                     && (this.bottomFixed ? highestY + 1 == pos.getY() : highestY - 1 == pos.getY())
                     && setting.canLink(this.bodiesState.get(index), state)
                     && other instanceof VineSetting) {
                     Ragdoll.LinkedBody appendTo = null;
                     PhysicsEntity highestEntity = this.bodies.get(highestIndex);

                     for (int ixxx = 0; ixxx < this.btBodies.size(); ixxx++) {
                        Ragdoll.LinkedBody linkx = this.btBodies.get(ixxx);
                        if (linkx.entity().equals(highestEntity)) {
                           appendTo = linkx;
                           break;
                        }
                     }

                     PhysicsEntity entityx = PhysicsMod.getInstance(physics.getWorld())
                        .renderBlockIntoEntity(physics.getLevel(), PhysicsEntity.Type.VINE, state, pos, true);
                     if (entityx == null) {
                        entityx = new PhysicsEntity(PhysicsEntity.Type.VINE, state);
                        entityx.getTransformation().set(new Matrix4d().translate((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
                        entityx.models.get(0).mesh = new Mesh();
                        entityx.models.get(0).mesh.offset = new Vector3f();
                     }

                     entityx.enlargeHitbox.set(this.hitboxScale);
                     VineRagdoll.Connector connector = new VineRagdoll.Connector(check, pos);
                     this.connectors.add(connector);
                     this.bodiesPos.add(pos);
                     this.bodiesState.add(state);
                     this.bodies.add(entityx);
                     this.validateHitbox();
                     IRigidBody childLink = physics.addBlockParticleBox(entityx);
                     entityx.time = PhysicsWorld.calculateLifetime(entityx);
                     childLink.setFrozen(this.frozen);
                     childLink.separateController = true;
                     this.btBodies.add(new Ragdoll.LinkedBody(childLink, entityx));
                     MemoryStack mem = MemoryStack.stackPush();

                     try {
                        Vector3f localPos1 = childLink.getEntity().models.get(0).mesh.offset;
                        Vector3f localPos2 = appendTo.rigid().getEntity().models.get(0).mesh.offset;
                        double childOffY = this.bottomFixed ? -((double)localPos1.y % 1.0) : 1.0 - (double)localPos1.y % 1.0;
                        double parentOffY = this.bottomFixed ? 1.0 - (double)localPos2.y % 1.0 : -((double)localPos2.y % 1.0);
                        PxTransform parentPose = PxTransform.createAt(
                           mem,
                           MemoryStack::nmalloc,
                           PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, (float)parentOffY, 0.0F),
                           PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
                        );
                        PxTransform childPose = PxTransform.createAt(
                           mem,
                           MemoryStack::nmalloc,
                           PxVec3.createAt(mem, MemoryStack::nmalloc, localPos2.x - localPos1.x, (float)childOffY, localPos2.z - localPos1.z),
                           PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
                        );
                        PxD6Joint joint = this.createJoint(appendTo.rigid().getRigidBody(), parentPose, childLink.getRigidBody(), childPose);
                        this.pxJoints.add(joint);
                     } catch (Throwable var28) {
                        if (mem != null) {
                           try {
                              mem.close();
                           } catch (Throwable var26) {
                              var28.addSuppressed(var26);
                           }
                        }

                        throw var28;
                     }

                     if (mem != null) {
                        mem.close();
                     }

                     return true;
                  }
               }

               return false;
            }
         } else {
            return false;
         }
      }
   }

   private void moveBodiesIntoNewRagdoll(BlockPos pos, List<Ragdoll.LinkedBody> btBodiesNew, List<PhysicsEntity> bodiesNew) {
      int removeBody = this.bodiesPos.indexOf(pos);
      if (removeBody != -1) {
         this.bodiesPos.remove(removeBody);
         this.bodiesState.remove(removeBody);
         PhysicsEntity entity = this.bodies.remove(removeBody);
         bodiesNew.add(entity);

         for (int i = 0; i < this.btBodies.size(); i++) {
            Ragdoll.LinkedBody btBody = this.btBodies.get(i);
            if (btBody.entity().equals(entity)) {
               this.btBodies.remove(btBody);
               btBodiesNew.add(btBody);
               break;
            }
         }
      }
   }

   public void setAlwaysInWater(boolean alwaysInWater) {
      this.alwaysInWater = alwaysInWater;

      for (Ragdoll.LinkedBody link : this.btBodies) {
         IRigidBody body = link.rigid();
         if (body.hasGravity()) {
            body.setGravity(!alwaysInWater);
         }
      }
   }

   @Override
   protected void createHook(PhysicsWorld physics, PhysicsEntity particle, IRigidBody rigidBody) {
      super.createHook(physics, particle, rigidBody);
      BlockPos pos = this.bodiesPos.get(this.bodies.indexOf(particle));
      this.connectors.add(new VineRagdoll.Connector(pos, this.bottomFixed ? pos.below() : pos.above()));
   }

   @Override
   protected void createChildLink(PhysicsWorld physics, IRigidBody rootLink, PhysicsEntity rootEntity, Ragdoll.Node root, double rnd) {
      PhysicsEntity particle = this.bodies.get(root.index);
      if (!particle.noVolume) {
         RagdollJoint rjoint = this.joints.get(root.jointIndex);
         if (rjoint.fixed) {
            return;
         }

         if (rjoint.stopCollision) {
            particle.physicsGroup = 8;
            particle.physicsMask = 0;
         }

         IRigidBody childLink = physics.addBlockParticleBox(particle);
         childLink.setFrozen(this.frozen);
         childLink.separateController = true;
         this.btBodies.add(new Ragdoll.LinkedBody(childLink, particle));
         MemoryStack mem = MemoryStack.stackPush();

         try {
            PxTransform parentPose = PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, (float)rjoint.point1.x, (float)rjoint.point1.y, (float)rjoint.point1.z),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            );
            PxTransform childPose = PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, (float)rjoint.point2.x, (float)rjoint.point2.y, (float)rjoint.point2.z),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            );
            if (rjoint.index1 == root.index) {
               PxTransform tmp = parentPose;
               parentPose = childPose;
               childPose = tmp;
            }

            PxD6Joint joint = this.createJoint(rootLink.getRigidBody(), parentPose, childLink.getRigidBody(), childPose);
            this.pxJoints.add(joint);
            this.connectors
               .add(new VineRagdoll.Connector(this.bodiesPos.get(this.bodies.indexOf(rootEntity)), this.bodiesPos.get(this.bodies.indexOf(particle))));

            for (int i = 0; i < root.children.size(); i++) {
               this.createChildLink(physics, childLink, particle, root.children.get(i), rnd);
            }
         } catch (Throwable var16) {
            if (mem != null) {
               try {
                  mem.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }
            }

            throw var16;
         }

         if (mem != null) {
            mem.close();
         }

         if (particle.equals(this.hookedEntity)) {
            this.createHook(physics, particle, childLink);
         }
      }
   }

   @Override
   protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
      PxD6Joint joint = null;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
         joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLIMITED);
         PxD6JointDrive drive = new PxD6JointDrive(this.stiffness, this.damping, 100000.0F, true);
         joint.setDrive(PxD6DriveEnum.eSWING, drive);
         joint.setDrive(PxD6DriveEnum.eTWIST, drive);
         joint.setDrivePosition(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            )
         );
         joint.setDriveVelocity(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F), PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F));
         PxSpring spring = new PxSpring(60.0F, 10.0F);
         PxJointAngularLimitPair angularLimit = new PxJointAngularLimitPair((float) (-Math.PI / 8), (float) (Math.PI / 8), spring);
         joint.setTwistLimit(angularLimit);
         drive.destroy();
         spring.destroy();
         angularLimit.destroy();
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

   class Connector {
      BlockPos pos1;
      BlockPos pos2;

      public Connector(BlockPos pos1, BlockPos pos2) {
         Objects.requireNonNull(VineRagdoll.this);
         super();
         this.pos1 = pos1;
         this.pos2 = pos2;
      }

      public boolean connects(BlockPos pos1, BlockPos pos2) {
         return this.pos1.equals(pos1) && this.pos2.equals(pos2) || this.pos1.equals(pos2) && this.pos2.equals(pos1);
      }
   }
}
