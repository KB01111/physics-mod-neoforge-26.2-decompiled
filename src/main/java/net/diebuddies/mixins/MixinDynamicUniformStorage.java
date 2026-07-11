package net.diebuddies.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.diebuddies.compat.Optifine;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.DynamicUniformStorageExtension;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({DynamicUniformStorage.class})
public class MixinDynamicUniformStorage implements DynamicUniformStorageExtension {
   @Shadow
   @Final
   private int blockSize;
   @Shadow
   private MappableRingBuffer ringBuffer;
   @Shadow
   private int nextBlock;
   @Shadow
   private int capacity;
   @Shadow
   private DynamicUniform lastUniform;
   @Shadow
   @Final
   private String label;

   @Shadow
   private void resizeBuffers(int i) {
   }

   @Override
   public <T> void physicsmod$writeUniforms(Collection<T> dynamicUniforms, Function<T, DynamicUniform> getter, BiConsumer<T, GpuBufferSlice> setter) {
      if (dynamicUniforms.size() != 0) {
         if (this.nextBlock + dynamicUniforms.size() > this.capacity) {
            int i = Mth.smallestEncompassingPowerOfTwo(Math.max(this.capacity + 1, dynamicUniforms.size()));
            LogUtils.getLogger()
               .info("Resizing " + this.label + ", capacity limit of {} reached during a single frame. New capacity will be {}.", this.capacity, i);
            this.resizeBuffers(i);
         }

         int i = this.nextBlock * this.blockSize;
         DynamicUniform dynamicUniform = null;
         MappedView mappedView = this.ringBuffer.currentBuffer().slice((long)i, (long)(dynamicUniforms.size() * this.blockSize)).map(false, true);

         try {
            ByteBuffer byteBuffer = mappedView.data();
            int j = 0;

            for (T item : dynamicUniforms) {
               dynamicUniform = getter.apply(item);
               GpuBufferSlice gpuBufferSlice = this.ringBuffer.currentBuffer().slice((long)(i + j * this.blockSize), (long)this.blockSize);
               if (StarterClient.optifabric) {
                  Optifine.setGpuBufferSliceData(gpuBufferSlice, dynamicUniform);
               }

               byteBuffer.position(j * this.blockSize);
               dynamicUniform.write(byteBuffer);
               setter.accept(item, gpuBufferSlice);
               j++;
            }
         } catch (Throwable var13) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }
            }

            throw var13;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         this.nextBlock = this.nextBlock + dynamicUniforms.size();
         this.lastUniform = dynamicUniform;
      }
   }
}
