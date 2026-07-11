package net.diebuddies.mixins.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.minecraft.ClientChunkCacheAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.vines.DynamicLoader;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.RenderSectionExtension;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientChunkCache.Storage;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityTagOutput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientChunkCache.class})
public class MixinClientChunkManager implements DynamicLoader, ClientChunkCacheAccessor {
   @Shadow
   @Final
   protected volatile Storage storage;
   @Shadow
   @Final
   protected ClientLevel level;
   @Unique
   protected Long2ObjectMap<List<DynamicRagdoll>> loadedVines = new Long2ObjectOpenHashMap();
   @Unique
   protected PhysicsMod mod;
   @Unique
   protected LongSet loadedChunksSodiumFix = new LongOpenHashSet();

   @Override
   public void chunkPosChanged() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();
         LevelRenderer levelRenderer = Minecraft.getInstance().levelRenderer;
         LevelExtractor levelExtractor = Minecraft.getInstance().levelExtractor;

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            boolean isLoaded = this.loadedVines.containsKey(chunkIndex);
            boolean shouldBeLoaded = VineHelper.isChunkInRange(chunkX, chunkZ);
            if (isLoaded != shouldBeLoaded) {
               ObjectSet<Vector3i> affectedChunks = new ObjectOpenHashSet();
               if (isLoaded) {
                  List<DynamicRagdoll> unloadedRagdolls = this.unloadDynamicBlockChunk(chunkX, chunkZ, affectedChunks);
                  this.rebuildChunks(levelRenderer, levelExtractor, affectedChunks);
                  if (affectedChunks.isEmpty()) {
                     this.unloadRagdolls(unloadedRagdolls, true);
                  } else {
                     ObjectIterator var12 = affectedChunks.iterator();

                     while (var12.hasNext()) {
                        Vector3i affectedChunk = (Vector3i)var12.next();
                        RenderSectionExtension section = null;
                        if (StarterClient.sodium) {
                           section = Sodium.getRenderSection(levelRenderer, affectedChunk.x, affectedChunk.y, affectedChunk.z);
                        } else {
                           section = (RenderSectionExtension)levelRenderer.viewArea
                              .getRenderSection(SectionPos.asLong(affectedChunk.x, affectedChunk.y, affectedChunk.z));
                        }

                        if (section != null) {
                           section.setUnloadDynamicBlocks(this.mod.physicsWorld, affectedChunks, unloadedRagdolls);
                        }
                     }
                  }
               } else {
                  this.loadDynamicBlockChunk(((ClientChunkCache)(Object)this).getChunk(chunkX, chunkZ, false), chunkX, chunkZ, affectedChunks);
                  this.rebuildChunks(levelRenderer, levelExtractor, affectedChunks);
               }
            }
         }
      }
   }

   @Unique
   private void rebuildChunks(LevelRenderer levelRenderer, LevelExtractor levelExtractor, ObjectSet<Vector3i> affectedChunks) {
      Iterator<Vector3i> chunkIt = affectedChunks.iterator();

      while (chunkIt.hasNext()) {
         Vector3i affectedChunk = chunkIt.next();
         if (StarterClient.sodium) {
            if (!Sodium.isRenderSectionVisible(levelRenderer, affectedChunk.x, affectedChunk.y, affectedChunk.z)) {
               chunkIt.remove();
            }

            Sodium.scheduleChunkRebuild(levelRenderer, affectedChunk.x, affectedChunk.y, affectedChunk.z, false);
         } else {
            if (!levelRenderer.visibleSections()
               .contains(levelRenderer.viewArea.getRenderSection(SectionPos.asLong(affectedChunk.x, affectedChunk.y, affectedChunk.z)))) {
               chunkIt.remove();
            }

            levelExtractor.setSectionDirty(affectedChunk.x, affectedChunk.y, affectedChunk.z);
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"updateViewRadius"}
   )
   public void updateLoadDistance(int loadDistance, CallbackInfo info) {
      int properLoadDistance = Math.max(loadDistance, 2) + 3;
      LongIterator itLoaded = this.loadedChunksSodiumFix.iterator();

      while (itLoaded.hasNext()) {
         long chunkIndex = itLoaded.nextLong();
         int chunkX = ChunkHelper.getChunkX(chunkIndex);
         int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
         if (!this.isInRadius(properLoadDistance, chunkX, chunkZ)) {
            itLoaded.remove();
         }
      }

      if (this.mod != null) {
         Iterator<Entry<List<DynamicRagdoll>>> it = this.loadedVines.long2ObjectEntrySet().iterator();

         while (it.hasNext()) {
            Entry<List<DynamicRagdoll>> entry = it.next();
            long chunkIndex = entry.getLongKey();
            List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)entry.getValue();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            if (!this.isInRadius(properLoadDistance, chunkX, chunkZ)) {
               this.unloadRagdolls(ragdolls, false);
               it.remove();
            }
         }
      }
   }

   @Unique
   public boolean isInRadius(int radius, int chunkX, int chunkZ) {
      return Math.abs(chunkX - this.storage.viewCenterX) <= radius && Math.abs(chunkZ - this.storage.viewCenterZ) <= radius;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"drop"}
   )
   public void drop(ChunkPos chunkPos, CallbackInfo info) {
      int chunkX = chunkPos.x();
      int chunkZ = chunkPos.z();
      long chunkIndex = ChunkHelper.calcChunkIndex(chunkX, chunkZ);
      this.loadedChunksSodiumFix.remove(chunkIndex);
      if (this.mod != null) {
         this.unloadDynamicBlockChunk(chunkX, chunkZ);
      }
   }

   @Unique
   protected void unloadDynamicBlockChunk(int chunkX, int chunkZ) {
      long chunkIndex = ChunkHelper.calcChunkIndex(chunkX, chunkZ);
      List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)(Object)this.loadedVines.remove(chunkIndex);
      this.unloadRagdolls(ragdolls, false);
   }

   @Unique
   protected List<DynamicRagdoll> unloadDynamicBlockChunk(int chunkX, int chunkZ, ObjectSet<Vector3i> affectedChunks) {
      long chunkIndex = ChunkHelper.calcChunkIndex(chunkX, chunkZ);
      List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)(Object)this.loadedVines.remove(chunkIndex);
      if (ragdolls != null) {
         for (DynamicRagdoll ragdoll : ragdolls) {
            ragdoll.markedForRemoval = true;

            for (BlockPos pos : ragdoll.getBlockPositions()) {
               affectedChunks.add(
                  new Vector3i(
                     SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ())
                  )
               );
            }
         }
      }

      return ragdolls;
   }

   @Unique
   protected void unloadRagdolls(List<DynamicRagdoll> ragdolls, boolean removeOneFrameLater) {
      if (ragdolls != null) {
         for (DynamicRagdoll ragdoll : ragdolls) {
            if (removeOneFrameLater) {
               this.mod.sodiumRemoveRagdolls.add(ragdoll);
            } else {
               this.mod.physicsWorld.removeRagdoll(ragdoll);
            }
         }
      }
   }

   @Override
   public void unloadAllRagdolls() {
      ObjectIterator var1 = this.loadedVines.values().iterator();

      while (var1.hasNext()) {
         List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)var1.next();
         this.unloadRagdolls(ragdolls, false);
      }

      this.loadedVines.clear();
   }

   @Override
   public void loadAllRagdolls() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk chunk = ((ClientChunkCache)(Object)this).getChunk(chunkX, chunkZ, null, false);
            if (VineHelper.isChunkInRange(chunkX, chunkZ)) {
               this.loadDynamicBlockChunk(chunk, chunkX, chunkZ);
            }
         }
      }
   }

   @Override
   public void unloadAllSnow() {
   }

   @Override
   public void loadAllSnow() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk var6 = ((ClientChunkCache)(Object)this).getChunk(chunkX, chunkZ, null, false);
         }
      }
   }

   @Override
   public void unloadAllOcean() {
   }

   @Override
   public void loadAllOcean() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk var6 = ((ClientChunkCache)(Object)this).getChunk(chunkX, chunkZ, null, false);
         }
      }
   }

   @Override
   public void unloadAllFire() {
   }

   @Override
   public void loadAllFire() {
      if (this.mod != null) {
         LongIterator it = this.loadedChunksSodiumFix.iterator();

         while (it.hasNext()) {
            long chunkIndex = it.nextLong();
            int chunkX = ChunkHelper.getChunkX(chunkIndex);
            int chunkZ = ChunkHelper.getChunkZ(chunkIndex);
            LevelChunk var6 = ((ClientChunkCache)(Object)this).getChunk(chunkX, chunkZ, null, false);
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"replaceWithPacketData"}
   )
   public void replaceWithPacketDataHead(
      int x, int z, FriendlyByteBuf buf, Map<Types, long[]> map, Consumer<BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> info
   ) {
      StorageInvoker storageInvoker = (StorageInvoker)(Object)this.storage;
      if (storageInvoker.invokeInRange(x, z)) {
         int storageIndex = storageInvoker.invokeGetIndex(x, z);
         LevelChunk levelChunk = storageInvoker.invokeGetChunk(storageIndex);
         if (levelChunk != null) {
            ChunkPos chunkPos = levelChunk.getPos();
            int chunkX = chunkPos.x();
            int chunkZ = chunkPos.z();
            if (chunkX != x || chunkZ != z) {
               long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
               this.loadedChunksSodiumFix.remove(chunkIndex);
               if (this.mod != null) {
                  this.unloadDynamicBlockChunk(chunkX, chunkZ);
               }
            }
         }
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"replaceWithPacketData"}
   )
   public void replaceWithPacketData(
      int x, int z, FriendlyByteBuf buf, Map<Types, long[]> map, Consumer<BlockEntityTagOutput> consumer, CallbackInfoReturnable<LevelChunk> info
   ) {
      LevelChunk chunk = (LevelChunk)info.getReturnValue();
      if (chunk != null) {
         long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
         this.loadedChunksSodiumFix.add(chunkIndex);
         if (this.mod != null) {
            this.loadCombinedPhysicsChunk(chunk, x, z);
         }
      }
   }

   @Unique
   protected void loadCombinedPhysicsChunk(LevelChunk chunk, int x, int z) {
      boolean snow = ConfigClient.areSnowPhysicsEnabled();
      boolean ocean = ConfigClient.areOceanPhysicsEnabled();
      if (ConfigClient.areDynamicBlockPhysicsEnabled() && VineHelper.isChunkInRange(x, z)) {
         boolean var8 = true;
      } else {
         boolean var10000 = false;
      }

      boolean smoke = ConfigClient.areVolumetricSmokePhysicsEnabled();
   }

   @Unique
   protected boolean isValidStorageChunk(@Nullable LevelChunk levelChunk, int x, int z) {
      if (levelChunk == null) {
         return false;
      } else {
         ChunkPos chunkPos = levelChunk.getPos();
         return chunkPos.x() == x && chunkPos.z() == z;
      }
   }

   @Unique
   protected void loadSnowChunk(LevelChunk chunk, int x, int z) {
   }

   @Unique
   protected void loadOceanChunk(LevelChunk chunk, int x, int z) {
   }

   @Unique
   protected void loadDynamicBlockChunk(LevelChunk chunk, int x, int z) {
      this.loadDynamicBlockChunk(chunk, x, z, null);
   }

   @Unique
   protected void loadDynamicBlockChunk(LevelChunk chunk, int x, int z, ObjectSet<Vector3i> affectedChunks) {
      long chunkIndex = ChunkHelper.calcChunkIndex(x, z);
   }

   @Unique
   protected void loadFireChunk(LevelChunk chunk, int x, int z) {
   }

   @Override
   public void addVineRagdoll(DynamicRagdoll ragdoll, BlockPos pos) {
      long chunkIndex = ChunkHelper.calcChunkIndex(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
      List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)(Object)this.loadedVines.get(chunkIndex);
      if (ragdolls == null) {
         ragdolls = new ObjectArrayList();
         this.loadedVines.put(chunkIndex, ragdolls);
      }

      ragdolls.add(ragdoll);
   }

   @Override
   public void removeVineRagdoll(DynamicRagdoll ragdoll) {
      if (ragdoll.getBlockPositions().size() > 0) {
         BlockPos pos = ragdoll.getBlockPositions().get(0);
         long chunkIndex = ChunkHelper.calcChunkIndex(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
         List<DynamicRagdoll> ragdolls = (List<DynamicRagdoll>)(Object)this.loadedVines.get(chunkIndex);
         if (ragdolls != null) {
            ragdolls.remove(ragdoll);
         }
      }
   }

   @Unique
   protected List<DynamicRagdoll> searchConnections(int chunkX, int chunkZ, Long2ObjectMap<BlockState> vines) {
      List<DynamicRagdoll> ragdolls = new ObjectArrayList();

      while (vines.size() > 0) {
         Entry<BlockState> entry = (Entry<BlockState>)vines.long2ObjectEntrySet().iterator().next();
         long index = entry.getLongKey();
         BlockState current = (BlockState)entry.getValue();
         int x = (int)(index >> 60) & 15;
         int y = (int)(index & 72057594037927935L);
         int z = (int)(index >> 56) & 15;
         DynamicSetting setting = VineHelper.getSetting(current);
         if (setting != null) {
            DynamicRagdoll ragdoll = setting.createRagdoll(this.mod, current, new BlockPos(x + chunkX * 16, y, z + chunkZ * 16), vines);
            if (ragdoll != null) {
               ragdolls.add(ragdoll);
            }
         }
      }

      return ragdolls;
   }

   @Override
   public void setPhysicsMod(PhysicsMod physicsMod) {
      if (this.mod != null) {
         if (this.mod != physicsMod) {
            this.unloadAllRagdolls();
            this.unloadAllSnow();
            this.unloadAllOcean();
            this.unloadAllFire();
            this.mod = physicsMod;
            if (physicsMod != null) {
               this.loadAllRagdolls();
               this.loadAllSnow();
               this.loadAllOcean();
               this.loadAllFire();
            }
         }
      } else {
         this.mod = physicsMod;
         if (physicsMod != null) {
            this.loadAllRagdolls();
            this.loadAllSnow();
            this.loadAllOcean();
            this.loadAllFire();
         }
      }
   }

   @Override
   public Storage getStorage() {
      return this.storage;
   }
}
