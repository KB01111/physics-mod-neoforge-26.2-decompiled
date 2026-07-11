package net.diebuddies.render;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class InstancedRenderer {
   private final String label;
   private final int meshStride;
   private final int instanceStride;
   private GpuBuffer vertexBuffer;
   private GpuBuffer indexBuffer;
   private GpuBuffer instanceBuffer;
   private int instanceCapacity;
   private int instanceCount;
   private int vertexCount;
   private int indexCount;

   public InstancedRenderer(String label, VertexFormat vertexFormat, VertexFormat instanceFormat, VertexWriter mesh) {
      this.label = label;
      this.meshStride = vertexFormat.getVertexSize();
      this.instanceStride = instanceFormat.getVertexSize();
      this.createMeshBuffers(mesh);
   }

   private void createMeshBuffers(VertexWriter mesh) {
      this.vertexCount = mesh.count();
      int totalSize = this.meshStride * this.vertexCount;
      this.vertexBuffer = RenderSystem.getDevice().createBuffer(() -> this.label + " Vertex Buffer", 34, (long)totalSize);
      MappedView view = this.vertexBuffer.map(false, true);

      try {
         mesh.write(view.data());
      } catch (Throwable var9) {
         if (view != null) {
            try {
               view.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (view != null) {
         view.close();
      }

      this.indexCount = this.vertexCount;
      int indexBytes = this.indexCount * 2;
      this.indexBuffer = RenderSystem.getDevice().createBuffer(() -> this.label + " Index Buffer", 66, (long)indexBytes);
      MappedView viewx = this.indexBuffer.map(false, true);

      try {
         ByteBuffer data = viewx.data();

         for (int i = 0; i < this.indexCount; i++) {
            data.putShort(i * 2, (short)i);
         }
      } catch (Throwable var10) {
         if (viewx != null) {
            try {
               viewx.close();
            } catch (Throwable var7) {
               var10.addSuppressed(var7);
            }
         }

         throw var10;
      }

      if (viewx != null) {
         viewx.close();
      }
   }

   private void ensureInstanceCapacity(int minInstances) {
      if (this.instanceBuffer == null || minInstances > this.instanceCapacity) {
         int newCapacity = Math.max(64, this.instanceCapacity);

         while (newCapacity < minInstances) {
            newCapacity <<= 1;
         }

         if (this.instanceBuffer != null) {
            this.instanceBuffer.close();
         }

         this.instanceCapacity = newCapacity;
         this.instanceBuffer = RenderSystem.getDevice()
            .createBuffer(() -> this.label + " Instance Texel Buffer", 258, (long)this.instanceCapacity * (long)this.instanceStride);
      }
   }

   public void writeInstanceData(VertexWriter writer, int count) {
      if (count <= 0) {
         this.instanceCount = 0;
      } else {
         this.ensureInstanceCapacity(count);
         MappedView view = this.instanceBuffer.map(false, true);

         try {
            ByteBuffer data = view.data();
            MemoryUtil.memSet(MemoryUtil.memAddress(data), 0, (long)data.capacity());
            writer.write(data);
         } catch (Throwable var7) {
            if (view != null) {
               try {
                  view.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (view != null) {
            view.close();
         }

         this.instanceCount = writer.count();
      }
   }

   public void render(RenderPass pass, String texelBufferUniformName) {
      if (this.instanceCount != 0 && this.instanceBuffer != null) {
         pass.setVertexBuffer(0, this.vertexBuffer.slice());
         pass.setIndexBuffer(this.indexBuffer, IndexType.SHORT);
         pass.setUniform(texelBufferUniformName, this.instanceBuffer);
         pass.drawIndexed(this.indexCount, this.instanceCount, 0, 0, 0);
      }
   }

   public int getRenderCount() {
      return this.instanceCount;
   }

   public void destroy() {
      if (this.vertexBuffer != null) {
         this.vertexBuffer.close();
         this.vertexBuffer = null;
      }

      if (this.indexBuffer != null) {
         this.indexBuffer.close();
         this.indexBuffer = null;
      }

      if (this.instanceBuffer != null) {
         this.instanceBuffer.close();
         this.instanceBuffer = null;
      }

      this.instanceCapacity = 0;
      this.instanceCount = 0;
   }
}
