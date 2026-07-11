package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.math.Math;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6AxisEnum;
import physx.extensions.PxD6Joint;
import physx.extensions.PxD6MotionEnum;
import physx.extensions.PxJoint;
import physx.extensions.PxJointAngularLimitPair;
import physx.extensions.PxJointLimitCone;
import physx.extensions.PxSpring;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;

public class BreakableRagdoll extends Ragdoll {
   private float breakForce;
   public List<PxJoint> pxJoints = new ObjectArrayList();

   public BreakableRagdoll(float breakForce) {
      this.breakForce = breakForce;
   }

   @Override
   public void add(PhysicsWorld physics) {
      float rnd = -1.0F;
      List<Ragdoll.Node> tree = this.generateTree();

      for (Ragdoll.Node root : tree) {
         for (int i = 0; i < root.children.size(); i++) {
            this.createChildLinkPrePass(root, root.children.get(i));
         }
      }

      for (Ragdoll.Node root : tree) {
         PhysicsEntity particle = this.bodies.get(root.index);
         if (!particle.noVolume) {
            if ((double)rnd < 0.0) {
               rnd = PhysicsWorld.calculateLifetime(particle);
            }

            if (physics.getBodies().size() == 0 && physics.getChunkBodies().size() == 0) {
               particle.getTransformation().getTranslation(physics.getOffset());
            }

            IRigidBody rigidBody = physics.addBlockParticle(particle);
            rigidBody.getEntity().time = rnd;
            rigidBody.separateController = true;
            this.btBodies.add(new Ragdoll.LinkedBody(rigidBody, particle));

            for (int i = 0; i < root.children.size(); i++) {
               this.createChildLink(physics, rigidBody, root, root.children.get(i), rnd);
            }
         }
      }

      if (this.velocity != null && this.velocity.lengthSquared() > 0.01) {
         for (Ragdoll.LinkedBody body : this.btBodies) {
            if (body.rigid().getRigidBody() instanceof PxRigidDynamic rigidBody) {
               float angularForce = 10.0F;
               PxVec3 v = rigidBody.getLinearVelocity();
               v.setX((float)this.velocity.x);
               v.setY((float)this.velocity.y);
               v.setZ((float)this.velocity.z);
               rigidBody.setLinearVelocity(v, true);
               PxVec3 a = rigidBody.getAngularVelocity();
               a.setX((Math.random() - 0.5F) * angularForce);
               a.setY((Math.random() - 0.5F) * angularForce);
               a.setZ((Math.random() - 0.5F) * angularForce);
               rigidBody.setAngularVelocity(a);
            }
         }

         this.velocity = null;
      }
   }

   private void createChildLink(PhysicsWorld physics, IRigidBody rootLink, Ragdoll.Node parent, Ragdoll.Node root, float rnd) {
      PhysicsEntity particle = this.bodies.get(root.index);
      if (!particle.noVolume) {
         RagdollJoint rjoint = this.joints.get(root.jointIndex);
         if (rjoint.fixed) {
            return;
         }

         if (rjoint.stopCollision) {
            particle.physicsGroup = 2;
            particle.physicsMask = 21;
         }

         IRigidBody rigidBody = physics.addBlockParticle(particle);
         rigidBody.getEntity().time = rnd;
         rigidBody.separateController = true;
         this.btBodies.add(new Ragdoll.LinkedBody(rigidBody, particle));
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

            PxJoint joint = this.createJoint(rootLink.getRigidBody(), parentPose, rigidBody.getRigidBody(), childPose);
            this.pxJoints.add(joint);
            physics.addJointParents(joint, new PhysicsWorld.Tuple<>(rootLink, rigidBody));
         } catch (Throwable var14) {
            if (mem != null) {
               try {
                  mem.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }
            }

            throw var14;
         }

         if (mem != null) {
            mem.close();
         }

         for (int i = 0; i < root.children.size(); i++) {
            this.createChildLink(physics, rigidBody, root, root.children.get(i), rnd);
         }
      }
   }

   protected PxD6Joint createJoint(PxRigidActor rigidBody1, PxTransform localPose1, PxRigidActor rigidBody2, PxTransform localPose2) {
      PxD6Joint joint = null;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         joint = PxTopLevelFunctions.D6JointCreate(StarterClient.physics, rigidBody1, localPose1, rigidBody2, localPose2);
         joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLIMITED);
         if (this.breakForce > 0.0F) {
            joint.setBreakForce(this.breakForce, this.breakForce);
         }

         PxSpring spring = new PxSpring(1.0F, 1.0F);
         PxJointLimitCone coneLimit = new PxJointLimitCone((float)java.lang.Math.toRadians(90.0), (float)java.lang.Math.toRadians(90.0), spring);
         joint.setSwingLimit(coneLimit);
         PxJointAngularLimitPair angularLimit = new PxJointAngularLimitPair(
            (float)java.lang.Math.toRadians(-90.0), (float)java.lang.Math.toRadians(90.0), spring
         );
         joint.setTwistLimit(angularLimit);
         spring.destroy();
         coneLimit.destroy();
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

   @Override
   public void remove(PhysicsWorld physicsWorld) {
      super.remove(physicsWorld);

      for (PxJoint joint : this.pxJoints) {
         physicsWorld.removeJointParents(joint);
      }

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

      this.btBodies.clear();
      this.pxJoints.clear();
   }
}
