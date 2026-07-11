package net.diebuddies.physics.settings.vines;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.config.ConfigVines;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.EditButton;
import net.diebuddies.physics.settings.gui.RemoveButton;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class VineObjectSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   private Map<Block, DynamicSetting> settings;
   private Map<BaseEntry, Button> buttons1 = new Object2ObjectOpenHashMap();
   private Map<BaseEntry, Button> buttons2 = new Object2ObjectOpenHashMap();

   public VineObjectSelectionList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
      super(minecraft, width, height, top, bottom, itemHeight);
      this.settings = ConfigVines.configSettings;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      this.buttons1 = new Object2ObjectOpenHashMap();
      this.buttons2 = new Object2ObjectOpenHashMap();

      for (Block block : this.settings.keySet()) {
         String id = PhysicsMod.registeredBlocks.get(block);
         if (id != null) {
            BlockEntry entry = new BlockEntry(this, id, block);
            this.addEntry(entry);
         }
      }
   }

   public void addSetting(Block block, VineSetting setting) {
      this.settings.put(block, setting);
      this.refreshEntries();
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, tickDelta);
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
      return super.mouseClicked(mouseButtonEvent, bl) | this.listButtons(null, mouseButtonEvent, bl, 0.0F, false);
   }

   public boolean listButtons(GuiGraphicsExtractor guiGraphics, MouseButtonEvent mouseButtonEvent, boolean bl, float tickDelta, boolean render) {
      boolean clicked = false;

      for (int i = 0; i < this.getItemCount(); i++) {
         int entryY = this.getRowTop(i);
         int p = this.getRowBottomCustom(i);
         if (p >= this.y0 && entryY <= this.y1) {
            int entryHeight = this.itemHeight - 4;
            BaseEntry entry = this.getEntry(i);
            Button editButton = this.buttons1
               .computeIfAbsent(entry, key -> new EditButton(this.getRowRight() + 3, entryY, entryHeight, entryHeight - 1, Component.literal(""), source -> {
                     Block block = (Block)((BlockEntry)entry).getUserData();
                     if (block != null) {
                        this.minecraft
                           .setScreenAndShow(new VineEditScreen(this.minecraft.gui.screen(), this.minecraft.options, this.settings.get(block), block));
                     }
                  }));
            editButton.setX(this.getRowRight() + 3);
            editButton.setY(entryY);
            Button removeButton = this.buttons2
               .computeIfAbsent(
                  entry, key -> new RemoveButton(this.getRowRight() + 26, entryY, entryHeight, entryHeight - 1, Component.literal(""), source -> {
                        this.removeEntry(entry);
                        this.settings.remove((Block)entry.getUserData());
                        ConfigVines.save();
                        VineHelper.initFromConfigSettings();
                     })
               );
            removeButton.setX(this.getRowRight() + 26);
            removeButton.setY(entryY);
            if (!render) {
               if (editButton.mouseClicked(mouseButtonEvent, bl)) {
                  return true;
               }

               if (removeButton.mouseClicked(mouseButtonEvent, bl)) {
                  return true;
               }
            } else {
               removeButton.extractRenderState(guiGraphics, (int)mouseButtonEvent.x(), (int)mouseButtonEvent.y(), tickDelta);
               editButton.extractRenderState(guiGraphics, (int)mouseButtonEvent.x(), (int)mouseButtonEvent.y(), tickDelta);
            }
         }
      }

      return clicked;
   }

   @Override
   protected void renderList(GuiGraphicsExtractor guiGraphics, int x, int scrollAmount, int mouseX, int mouseY, float tickDelta) {
      super.renderList(guiGraphics, x, scrollAmount, mouseX, mouseY, tickDelta);
      this.listButtons(guiGraphics, new MouseButtonEvent((double)mouseX, (double)mouseY, new MouseButtonInfo(0, 0)), false, tickDelta, true);
   }

   private int getRowBottomCustom(int i) {
      return this.getRowTop(i) + this.itemHeight;
   }

   @Override
   protected int getScrollbarPosition() {
      return this.width - 20;
   }

   @Override
   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   @Override
   public int getRowWidth() {
      return 220;
   }

   public void setSelected(BaseEntry entry) {
      super.setSelected(entry);
   }
}
