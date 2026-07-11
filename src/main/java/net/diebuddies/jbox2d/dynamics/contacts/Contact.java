package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.callbacks.ContactListener;
import net.diebuddies.jbox2d.collision.ContactID;
import net.diebuddies.jbox2d.collision.Manifold;
import net.diebuddies.jbox2d.collision.ManifoldPoint;
import net.diebuddies.jbox2d.collision.WorldManifold;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public abstract class Contact {
   public static final int ISLAND_FLAG = 1;
   public static final int TOUCHING_FLAG = 2;
   public static final int ENABLED_FLAG = 4;
   public static final int FILTER_FLAG = 8;
   public static final int BULLET_HIT_FLAG = 16;
   public static final int TOI_FLAG = 32;
   public int m_flags;
   public Contact m_prev;
   public Contact m_next;
   public ContactEdge m_nodeA = null;
   public ContactEdge m_nodeB = null;
   public Fixture m_fixtureA;
   public Fixture m_fixtureB;
   public int m_indexA;
   public int m_indexB;
   public final Manifold m_manifold;
   public float m_toiCount;
   public float m_toi;
   public float m_friction;
   public float m_restitution;
   public float m_tangentSpeed;
   protected final IWorldPool pool;
   private final Manifold oldManifold = new Manifold();

   protected Contact(IWorldPool argPool) {
      this.m_fixtureA = null;
      this.m_fixtureB = null;
      this.m_nodeA = new ContactEdge();
      this.m_nodeB = new ContactEdge();
      this.m_manifold = new Manifold();
      this.pool = argPool;
   }

   public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
      this.m_flags = 4;
      this.m_fixtureA = fA;
      this.m_fixtureB = fB;
      this.m_indexA = indexA;
      this.m_indexB = indexB;
      this.m_manifold.pointCount = 0;
      this.m_prev = null;
      this.m_next = null;
      this.m_nodeA.contact = null;
      this.m_nodeA.prev = null;
      this.m_nodeA.next = null;
      this.m_nodeA.other = null;
      this.m_nodeB.contact = null;
      this.m_nodeB.prev = null;
      this.m_nodeB.next = null;
      this.m_nodeB.other = null;
      this.m_toiCount = 0.0F;
      this.m_friction = mixFriction(fA.m_friction, fB.m_friction);
      this.m_restitution = mixRestitution(fA.m_restitution, fB.m_restitution);
      this.m_tangentSpeed = 0.0F;
   }

   public Manifold getManifold() {
      return this.m_manifold;
   }

   public void getWorldManifold(WorldManifold worldManifold) {
      Body bodyA = this.m_fixtureA.getBody();
      Body bodyB = this.m_fixtureB.getBody();
      Shape shapeA = this.m_fixtureA.getShape();
      Shape shapeB = this.m_fixtureB.getShape();
      worldManifold.initialize(this.m_manifold, bodyA.getTransform(), shapeA.m_radius, bodyB.getTransform(), shapeB.m_radius);
   }

   public boolean isTouching() {
      return (this.m_flags & 2) == 2;
   }

   public void setEnabled(boolean flag) {
      if (flag) {
         this.m_flags |= 4;
      } else {
         this.m_flags &= -5;
      }
   }

   public boolean isEnabled() {
      return (this.m_flags & 4) == 4;
   }

   public Contact getNext() {
      return this.m_next;
   }

   public Fixture getFixtureA() {
      return this.m_fixtureA;
   }

   public int getChildIndexA() {
      return this.m_indexA;
   }

   public Fixture getFixtureB() {
      return this.m_fixtureB;
   }

   public int getChildIndexB() {
      return this.m_indexB;
   }

   public void setFriction(float friction) {
      this.m_friction = friction;
   }

   public float getFriction() {
      return this.m_friction;
   }

   public void resetFriction() {
      this.m_friction = mixFriction(this.m_fixtureA.m_friction, this.m_fixtureB.m_friction);
   }

   public void setRestitution(float restitution) {
      this.m_restitution = restitution;
   }

   public float getRestitution() {
      return this.m_restitution;
   }

   public void resetRestitution() {
      this.m_restitution = mixRestitution(this.m_fixtureA.m_restitution, this.m_fixtureB.m_restitution);
   }

   public void setTangentSpeed(float speed) {
      this.m_tangentSpeed = speed;
   }

   public float getTangentSpeed() {
      return this.m_tangentSpeed;
   }

   public abstract void evaluate(Manifold var1, Transform var2, Transform var3);

   public void flagForFiltering() {
      this.m_flags |= 8;
   }

   public void update(ContactListener listener) {
      this.oldManifold.set(this.m_manifold);
      this.m_flags |= 4;
      boolean touching = false;
      boolean wasTouching = (this.m_flags & 2) == 2;
      boolean sensorA = this.m_fixtureA.isSensor();
      boolean sensorB = this.m_fixtureB.isSensor();
      boolean sensor = sensorA || sensorB;
      Body bodyA = this.m_fixtureA.getBody();
      Body bodyB = this.m_fixtureB.getBody();
      Transform xfA = bodyA.getTransform();
      Transform xfB = bodyB.getTransform();
      if (sensor) {
         Shape shapeA = this.m_fixtureA.getShape();
         Shape shapeB = this.m_fixtureB.getShape();
         touching = this.pool.getCollision().testOverlap(shapeA, this.m_indexA, shapeB, this.m_indexB, xfA, xfB);
         this.m_manifold.pointCount = 0;
      } else {
         this.evaluate(this.m_manifold, xfA, xfB);
         touching = this.m_manifold.pointCount > 0;

         for (int i = 0; i < this.m_manifold.pointCount; i++) {
            ManifoldPoint mp2 = this.m_manifold.points[i];
            mp2.normalImpulse = 0.0F;
            mp2.tangentImpulse = 0.0F;
            ContactID id2 = mp2.id;

            for (int j = 0; j < this.oldManifold.pointCount; j++) {
               ManifoldPoint mp1 = this.oldManifold.points[j];
               if (mp1.id.isEqual(id2)) {
                  mp2.normalImpulse = mp1.normalImpulse;
                  mp2.tangentImpulse = mp1.tangentImpulse;
                  break;
               }
            }
         }

         if (touching != wasTouching) {
            bodyA.setAwake(true);
            bodyB.setAwake(true);
         }
      }

      if (touching) {
         this.m_flags |= 2;
      } else {
         this.m_flags &= -3;
      }

      if (listener != null) {
         if (!wasTouching && touching) {
            listener.beginContact(this);
         }

         if (wasTouching && !touching) {
            listener.endContact(this);
         }

         if (!sensor && touching) {
            listener.preSolve(this, this.oldManifold);
         }
      }
   }

   public static final float mixFriction(float friction1, float friction2) {
      return MathUtils.sqrt(friction1 * friction2);
   }

   public static final float mixRestitution(float restitution1, float restitution2) {
      return restitution1 > restitution2 ? restitution1 : restitution2;
   }
}
