package net.diebuddies.physics.ocean.thread;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import net.diebuddies.physics.ocean.OceanChunk;
import net.diebuddies.physics.ocean.OceanSearcher;
import net.diebuddies.physics.ocean.storage.StorageSimple;
import net.diebuddies.physics.vines.FastBlockSearcher;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;

public class OceanChunkCreator {
   private PalettedContainer<BlockState> container;
   private byte data;
   private int chunkX;
   private int chunkY;
   private int chunkZ;

   public OceanChunkCreator(byte data, int chunkX, int chunkY, int chunkZ) {
      this.data = data;
      this.chunkX = chunkX;
      this.chunkY = chunkY;
      this.chunkZ = chunkZ;
   }

   public OceanChunkCreator(PalettedContainer<BlockState> container, int chunkX, int chunkY, int chunkZ) {
      this.container = container;
      this.chunkX = chunkX;
      this.chunkY = chunkY;
      this.chunkZ = chunkZ;
   }

   public OceanChunk create() {
      if (this.container == null) {
         return new OceanChunk(this.chunkX, this.chunkY, this.chunkZ, new StorageSimple(this.data, 4096));
      } else if (!this.container.maybeHas(OceanSearcher::isPhysicsOcean)) {
         return new OceanChunk(this.chunkX, this.chunkY, this.chunkZ, new StorageSimple((byte)-1, 4096));
      } else {
         OceanSearcher searcher = new OceanSearcher(this.container.data.palette());
         ((FastBlockSearcher)this.container.data.storage()).getAllFast(searcher);
         IntList countData = searcher.getCountData();
         if (countData.size() == 1) {
            return new OceanChunk(this.chunkX, this.chunkY, this.chunkZ, new StorageSimple((byte)(countData.getInt(0) >> 16), 4096));
         } else {
            byte[] rawData = new byte[4096];
            int offset = 0;

            for (int i = 0; i < countData.size(); i++) {
               int raw = countData.getInt(i);
               int amount = raw & 65535;
               byte data = (byte)(countData.getInt(i) >> 16);
               Arrays.fill(rawData, offset, offset + amount, data);
               offset += amount;
            }

            return new OceanChunk(this.chunkX, this.chunkY, this.chunkZ, new StorageSimple(rawData, 4096));
         }
      }
   }

   public int getX() {
      return this.chunkX;
   }

   public int getY() {
      return this.chunkY;
   }

   public int getZ() {
      return this.chunkZ;
   }
}
