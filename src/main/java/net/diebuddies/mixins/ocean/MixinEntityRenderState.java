package net.diebuddies.mixins.ocean;

import net.diebuddies.physics.ocean.EntityRenderStateExtended;
import net.diebuddies.physics.ocean.OceanRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({EntityRenderState.class})
public class MixinEntityRenderState implements EntityRenderStateExtended {
   @Unique
   private OceanRenderState physicsmod$oceanRenderState;

   @Override
   public OceanRenderState physicsmod$getOceanRenderState() {
      return this.physicsmod$oceanRenderState;
   }

   @Override
   public void physicsmod$setOceanRenderState(OceanRenderState state) {
      this.physicsmod$oceanRenderState = state;
   }
}
