package net.diebuddies.mixins.weather;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SingleQuadParticle.class})
public interface MixinParticleAccessor {
   @Accessor("alpha")
   void setAlpha(float var1);
}
