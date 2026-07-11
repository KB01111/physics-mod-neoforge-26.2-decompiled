package net.diebuddies.mixins.item;

import net.diebuddies.minecraft.ThrownItemRenderStatePhysics;
import net.diebuddies.physics.ThrownItemStateExtended;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ThrownItemRenderState.class})
public class MixinThrownItemRendererState implements ThrownItemStateExtended {
   @Unique
   private ThrownItemRenderStatePhysics physicsmod$itemRenderState;

   @Override
   public ThrownItemRenderStatePhysics physicsmod$getThrownItemRenderState() {
      return this.physicsmod$itemRenderState;
   }

   @Override
   public void physicsmod$setThrownItemRenderState(ThrownItemRenderStatePhysics state) {
      this.physicsmod$itemRenderState = state;
   }
}
