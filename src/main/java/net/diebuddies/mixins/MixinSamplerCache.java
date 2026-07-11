package net.diebuddies.mixins;

import com.mojang.blaze3d.systems.SamplerCache;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import java.util.OptionalDouble;
import net.diebuddies.render.util.PhysicsRenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SamplerCache.class})
public class MixinSamplerCache {
   @Unique
   private GpuSampler physicsmod$sampler3d;

   @Inject(
      at = {@At("HEAD")},
      method = {"initialize"}
   )
   private void physicsmod$initialize(CallbackInfo info) {
      this.physicsmod$sampler3d = PhysicsRenderUtil.createSampler3D(
         AddressMode.REPEAT, AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.of(0.0)
      );
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"close"}
   )
   private void physicsmod$close(CallbackInfo info) {
      if (this.physicsmod$sampler3d != null) {
         this.physicsmod$sampler3d.close();
      }
   }
}
