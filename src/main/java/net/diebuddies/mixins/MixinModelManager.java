package net.diebuddies.mixins;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.PreparableReloadListener.SharedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ModelManager.class})
public class MixinModelManager {
   @Inject(
      at = {@At("HEAD")},
      method = {"reload"},
      cancellable = true
   )
   private void physicsmod$clearLoadedModels(
      SharedState sharedState,
      Executor executor,
      PreparationBarrier preparationBarrier,
      Executor executor2,
      CallbackInfoReturnable<CompletableFuture<Void>> info
   ) {
      PhysicsMod.loadedModels.clear();
   }
}
