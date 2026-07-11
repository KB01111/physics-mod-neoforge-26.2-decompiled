package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.common.Color3f;

public class ParticleColor {
   public byte r;
   public byte g;
   public byte b;
   public byte a;

   public ParticleColor() {
      this.r = 127;
      this.g = 127;
      this.b = 127;
      this.a = 50;
   }

   public ParticleColor(byte r, byte g, byte b, byte a) {
      this.set(r, g, b, a);
   }

   public ParticleColor(Color3f color) {
      this.set(color);
   }

   public void set(Color3f color) {
      this.r = (byte)((int)(255.0F * color.x));
      this.g = (byte)((int)(255.0F * color.y));
      this.b = (byte)((int)(255.0F * color.z));
      this.a = -1;
   }

   public void set(ParticleColor color) {
      this.r = color.r;
      this.g = color.g;
      this.b = color.b;
      this.a = color.a;
   }

   public boolean isZero() {
      return this.r == 0 && this.g == 0 && this.b == 0 && this.a == 0;
   }

   public void set(byte r, byte g, byte b, byte a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
   }
}
