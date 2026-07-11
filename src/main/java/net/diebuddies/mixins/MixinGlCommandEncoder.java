package net.diebuddies.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlTexture;
import org.lwjgl.opengl.GL33C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GlCommandEncoder.class})
public abstract class MixinGlCommandEncoder {
   @Unique
   private static boolean physicsmod$is3DTexture(GlTexture texture) {
      return (texture.usage() & 128) != 0;
   }

   @Unique
   private static int mymod$getTarget(GlTexture texture, int originalTarget) {
      return physicsmod$is3DTexture(texture) ? 32879 : originalTarget;
   }

   @WrapOperation(
      method = {"trySetup"},
      at = {@At(
         value = "INVOKE",
         target = "Lorg/lwjgl/opengl/GL33C;glBindTexture(II)V"
      )}
   )
   private void physicsmod$bind3DInsteadOfCubeMap(int target, int textureId, Operation<Void> original, @Local GlTexture texture) {
      if (physicsmod$is3DTexture(texture)) {
         GL33C.glBindTexture(32879, textureId);
      } else {
         original.call(new Object[]{target, textureId});
      }
   }

   @WrapOperation(
      method = {"trySetup"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_bindTexture(I)V"
      )}
   )
   private void physicsmod$bind3DInsteadOf2D(int textureId, Operation<Void> original, @Local GlTexture texture) {
      if (physicsmod$is3DTexture(texture)) {
         GL33C.glBindTexture(32879, textureId);
      } else {
         original.call(new Object[]{textureId});
      }
   }

   @WrapOperation(
      method = {"trySetup"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texParameter(III)V"
      )}
   )
   private void physicsmod$fixTexParameterTarget(int target, int pname, int param, Operation<Void> original, @Local GlTexture texture) {
      original.call(new Object[]{mymod$getTarget(texture, target), pname, param});
   }
}
