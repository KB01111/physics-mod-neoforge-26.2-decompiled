package net.diebuddies.mixins;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Registry.class})
public interface MixinRegistry {
   @Inject(
      at = {@At("HEAD")},
      method = {"register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;)Ljava/lang/Object;"}
   )
   private static void register(Registry registry, ResourceKey resourceKey, Object entry, CallbackInfoReturnable<Object> ci) {
      if (registry == BuiltInRegistries.BLOCK) {
         if (entry instanceof Block block) {
            Identifier location = resourceKey.identifier();
            String id = location.toString();
            PhysicsMod.registeredBlocks.put(block, id);
            PhysicsMod.invRegisteredBlocks.put(id, block);
         }
      } else if (registry == BuiltInRegistries.PARTICLE_TYPE) {
         if (entry instanceof ParticleOptions particle) {
            Identifier location = resourceKey.identifier();
            String identifier = location.getNamespace() + ":" + location.getPath();
            PhysicsMod.registeredParticles.put(identifier, particle);
            PhysicsMod.invRegisteredParticles.put(particle, identifier);
         }
      } else if (registry == BuiltInRegistries.SOUND_EVENT && entry instanceof SoundEvent sound) {
         Identifier location = resourceKey.identifier();
         String identifier = location.getNamespace() + ":" + location.getPath();
         PhysicsMod.registeredSounds.put(identifier, sound);
         PhysicsMod.invRegisteredSounds.put(sound, identifier);
      }
   }
}
