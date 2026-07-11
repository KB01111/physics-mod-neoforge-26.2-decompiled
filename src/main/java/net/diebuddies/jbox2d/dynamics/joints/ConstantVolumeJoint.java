package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.jbox2d.dynamics.contacts.Position;
import net.diebuddies.jbox2d.dynamics.contacts.Velocity;

public class ConstantVolumeJoint extends Joint {
   private final Body[] bodies;
   private float[] targetLengths;
   private float targetVolume;
   private Vec2[] normals;
   private float m_impulse = 0.0F;
   private World world;
   private DistanceJoint[] distanceJoints;

   public Body[] getBodies() {
      return this.bodies;
   }

   public DistanceJoint[] getJoints() {
      return this.distanceJoints;
   }

   public void inflate(float factor) {
      this.targetVolume *= factor;
   }

   public ConstantVolumeJoint(World argWorld, ConstantVolumeJointDef def) {
      super(argWorld.getPool(), def);
      this.world = argWorld;
      if (def.bodies.size() <= 2) {
         throw new IllegalArgumentException("You cannot create a constant volume joint with less than three bodies.");
      } else {
         this.bodies = def.bodies.toArray(new Body[0]);
         this.targetLengths = new float[this.bodies.length];

         for (int i = 0; i < this.targetLengths.length; i++) {
            int next = i == this.targetLengths.length - 1 ? 0 : i + 1;
            float dist = this.bodies[i].getWorldCenter().sub(this.bodies[next].getWorldCenter()).length();
            this.targetLengths[i] = dist;
         }

         this.targetVolume = this.getBodyArea();
         if (def.joints != null && def.joints.size() != def.bodies.size()) {
            throw new IllegalArgumentException("Incorrect joint definition.  Joints have to correspond to the bodies");
         } else {
            if (def.joints == null) {
               DistanceJointDef djd = new DistanceJointDef();
               this.distanceJoints = new DistanceJoint[this.bodies.length];

               for (int i = 0; i < this.targetLengths.length; i++) {
                  int next = i == this.targetLengths.length - 1 ? 0 : i + 1;
                  djd.frequencyHz = def.frequencyHz;
                  djd.dampingRatio = def.dampingRatio;
                  djd.collideConnected = def.collideConnected;
                  djd.initialize(this.bodies[i], this.bodies[next], this.bodies[i].getWorldCenter(), this.bodies[next].getWorldCenter());
                  this.distanceJoints[i] = (DistanceJoint)this.world.createJoint(djd);
               }
            } else {
               this.distanceJoints = def.joints.toArray(new DistanceJoint[0]);
            }

            this.normals = new Vec2[this.bodies.length];

            for (int i = 0; i < this.normals.length; i++) {
               this.normals[i] = new Vec2();
            }
         }
      }
   }

   @Override
   public void destructor() {
      for (int i = 0; i < this.distanceJoints.length; i++) {
         this.world.destroyJoint(this.distanceJoints[i]);
      }
   }

   private float getBodyArea() {
      float area = 0.0F;

      for (int i = 0; i < this.bodies.length; i++) {
         int next = i == this.bodies.length - 1 ? 0 : i + 1;
         area += this.bodies[i].getWorldCenter().x * this.bodies[next].getWorldCenter().y
            - this.bodies[next].getWorldCenter().x * this.bodies[i].getWorldCenter().y;
      }

      return area * 0.5F;
   }

   private float getSolverArea(Position[] positions) {
      float area = 0.0F;

      for (int i = 0; i < this.bodies.length; i++) {
         int next = i == this.bodies.length - 1 ? 0 : i + 1;
         area += positions[this.bodies[i].m_islandIndex].c.x * positions[this.bodies[next].m_islandIndex].c.y
            - positions[this.bodies[next].m_islandIndex].c.x * positions[this.bodies[i].m_islandIndex].c.y;
      }

      return area * 0.5F;
   }

