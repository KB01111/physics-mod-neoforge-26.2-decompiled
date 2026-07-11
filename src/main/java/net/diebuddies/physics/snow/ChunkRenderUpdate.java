package net.diebuddies.physics.snow;

import net.diebuddies.physics.snow.thread.MultipleEvent;

public class ChunkRenderUpdate {
   public ChunkContouring chunk;
   public MultipleEvent event;
   public boolean dataChanged;

   public ChunkRenderUpdate(ChunkContouring chunk, MultipleEvent event, boolean dataChanged) {
      this.chunk = chunk;
      this.event = event;
      this.dataChanged = dataChanged;
   }

   @Override
   public int hashCode() {
      return this.chunk.hashCode();
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
         ChunkRenderUpdate other = (ChunkRenderUpdate)obj;
         if (this.chunk == null) {
            if (other.chunk != null) {
               return false;
            }
         } else if (!this.chunk.equals(other.chunk)) {
            return false;
         }

         return true;
      }
   }
}
