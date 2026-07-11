package net.diebuddies.physics.settings.cloth;

import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LabelEntry extends BaseEntry {
   private final String text;
   private ChatFormatting extraStyle;

   public LabelEntry(LegacyObjectSelectionList objectSelectionList, String text) {
      super(objectSelectionList, text);
      this.text = text;
   }

   @Override
   public void extractRenderState(
      GuiGraphicsExtractor guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      Font font = Minecraft.getInstance().font;
      String text = this.text;
      if (font.width(Component.literal(text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55) {
         String newText = font.plainSubstrByWidth(text, this.objectSelectionList.getRowWidth() - 58);
         if (!text.equalsIgnoreCase(newText)) {
            text = newText + "...";
         }
      }

      MutableComponent label = Component.literal(text);
      if (this.extraStyle != null) {
         label = label.withStyle(this.extraStyle);
      }

      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -1);
      } else {
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -4013374);
      }
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.text);
   }

   public void setExtraStyle(ChatFormatting extraStyle) {
      this.extraStyle = extraStyle;
   }
}
