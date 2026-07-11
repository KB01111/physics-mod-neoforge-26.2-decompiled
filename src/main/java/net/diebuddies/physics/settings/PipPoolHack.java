package net.diebuddies.physics.settings;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.lang.reflect.Field;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.neoforged.neoforge.client.gui.PictureInPictureRendererPool;

public final class PipPoolHack {
   private static Field LAST_FRAME_F;

   private static void init() {
      try {
         LAST_FRAME_F = PictureInPictureRendererPool.class.getDeclaredField("renderersLastFrame");
         LAST_FRAME_F.setAccessible(true);
      } catch (NoSuchFieldException var1) {
      }
   }

   public static <T extends PictureInPictureRenderState> Object2ObjectMap<T, PictureInPictureRenderer<T>> getRenderersLastFrame(
      PictureInPictureRendererPool<T> pool
   ) {
      if (LAST_FRAME_F == null) {
         init();
      }

      try {
         return (Object2ObjectMap<T, PictureInPictureRenderer<T>>)LAST_FRAME_F.get(pool);
      } catch (Exception var2) {
         return null;
      }
   }
}
