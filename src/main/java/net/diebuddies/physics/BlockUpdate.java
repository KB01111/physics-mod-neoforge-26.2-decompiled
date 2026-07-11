package net.diebuddies.physics;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUpdate {
   public ClientLevel level;
   public BlockPos pos;
   public BlockState state;
   public BlockEntity blockEntity;

   public BlockUpdate(ClientLevel level, BlockPos pos, BlockState state) {
      this.level = level;
      this.pos = pos;
      this.state = state;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.pos.getX();
      result = 31 * result + this.pos.getX();
      return 31 * result + this.pos.getX();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         BlockUpdate other = (BlockUpdate)obj;
         if (this.pos == null) {
            if (other.pos != null) {
               return false;
            }
         } else if (!this.pos.equals(other.pos)) {
            return false;
         }

         return true;
      }
   }
}
