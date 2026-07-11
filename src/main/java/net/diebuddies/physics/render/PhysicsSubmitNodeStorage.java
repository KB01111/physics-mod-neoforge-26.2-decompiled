package net.diebuddies.physics.render;

import net.minecraft.client.renderer.SubmitNodeStorage;

public class PhysicsSubmitNodeStorage extends SubmitNodeStorage {
   private boolean destruction;

   public PhysicsSubmitNodeStorage(boolean destruction) {
      this.destruction = destruction;
   }

   public boolean isDestruction() {
      return this.destruction;
   }
}
