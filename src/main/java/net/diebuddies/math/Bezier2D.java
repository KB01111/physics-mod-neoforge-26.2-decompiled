package net.diebuddies.math;

import org.joml.Vector2f;

public class Bezier2D implements Curve {
   public static final Bezier2D EASE_IN_SINE = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.12F, 0.0F), new Vector2f(0.41F, 0.0F));
   public static final Bezier2D EASE_OUT_SINE = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.61F, 1.0F), new Vector2f(0.88F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_SINE = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.37F, 0.0F), new Vector2f(0.63F, 1.0F));
   public static final Bezier2D EASE_IN_CUBIC = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.32F, 0.0F), new Vector2f(0.67F, 0.0F));
   public static final Bezier2D EASE_OUT_CUBIC = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.33F, 1.0F), new Vector2f(0.68F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_CUBIC = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.65F, 0.0F), new Vector2f(0.35F, 1.0F));
   public static final Bezier2D EASE_IN_QUINT = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.64F, 0.0F), new Vector2f(0.78F, 0.0F));
   public static final Bezier2D EASE_OUT_QUINT = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.22F, 1.0F), new Vector2f(0.36F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_QUINT = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.83F, 0.0F), new Vector2f(0.17F, 1.0F));
   public static final Bezier2D EASE_IN_CIRC = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.55F, 0.0F), new Vector2f(1.0F, 0.45F));
   public static final Bezier2D EASE_OUT_CIRC = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.0F, 0.55F), new Vector2f(0.45F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_CIRC = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.85F, 0.0F), new Vector2f(0.15F, 1.0F));
   public static final Bezier2D EASE_IN_QUAD = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.11F, 0.0F), new Vector2f(0.5F, 0.0F));
   public static final Bezier2D EASE_OUT_QUAD = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.5F, 1.0F), new Vector2f(0.89F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_QUAD = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.45F, 0.0F), new Vector2f(0.55F, 1.0F));
   public static final Bezier2D EASE_IN_QUART = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.5F, 0.0F), new Vector2f(0.75F, 0.0F));
   public static final Bezier2D EASE_OUT_QUART = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.25F, 1.0F), new Vector2f(0.5F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_QUART = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.76F, 0.0F), new Vector2f(0.24F, 1.0F));
   public static final Bezier2D EASE_IN_EXPO = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.7F, 0.0F), new Vector2f(0.84F, 0.0F));
   public static final Bezier2D EASE_OUT_EXPO = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.16F, 1.0F), new Vector2f(0.3F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_EXPO = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.87F, 0.0F), new Vector2f(0.13F, 1.0F));
   public static final Bezier2D EASE_IN_BACK = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.36F, 0.0F), new Vector2f(0.66F, -0.56F));
   public static final Bezier2D EASE_OUT_BACK = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.34F, 1.56F), new Vector2f(0.64F, 1.0F));
   public static final Bezier2D EASE_IN_OUT_BACK = new Bezier2D(new Vector2f(0.0F), new Vector2f(1.0F), new Vector2f(0.68F, -0.6F), new Vector2f(0.32F, 1.6F));
   public Bezier x;
   public Bezier y;
   public float[] sampleXForTValues = new float[20];

   public Bezier2D(Vector2f p0, Vector2f p1, Vector2f c0, Vector2f c1) {
      this.x = new Bezier(p0.x, p1.x, c0.x, c1.x);
      this.y = new Bezier(p0.y, p1.y, c0.y, c1.y);
      this.initSampleValues();
   }

   private void initSampleValues() {
      for (int i = 0; i < this.sampleXForTValues.length; i++) {
         this.sampleXForTValues[i] = this.x.get((float)i / (float)(this.sampleXForTValues.length - 1));
      }
   }

   public void get(float time, Vector2f dst) {
      dst.set(this.x.get(time), this.y.get(time));
   }

   @Override
   public float get(float time) {
      float indexFloat = Math.clamp(time * ((float)this.sampleXForTValues.length - 1.0F), 0.0F, (float)(this.sampleXForTValues.length - 1));
      float fraction = indexFloat - (float)((int)indexFloat);
      int index = 0;

      for (int i = 0; i < this.sampleXForTValues.length; i++) {
         if (time < this.sampleXForTValues[i]) {
            index = i;
            break;
         }
      }

      if (index == this.sampleXForTValues.length - 1) {
         return this.y.get(time);
      } else {
         float ntime = org.joml.Math.lerp(this.sampleXForTValues[index], this.sampleXForTValues[index + 1], fraction);
         return this.y.get(ntime);
      }
   }
}
