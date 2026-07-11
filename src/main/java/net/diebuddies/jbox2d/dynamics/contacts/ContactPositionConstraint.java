package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.collision.Manifold;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;

public class ContactPositionConstraint {
   Vec2[] localPoints = new Vec2[Settings.maxManifoldPoints];
   final Vec2 localNormal = new Vec2();
   final Vec2 localPoint = new Vec2();
   int indexA;
   int indexB;
   float invMassA;
   float invMassB;
   final Vec2 localCenterA = new Vec2();
   final Vec2 localCenterB = new Vec2();
   float invIA;
   float invIB;
   Manifold.ManifoldType type;
   float radiusA;
   float radiusB;
   int pointCount;

   public ContactPositionConstraint() {
      for (int i = 0; i < this.localPoints.length; i++) {
         this.localPoints[i] = new Vec2();
      }
   }
}
