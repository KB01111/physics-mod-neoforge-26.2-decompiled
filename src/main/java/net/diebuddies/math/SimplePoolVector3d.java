package net.diebuddies.math;

import org.joml.Vector3d;

public class SimplePoolVector3d {
   private Vector3d[] objects;
   private int index;

   public SimplePoolVector3d(int size) {
      this.objects = new Vector3d[size];

      for (int i = 0; i < size; i++) {
         this.objects[i] = new Vector3d();
      }
   }

   public Vector3d get(double x, double y, double z) {
      if (this.index < this.objects.length) {
         return this.objects[this.index++].set(x, y, z);
      } else {
         this.resize();
         return this.objects[this.index++];
      }
   }

   public Vector3d get() {
      return this.get(0.0, 0.0, 0.0);
   }

   private void resize() {
      Vector3d[] newArray = new Vector3d[this.objects.length * 2];

      for (int i = 0; i < this.objects.length; i++) {
         newArray[i] = this.objects[i];
      }

      for (int i = this.objects.length; i < newArray.length; i++) {
         newArray[i] = new Vector3d();
      }

      this.objects = newArray;
   }

   public void reset() {
      this.index = 0;
   }
}
