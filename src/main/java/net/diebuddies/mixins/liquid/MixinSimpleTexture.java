package net.diebuddies.mixins.liquid;

import net.diebuddies.physics.liquid.SimpleTextureDimension;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SimpleTexture.class})
public class MixinSimpleTexture implements SimpleTextureDimension {
   @Unique
   private int tWidthP;
   @Unique
   private int tHeightP;

   @Inject(
      at = {@At("RETURN")},
      method = {"loadContents"}
   )
   private void physicsmod$loadContents(ResourceManager resourceManager, CallbackInfoReturnable<TextureContents> info) {
      TextureContents contents = (TextureContents)info.getReturnValue();
      this.tWidthP = contents.image().getWidth();
      this.tHeightP = contents.image().getHeight();
   }

   @Override
   public int getWidth() {
      return this.tWidthP;
   }

   @Override
   public int getHeight() {
      return this.tHeightP;
   }
}
