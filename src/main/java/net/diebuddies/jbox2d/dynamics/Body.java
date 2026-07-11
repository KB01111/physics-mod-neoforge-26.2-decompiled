package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.collision.broadphase.BroadPhase;
import net.diebuddies.jbox2d.collision.shapes.MassData;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Sweep;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;
import net.diebuddies.jbox2d.dynamics.contacts.ContactEdge;
import net.diebuddies.jbox2d.dynamics.joints.JointEdge;

public class Body {
   public static final int e_islandFlag = 1;
   public static final int e_awakeFlag = 2;
   public static final int e_autoSleepFlag = 4;
   public static final int e_bulletFlag = 8;
   public static final int e_fixedRotationFlag = 16;
   public static final int e_activeFlag = 32;
   public static final int e_toiFlag = 64;
   public BodyType m_type;
   public int m_flags;
   public int m_islandIndex;
   public final Transform m_xf = new Transform();
   public final Transform m_xf0 = new Transform();
   public final Sweep m_sweep = new Sweep();
   public final Vec2 m_linearVelocity = new Vec2();
   public float m_angularVelocity = 0.0F;
   public final Vec2 m_force = new Vec2();
   public float m_torque = 0.0F;
   public World m_world;
   public Body m_prev;
   public Body m_next;
   public Fixture m_fixtureList;
   public int m_fixtureCount;
   public JointEdge m_jointList;
   public ContactEdge m_contactList;
   public float m_mass;
   public float m_invMass;
   public float m_I;
   public float m_invI;
   public float m_linearDamping;
   public float m_angularDamping;
   public float m_gravityScale;
   public float m_sleepTime;
   public Object m_userData;
   private final FixtureDef fixDef = new FixtureDef();
   private final MassData pmd = new MassData();
   private final Transform pxf = new Transform();

   public Body(BodyDef bd, World world) {
      assert bd.position.isValid();

      assert bd.linearVelocity.isValid();

      assert bd.gravityScale >= 0.0F;

      assert bd.angularDamping >= 0.0F;

      assert bd.linearDamping >= 0.0F;

      this.m_flags = 0;
      if (bd.bullet) {
         this.m_flags |= 8;
      }

      if (bd.fixedRotation) {
         this.m_flags |= 16;
      }

      if (bd.allowSleep) {
         this.m_flags |= 4;
      }

      if (bd.awake) {
         this.m_flags |= 2;
      }

      if (bd.active) {
         this.m_flags |= 32;
      }

      this.m_world = world;
      this.m_xf.p.set(bd.position);
      this.m_xf.q.set(bd.angle);
      this.m_sweep.localCenter.setZero();
      this.m_sweep.c0.set(this.m_xf.p);
      this.m_sweep.c.set(this.m_xf.p);
      this.m_sweep.a0 = bd.angle;
      this.m_sweep.a = bd.angle;
      this.m_sweep.alpha0 = 0.0F;
      this.m_jointList = null;
      this.m_contactList = null;
      this.m_prev = null;
      this.m_next = null;
      this.m_linearVelocity.set(bd.linearVelocity);
      this.m_angularVelocity = bd.angularVelocity;
      this.m_linearDamping = bd.linearDamping;
      this.m_angularDamping = bd.angularDamping;
      this.m_gravityScale = bd.gravityScale;
      this.m_force.setZero();
      this.m_torque = 0.0F;
      this.m_sleepTime = 0.0F;
      this.m_type = bd.type;
      if (this.m_type == BodyType.DYNAMIC) {
         this.m_mass = 1.0F;
         this.m_invMass = 1.0F;
      } else {
         this.m_mass = 0.0F;
         this.m_invMass = 0.0F;
      }

      this.m_I = 0.0F;
      this.m_invI = 0.0F;
      this.m_userData = bd.userData;
      this.m_fixtureList = null;
      this.m_fixtureCount = 0;
   }

