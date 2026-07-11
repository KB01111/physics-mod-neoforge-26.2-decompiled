package net.diebuddies.physics.verlet.constraints;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart.Cube;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class ModelCube {
   public PartPose pose;
   public ModelPart part;
   public Matrix4d transform;
   public Vector3d translation;
   public Vector3d lastTranslation;
   public Vector3d currentTranslation;
   public Vector3d scale;
   public Vector3d lastScale;
   public Vector3d currentScale;
   public Quaterniond rotation;
   public Quaterniond lastRotation;
   public Quaterniond currentRotation;
   public float minX;
   public float minY;
   public float minZ;
   public float maxX;
   public float maxY;
   public float maxZ;
   public boolean first = true;

   public ModelCube(ModelPart part) {
      this.part = part;
      this.transform = new Matrix4d();
      this.translation = new Vector3d();
      this.lastTranslation = new Vector3d();
      this.currentTranslation = new Vector3d();
      this.scale = new Vector3d();
      this.lastScale = new Vector3d();
      this.currentScale = new Vector3d();
      this.rotation = new Quaterniond();
      this.lastRotation = new Quaterniond();
      this.currentRotation = new Quaterniond();
   }

   public ModelCube() {
      this(null);
   }

   public void updateTransformation() {
      this.lastTranslation.set(this.translation);
      this.lastScale.set(this.scale);
      this.lastRotation.set(this.rotation);
      this.transform.getTranslation(this.translation);
      this.transform.getScale(this.scale);
      this.transform.getUnnormalizedRotation(this.rotation);
      if (this.first) {
         this.lastTranslation.set(this.translation);
         this.lastScale.set(this.scale);
         this.lastRotation.set(this.rotation);
         this.first = false;
      }
   }

   public Matrix4d getTransform(double percent, Matrix4d dst) {
      return dst.translationRotateScale(
         this.lastTranslation.lerp(this.translation, percent, this.currentTranslation),
         this.lastRotation.slerp(this.rotation, percent, this.currentRotation),
         this.lastScale.lerp(this.scale, percent, this.currentScale)
      );
   }

   public void updateHitbox() {
      if (this.part != null && !this.part.cubes.isEmpty()) {
         Cube cuboid = (Cube)this.part.cubes.get(0);
         this.minX = cuboid.minX / 16.0F;
         this.minY = cuboid.minY / 16.0F;
         this.minZ = cuboid.minZ / 16.0F;
         this.maxX = cuboid.maxX / 16.0F;
         this.maxY = cuboid.maxY / 16.0F;
         this.maxZ = cuboid.maxZ / 16.0F;
      }
   }
}
