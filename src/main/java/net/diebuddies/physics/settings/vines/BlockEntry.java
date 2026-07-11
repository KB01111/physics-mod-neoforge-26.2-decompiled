package net.diebuddies.physics.settings.vines;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockEntry extends BaseEntry {
   private final String text;
   private ItemStack itemStack;

   public BlockEntry(LegacyObjectSelectionList objectSelectionList, String text, Block block) {
      super(objectSelectionList, block);
      this.text = text;

      try {
         this.itemStack = new ItemStack(block);
      } catch (Exception var5) {
      }
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
      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -1);
      } else {
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -4013374);
      }

      try {
         if (this.itemStack != null) {
            guiGraphics.fakeItem(this.itemStack, this.objectSelectionList.getRowLeft() + 2, y + (entryHeight - 16) / 2);
         }
      } catch (Exception var15) {
      }
   }

   public String getText() {
      return this.text;
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.text);
   }
}
