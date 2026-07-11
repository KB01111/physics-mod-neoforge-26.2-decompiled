package net.diebuddies.physics.animation;

import net.diebuddies.math.Bezier;
import net.diebuddies.math.Curve;
import net.diebuddies.math.EaseInBounce;
import net.diebuddies.math.EaseOutElastic;
import net.diebuddies.math.LinearCurve;

public enum CurveType {
   Linear(new LinearCurve(), "physicsmod.enum.curve.linear"),
   Ease_in(Bezier.EASE_IN_EXPO, "physicsmod.enum.curve.easein"),
   Ease_out(Bezier.EASE_OUT_EXPO, "physicsmod.enum.curve.easeout"),
   Bounce(Bezier.BOUNCE, "physicsmod.enum.curve.bounce"),
   Ease_out_elastic(new EaseOutElastic(), "physicsmod.enum.curve.easeoutelastic"),
   Ease_in_bounce(new EaseInBounce(), "physicsmod.enum.curve.easeinbounce");

   private Curve curve;
   private String translationId;

   private CurveType(Curve curve, String translationId) {
      this.curve = curve;
      this.translationId = translationId;
   }

   public Curve getCurve() {
      return this.curve;
   }

   @Override
   public String toString() {
      return this.translationId;
   }
}
