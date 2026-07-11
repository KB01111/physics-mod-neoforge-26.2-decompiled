package net.diebuddies.mixins;

import net.diebuddies.physics.JsonUnbakedModelHolder;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SimpleModelWrapper.class})
public class MixinBlockModel {
   @Inject(
      at = {@At("RETURN")},
      method = {"bake"}
   )
   private static void physicsmod$bake(ModelBaker modelBaker, Identifier Identifier, ModelState modelState, CallbackInfoReturnable<SimpleModelWrapper> info) {
      PhysicsMod.loadedModels
         .put(
            (BlockStateModelPart)info.getReturnValue(),
            new JsonUnbakedModelHolder(modelBaker.getModel(Identifier), modelState.transformation().getMatrixCopy())
         );
   }
}
