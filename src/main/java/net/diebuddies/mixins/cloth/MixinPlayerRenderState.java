package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.PlayerRenderStateExtended;
import net.diebuddies.physics.verlet.ClothRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({AvatarRenderState.class})
public class MixinPlayerRenderState implements PlayerRenderStateExtended {
   @Unique
   private ClothRenderState physicsmod$clothRenderState;

   @Override
   public ClothRenderState physicsmod$getClothRenderState() {
      return this.physicsmod$clothRenderState;
   }

   @Override
   public void physicsmod$setClothRenderState(ClothRenderState state) {
      this.physicsmod$clothRenderState = state;
   }
}
