package net.diebuddies.physics.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class DummyVertexConsumer implements VertexConsumer {
   public boolean trackVertices = false;
   private Vector3f tmpPos = new Vector3f();
   private Vector3f tmpNormal = new Vector3f();
   private GpuTextureView textureView;

   public DummyVertexConsumer(GpuTextureView gpuTexture) {
      this.textureView = gpuTexture;
   }

   public VertexConsumer addVertex(float x, float y, float z) {
      return this;
   }

   public VertexConsumer setColor(int red, int green, int blue, int alpha) {
      return this;
   }

   public VertexConsumer setUv(float u, float v) {
      return this;
   }

   public VertexConsumer setUv1(int u, int v) {
      return this;
   }

   public VertexConsumer setUv2(int u, int v) {
      return this;
   }

   public VertexConsumer setNormal(float x, float y, float z) {
      return this;
   }

   public void putBlockBakedQuad(float x, float y, float z, BakedQuad quad, QuadInstance instance) {
      if (this.trackVertices) {
         PhysicsMod mod = PhysicsMod.getCurrentInstance();
         Mesh mesh = mod.itemStackEntity.models.get(0).mesh;
         mod.itemStackEntity.models.get(0).textureID = this.textureView;
         Vector3fc normal = quad.direction().getUnitVec3f();

         for (int l = 0; l < 4; l++) {
            Vector3fc quadPos = quad.position(l);
            long packedUv = quad.packedUV(l);
            int vertexColor = instance.getColor(l);
            float u = UVPair.unpackU(packedUv);
            float v = UVPair.unpackV(packedUv);
            float red = ARGB.redFloat(vertexColor);
            float green = ARGB.greenFloat(vertexColor);
            float blue = ARGB.blueFloat(vertexColor);
            mesh.positions.add(new Vector3f(quadPos.x() + x, quadPos.y() + y, quadPos.z() + z));
            mesh.addColor(red, green, blue);
            mesh.normals.add(new Vector3f(normal.x(), normal.y(), normal.z()));
            mesh.uvs.add(new Vector2f(u, v));
         }

         int index = mesh.positions.size() - 4;
         mesh.indices.add(index);
         mesh.indices.add(index + 1);
         mesh.indices.add(index + 2);
         mesh.indices.add(index);
         mesh.indices.add(index + 2);
         mesh.indices.add(index + 3);
         mod.itemStackEntity.feature = mod.blockifyFeature;
         mod.itemStackEntity.shade(quad.materialInfo().shade());
      }
   }

   public void putBakedQuad(Pose pose, BakedQuad quad, QuadInstance instance) {
      if (this.trackVertices) {
         PhysicsMod mod = PhysicsMod.getCurrentInstance();
         Mesh mesh = mod.itemStackEntity.models.get(0).mesh;
         mod.itemStackEntity.models.get(0).textureID = this.textureView;
         Vector3fc direction = quad.direction().getUnitVec3f();
         Matrix4f matrix4f = pose.pose();
         pose.transformNormal(direction, this.tmpNormal);

         for (int l = 0; l < 4; l++) {
            Vector3fc quadPos = quad.position(l);
            long packedUv = quad.packedUV(l);
            int vertexColor = instance.getColor(l);
            matrix4f.transformPosition(quadPos, this.tmpPos);
            float u = UVPair.unpackU(packedUv);
            float v = UVPair.unpackV(packedUv);
            float red = ARGB.redFloat(vertexColor);
            float green = ARGB.greenFloat(vertexColor);
            float blue = ARGB.blueFloat(vertexColor);
            mesh.positions.add(new Vector3f(this.tmpPos.x(), this.tmpPos.y(), this.tmpPos.z()));
            mesh.addColor(red, green, blue);
            mesh.normals.add(new Vector3f(this.tmpNormal.x(), this.tmpNormal.y(), this.tmpNormal.z()));
            mesh.uvs.add(new Vector2f(u, v));
         }

         int index = mesh.positions.size() - 4;
         mesh.indices.add(index);
         mesh.indices.add(index + 1);
         mesh.indices.add(index + 2);
         mesh.indices.add(index);
         mesh.indices.add(index + 2);
         mesh.indices.add(index + 3);
         mod.itemStackEntity.feature = mod.blockifyFeature;
         mod.itemStackEntity.shade(quad.materialInfo().shade());
      }
   }

   public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
      if (this.trackVertices) {
         PhysicsMod mod = PhysicsMod.getCurrentInstance();
         Mesh mesh = mod.itemStackEntity.models.get(0).mesh;
         mod.itemStackEntity.models.get(0).textureID = this.textureView;
         mesh.positions.add(new Vector3f(x, y, z));
         mesh.addColor((float)ARGB.red(color) / 255.0F, (float)ARGB.green(color) / 255.0F, (float)ARGB.blue(color) / 255.0F, (float)ARGB.alpha(color) / 255.0F);
         mesh.normals.add(new Vector3f(normalX, normalY, normalZ));
         mesh.uvs.add(new Vector2f(u, v));
         int index = mesh.positions.size() - 1;
         mesh.indices.add(index);
      }
   }

   public VertexConsumer addVertex(Matrix4fc matrix, float x, float y, float z) {
      return this;
   }

   public VertexConsumer setNormal(Pose matrix, float x, float y, float z) {
      return this;
   }

   public VertexConsumer setColor(float red, float green, float blue, float alpha) {
      return this;
   }

   public VertexConsumer setLight(int uv) {
      return this;
   }

   public VertexConsumer setOverlay(int uv) {
      return this;
   }

   public VertexConsumer setColor(int color) {
      return this;
   }

   public VertexConsumer setLineWidth(float f) {
      return this;
   }
}
