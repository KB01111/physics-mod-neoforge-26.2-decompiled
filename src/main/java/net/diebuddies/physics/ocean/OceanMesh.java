package net.diebuddies.physics.ocean;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.jspecify.annotations.Nullable;

public class OceanMesh implements DynamicFrustumBVH.BVHNode {
   @Nullable
   public RawMesh mesh;
   @Nullable
   public Matrix4d transformation;
   public byte[] textureData;
   public int width;
   public int height;
   public int offsetX;
   public int offsetZ;
   public int layer;
   public long meshIndex;
   @Nullable
   public final AABB3D aabb;
   public float maxInfluence;
   public GpuBuffer vertexBuffer;
   public long vertexBufferOffset;
   public GpuBuffer indexBuffer;
   public long indexBufferOffset;
   public int uploadedIndexCount;
   private int bvhNode = -1;

   public boolean hasRenderBuffers() {
      return this.vertexBuffer != null && this.indexBuffer != null && this.uploadedIndexCount > 0;
   }

   public void clearRenderBuffers() {
      this.vertexBuffer = null;
      this.vertexBufferOffset = 0L;
      this.indexBuffer = null;
      this.indexBufferOffset = 0L;
      this.uploadedIndexCount = 0;
   }

   public OceanMesh(
      RawMesh mesh,
      Matrix4d transformation,
      float maxInfluence,
      int layer,
      int width,
      int height,
      int offsetX,
      int offsetZ,
      int aabbStartX,
      int aabbStartZ,
      long meshIndex
   ) {
      this.mesh = mesh;
      this.transformation = transformation;
      this.layer = layer;
      this.width = width;
      this.height = height;
      this.offsetX = offsetX;
      this.offsetZ = offsetZ;
      this.maxInfluence = maxInfluence;
      this.meshIndex = meshIndex;
      Vector3d start = new Vector3d((double)aabbStartX, (double)layer, (double)aabbStartZ);
      this.aabb = new AABB3D(start, new Vector3d((double)(width + 1), 0.0, (double)(height + 1)).add(start));
      this.transformation.transformAab(this.aabb.getMin(), this.aabb.getMax(), this.aabb.getMin(), this.aabb.getMax());
   }

   public void discardCpuMesh() {
      if (this.mesh != null) {
         this.mesh.destroy();
         this.mesh = null;
      }
   }

   public void destroy() {
      this.discardCpuMesh();
   }

   @Override
   public void setId(int id) {
      this.bvhNode = id;
   }

   @Override
   public int getId() {
      return this.bvhNode;
   }
}