   public final Fixture createFixture(FixtureDef def) {
      assert !this.m_world.isLocked();

      if (this.m_world.isLocked()) {
         return null;
      } else {
         Fixture fixture = new Fixture();
         fixture.create(this, def);
         if ((this.m_flags & 32) == 32) {
            BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
            fixture.createProxies(broadPhase, this.m_xf);
         }

         fixture.m_next = this.m_fixtureList;
         this.m_fixtureList = fixture;
         this.m_fixtureCount++;
         fixture.m_body = this;
         if (fixture.m_density > 0.0F) {
            this.resetMassData();
         }

         this.m_world.m_flags |= 1;
         return fixture;
      }
   }

   public final Fixture createFixture(Shape shape, float density) {
      this.fixDef.shape = shape;
      this.fixDef.density = density;
      return this.createFixture(this.fixDef);
   }

   public final void destroyFixture(Fixture fixture) {
      assert !this.m_world.isLocked();

      if (!this.m_world.isLocked()) {
         assert fixture.m_body == this;

         assert this.m_fixtureCount > 0;

         Fixture node = this.m_fixtureList;
         Fixture last = null;

         boolean found;
         for (found = false; node != null; node = node.m_next) {
            if (node == fixture) {
               node = fixture.m_next;
               found = true;
               break;
            }

            last = node;
         }

         assert found;

         if (last == null) {
            this.m_fixtureList = fixture.m_next;
         } else {
            last.m_next = fixture.m_next;
         }

         ContactEdge edge = this.m_contactList;

         while (edge != null) {
            Contact c = edge.contact;
            edge = edge.next;
            Fixture fixtureA = c.getFixtureA();
            Fixture fixtureB = c.getFixtureB();
            if (fixture == fixtureA || fixture == fixtureB) {
               this.m_world.m_contactManager.destroy(c);
            }
         }

         if ((this.m_flags & 32) == 32) {
            BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;
            fixture.destroyProxies(broadPhase);
         }

         fixture.destroy();
         fixture.m_body = null;
         fixture.m_next = null;
         Fixture var9 = null;
         this.m_fixtureCount--;
         this.resetMassData();
      }
   }

