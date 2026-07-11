package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.callbacks.ContactImpulse;
import net.diebuddies.jbox2d.callbacks.ContactListener;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Sweep;
import net.diebuddies.jbox2d.common.Timer;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;
import net.diebuddies.jbox2d.dynamics.contacts.ContactSolver;
import net.diebuddies.jbox2d.dynamics.contacts.ContactVelocityConstraint;
import net.diebuddies.jbox2d.dynamics.contacts.Position;
import net.diebuddies.jbox2d.dynamics.contacts.Velocity;
import net.diebuddies.jbox2d.dynamics.joints.Joint;

public class Island {
   public ContactListener m_listener;
   public Body[] m_bodies;
   public Contact[] m_contacts;
   public Joint[] m_joints;
   public Position[] m_positions;
   public Velocity[] m_velocities;
   public int m_bodyCount;
   public int m_jointCount;
   public int m_contactCount;
   public int m_bodyCapacity;
   public int m_contactCapacity;
   public int m_jointCapacity;
   private final ContactSolver contactSolver = new ContactSolver();
   private final Timer timer = new Timer();
   private final SolverData solverData = new SolverData();
   private final ContactSolver.ContactSolverDef solverDef = new ContactSolver.ContactSolverDef();
   private final ContactSolver toiContactSolver = new ContactSolver();
   private final ContactSolver.ContactSolverDef toiSolverDef = new ContactSolver.ContactSolverDef();
   private final ContactImpulse impulse = new ContactImpulse();

   public void init(int bodyCapacity, int contactCapacity, int jointCapacity, ContactListener listener) {
      this.m_bodyCapacity = bodyCapacity;
      this.m_contactCapacity = contactCapacity;
      this.m_jointCapacity = jointCapacity;
      this.m_bodyCount = 0;
      this.m_contactCount = 0;
      this.m_jointCount = 0;
      this.m_listener = listener;
      if (this.m_bodies == null || this.m_bodyCapacity > this.m_bodies.length) {
         this.m_bodies = new Body[this.m_bodyCapacity];
      }

      if (this.m_joints == null || this.m_jointCapacity > this.m_joints.length) {
         this.m_joints = new Joint[this.m_jointCapacity];
      }

      if (this.m_contacts == null || this.m_contactCapacity > this.m_contacts.length) {
         this.m_contacts = new Contact[this.m_contactCapacity];
      }

      if (this.m_velocities == null || this.m_bodyCapacity > this.m_velocities.length) {
         Velocity[] old = this.m_velocities == null ? new Velocity[0] : this.m_velocities;
         this.m_velocities = new Velocity[this.m_bodyCapacity];
         System.arraycopy(old, 0, this.m_velocities, 0, old.length);

         for (int i = old.length; i < this.m_velocities.length; i++) {
            this.m_velocities[i] = new Velocity();
         }
      }

      if (this.m_positions == null || this.m_bodyCapacity > this.m_positions.length) {
         Position[] old = this.m_positions == null ? new Position[0] : this.m_positions;
         this.m_positions = new Position[this.m_bodyCapacity];
         System.arraycopy(old, 0, this.m_positions, 0, old.length);

         for (int i = old.length; i < this.m_positions.length; i++) {
            this.m_positions[i] = new Position();
         }
      }
   }

   public void clear() {
      this.m_bodyCount = 0;
      this.m_contactCount = 0;
      this.m_jointCount = 0;
   }

