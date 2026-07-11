package net.diebuddies.physics.snow;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class ChunkEntity {
   public Vector3i position;
   public Vector3i batchPosition;
   public AABB3D aabb;
   public Vector3d center;
   public GpuBuffer vertexBuffer;
   public long vertexBufferOffset;
   public GpuBuffer indexBuffer;
   public long indexBufferOffset;
   public int uploadedIndexCount;

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

   public void calculateTransformations() {
      Matrix4d transformation = new Matrix4d();
      double scale = 1.0 / (double)IChunk.CHUNK_MULTIPLE;
      transformation.identity();
      transformation.translate(
         ((double)(this.batchPosition.x * IChunk.CHUNK_SIZE) + 0.5) * scale,
         ((double)(this.batchPosition.y * IChunk.CHUNK_SIZE) + 0.5) * scale,
         ((double)(this.batchPosition.z * IChunk.CHUNK_SIZE) + 0.5) * scale
      );
      transformation.scale(scale);
      transformation.transformAab(this.aabb.getMin(), this.aabb.getMax(), this.aabb.getMin(), this.aabb.getMax());
   }
}
