package net.diebuddies.render;

import com.mojang.blaze3d.opengl.GlStateManager.TextureState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.mixins.GlStateManagerAccessor;
import org.lwjgl.opengl.GL11;

public class GlStateManagerPhysics {
   public static void _bindTexture(int target, int texture) {
      RenderSystem.assertOnRenderThread();
      int activeTexture = GlStateManagerAccessor.getActiveTexture();
      TextureState[] TEXTURES = GlStateManagerAccessor.getTEXTURES();
      if (texture != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = texture;
         GL11.glBindTexture(target, texture);
      }
   }
}