   public void solve(Profile profile, TimeStep step, Vec2 gravity, boolean allowSleep) {
      float h = step.dt;

      for (int i = 0; i < this.m_bodyCount; i++) {
         Body b = this.m_bodies[i];
         Sweep bm_sweep = b.m_sweep;
         Vec2 c = bm_sweep.c;
         float a = bm_sweep.a;
         Vec2 v = b.m_linearVelocity;
         float w = b.m_angularVelocity;
         bm_sweep.c0.set(bm_sweep.c);
         bm_sweep.a0 = bm_sweep.a;
         if (b.m_type == BodyType.DYNAMIC) {
            v.x = v.x + h * (b.m_gravityScale * gravity.x + b.m_invMass * b.m_force.x);
            v.y = v.y + h * (b.m_gravityScale * gravity.y + b.m_invMass * b.m_force.y);
            w += h * b.m_invI * b.m_torque;
            v.x = v.x * (1.0F / (1.0F + h * b.m_linearDamping));
            v.y = v.y * (1.0F / (1.0F + h * b.m_linearDamping));
            w *= 1.0F / (1.0F + h * b.m_angularDamping);
         }

         this.m_positions[i].c.x = c.x;
         this.m_positions[i].c.y = c.y;
         this.m_positions[i].a = a;
         this.m_velocities[i].v.x = v.x;
         this.m_velocities[i].v.y = v.y;
         this.m_velocities[i].w = w;
      }

      this.timer.reset();
      this.solverData.step = step;
      this.solverData.positions = this.m_positions;
      this.solverData.velocities = this.m_velocities;
      this.solverDef.step = step;
      this.solverDef.contacts = this.m_contacts;
      this.solverDef.count = this.m_contactCount;
      this.solverDef.positions = this.m_positions;
      this.solverDef.velocities = this.m_velocities;
      this.contactSolver.init(this.solverDef);
      this.contactSolver.initializeVelocityConstraints();
      if (step.warmStarting) {
         this.contactSolver.warmStart();
      }

      for (int i = 0; i < this.m_jointCount; i++) {
         this.m_joints[i].initVelocityConstraints(this.solverData);
      }

      profile.solveInit.accum(this.timer.getMilliseconds());
      this.timer.reset();

      for (int i = 0; i < step.velocityIterations; i++) {
         for (int j = 0; j < this.m_jointCount; j++) {
            this.m_joints[j].solveVelocityConstraints(this.solverData);
         }

         this.contactSolver.solveVelocityConstraints();
      }

      this.contactSolver.storeImpulses();
      profile.solveVelocity.accum(this.timer.getMilliseconds());

      for (int i = 0; i < this.m_bodyCount; i++) {
         Vec2 c = this.m_positions[i].c;
         float a = this.m_positions[i].a;
         Vec2 v = this.m_velocities[i].v;
         float w = this.m_velocities[i].w;
         float translationx = v.x * h;
         float translationy = v.y * h;
         if (translationx * translationx + translationy * translationy > Settings.maxTranslationSquared) {
            float ratio = Settings.maxTranslation / MathUtils.sqrt(translationx * translationx + translationy * translationy);
            v.x *= ratio;
            v.y *= ratio;
         }

         float rotation = h * w;
         if (rotation * rotation > Settings.maxRotationSquared) {
            float ratio = Settings.maxRotation / MathUtils.abs(rotation);
            w *= ratio;
         }

         c.x = c.x + h * v.x;
         c.y = c.y + h * v.y;
         a += h * w;
         this.m_positions[i].a = a;
         this.m_velocities[i].w = w;
      }

      this.timer.reset();
      boolean positionSolved = false;

      for (int i = 0; i < step.positionIterations; i++) {
         boolean contactsOkay = this.contactSolver.solvePositionConstraints();
         boolean jointsOkay = true;

         for (int j = 0; j < this.m_jointCount; j++) {
            boolean jointOkay = this.m_joints[j].solvePositionConstraints(this.solverData);
            jointsOkay = jointsOkay && jointOkay;
         }

         if (contactsOkay && jointsOkay) {
            positionSolved = true;
            break;
         }
      }

      for (int i = 0; i < this.m_bodyCount; i++) {
         Body body = this.m_bodies[i];
         body.m_sweep.c.x = this.m_positions[i].c.x;
         body.m_sweep.c.y = this.m_positions[i].c.y;
         body.m_sweep.a = this.m_positions[i].a;
         body.m_linearVelocity.x = this.m_velocities[i].v.x;
         body.m_linearVelocity.y = this.m_velocities[i].v.y;
         body.m_angularVelocity = this.m_velocities[i].w;
         body.synchronizeTransform();
      }

      profile.solvePosition.accum(this.timer.getMilliseconds());
      this.report(this.contactSolver.m_velocityConstraints);
      if (allowSleep) {
         float minSleepTime = Float.MAX_VALUE;
         float linTolSqr = Settings.linearSleepTolerance * Settings.linearSleepTolerance;
         float angTolSqr = Settings.angularSleepTolerance * Settings.angularSleepTolerance;

         for (int i = 0; i < this.m_bodyCount; i++) {
            Body b = this.m_bodies[i];
            if (b.getType() != BodyType.STATIC) {
               if ((b.m_flags & 4) != 0
                  && !(b.m_angularVelocity * b.m_angularVelocity > angTolSqr)
                  && !(Vec2.dot(b.m_linearVelocity, b.m_linearVelocity) > linTolSqr)) {
                  b.m_sleepTime += h;
                  minSleepTime = MathUtils.min(minSleepTime, b.m_sleepTime);
               } else {
                  b.m_sleepTime = 0.0F;
                  minSleepTime = 0.0F;
               }
            }
         }

         if (minSleepTime >= Settings.timeToSleep && positionSolved) {
            for (int ix = 0; ix < this.m_bodyCount; ix++) {
               Body b = this.m_bodies[ix];
               b.setAwake(false);
            }
         }
      }
   }

