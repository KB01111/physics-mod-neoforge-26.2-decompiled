package net.diebuddies.mixins;

import net.diebuddies.minecraft.PlayerPhysicsHealth;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({AbstractClientPlayer.class})
public class MixinAbstractClientPlayer implements PlayerPhysicsHealth {
   @Unique
   private float physicsmod$health = -1.0F;

   @Override
   public void setPhysicsHealth(float health) {
      this.physicsmod$health = health;
   }

   @Override
   public float getPhysicsHealth() {
      return this.physicsmod$health;
   }
}
