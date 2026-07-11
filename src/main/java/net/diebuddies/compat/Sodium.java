package net.diebuddies.compat;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.vertex.VertexFormatOffsetCache;
import net.caffeinemc.mods.sodium.client.world.LevelRendererExtension;
import net.diebuddies.minecraft.ChunkSectionsToRenderExtension;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.mixins.vines.RenderSectionManagerAccessor;
import net.diebuddies.mixins.vines.SodiumWorldRendererAccessor;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.render.BlockEntityVertexConsumer;
import net.diebuddies.physics.render.DummyVertexConsumer;
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
import net.diebuddies.physics.vines.RenderSectionExtension;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.SectionPos;

public class Sodium {
   public static void markSpriteActive(TextureAtlasSprite sprite) {
      if (StarterClient.sodium) {
         try {
            SpriteUtil.INSTANCE.markSpriteActive(sprite);
         } catch (Throwable var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void scheduleChunkRebuild(LevelRenderer renderer, int x, int y, int z, boolean important) {
      if (StarterClient.sodium) {
         try {
            ((LevelRendererExtension)renderer).sodium$getWorldRenderer().scheduleRebuildForChunk(x, y, z, important);
         } catch (Throwable var6) {
            var6.printStackTrace();
         }
      }
   }

   public static RenderSectionExtension getRenderSection(LevelRenderer renderer, int x, int y, int z) {
      if (StarterClient.sodium) {
         try {
            RenderSectionManager manager = ((SodiumWorldRendererAccessor)((LevelRendererExtension)renderer).sodium$getWorldRenderer())
               .getRenderSectionManager();
            RenderSection section = (RenderSection)((RenderSectionManagerAccessor)manager).getSectionByPosition().get(SectionPos.asLong(x, y, z));
            if (section != null) {
               return (RenderSectionExtension)section;
            }
         } catch (Throwable var6) {
            var6.printStackTrace();
         }
      }

      return null;
   }

   public static boolean isRenderSectionVisible(LevelRenderer renderer, int x, int y, int z) {
      if (StarterClient.sodium) {
         try {
            RenderSectionManager manager = ((SodiumWorldRendererAccessor)((LevelRendererExtension)renderer).sodium$getWorldRenderer())
               .getRenderSectionManager();
            return manager.isSectionBuilt(x, y, z);
         } catch (Throwable var5) {
            var5.printStackTrace();
         }
      }

      return true;
   }

   public static void setRenderer(LevelRenderer renderer) {
      if (StarterClient.sodium) {
         try {
            ((ChunkSectionsToRenderExtension)((LevelRendererExtension)renderer).sodium$getWorldRenderer())
               .physicsmod$setRenderer((LevelRendererAccessor)renderer);
         } catch (Throwable var2) {
            var2.printStackTrace();
         }
      }
   }

   public static BlockEntityVertexConsumer getNewBlockConsumer(GpuTextureView gpuTexture) {
      return new BlockEntityVertexConsumerSodium(gpuTexture);
   }

   public static DummyVertexConsumer getNewDummyConsumer(GpuTextureView gpuTexture) {
      return new DummyVertexConsumerSodium(gpuTexture);
   }

   public static BoundingBoxGetter getNewBoundingBoxConsumer() {
      return new BoundingBoxGetterSodium();
   }

   public static long getTextureElementOffset(VertexFormat format) {
      int[] cachedOffsets = VertexFormatOffsetCache.getInstance().getCachedOffsets(format);
      return (long)cachedOffsets[2];
   }

   public static long getStride(Object format) {
      return (long)((VertexFormat)format).getVertexSize();
   }
}
