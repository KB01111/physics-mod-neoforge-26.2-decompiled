package net.diebuddies.physics;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.Pack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Model {
   public TextureAtlasSprite texture;
   public Matrix4f textureMatrix;
   public TextureAtlasSprite animationSprite;
   public GpuTextureView textureID;
   public Mesh mesh;
   public Mesh physicsMesh;
   public boolean onlyVisual;
   public boolean translucent;
   public boolean backfaceCulling = true;
   public boolean shade = true;
   private PhysicsWorld renderOwner;

   public void createModelMemorySegment(PhysicsWorld physics) {
      if (this.mesh != null && this.mesh.indices != null && this.mesh.indices.size() >= 3) {
         int vertexCount = this.mesh.positions.size();
         int indexCount = this.mesh.indices.size();
         int vertexStride = physics.getModelVertexFormat().getVertexSize();
         int vertexSize = vertexCount * vertexStride;
         int indexSize = indexCount * 4;
         boolean shaderMods = StarterClient.iris() || StarterClient.optifabric;
         boolean hasColors = this.mesh.colors.size() > 0;
         boolean hasUvs = this.mesh.uvs.size() > 0;
         boolean hasNormals = this.shade && this.mesh.normals != null && this.mesh.normals.size() > 0;
         boolean hasTangents = shaderMods && this.shade && this.mesh.tangents != null && this.mesh.tangents.size() > 0;
         boolean hasMid = shaderMods && this.mesh.midcoords != null && this.mesh.midcoords.size() > 0;
         boolean stackAlloc = vertexSize + indexSize <= StarterClient.memoryStack.getSize();
         ByteBuffer vertexData;
         ByteBuffer indexData;
         if (stackAlloc) {
            MemoryStack stack = StarterClient.memoryStack.push();
            vertexData = stack.malloc(vertexSize);
            indexData = stack.malloc(indexSize);
         } else {
            vertexData = MemoryUtil.memAlloc(vertexSize);
            indexData = MemoryUtil.memAlloc(indexSize);
         }

         long vertexPointer = MemoryUtil.memAddress(vertexData);
         float minU = 0.0F;
         float maxU = 1.0F;
         float minV = 0.0F;
         float maxV = 1.0F;
         if (this.texture != null && this.textureMatrix == null) {
            minU = this.texture.getU0();
            maxU = this.texture.getU1();
            minV = this.texture.getV0();
            maxV = this.texture.getV1();
         }

         for (int i = 0; i < vertexCount; i++) {
            Vector3f p = this.mesh.positions.get(i);
            MemoryUtil.memPutFloat(vertexPointer, p.x);
            MemoryUtil.memPutFloat(vertexPointer + 4L, p.y);
            MemoryUtil.memPutFloat(vertexPointer + 8L, p.z);
            MemoryUtil.memPutInt(vertexPointer + 12L, hasColors ? this.mesh.colors.getInt(i) : -1);
            if (hasUvs) {
               Vector2f uv = this.mesh.uvs.get(i);
               MemoryUtil.memPutFloat(vertexPointer + 16L, Math.remapClamp(uv.x, 0.0F, 1.0F, minU, maxU));
               MemoryUtil.memPutFloat(vertexPointer + 20L, Math.remapClamp(uv.y, 0.0F, 1.0F, minV, maxV));
            } else {
               MemoryUtil.memPutFloat(vertexPointer + 16L, Math.remapClamp(0.5F, 0.0F, 1.0F, minU, maxU));
               MemoryUtil.memPutFloat(vertexPointer + 20L, Math.remapClamp(0.5F, 0.0F, 1.0F, minV, maxV));
            }

            if (hasNormals) {
               Vector3f normal = this.mesh.normals.get(i);
               MemoryUtil.memPutInt(vertexPointer + 24L, Pack.normal(normal.x, normal.y, normal.z));
            } else {
               MemoryUtil.memPutInt(vertexPointer + 24L, Pack.Y_POS_NORMAL);
            }

            if (vertexStride >= 40) {
               if (hasMid) {
                  Vector2f miduv = this.mesh.midcoords.get(i);
                  MemoryUtil.memPutFloat(vertexPointer + 28L, Math.remapClamp(miduv.x, 0.0F, 1.0F, minU, maxU));
                  MemoryUtil.memPutFloat(vertexPointer + 32L, Math.remapClamp(miduv.y, 0.0F, 1.0F, minV, maxV));
               } else {
                  MemoryUtil.memPutFloat(vertexPointer + 28L, Math.remapClamp(0.5F, 0.0F, 1.0F, minU, maxU));
                  MemoryUtil.memPutFloat(vertexPointer + 32L, Math.remapClamp(0.5F, 0.0F, 1.0F, minV, maxV));
               }

               if (hasTangents) {
                  Vector4f tangent = this.mesh.tangents.get(i);
                  MemoryUtil.memPutInt(vertexPointer + 36L, Pack.normal(tangent.x, tangent.y, tangent.z, tangent.w));
               } else {
                  MemoryUtil.memPutInt(vertexPointer + 36L, Pack.X_POS_TANGENT);
               }
            }

            vertexPointer += (long)vertexStride;
         }

         long indexPointer = MemoryUtil.memAddress(indexData);

         for (int i = 0; i < indexCount; i++) {
            MemoryUtil.memPutInt(indexPointer + (long)i * 4L, this.mesh.indices.getInt(i));
         }

         if (physics.stageModelUpload(this, vertexData, indexData, indexCount)) {
            this.renderOwner = physics;
         }

         if (stackAlloc) {
            StarterClient.memoryStack.pop();
         } else {
            MemoryUtil.memFree(vertexData);
            MemoryUtil.memFree(indexData);
         }

         this.mesh.clearMemory();
         if (this.physicsMesh != null) {
            this.physicsMesh.clearMemory();
         }
      }
   }

   public boolean hasRenderData() {
      return this.renderOwner != null && this.renderOwner.getRenderSlice(this) != null;
   }

   public void freeRenderData() {
      if (this.renderOwner != null) {
         this.renderOwner.removeModelRenderBuffers(this);
         this.renderOwner = null;
      }
   }
}
