package net.diebuddies.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;

public interface DynamicUniformStorageExtension {
   <T> void physicsmod$writeUniforms(Collection<T> var1, Function<T, DynamicUniform> var2, BiConsumer<T, GpuBufferSlice> var3);
}
