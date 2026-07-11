package net.diebuddies.physics.settings.mobs;

import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BoundingBoxGetter implements VertexConsumer {
   public Vector3d min = new Vector3d(Double.MAX_VALUE);
   public Vector3d max = new Vector3d(-Double.MAX_VALUE);
   private Vector3d tmp = new Vector3d();
   private Vector3f tmpPos = new Vector3f();

   public VertexConsumer addVertex(float x, float y, float z) {
      this.min.min(this.tmp.set((double)x, (double)y, (double)z));
      this.max.max(this.tmp.set((double)x, (double)y, (double)z));
      return this;
   }

   public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
      return this;
   }

   public VertexConsumer setUv(float var1, float var2) {
      return this;
   }

   public VertexConsumer setUv1(int var1, int var2) {
      return this;
   }

   public VertexConsumer setUv2(int var1, int var2) {
      return this;
   }

   public VertexConsumer setNormal(float var1, float var2, float var3) {
      return this;
   }

   public VertexConsumer setColor(int i) {
      return this;
   }

   public VertexConsumer setLineWidth(float f) {
      return this;
   }

   public void putBlockBakedQuad(float x, float y, float z, BakedQuad quad, QuadInstance instance) {
      for (int l = 0; l < 4; l++) {
         Vector3fc quadPos = quad.position(l);
         this.tmp.set((double)(quadPos.x() + x), (double)(quadPos.y() + y), (double)(quadPos.z() + z));
         this.min.min(this.tmp);
         this.max.max(this.tmp);
      }
   }

   public void putBakedQuad(Pose pose, BakedQuad quad, QuadInstance instance) {
      Matrix4f matrix4f = pose.pose();

      for (int l = 0; l < 4; l++) {
         Vector3fc quadPos = quad.position(l);
         matrix4f.transformPosition(quadPos, this.tmpPos);
         this.tmp.set(this.tmpPos);
         this.min.min(this.tmp);
         this.max.max(this.tmp);
      }
   }
}
