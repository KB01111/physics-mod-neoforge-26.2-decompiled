package net.diebuddies.physics.settings.blocks;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.EditButton;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.settings.vines.BlockEntry;
import net.diebuddies.physics.vines.BlockFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public String filter = "";
   public BlockFilter blockFilter;
   public Consumer<String> editBlock;
   public boolean hasEditButton;
   private Map<BaseEntry, Button> buttons = new Object2ObjectOpenHashMap();

   public BlockSelectionList(Minecraft minecraft, BlockFilter blockFilter, int i, int j, int k, int l, int m, Consumer<String> editBlock) {
      super(minecraft, i, j, k, l, m);
      this.blockFilter = blockFilter;
      this.editBlock = editBlock;
      this.hasEditButton = editBlock != null;
      this.refreshEntries();
   }

   public BlockSelectionList(Minecraft minecraft, BlockFilter blockFilter, int i, int j, int k, int l, int m) {
      this(minecraft, blockFilter, i, j, k, l, m, null);
   }

   public void refreshEntries() {
      this.clearEntries();
      this.buttons = new Object2ObjectOpenHashMap();
      List<String> ids = new ObjectArrayList();

      for (String id : PhysicsMod.registeredBlocks.values()) {
         ids.add(id);
      }

      Collections.sort(ids);
      BlockEntry first = null;

      for (String id : ids) {
         if (id.toLowerCase().contains(this.filter.toLowerCase())) {
            Block block = PhysicsMod.invRegisteredBlocks.get(id);
            if (block != null) {
               BlockState state = block.defaultBlockState();
               if (state != null && (this.blockFilter == null || this.blockFilter.isValid(state))) {
                  BlockEntry entry = new BlockEntry(this, id, PhysicsMod.invRegisteredBlocks.get(id));
                  this.addEntry(entry);
                  if (first == null) {
                     first = entry;
                  }
               }
            }
         }
      }

      if (first != null) {
         this.ensureVisible(first);
      }
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
      return super.mouseClicked(mouseButtonEvent, bl) | this.listButtons(null, mouseButtonEvent, bl, 0.0F, false);
   }

   public boolean listButtons(GuiGraphicsExtractor guiGraphics, MouseButtonEvent mouseButtonEvent, boolean bl, float tickDelta, boolean render) {
      if (this.editBlock != null) {
         boolean clicked = false;
         int m = this.getItemCount();

         for (int index = 0; index < m; index++) {
            int entryY = this.getRowTop(index);
            int p = this.getRowBottomCustom(index);
            if (p >= this.y0 && entryY <= this.y1) {
               int entryHeight = this.itemHeight - 4;
               BaseEntry entry = this.getEntry(index);
               Button button = this.buttons
                  .computeIfAbsent(
                     entry,
                     key -> new EditButton(
                           this.getRowRight() + 3,
                           entryY,
                           entryHeight,
                           entryHeight - 1,
                           Component.literal(""),
                           source -> this.editBlock.accept(((BlockEntry)entry).getText())
                        )
                  );
               button.setX(this.getRowRight() + 3);
               button.setY(entryY);
               if (!render) {
                  if (button.mouseClicked(mouseButtonEvent, bl)) {
                     return true;
                  }
               } else {
                  button.extractRenderState(guiGraphics, (int)mouseButtonEvent.x(), (int)mouseButtonEvent.y(), tickDelta);
               }
            }
         }

         return clicked;
      } else {
         return false;
      }
   }

   private int getRowBottomCustom(int i) {
      return this.getRowTop(i) + this.itemHeight;
   }

   @Override
   protected int getScrollbarPosition() {
      return this.hasEditButton ? this.width - 20 : super.getScrollbarPosition();
   }

   @Override
   protected void renderList(GuiGraphicsExtractor guiGraphics, int x, int scrollAmount, int mouseX, int mouseY, float tickDelta) {
      super.renderList(guiGraphics, x, scrollAmount, mouseX, mouseY, tickDelta);
      this.listButtons(guiGraphics, new MouseButtonEvent((double)mouseX, (double)mouseY, new MouseButtonInfo(0, 0)), false, tickDelta, true);
   }
}
