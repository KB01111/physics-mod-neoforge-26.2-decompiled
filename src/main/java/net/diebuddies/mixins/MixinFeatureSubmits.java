package net.diebuddies.mixins;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase.FeatureSubmits;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FeatureSubmits.class})
public class MixinFeatureSubmits {
   @Shadow
   @Final
   @Mutable
   private Map<Object, List<SubmitNode>> batches;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void physicsmod$useLinkedHashMap(FeatureRendererType<SubmitNode> featureType, CallbackInfo info) {
      this.batches = new Object2ObjectLinkedOpenHashMap();
   }
}
