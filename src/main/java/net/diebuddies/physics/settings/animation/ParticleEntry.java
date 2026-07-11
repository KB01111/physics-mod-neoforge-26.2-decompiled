package net.diebuddies.physics.settings.animation;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2fStack;

public class ParticleEntry extends BaseEntry {
   private final String particleID;
   private Particle particle;

   public ParticleEntry(LegacyObjectSelectionList objectSelectionList, String particleID) {
      super(objectSelectionList, particleID);
      this.particleID = particleID;

      try {
         ParticleOptions particleOptions = PhysicsMod.registeredParticles.get(particleID);
         this.particle = Minecraft.getInstance().particleEngine.createParticle(particleOptions, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
      } catch (Exception var4) {
      }
   }

   @Override
   public void extractRenderState(
      GuiGraphicsExtractor guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      Font font = Minecraft.getInstance().font;
      String text = this.particleID;
      if (font.width(Component.literal(text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55) {
         String newText = font.plainSubstrByWidth(text, this.objectSelectionList.getRowWidth() - 58);
         if (!text.equalsIgnoreCase(newText)) {
            text = newText + "...";
         }
      }

      Matrix3x2fStack matrices = guiGraphics.pose();
      matrices.pushMatrix();
      MutableComponent label = Component.literal(text);
      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -1);
      } else {
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -4013374);
      }

      if (this.particle != null && this.particle instanceof TextureSheetParticleExtension uvParticle) {
         float scale = (float)entryHeight / 2.0F * 0.9F;
         double startX = -1.0;
         double endX = 1.0;
         double startY = -1.0;
         double endY = 1.0;
         double particleWidth = endX - startX;
         double particleHeight = endY - startY;
         double xPosition = (double)(this.objectSelectionList.getRowLeft() + 2 + (int)scale);
         double yPosition = (double)(y + entryHeight / 2);
         matrices.translate((float)xPosition, (float)yPosition);
         matrices.scale(scale, scale);
         matrices.translate((float)(-particleWidth * 0.5 - startX), (float)(-particleHeight * 0.5 - startY));

         try {
            TextureAtlasSprite sprite = uvParticle.physicsmod$getSprite();
            float rCol = uvParticle.physicsmod$getRed();
            float gCol = uvParticle.physicsmod$getGreen();
            float bCol = uvParticle.physicsmod$getBlue();
            float alpha = uvParticle.physicsmod$getAlpha();
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, -1, -1, 2, 2, ARGB.colorFromFloat(alpha, rCol, gCol, bCol));
         } catch (Exception var38) {
            var38.printStackTrace();
         }
      }

      matrices.popMatrix();
   }

   public String getText() {
      return this.particleID;
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.particleID);
   }
}