   public final void setTransform(Vec2 position, float angle) {
      assert !this.m_world.isLocked();

      if (!this.m_world.isLocked()) {
         this.m_xf.q.set(angle);
         this.m_xf.p.set(position);
         Transform.mulToOutUnsafe(this.m_xf, this.m_sweep.localCenter, this.m_sweep.c);
         this.m_sweep.a = angle;
         this.m_sweep.c0.set(this.m_sweep.c);
         this.m_sweep.a0 = this.m_sweep.a;
         BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;

         for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
            f.synchronize(broadPhase, this.m_xf, this.m_xf);
         }
      }
   }

   public final Transform getTransform() {
      return this.m_xf;
   }

   public final Vec2 getPosition() {
      return this.m_xf.p;
   }

   public final float getAngle() {
      return this.m_sweep.a;
   }

   public final Vec2 getWorldCenter() {
      return this.m_sweep.c;
   }

   public final Vec2 getLocalCenter() {
      return this.m_sweep.localCenter;
   }

   public final void setLinearVelocity(Vec2 v) {
      if (this.m_type != BodyType.STATIC) {
         if (Vec2.dot(v, v) > 0.0F) {
            this.setAwake(true);
         }

         this.m_linearVelocity.set(v);
      }
   }

   public final Vec2 getLinearVelocity() {
      return this.m_linearVelocity;
   }

   public final void setAngularVelocity(float w) {
      if (this.m_type != BodyType.STATIC) {
         if (w * w > 0.0F) {
            this.setAwake(true);
         }

         this.m_angularVelocity = w;
      }
   }

   public final float getAngularVelocity() {
      return this.m_angularVelocity;
   }

   public float getGravityScale() {
      return this.m_gravityScale;
   }

   public void setGravityScale(float gravityScale) {
      this.m_gravityScale = gravityScale;
   }

   public final void applyForce(Vec2 force, Vec2 point) {
      if (this.m_type == BodyType.DYNAMIC) {
         if (!this.isAwake()) {
            this.setAwake(true);
         }

         this.m_force.x = this.m_force.x + force.x;
         this.m_force.y = this.m_force.y + force.y;
         this.m_torque = this.m_torque + ((point.x - this.m_sweep.c.x) * force.y - (point.y - this.m_sweep.c.y) * force.x);
      }
   }

   public final void applyForceToCenter(Vec2 force) {
      if (this.m_type == BodyType.DYNAMIC) {
         if (!this.isAwake()) {
            this.setAwake(true);
         }

         this.m_force.x = this.m_force.x + force.x;
         this.m_force.y = this.m_force.y + force.y;
      }
   }

   public final void applyTorque(float torque) {
      if (this.m_type == BodyType.DYNAMIC) {
         if (!this.isAwake()) {
            this.setAwake(true);
         }

         this.m_torque += torque;
      }
   }

   public final void applyLinearImpulse(Vec2 impulse, Vec2 point, boolean wake) {
      if (this.m_type == BodyType.DYNAMIC) {
         if (!this.isAwake()) {
            if (!wake) {
               return;
            }

            this.setAwake(true);
         }

         this.m_linearVelocity.x = this.m_linearVelocity.x + impulse.x * this.m_invMass;
         this.m_linearVelocity.y = this.m_linearVelocity.y + impulse.y * this.m_invMass;
         this.m_angularVelocity = this.m_angularVelocity + this.m_invI * ((point.x - this.m_sweep.c.x) * impulse.y - (point.y - this.m_sweep.c.y) * impulse.x);
      }
   }

   public void applyAngularImpulse(float impulse) {
      if (this.m_type == BodyType.DYNAMIC) {
         if (!this.isAwake()) {
            this.setAwake(true);
         }

         this.m_angularVelocity = this.m_angularVelocity + this.m_invI * impulse;
      }
   }

   public final float getMass() {
      return this.m_mass;
   }

   public final float getInertia() {
      return this.m_I + this.m_mass * (this.m_sweep.localCenter.x * this.m_sweep.localCenter.x + this.m_sweep.localCenter.y * this.m_sweep.localCenter.y);
   }

   public final void getMassData(MassData data) {
      data.mass = this.m_mass;
      data.I = this.m_I + this.m_mass * (this.m_sweep.localCenter.x * this.m_sweep.localCenter.x + this.m_sweep.localCenter.y * this.m_sweep.localCenter.y);
      data.center.x = this.m_sweep.localCenter.x;
      data.center.y = this.m_sweep.localCenter.y;
   }

   public final void setMassData(MassData massData) {
      assert !this.m_world.isLocked();

      if (!this.m_world.isLocked()) {
         if (this.m_type == BodyType.DYNAMIC) {
            this.m_invMass = 0.0F;
            this.m_I = 0.0F;
            this.m_invI = 0.0F;
            this.m_mass = massData.mass;
            if (this.m_mass <= 0.0F) {
               this.m_mass = 1.0F;
            }

            this.m_invMass = 1.0F / this.m_mass;
            if (massData.I > 0.0F && (this.m_flags & 16) == 0) {
               this.m_I = massData.I - this.m_mass * Vec2.dot(massData.center, massData.center);

               assert this.m_I > 0.0F;

               this.m_invI = 1.0F / this.m_I;
            }

            Vec2 oldCenter = this.m_world.getPool().popVec2();
            oldCenter.set(this.m_sweep.c);
            this.m_sweep.localCenter.set(massData.center);
            Transform.mulToOutUnsafe(this.m_xf, this.m_sweep.localCenter, this.m_sweep.c0);
            this.m_sweep.c.set(this.m_sweep.c0);
            Vec2 temp = this.m_world.getPool().popVec2();
            temp.set(this.m_sweep.c).subLocal(oldCenter);
            Vec2.crossToOut(this.m_angularVelocity, temp, temp);
            this.m_linearVelocity.addLocal(temp);
            this.m_world.getPool().pushVec2(2);
         }
      }
   }

   public final void resetMassData() {
      this.m_mass = 0.0F;
      this.m_invMass = 0.0F;
      this.m_I = 0.0F;
      this.m_invI = 0.0F;
      this.m_sweep.localCenter.setZero();
      if (this.m_type == BodyType.STATIC || this.m_type == BodyType.KINEMATIC) {
         this.m_sweep.c0.set(this.m_xf.p);
         this.m_sweep.c.set(this.m_xf.p);
         this.m_sweep.a0 = this.m_sweep.a;
      } else {
         assert this.m_type == BodyType.DYNAMIC;

         Vec2 localCenter = this.m_world.getPool().popVec2();
         localCenter.setZero();
         Vec2 temp = this.m_world.getPool().popVec2();
         MassData massData = this.pmd;

         for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
            if (f.m_density != 0.0F) {
               f.getMassData(massData);
               this.m_mass = this.m_mass + massData.mass;
               temp.set(massData.center).mulLocal(massData.mass);
               localCenter.addLocal(temp);
               this.m_I = this.m_I + massData.I;
            }
         }

         if (this.m_mass > 0.0F) {
            this.m_invMass = 1.0F / this.m_mass;
            localCenter.mulLocal(this.m_invMass);
         } else {
            this.m_mass = 1.0F;
            this.m_invMass = 1.0F;
         }

         if (this.m_I > 0.0F && (this.m_flags & 16) == 0) {
            this.m_I = this.m_I - this.m_mass * Vec2.dot(localCenter, localCenter);

            assert this.m_I > 0.0F;

            this.m_invI = 1.0F / this.m_I;
         } else {
            this.m_I = 0.0F;
            this.m_invI = 0.0F;
         }

         Vec2 oldCenter = this.m_world.getPool().popVec2();
         oldCenter.set(this.m_sweep.c);
         this.m_sweep.localCenter.set(localCenter);
         Transform.mulToOutUnsafe(this.m_xf, this.m_sweep.localCenter, this.m_sweep.c0);
         this.m_sweep.c.set(this.m_sweep.c0);
         temp.set(this.m_sweep.c).subLocal(oldCenter);
         Vec2.crossToOutUnsafe(this.m_angularVelocity, temp, oldCenter);
         this.m_linearVelocity.addLocal(oldCenter);
         this.m_world.getPool().pushVec2(3);
      }
   }

   public final Vec2 getWorldPoint(Vec2 localPoint) {
      Vec2 v = new Vec2();
      this.getWorldPointToOut(localPoint, v);
      return v;
   }

   public final void getWorldPointToOut(Vec2 localPoint, Vec2 out) {
      Transform.mulToOut(this.m_xf, localPoint, out);
   }

   public final Vec2 getWorldVector(Vec2 localVector) {
      Vec2 out = new Vec2();
      this.getWorldVectorToOut(localVector, out);
      return out;
   }

   public final void getWorldVectorToOut(Vec2 localVector, Vec2 out) {
      Rot.mulToOut(this.m_xf.q, localVector, out);
   }

   public final void getWorldVectorToOutUnsafe(Vec2 localVector, Vec2 out) {
      Rot.mulToOutUnsafe(this.m_xf.q, localVector, out);
   }

   public final Vec2 getLocalPoint(Vec2 worldPoint) {
      Vec2 out = new Vec2();
      this.getLocalPointToOut(worldPoint, out);
      return out;
   }

   public final void getLocalPointToOut(Vec2 worldPoint, Vec2 out) {
      Transform.mulTransToOut(this.m_xf, worldPoint, out);
   }

   public final Vec2 getLocalVector(Vec2 worldVector) {
      Vec2 out = new Vec2();
      this.getLocalVectorToOut(worldVector, out);
      return out;
   }

   public final void getLocalVectorToOut(Vec2 worldVector, Vec2 out) {
      Rot.mulTrans(this.m_xf.q, worldVector, out);
   }

   public final void getLocalVectorToOutUnsafe(Vec2 worldVector, Vec2 out) {
      Rot.mulTransUnsafe(this.m_xf.q, worldVector, out);
   }

   public final Vec2 getLinearVelocityFromWorldPoint(Vec2 worldPoint) {
      Vec2 out = new Vec2();
      this.getLinearVelocityFromWorldPointToOut(worldPoint, out);
      return out;
   }

   public final void getLinearVelocityFromWorldPointToOut(Vec2 worldPoint, Vec2 out) {
      float tempX = worldPoint.x - this.m_sweep.c.x;
      float tempY = worldPoint.y - this.m_sweep.c.y;
      out.x = -this.m_angularVelocity * tempY + this.m_linearVelocity.x;
      out.y = this.m_angularVelocity * tempX + this.m_linearVelocity.y;
   }

   public final Vec2 getLinearVelocityFromLocalPoint(Vec2 localPoint) {
      Vec2 out = new Vec2();
      this.getLinearVelocityFromLocalPointToOut(localPoint, out);
      return out;
   }

   public final void getLinearVelocityFromLocalPointToOut(Vec2 localPoint, Vec2 out) {
      this.getWorldPointToOut(localPoint, out);
      this.getLinearVelocityFromWorldPointToOut(out, out);
   }

   public final float getLinearDamping() {
      return this.m_linearDamping;
   }

   public final void setLinearDamping(float linearDamping) {
      this.m_linearDamping = linearDamping;
   }

   public final float getAngularDamping() {
      return this.m_angularDamping;
   }

   public final void setAngularDamping(float angularDamping) {
      this.m_angularDamping = angularDamping;
   }

   public BodyType getType() {
      return this.m_type;
   }

   public void setType(BodyType type) {
      assert !this.m_world.isLocked();

      if (!this.m_world.isLocked()) {
         if (this.m_type != type) {
            this.m_type = type;
            this.resetMassData();
            if (this.m_type == BodyType.STATIC) {
               this.m_linearVelocity.setZero();
               this.m_angularVelocity = 0.0F;
               this.m_sweep.a0 = this.m_sweep.a;
               this.m_sweep.c0.set(this.m_sweep.c);
               this.synchronizeFixtures();
            }

            this.setAwake(true);
            this.m_force.setZero();
            this.m_torque = 0.0F;
            ContactEdge ce = this.m_contactList;

            while (ce != null) {
               ContactEdge ce0 = ce;
               ce = ce.next;
               this.m_world.m_contactManager.destroy(ce0.contact);
            }

            this.m_contactList = null;
            BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;

            for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
               int proxyCount = f.m_proxyCount;

               for (int i = 0; i < proxyCount; i++) {
                  broadPhase.touchProxy(f.m_proxies[i].proxyId);
               }
            }
         }
      }
   }

   public final boolean isBullet() {
      return (this.m_flags & 8) == 8;
   }

   public final void setBullet(boolean flag) {
      if (flag) {
         this.m_flags |= 8;
      } else {
         this.m_flags &= -9;
      }
   }

   public void setSleepingAllowed(boolean flag) {
      if (flag) {
         this.m_flags |= 4;
      } else {
         this.m_flags &= -5;
         this.setAwake(true);
      }
   }

   public boolean isSleepingAllowed() {
      return (this.m_flags & 4) == 4;
   }

   public void setAwake(boolean flag) {
      if (flag) {
         if ((this.m_flags & 2) == 0) {
            this.m_flags |= 2;
            this.m_sleepTime = 0.0F;
         }
      } else {
         this.m_flags &= -3;
         this.m_sleepTime = 0.0F;
         this.m_linearVelocity.setZero();
         this.m_angularVelocity = 0.0F;
         this.m_force.setZero();
         this.m_torque = 0.0F;
      }
   }

   public boolean isAwake() {
      return (this.m_flags & 2) == 2;
   }

   public void setActive(boolean flag) {
      assert !this.m_world.isLocked();

      if (flag != this.isActive()) {
         if (flag) {
            this.m_flags |= 32;
            BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;

            for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
               f.createProxies(broadPhase, this.m_xf);
            }
         } else {
            this.m_flags &= -33;
            BroadPhase broadPhase = this.m_world.m_contactManager.m_broadPhase;

            for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
               f.destroyProxies(broadPhase);
            }

            ContactEdge ce = this.m_contactList;

            while (ce != null) {
               ContactEdge ce0 = ce;
               ce = ce.next;
               this.m_world.m_contactManager.destroy(ce0.contact);
            }

            this.m_contactList = null;
         }
      }
   }

   public boolean isActive() {
      return (this.m_flags & 32) == 32;
   }

   public void setFixedRotation(boolean flag) {
      if (flag) {
         this.m_flags |= 16;
      } else {
         this.m_flags &= -17;
      }

      this.resetMassData();
   }

   public boolean isFixedRotation() {
      return (this.m_flags & 16) == 16;
   }

   public final Fixture getFixtureList() {
      return this.m_fixtureList;
   }

   public final JointEdge getJointList() {
      return this.m_jointList;
   }

   public final ContactEdge getContactList() {
      return this.m_contactList;
   }

   public final Body getNext() {
      return this.m_next;
   }

   public final Object getUserData() {
      return this.m_userData;
   }

   public final void setUserData(Object data) {
      this.m_userData = data;
   }

   public final World getWorld() {
      return this.m_world;
   }

   protected final void synchronizeFixtures() {
      Transform xf1 = this.pxf;
      xf1.q.s = MathUtils.sin(this.m_sweep.a0);
      xf1.q.c = MathUtils.cos(this.m_sweep.a0);
      xf1.p.x = this.m_sweep.c0.x - xf1.q.c * this.m_sweep.localCenter.x + xf1.q.s * this.m_sweep.localCenter.y;
      xf1.p.y = this.m_sweep.c0.y - xf1.q.s * this.m_sweep.localCenter.x - xf1.q.c * this.m_sweep.localCenter.y;

      for (Fixture f = this.m_fixtureList; f != null; f = f.m_next) {
         f.synchronize(this.m_world.m_contactManager.m_broadPhase, xf1, this.m_xf);
      }
   }

   public final void synchronizeTransform() {
      this.m_xf.q.s = MathUtils.sin(this.m_sweep.a);
      this.m_xf.q.c = MathUtils.cos(this.m_sweep.a);
      Rot q = this.m_xf.q;
      Vec2 v = this.m_sweep.localCenter;
      this.m_xf.p.x = this.m_sweep.c.x - q.c * v.x + q.s * v.y;
      this.m_xf.p.y = this.m_sweep.c.y - q.s * v.x - q.c * v.y;
   }

   public boolean shouldCollide(Body other) {
      if (this.m_type != BodyType.DYNAMIC && other.m_type != BodyType.DYNAMIC) {
         return false;
      } else {
         for (JointEdge jn = this.m_jointList; jn != null; jn = jn.next) {
            if (jn.other == other && !jn.joint.getCollideConnected()) {
               return false;
            }
         }

         return true;
      }
   }

   protected final void advance(float t) {
      this.m_sweep.advance(t);
      this.m_sweep.c.set(this.m_sweep.c0);
      this.m_sweep.a = this.m_sweep.a0;
      this.m_xf.q.set(this.m_sweep.a);
      Rot.mulToOutUnsafe(this.m_xf.q, this.m_sweep.localCenter, this.m_xf.p);
      this.m_xf.p.mulLocal(-1.0F).addLocal(this.m_sweep.c);
   }
}
