package net.diebuddies.mixins.particle;

import net.diebuddies.physics.settings.animation.TextureSheetParticleExtension;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({SingleQuadParticle.class})
public class MixinTextureSheetParticle implements TextureSheetParticleExtension {
   @Shadow
   protected TextureAtlasSprite sprite;
   @Shadow
   protected float rCol;
   @Shadow
   protected float gCol;
   @Shadow
   protected float bCol;
   @Shadow
   protected float alpha;

   @Override
   public TextureAtlasSprite physicsmod$getSprite() {
      return this.sprite;
   }

   @Override
   public float physicsmod$getRed() {
      return this.rCol;
   }

   @Override
   public float physicsmod$getGreen() {
      return this.gCol;
   }

   @Override
   public float physicsmod$getBlue() {
      return this.bCol;
   }

   @Override
   public float physicsmod$getAlpha() {
      return this.alpha;
   }
}