   public void solveTOI(TimeStep subStep, int toiIndexA, int toiIndexB) {
      assert toiIndexA < this.m_bodyCount;

      assert toiIndexB < this.m_bodyCount;

      for (int i = 0; i < this.m_bodyCount; i++) {
         this.m_positions[i].c.x = this.m_bodies[i].m_sweep.c.x;
         this.m_positions[i].c.y = this.m_bodies[i].m_sweep.c.y;
         this.m_positions[i].a = this.m_bodies[i].m_sweep.a;
         this.m_velocities[i].v.x = this.m_bodies[i].m_linearVelocity.x;
         this.m_velocities[i].v.y = this.m_bodies[i].m_linearVelocity.y;
         this.m_velocities[i].w = this.m_bodies[i].m_angularVelocity;
      }

      this.toiSolverDef.contacts = this.m_contacts;
      this.toiSolverDef.count = this.m_contactCount;
      this.toiSolverDef.step = subStep;
      this.toiSolverDef.positions = this.m_positions;
      this.toiSolverDef.velocities = this.m_velocities;
      this.toiContactSolver.init(this.toiSolverDef);

      for (int i = 0; i < subStep.positionIterations; i++) {
         boolean contactsOkay = this.toiContactSolver.solveTOIPositionConstraints(toiIndexA, toiIndexB);
         if (contactsOkay) {
            break;
         }
      }

      this.m_bodies[toiIndexA].m_sweep.c0.x = this.m_positions[toiIndexA].c.x;
      this.m_bodies[toiIndexA].m_sweep.c0.y = this.m_positions[toiIndexA].c.y;
      this.m_bodies[toiIndexA].m_sweep.a0 = this.m_positions[toiIndexA].a;
      this.m_bodies[toiIndexB].m_sweep.c0.set(this.m_positions[toiIndexB].c);
      this.m_bodies[toiIndexB].m_sweep.a0 = this.m_positions[toiIndexB].a;
      this.toiContactSolver.initializeVelocityConstraints();

      for (int ix = 0; ix < subStep.velocityIterations; ix++) {
         this.toiContactSolver.solveVelocityConstraints();
      }

      float h = subStep.dt;

      for (int ix = 0; ix < this.m_bodyCount; ix++) {
         Vec2 c = this.m_positions[ix].c;
         float a = this.m_positions[ix].a;
         Vec2 v = this.m_velocities[ix].v;
         float w = this.m_velocities[ix].w;
         float translationx = v.x * h;
         float translationy = v.y * h;
         if (translationx * translationx + translationy * translationy > Settings.maxTranslationSquared) {
            float ratio = Settings.maxTranslation / MathUtils.sqrt(translationx * translationx + translationy * translationy);
            v.mulLocal(ratio);
         }

         float rotation = h * w;
         if (rotation * rotation > Settings.maxRotationSquared) {
            float ratio = Settings.maxRotation / MathUtils.abs(rotation);
            w *= ratio;
         }

         c.x = c.x + v.x * h;
         c.y = c.y + v.y * h;
         a += h * w;
         this.m_positions[ix].c.x = c.x;
         this.m_positions[ix].c.y = c.y;
         this.m_positions[ix].a = a;
         this.m_velocities[ix].v.x = v.x;
         this.m_velocities[ix].v.y = v.y;
         this.m_velocities[ix].w = w;
         Body body = this.m_bodies[ix];
         body.m_sweep.c.x = c.x;
         body.m_sweep.c.y = c.y;
         body.m_sweep.a = a;
         body.m_linearVelocity.x = v.x;
         body.m_linearVelocity.y = v.y;
         body.m_angularVelocity = w;
         body.synchronizeTransform();
      }

      this.report(this.toiContactSolver.m_velocityConstraints);
   }

   public void add(Body body) {
      assert this.m_bodyCount < this.m_bodyCapacity;

      body.m_islandIndex = this.m_bodyCount;
      this.m_bodies[this.m_bodyCount] = body;
      this.m_bodyCount++;
   }

   public void add(Contact contact) {
      assert this.m_contactCount < this.m_contactCapacity;

      this.m_contacts[this.m_contactCount++] = contact;
   }

   public void add(Joint joint) {
      assert this.m_jointCount < this.m_jointCapacity;

      this.m_joints[this.m_jointCount++] = joint;
   }

   public void report(ContactVelocityConstraint[] constraints) {
      if (this.m_listener != null) {
         for (int i = 0; i < this.m_contactCount; i++) {
            Contact c = this.m_contacts[i];
            ContactVelocityConstraint vc = constraints[i];
            this.impulse.count = vc.pointCount;

            for (int j = 0; j < vc.pointCount; j++) {
               this.impulse.normalImpulses[j] = vc.points[j].normalImpulse;
               this.impulse.tangentImpulses[j] = vc.points[j].tangentImpulse;
            }

            this.m_listener.postSolve(c, this.impulse);
         }
      }
   }
}
