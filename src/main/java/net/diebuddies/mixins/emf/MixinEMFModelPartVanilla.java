package net.diebuddies.mixins.emf;

import java.util.Collection;
import java.util.Map;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"traben.entity_model_features.models.parts.EMFModelPartVanilla"}
)
public class MixinEMFModelPartVanilla {
   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   private void physicsmod$setParentInConstructor(
      String name, ModelPart vanillaPart, Collection<String> optifinePartNames, Map allVanillaParts, @Coerce Object root, CallbackInfo info
   ) {
      if (vanillaPart != null) {
         ((ModelPartParent)this).physicsmod$setParent(vanillaPart);
      }

      ((ModelPartParent)this).physicsmod$setName(name);
   }
}
