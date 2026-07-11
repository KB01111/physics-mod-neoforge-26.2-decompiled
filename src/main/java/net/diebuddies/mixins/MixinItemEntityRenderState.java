package net.diebuddies.mixins;

import net.diebuddies.minecraft.ItemEntityRenderStatePhysics;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ItemEntityRenderState.class})
public class MixinItemEntityRenderState implements ItemEntityRenderStatePhysics {
   @Unique
   private float physicsmod$rotation;
   @Unique
   private boolean physicsmod$isBlock;

   @Override
   public float physicsmod$rotation() {
      return this.physicsmod$rotation;
   }

   @Override
   public void physicsmod$rotation(float value) {
      this.physicsmod$rotation = value;
   }

   @Override
   public boolean physicsmod$isBlock() {
      return this.physicsmod$isBlock;
   }

   @Override
   public void physicsmod$isBlock(boolean value) {
      this.physicsmod$isBlock = value;
   }
}