   private boolean constrainEdges(Position[] positions) {
      float perimeter = 0.0F;

      for (int i = 0; i < this.bodies.length; i++) {
         int next = i == this.bodies.length - 1 ? 0 : i + 1;
         float dx = positions[this.bodies[next].m_islandIndex].c.x - positions[this.bodies[i].m_islandIndex].c.x;
         float dy = positions[this.bodies[next].m_islandIndex].c.y - positions[this.bodies[i].m_islandIndex].c.y;
         float dist = MathUtils.sqrt(dx * dx + dy * dy);
         if (dist < 1.1920929E-7F) {
            dist = 1.0F;
         }

         this.normals[i].x = dy / dist;
         this.normals[i].y = -dx / dist;
         perimeter += dist;
      }

      Vec2 delta = this.pool.popVec2();
      float deltaArea = this.targetVolume - this.getSolverArea(positions);
      float toExtrude = 0.5F * deltaArea / perimeter;
      boolean done = true;

      for (int i = 0; i < this.bodies.length; i++) {
         int next = i == this.bodies.length - 1 ? 0 : i + 1;
         delta.set(toExtrude * (this.normals[i].x + this.normals[next].x), toExtrude * (this.normals[i].y + this.normals[next].y));
         float normSqrd = delta.lengthSquared();
         if (normSqrd > Settings.maxLinearCorrection * Settings.maxLinearCorrection) {
            delta.mulLocal(Settings.maxLinearCorrection / MathUtils.sqrt(normSqrd));
         }

         if (normSqrd > Settings.linearSlop * Settings.linearSlop) {
            done = false;
         }

         positions[this.bodies[next].m_islandIndex].c.x = positions[this.bodies[next].m_islandIndex].c.x + delta.x;
         positions[this.bodies[next].m_islandIndex].c.y = positions[this.bodies[next].m_islandIndex].c.y + delta.y;
      }

      this.pool.pushVec2(1);
      return done;
   }

   @Override
   public void initVelocityConstraints(SolverData step) {
      Velocity[] velocities = step.velocities;
      Position[] positions = step.positions;
      Vec2[] d = this.pool.getVec2Array(this.bodies.length);

      for (int i = 0; i < this.bodies.length; i++) {
         int prev = i == 0 ? this.bodies.length - 1 : i - 1;
         int next = i == this.bodies.length - 1 ? 0 : i + 1;
         d[i].set(positions[this.bodies[next].m_islandIndex].c);
         d[i].subLocal(positions[this.bodies[prev].m_islandIndex].c);
      }

      if (step.step.warmStarting) {
         this.m_impulse = this.m_impulse * step.step.dtRatio;

         for (int i = 0; i < this.bodies.length; i++) {
            velocities[this.bodies[i].m_islandIndex].v.x = velocities[this.bodies[i].m_islandIndex].v.x
               + this.bodies[i].m_invMass * d[i].y * 0.5F * this.m_impulse;
            velocities[this.bodies[i].m_islandIndex].v.y = velocities[this.bodies[i].m_islandIndex].v.y
               + this.bodies[i].m_invMass * -d[i].x * 0.5F * this.m_impulse;
         }
      } else {
         this.m_impulse = 0.0F;
      }
   }

   @Override
   public boolean solvePositionConstraints(SolverData step) {
      return this.constrainEdges(step.positions);
   }

   @Override
   public void solveVelocityConstraints(SolverData step) {
      float crossMassSum = 0.0F;
      float dotMassSum = 0.0F;
      Velocity[] velocities = step.velocities;
      Position[] positions = step.positions;
      Vec2[] d = this.pool.getVec2Array(this.bodies.length);

      for (int i = 0; i < this.bodies.length; i++) {
         int prev = i == 0 ? this.bodies.length - 1 : i - 1;
         int next = i == this.bodies.length - 1 ? 0 : i + 1;
         d[i].set(positions[this.bodies[next].m_islandIndex].c);
         d[i].subLocal(positions[this.bodies[prev].m_islandIndex].c);
         dotMassSum += d[i].lengthSquared() / this.bodies[i].getMass();
         crossMassSum += Vec2.cross(velocities[this.bodies[i].m_islandIndex].v, d[i]);
      }

      float lambda = -2.0F * crossMassSum / dotMassSum;
      this.m_impulse += lambda;

      for (int i = 0; i < this.bodies.length; i++) {
         velocities[this.bodies[i].m_islandIndex].v.x = velocities[this.bodies[i].m_islandIndex].v.x + this.bodies[i].m_invMass * d[i].y * 0.5F * lambda;
         velocities[this.bodies[i].m_islandIndex].v.y = velocities[this.bodies[i].m_islandIndex].v.y + this.bodies[i].m_invMass * -d[i].x * 0.5F * lambda;
      }
   }

   @Override
   public void getAnchorA(Vec2 argOut) {
   }

   @Override
   public void getAnchorB(Vec2 argOut) {
   }

   @Override
   public void getReactionForce(float inv_dt, Vec2 argOut) {
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return 0.0F;
   }
}
