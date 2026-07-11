package net.diebuddies.model;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class ClothMesh implements AutoCloseable {
   private final VertexFormat format;
   private final GpuBuffer vertexBuffer;
   private final GpuBuffer indexBuffer;
   private final int indexCount;
   private final IndexType indexType;

   public ClothMesh(VertexFormat format, GpuBuffer vertexBuffer, GpuBuffer indexBuffer, int indexCount, IndexType indexType) {
      this.format = format;
      this.vertexBuffer = vertexBuffer;
      this.indexBuffer = indexBuffer;
      this.indexCount = indexCount;
      this.indexType = indexType;
   }

   public VertexFormat format() {
      return this.format;
   }

   public GpuBuffer vertexBuffer() {
      return this.vertexBuffer;
   }

   public GpuBuffer indexBuffer() {
      return this.indexBuffer;
   }

   public int indexCount() {
      return this.indexCount;
   }

   public IndexType indexType() {
      return this.indexType;
   }

   @Override
   public void close() {
      this.vertexBuffer.close();
      this.indexBuffer.close();
   }
}
