package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.verlet.BannerPhysicsRenderState;
import net.diebuddies.physics.verlet.BannerRenderStateExtended;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BannerRenderState.class})
public class MixinBannerRenderState implements BannerRenderStateExtended {
   @Unique
   private BannerPhysicsRenderState physicsmod$bannerRenderState;

   @Override
   public BannerPhysicsRenderState physicsmod$getBannerRenderState() {
      return this.physicsmod$bannerRenderState;
   }

   @Override
   public void physicsmod$setBannerRenderState(BannerPhysicsRenderState state) {
      this.physicsmod$bannerRenderState = state;
   }
}
