package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.math.Math;
import net.diebuddies.physics.BoxRigidBody;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.snow.math.AABB3D;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6Joint;
import physx.extensions.PxJoint;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public abstract class DynamicRagdoll extends Ragdoll {
   public List<BlockPos> bodiesPos = new ObjectArrayList();
   public List<BlockState> bodiesState = new ObjectArrayList();
   public List<PxJoint> pxJoints = new ObjectArrayList();
   public PhysicsEntity hookedEntity;
   public Vector3d hook;
   public Ragdoll.LinkedBody hookBody;
   public PxD6Joint hookJoint;
   public boolean initFreeze = true;
   public boolean linkedPhysics;
   public boolean collision = true;
   public boolean markedForRemoval = false;
   public AABB3D aabb;
   public double distanceToCamera;

   public DynamicRagdoll() {
      this.frozen = true;
   }

   @Override
   public void updatePhysics(PhysicsWorld physics) {
      if (this.initFreeze) {
         if (this.bodies.size() == 0) {
            return;
         }

         Vector3d pos = this.bodies.get(0).getTransformation().getTranslation(new Vector3d());
         int chunkX = SectionPos.posToSectionCoord(pos.x + physics.getOffset().x);
         int chunkZ = SectionPos.posToSectionCoord(pos.z + physics.getOffset().z);
         if (physics.getWorld().getChunk(chunkX, chunkZ, null, false) != null) {
            this.initFreeze = false;
            this.validateHitbox();

            for (Ragdoll.LinkedBody body : this.btBodies) {
               body.rigid().recalculateLight();
            }
         }
      }
   }

   public void validateHitbox() {
      if (this.bodiesPos.size() == 0) {
         this.aabb = null;
      } else {
         this.aabb = new AABB3D(new Vector3d(Double.MAX_VALUE), new Vector3d(-Double.MAX_VALUE));
         Vector3d start = this.aabb.start;
         Vector3d end = this.aabb.end;
         Vector3d tmp = new Vector3d();

         for (BlockPos pos : this.bodiesPos) {
            start.min(tmp.set((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
            end.max(tmp.set((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
         }

         end.add(1.0, 1.0, 1.0);
      }
   }

   public void updateCameraDistance(Vec3 cameraPos) {
      this.distanceToCamera = this.aabb.distanceSquared(cameraPos.x, cameraPos.y, cameraPos.z);
   }

   public void wakeUp() {
      for (Ragdoll.LinkedBody body : this.btBodies) {
         if (body != this.hookBody && body.rigid().getRigidBody() instanceof PxRigidDynamic dynamic) {
            dynamic.wakeUp();
         }
      }
   }

   @Override
   public void add(PhysicsWorld physics) {
      double rnd = (double)Math.random() * 3.0;
      List<Ragdoll.Node> tree = this.generateTree();

      for (Ragdoll.Node root : tree) {
         for (int i = 0; i < root.children.size(); i++) {
            this.createChildLinkPrePass(root, root.children.get(i));
         }
      }

      for (Ragdoll.Node root : tree) {
         PhysicsEntity particle = this.bodies.get(root.index);
         if (!particle.noVolume) {
            if (physics.getBodies().size() == 0 && physics.getChunkBodies().size() == 0) {
               particle.getTransformation().getTranslation(physics.getOffset());
            }

            if (!this.collision) {
               particle.physicsGroup = 32;
               particle.physicsMask = 0;
            }

            IRigidBody rigidBody = physics.addBlockParticleBox(particle);
            rigidBody.setFrozen(this.frozen);
            rigidBody.separateController = true;
            this.btBodies.add(new Ragdoll.LinkedBody(rigidBody, particle));

            for (int i = 0; i < root.children.size(); i++) {
               this.createChildLink(physics, rigidBody, particle, root.children.get(i), rnd);
            }

            if (particle.equals(this.hookedEntity)) {
               this.createHook(physics, particle, rigidBody);
            }
         }
      }
   }

   protected void createHook(PhysicsWorld physics, PhysicsEntity particle, IRigidBody rigidBody) {
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.VINE, null);
      entity.getTransformation().translate(this.hook).translate(particle.getTransformation().getTranslation(new Vector3d()));
      entity.physicsGroup = 8;
      entity.physicsMask = 0;
      this.hookBody = new Ragdoll.LinkedBody(BoxRigidBody.create(entity, 0.5F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F, true), entity);
      this.hookBody.rigid().separateController = true;
      this.hookBody.rigid().setKinematic(true);
      this.btBodies.add(this.hookBody);
      physics.getDynamicsWorld().addActor(this.hookBody.rigid().getRigidBody());
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxTransform localPose1 = PxTransform.createAt(
            mem,
            MemoryStack::nmalloc,
            PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
            PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
         );
         PxTransform localPose2 = PxTransform.createAt(
            mem,
            MemoryStack::nmalloc,
            PxVec3.createAt(mem, MemoryStack::nmalloc, (float)this.hook.x, (float)this.hook.y, (float)this.hook.z),
            PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
         );
         this.hookJoint = this.createJoint(this.hookBody.rigid().getRigidBody(), localPose1, rigidBody.getRigidBody(), localPose2);
         this.pxJoints.add(this.hookJoint);
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
   }

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

         if (!this.collision) {
            particle.physicsGroup = 32;
            particle.physicsMask = 0;
         }

         IRigidBody childLink = physics.addBlockParticleBox(particle);
         childLink.setFrozen(this.frozen);
         childLink.separateController = true;
         if (childLink.getRigidBody() instanceof PxRigidDynamic) {
            ((PxRigidDynamic)childLink.getRigidBody()).setSleepThreshold(-1.0F);
         }

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

            PxJoint joint = this.createJoint(rootLink.getRigidBody(), parentPose, childLink.getRigidBody(), childPose);
            this.pxJoints.add(joint);

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

   protected abstract PxD6Joint createJoint(PxRigidActor var1, PxTransform var2, PxRigidActor var3, PxTransform var4);

   public List<BlockPos> getBlockPositions() {
      return this.bodiesPos;
   }

   public List<BlockState> getBlockStates() {
      return this.bodiesState;
   }

   @Override
   public void setKinematic(boolean kinematic) {
      super.setKinematic(kinematic);
      if (this.hookBody != null) {
         this.hookBody.rigid().setKinematic(true);
      }
   }

   @Override
   public void setFrozen(boolean frozen) {
      if (this.frozen != frozen) {
         for (Ragdoll.LinkedBody body : this.btBodies) {
            if (body != this.hookBody) {
               body.rigid().setFrozen(frozen);
            }
         }

         this.frozen = frozen;
      }
   }

   @Override
   public void remove(PhysicsWorld physicsWorld) {
      super.remove(physicsWorld);

      for (Ragdoll.LinkedBody body : this.btBodies) {
         physicsWorld.getDynamicsWorld().removeActor(body.rigid().getRigidBody());
      }
   }

   @Override
   public void destroy() {
      super.destroy();

      for (Ragdoll.LinkedBody body : this.btBodies) {
         body.rigid().destroy();
      }

      for (PxJoint joint : this.pxJoints) {
         joint.release();
      }

      if (this.hookBody != null) {
         this.hookBody.rigid().destroy();
         this.hookBody = null;
      }

      this.btBodies.clear();
      this.pxJoints.clear();
   }
}
