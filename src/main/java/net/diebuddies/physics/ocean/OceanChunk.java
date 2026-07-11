package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.storage.StorageContainer;

public class OceanChunk extends IChunk<OceanProcessor> {
   public OceanChunk(int x, int y, int z, StorageContainer dataStorage) {
      super(x, y, z, dataStorage);
   }

   @Override
   public void setLoadedNeighbourCount(IWorld<?> world, byte loadedNeighbourCount) {
      if (this.loadedNeighbourCount != loadedNeighbourCount) {
         if (loadedNeighbourCount == 8) {
            ((OceanProcessor)world).processChunkColumns.add(Index.oceanLayerChunk(this.x, this.z));
         } else {
            ((OceanProcessor)world).processChunkColumns.remove(Index.oceanLayerChunk(this.x, this.z));
         }
      }

      super.setLoadedNeighbourCount(world, loadedNeighbourCount);
   }
}
