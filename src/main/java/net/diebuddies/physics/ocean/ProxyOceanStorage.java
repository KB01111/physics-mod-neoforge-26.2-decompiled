package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.storage.FullStorageType2DBit;
import net.diebuddies.physics.ocean.storage.FullStorageType2DShort;

public class ProxyOceanStorage {
   public int x;
   public int z;
   public int size = 256;
   public FullStorageType2DBit blocks;
   public FullStorageType2DShort depths;

   public ProxyOceanStorage(long index) {
      this.x = Index.getXFromOceanLayer(index);
      this.z = Index.getZFromOceanLayer(index);
      this.blocks = new FullStorageType2DBit(this.size);
      this.depths = new FullStorageType2DShort(this.size);
   }
}
