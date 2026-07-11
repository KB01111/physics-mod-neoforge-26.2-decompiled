package net.diebuddies.mixins;

import java.util.Map;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleResources;
import net.minecraft.client.particle.ParticleResources.MutableSpriteSet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ParticleResources.class})
public interface MixinParticleResourcesAccessor {
   @Accessor("providers")
   Map<Identifier, ParticleProvider<?>> getParticleProviders();

   @Accessor("spriteSets")
   Map<Identifier, MutableSpriteSet> getSpriteSets();
}
