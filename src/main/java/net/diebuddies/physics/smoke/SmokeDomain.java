package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.util.ObjectOpenHashSetReplace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public abstract class SmokeDomain {
   private static final int SMOKE_STYLE_SHIFT = 24;
   private static final int SMOKE_STYLE_MASK = 255;
   private static final int SMOKE_RANDOM_MASK = 16777215;
   public PhysicsWorld world;
   public InstanceUpdateCallback instanceUpdateCallback;
   protected Long2ObjectMap<LongSet> loadedColumns;
   private ObjectOpenHashSetReplace<BlockUpdate> blockUpdates;

   public SmokeDomain(PhysicsWorld world) {
      this.world = world;
      this.loadedColumns = new Long2ObjectOpenHashMap();
      this.blockUpdates = new ObjectOpenHashSetReplace<>();
   }

   public static boolean isFire(BlockState blockState) {
      return blockState.getBlock() instanceof BaseFireBlock;
   }

   public static boolean isInOpenAir(Level level, int x, int y, int z) {
      return y >= level.getHeight(Types.MOTION_BLOCKING, x, z);
   }

   public void spawnParticle(double x, double y, double z, float scale, boolean fadeIn) {
      this.spawnParticle(x, y, z, scale, fadeIn, SmokeDomain.SmokeParticleStyle.FIRE);
   }

   public void spawnSteamParticle(double x, double y, double z, float scale, boolean fadeIn) {
      this.spawnParticle(x, y, z, scale, fadeIn, SmokeDomain.SmokeParticleStyle.STEAM);
   }

   public abstract void spawnParticle(double var1, double var3, double var5, float var7, boolean var8, SmokeDomain.SmokeParticleStyle var9);

   public abstract void executeExplosion(Explosion var1);

   public abstract int particleCount();

   public abstract int fillInstances(Vec3 var1, long var2);

   public abstract int fillVolume(long var1);

   public abstract void clearParticles();

   public abstract void destroy();

   public static int encodeParticleId(SmokeDomain.SmokeParticleStyle style, int randomId) {
      int styleId = (style == null ? SmokeDomain.SmokeParticleStyle.FIRE : style).id() & 0xFF;
      return styleId << 24 | randomId & 16777215;
   }

   public static SmokeDomain.SmokeParticleStyle particleStyleFromId(int particleId) {
      return SmokeDomain.SmokeParticleStyle.byId(particleId >>> 24 & 0xFF);
   }

   public static boolean isSteamParticleId(int particleId) {
      return particleStyleFromId(particleId) == SmokeDomain.SmokeParticleStyle.STEAM;
   }

   public void invalidateBrightness(LongSet updatedLightBlocks) {
   }

   public void update(double diff) {
      if (this.instanceUpdateCallback != null) {
         this.instanceUpdateCallback.instanceUpdate();
      }

      this.applyBlockUpdates();
   }

   private void applyBlockUpdates() {
      if (!this.blockUpdates.isEmpty()) {
         ObjectIterator var1 = this.blockUpdates.iterator();

         while (var1.hasNext()) {
            BlockUpdate update = (BlockUpdate)var1.next();
            BlockPos pos = update.pos;
            int cx = SectionPos.blockToSectionCoord(pos.getX());
            int cz = SectionPos.blockToSectionCoord(pos.getZ());
            long index = SectionPos.asLong(cx, 0, cz);
            LongSet values = (LongSet)this.loadedColumns.get(index);
            boolean isFire = isFire(update.state);
            if (values == null && isFire) {
               values = new LongOpenHashSet();
               this.loadedColumns.put(index, values);
            }

            if (values != null) {
               long blockIndex = BlockPos.asLong(SectionPos.sectionRelative(pos.getX()), pos.getY(), SectionPos.sectionRelative(pos.getZ()));
               if (isFire) {
                  values.add(blockIndex);
               } else {
                  values.remove(blockIndex);
                  if (values.isEmpty()) {
                     this.loadedColumns.remove(index);
                  }
               }
            }
         }

         this.blockUpdates.clear();
      }
   }

   public void setInstanceUpdateCallback(InstanceUpdateCallback instanceUpdateCallback) {
      this.instanceUpdateCallback = instanceUpdateCallback;
   }

   public InstanceUpdateCallback getInstanceUpdateCallback() {
      return this.instanceUpdateCallback;
   }

   public PhysicsWorld getWorld() {
      return this.world;
   }

   public void addChunkColumn(LongSet loadFireChunks, int chunkX, int chunkZ) {
      long index = SectionPos.asLong(chunkX, 0, chunkZ);
      this.loadedColumns.put(index, loadFireChunks);
   }

   public void removeChunkColumn(int chunkX, int chunkZ) {
      long index = SectionPos.asLong(chunkX, 0, chunkZ);
      this.loadedColumns.remove(index);
   }

   public void clearFire() {
      this.loadedColumns.clear();
   }

   public ObjectOpenHashSetReplace<BlockUpdate> getBlockUpdates() {
      return this.blockUpdates;
   }

   public static enum SmokeParticleStyle {
      FIRE(0),
      STEAM(1);

      private final int id;

      private SmokeParticleStyle(int id) {
         this.id = id;
      }

      public int id() {
         return this.id;
      }

      public static SmokeDomain.SmokeParticleStyle byId(int id) {
         for (SmokeDomain.SmokeParticleStyle style : values()) {
            if (style.id == id) {
               return style;
            }
         }

         return FIRE;
      }
   }
}
