package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;

public class Manifold {
   public final ManifoldPoint[] points = new ManifoldPoint[Settings.maxManifoldPoints];
   public final Vec2 localNormal;
   public final Vec2 localPoint;
   public Manifold.ManifoldType type;
   public int pointCount;

   public Manifold() {
      for (int i = 0; i < Settings.maxManifoldPoints; i++) {
         this.points[i] = new ManifoldPoint();
      }

      this.localNormal = new Vec2();
      this.localPoint = new Vec2();
      this.pointCount = 0;
   }

   public Manifold(Manifold other) {
      this.localNormal = other.localNormal.clone();
      this.localPoint = other.localPoint.clone();
      this.pointCount = other.pointCount;
      this.type = other.type;

      for (int i = 0; i < Settings.maxManifoldPoints; i++) {
         this.points[i] = new ManifoldPoint(other.points[i]);
      }
   }

   public void set(Manifold cp) {
      for (int i = 0; i < cp.pointCount; i++) {
         this.points[i].set(cp.points[i]);
      }

      this.type = cp.type;
      this.localNormal.set(cp.localNormal);
      this.localPoint.set(cp.localPoint);
      this.pointCount = cp.pointCount;
   }

   public static enum ManifoldType {
      CIRCLES,
      FACE_A,
      FACE_B;
   }
}
