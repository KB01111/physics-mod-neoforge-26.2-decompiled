package net.diebuddies.mixins;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlStateManager.TextureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GlStateManager.class})
public interface GlStateManagerAccessor {
   @Accessor("activeTexture")
   static int getActiveTexture() {
      throw new AssertionError();
   }

   @Accessor("TEXTURES")
   static TextureState[] getTEXTURES() {
      throw new AssertionError();
   }
}
