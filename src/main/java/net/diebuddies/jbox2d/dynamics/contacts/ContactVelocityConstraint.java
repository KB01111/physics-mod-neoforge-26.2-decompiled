package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;

public class ContactVelocityConstraint {
   public ContactVelocityConstraint.VelocityConstraintPoint[] points = new ContactVelocityConstraint.VelocityConstraintPoint[Settings.maxManifoldPoints];
   public final Vec2 normal = new Vec2();
   public final Mat22 normalMass = new Mat22();
   public final Mat22 K = new Mat22();
   public int indexA;
   public int indexB;
   public float invMassA;
   public float invMassB;
   public float invIA;
   public float invIB;
   public float friction;
   public float restitution;
   public float tangentSpeed;
   public int pointCount;
   public int contactIndex;

   public ContactVelocityConstraint() {
      for (int i = 0; i < this.points.length; i++) {
         this.points[i] = new ContactVelocityConstraint.VelocityConstraintPoint();
      }
   }

   public static class VelocityConstraintPoint {
      public final Vec2 rA = new Vec2();
      public final Vec2 rB = new Vec2();
      public float normalImpulse;
      public float tangentImpulse;
      public float normalMass;
      public float tangentMass;
      public float velocityBias;
   }
}
