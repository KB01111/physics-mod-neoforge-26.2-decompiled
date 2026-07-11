package net.diebuddies.physics.ocean;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class OceanBlockUpdate {
   public Level level;
   public BlockPos pos;
   public byte state;

   public OceanBlockUpdate(Level level, BlockPos pos, byte state) {
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
         OceanBlockUpdate other = (OceanBlockUpdate)obj;
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
