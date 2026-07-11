package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.Vec2;

public class ManifoldPoint {
   public final Vec2 localPoint;
   public float normalImpulse;
   public float tangentImpulse;
   public final ContactID id;

   public ManifoldPoint() {
      this.localPoint = new Vec2();
      this.normalImpulse = this.tangentImpulse = 0.0F;
      this.id = new ContactID();
   }

   public ManifoldPoint(ManifoldPoint cp) {
      this.localPoint = cp.localPoint.clone();
      this.normalImpulse = cp.normalImpulse;
      this.tangentImpulse = cp.tangentImpulse;
      this.id = new ContactID(cp.id);
   }

   public void set(ManifoldPoint cp) {
      this.localPoint.set(cp.localPoint);
      this.normalImpulse = cp.normalImpulse;
      this.tangentImpulse = cp.tangentImpulse;
      this.id.set(cp.id);
   }
}
