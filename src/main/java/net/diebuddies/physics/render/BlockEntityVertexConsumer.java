package net.diebuddies.physics.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BlockEntityVertexConsumer implements VertexConsumer {
   private Model model = new Model();
   private Mesh mesh;
   private int vertCount = 0;
   private Vector3f tmpPos = new Vector3f();
   private Vector3f tmpNormal = new Vector3f();
   private Vector3f tmp1 = new Vector3f();
   private Vector3f tmp2 = new Vector3f();

   public BlockEntityVertexConsumer(GpuTextureView gpuTexture) {
      this.mesh = this.model.mesh = new Mesh();
      this.model.textureID = gpuTexture;
   }

   public void validateModel() {
      int faceCount = this.mesh.positions.size() / 4;
      int indicesFaceCount = this.mesh.indices.size() / 6;
      if (faceCount != indicesFaceCount) {
         this.mesh.indices.clear();
         int index = 0;

         for (int i = 0; i < faceCount; i++) {
            this.mesh.indices.add(index);
            this.mesh.indices.add(index + 1);
            this.mesh.indices.add(index + 2);
            this.mesh.indices.add(index);
            this.mesh.indices.add(index + 2);
            this.mesh.indices.add(index + 3);
            index += 4;
         }

         if (this.mesh.positions.size() != this.mesh.normals.size()) {
            for (int i = this.mesh.normals.size(); i < this.mesh.positions.size(); i++) {
               int face = i / 4;
               Vector3f pos0 = this.mesh.positions.get(face * 4 + 1);
               Vector3f pos1 = this.mesh.positions.get(face * 4 + 2);
               Vector3f pos2 = this.mesh.positions.get(face * 4 + 3);
               Vector3f tmp0 = pos1.sub(pos0, this.tmp1);
               Vector3f tmp1 = pos2.sub(pos0, this.tmp2);
               Vector3f normal = tmp0.cross(tmp1);
               float length = normal.lengthSquared();
               if ((double)length != 0.0) {
                  normal.mul(1.0F / length);
               } else {
                  normal.set(0.0, 1.0, 0.0);
               }

               this.mesh.normals.add(normal);
            }
         }
      }
   }

   public VertexConsumer addVertex(float x, float y, float z) {
      this.mesh.positions.add(new Vector3f(x, y, z));
      return this;
   }

   public VertexConsumer setColor(int red, int green, int blue, int alpha) {
      this.mesh.addColor(red, green, blue, alpha);
      return this;
   }

   public VertexConsumer setUv(float u, float v) {
      this.mesh.uvs.add(new Vector2f(u, v));
      return this;
   }

   public VertexConsumer setUv1(int u, int v) {
      return this;
   }

   public VertexConsumer setUv2(int u, int v) {
      return this;
   }

   public VertexConsumer setNormal(float x, float y, float z) {
      this.mesh.normals.add(new Vector3f(x, y, z));
      return this;
   }

   public VertexConsumer addVertex(Matrix4fc matrix, float x, float y, float z) {
      this.tmpPos.set(x, y, z);
      matrix.transformPosition(this.tmpPos);
      this.mesh.positions.add(new Vector3f(this.tmpPos.x(), this.tmpPos.y(), this.tmpPos.z()));
      return this;
   }

   public VertexConsumer setNormal(Pose pose, float x, float y, float z) {
      this.tmpNormal.set(x, y, z);
      pose.transformNormal(x, y, z, this.tmpNormal);
      this.mesh.normals.add(new Vector3f(this.tmpNormal.x(), this.tmpNormal.y(), this.tmpNormal.z()));
      return this;
   }

   public void putBlockBakedQuad(float x, float y, float z, BakedQuad quad, QuadInstance instance) {
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
         this.mesh.positions.add(new Vector3f(quadPos.x() + x, quadPos.y() + y, quadPos.z() + z));
         this.mesh.addColor(red, green, blue);
         this.mesh.normals.add(new Vector3f(normal.x(), normal.y(), normal.z()));
         this.mesh.uvs.add(new Vector2f(u, v));
      }

      int index = this.mesh.positions.size() - 4;
      this.mesh.indices.add(index);
      this.mesh.indices.add(index + 1);
      this.mesh.indices.add(index + 2);
      this.mesh.indices.add(index);
      this.mesh.indices.add(index + 2);
      this.mesh.indices.add(index + 3);
      PhysicsMod.getCurrentInstance().itemStackEntity.shade(quad.materialInfo().shade());
   }

   public void putBakedQuad(Pose pose, BakedQuad quad, QuadInstance instance) {
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
         this.mesh.positions.add(new Vector3f(this.tmpPos.x(), this.tmpPos.y(), this.tmpPos.z()));
         this.mesh.addColor(red, green, blue);
         this.mesh.normals.add(new Vector3f(this.tmpNormal.x(), this.tmpNormal.y(), this.tmpNormal.z()));
         this.mesh.uvs.add(new Vector2f(u, v));
      }

      int index = this.mesh.positions.size() - 4;
      this.mesh.indices.add(index);
      this.mesh.indices.add(index + 1);
      this.mesh.indices.add(index + 2);
      this.mesh.indices.add(index);
      this.mesh.indices.add(index + 2);
      this.mesh.indices.add(index + 3);
      PhysicsMod.getCurrentInstance().itemStackEntity.shade(quad.materialInfo().shade());
   }

   public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
      this.vertCount++;
      this.mesh.positions.add(new Vector3f(x, y, z));
      this.mesh
         .addColor((float)ARGB.red(color) / 255.0F, (float)ARGB.green(color) / 255.0F, (float)ARGB.blue(color) / 255.0F, (float)ARGB.alpha(color) / 255.0F);
      this.mesh.normals.add(new Vector3f(normalX, normalY, normalZ));
      this.mesh.uvs.add(new Vector2f(u, v));
      if (this.vertCount == 4) {
         this.vertCount = 0;
         int index = this.mesh.positions.size() - 4;
         this.mesh.indices.add(index);
         this.mesh.indices.add(index + 1);
         this.mesh.indices.add(index + 2);
         this.mesh.indices.add(index);
         this.mesh.indices.add(index + 2);
         this.mesh.indices.add(index + 3);
      }
   }

   public VertexConsumer setColor(float red, float green, float blue, float alpha) {
      this.mesh.addColor(red, green, blue, alpha);
      return this;
   }

   public VertexConsumer setLight(int uv) {
      return this;
   }

   public VertexConsumer setOverlay(int uv) {
      return this;
   }

   public VertexConsumer setColor(int color) {
      this.mesh
         .addColor((float)ARGB.red(color) / 255.0F, (float)ARGB.green(color) / 255.0F, (float)ARGB.blue(color) / 255.0F, (float)ARGB.alpha(color) / 255.0F);
      return this;
   }

   public VertexConsumer setLineWidth(float f) {
      return this;
   }

   public Model getModel() {
      return this.model;
   }
}
