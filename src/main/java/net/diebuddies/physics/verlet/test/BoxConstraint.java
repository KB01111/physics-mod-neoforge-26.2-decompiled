package net.diebuddies.physics.verlet.test;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class BoxConstraint implements VerletTestConstraint {
   public Matrix4d transformation;
   public AABB3D aabb;
   public double rotation = 1.0;
   public double lastRotation;
   private Vector3d invPoint = new Vector3d();
   private Matrix4d transform = new Matrix4d();
   private Matrix4d invTransform = new Matrix4d();
   private Vector3d tmpPos1 = new Vector3d();
   private Vector3d tmpPos2 = new Vector3d();
   private double[] dist = new double[4];

   public BoxConstraint(AABB3D aabb, Matrix4d transformation) {
      this.aabb = aabb;
      this.transformation = transformation;
   }

   @Override
   public boolean initAsyncData(VerletSimulationTest simulation) {
      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulationTest simulation) {
      this.lastRotation = this.rotation;
      this.rotation += delta * 1.2;
   }

   @Override
   public void subStep(double percent, VerletSimulationTest simulation) {
      this.doCollisionCheck(percent, simulation);
   }

   private void doCollisionCheck(double percent, VerletSimulationTest simulation) {
   }

   public boolean movePointOutOfBox(Vector3d prevPoint, Vector3d point, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (point.x > minX && point.x < maxX && point.y > minY && point.y < maxY && point.z > minZ && point.z < maxZ) {
         this.dist[0] = point.x - minX;
         this.dist[1] = maxX - point.x;
         this.dist[2] = point.y - minY;
         this.dist[3] = maxY - point.y;
         Vector3d rayOrigin = new Vector3d(prevPoint);
         Vector3d rayDir = new Vector3d(point).sub(prevPoint);
         double lengthSquared = rayDir.lengthSquared();
         if (lengthSquared != 0.0) {
            double length = Math.sqrt(lengthSquared);
            rayDir.div(length);
            Vector3d boxMin = new Vector3d(minX, minY, minZ);
            Vector3d boxMax = new Vector3d(maxX, maxY, maxZ);
            double dist = intersectAABB(rayOrigin, rayDir, boxMin, boxMax);
            if (!Double.isNaN(dist) && dist < length && dist > 0.0) {
               point.set(rayOrigin.add(rayDir.mul(dist)));
               return true;
            }
         }

         double smallest = this.dist[0];
         int index = 0;

         for (int i = 1; i < this.dist.length; i++) {
            if (this.dist[i] < smallest) {
               smallest = this.dist[i];
               index = i;
            }
         }

         if (index == 0) {
            point.x = minX;
         } else if (index == 1) {
            point.x = maxX;
         } else if (index == 2) {
            point.y = minY;
         } else if (index == 3) {
            point.y = maxY;
         } else if (index == 4) {
            point.z = minZ;
         } else if (index == 5) {
            point.z = maxZ;
         }

         return true;
      } else {
         return false;
      }
   }

   public static double intersectAABB(Vector3d rayOrigin, Vector3d rayDir, Vector3d boxMin, Vector3d boxMax) {
      double invraydirx = 1.0 / rayDir.x;
      double invraydiry = 1.0 / rayDir.y;
      double invraydirz = 1.0 / rayDir.z;
      double tminx = (boxMin.x - rayOrigin.x) * invraydirx;
      double tminy = (boxMin.y - rayOrigin.y) * invraydiry;
      double tminz = (boxMin.z - rayOrigin.z) * invraydirz;
      double tmaxx = (boxMax.x - rayOrigin.x) * invraydirx;
      double tmaxy = (boxMax.y - rayOrigin.y) * invraydiry;
      double tmaxz = (boxMax.z - rayOrigin.z) * invraydirz;
      double t1x = Math.min(tminx, tmaxx);
      double t1y = Math.min(tminy, tmaxy);
      double t1z = Math.min(tminz, tmaxz);
      double t2x = Math.max(tminx, tmaxx);
      double t2y = Math.max(tminy, tmaxy);
      double t2z = Math.max(tminz, tmaxz);
      double near = Math.max(Math.max(t1x, t1y), t1z);
      double far = Math.min(Math.min(t2x, t2y), t2z);
      if (far < 0.0) {
         return Double.NaN;
      } else {
         return near > far ? Double.NaN : near;
      }
   }

   public static void main(String[] args) {
      Vector3d rayOrigin = new Vector3d(0.1, -0.1, 0.01);
      Vector3d rayDir = new Vector3d(0.0, 1.0, 0.0).normalize();
      Vector3d boxMin = new Vector3d(0.0, 0.0, 0.0);
      Vector3d boxMax = new Vector3d(1.0, 1.0, 1.0);
      double dist = 0.0;
      System.out.println(dist = intersectAABB(rayOrigin, rayDir, boxMin, boxMax));
      if (!Double.isNaN(dist)) {
         System.out.println("hit > " + rayOrigin.add(rayDir.mul(dist)));
      }
   }

   public Matrix4d getTransformation(double percent) {
      Matrix4d tmp = new Matrix4d(this.transformation);
      tmp.translate(Math.sin(org.joml.Math.lerp(this.lastRotation, this.rotation, percent)) * 500.0, 0.0, 0.0);
      return tmp;
   }

   public boolean movePointOutOfBox(Vector3d point, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (point.x > minX && point.x < maxX && point.y > minY && point.y < maxY && point.z > minZ && point.z < maxZ) {
         this.dist[0] = point.x - minX;
         this.dist[1] = maxX - point.x;
         this.dist[2] = point.y - minY;
         this.dist[3] = maxY - point.y;
         double smallest = this.dist[0];
         int index = 0;

         for (int i = 1; i < this.dist.length; i++) {
            if (this.dist[i] < smallest) {
               smallest = this.dist[i];
               index = i;
            }
         }

         if (index == 0) {
            point.x = minX;
         } else if (index == 1) {
            point.x = maxX;
         } else if (index == 2) {
            point.y = minY;
         } else if (index == 3) {
            point.y = maxY;
         } else if (index == 4) {
            point.z = minZ;
         } else if (index == 5) {
            point.z = maxZ;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void renderBefore(PoseStack matrixStack, double delta, VerletSimulationTest simulation) {
   }

   @Override
   public void renderAfter(PoseStack matrixStack, double delta, VerletSimulationTest simulation) {
   }

   @Override
   public void render(PoseStack matrixStack, double delta, VerletSimulationTest simulation) {
   }

   @Override
   public void updateAfter(double delta, VerletSimulationTest simulation) {
   }
}
