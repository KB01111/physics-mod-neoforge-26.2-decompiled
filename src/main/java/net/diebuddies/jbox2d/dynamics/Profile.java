package net.diebuddies.jbox2d.dynamics;

import java.util.List;
import net.diebuddies.jbox2d.common.MathUtils;

public class Profile {
   private static final int LONG_AVG_NUMS = 20;
   private static final float LONG_FRACTION = 0.05F;
   private static final int SHORT_AVG_NUMS = 5;
   private static final float SHORT_FRACTION = 0.2F;
   public final Profile.ProfileEntry step = new Profile.ProfileEntry();
   public final Profile.ProfileEntry stepInit = new Profile.ProfileEntry();
   public final Profile.ProfileEntry collide = new Profile.ProfileEntry();
   public final Profile.ProfileEntry solveParticleSystem = new Profile.ProfileEntry();
   public final Profile.ProfileEntry solve = new Profile.ProfileEntry();
   public final Profile.ProfileEntry solveInit = new Profile.ProfileEntry();
   public final Profile.ProfileEntry solveVelocity = new Profile.ProfileEntry();
   public final Profile.ProfileEntry solvePosition = new Profile.ProfileEntry();
   public final Profile.ProfileEntry broadphase = new Profile.ProfileEntry();
   public final Profile.ProfileEntry solveTOI = new Profile.ProfileEntry();

   public void toDebugStrings(List<String> strings) {
      strings.add("Profile:");
      strings.add(" step: " + this.step);
      strings.add("  init: " + this.stepInit);
      strings.add("  collide: " + this.collide);
      strings.add("  particles: " + this.solveParticleSystem);
      strings.add("  solve: " + this.solve);
      strings.add("   solveInit: " + this.solveInit);
      strings.add("   solveVelocity: " + this.solveVelocity);
      strings.add("   solvePosition: " + this.solvePosition);
      strings.add("   broadphase: " + this.broadphase);
      strings.add("  solveTOI: " + this.solveTOI);
   }

   public static class ProfileEntry {
      float longAvg;
      float shortAvg;
      float min = Float.MAX_VALUE;
      float max = -Float.MAX_VALUE;
      float accum;

      public void record(float value) {
         this.longAvg = this.longAvg * 0.95F + value * 0.05F;
         this.shortAvg = this.shortAvg * 0.8F + value * 0.2F;
         this.min = MathUtils.min(value, this.min);
         this.max = MathUtils.max(value, this.max);
      }

      public void startAccum() {
         this.accum = 0.0F;
      }

      public void accum(float value) {
         this.accum += value;
      }

      public void endAccum() {
         this.record(this.accum);
      }

      @Override
      public String toString() {
         return String.format("%.2f (%.2f) [%.2f,%.2f]", this.shortAvg, this.longAvg, this.min, this.max);
      }
   }
}
