package net.diebuddies.mixins;

import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
   @Shadow
   private Map<EntityType<?>, EntityRenderer<?, ?>> renderers;

   @Inject(
      at = {@At("TAIL")},
      method = {"onResourceManagerReload"},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void onResourceManagerReload(ResourceManager manager, CallbackInfo ci, Context context) {
      for (Entry<EntityType<?>, EntityRenderer<?, ?>> entry : this.renderers.entrySet()) {
         PhysicsMod.renderers.put(entry.getKey(), entry.getValue());
      }

      PhysicsMod.renderers.put(EntityTypes.PLAYER, null);
   }
}
