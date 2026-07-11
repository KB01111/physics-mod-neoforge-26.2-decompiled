package net.diebuddies.mixins.cloth;

import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   extends EntityRenderer<T, S>
   implements RenderLayerParent<S, M> {
   @Shadow
   @Final
   protected List<RenderLayer> layers;

   protected MixinLivingEntityRenderer(Context context) {
      super(context);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   private void physicsmod$addPhysicsClothLayer(Context context, M entityModel, float shadowRadius, CallbackInfo info) {
      CapeLayer capeLayer = null;

      for (int i = 0; i < this.layers.size(); i++) {
         if (this.layers.get(i) instanceof CapeLayer layer) {
            capeLayer = layer;
         }
      }

      if (capeLayer != null) {
         this.layers.remove(capeLayer);
         this.layers.add(capeLayer);
      }
   }
}
