package net.diebuddies.physics.settings.snow;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.settings.vines.BlockEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SnowObjectSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   private static final Identifier SELECT_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("transferable_list/select_highlighted");
   private static final Identifier UNSELECT_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("transferable_list/unselect_highlighted");
   public String filter = "";
   private Set<Block> blocks;
   private Map<BaseEntry, Button> buttons = new Object2ObjectOpenHashMap();
   private SnowObjectSelectionList.BlockRemoved removed;
   private boolean unselectList;

   public SnowObjectSelectionList(
      Minecraft minecraft,
      Set<Block> blocks,
      SnowObjectSelectionList.BlockRemoved removed,
      boolean unselectList,
      int width,
      int height,
      int top,
      int bottom,
      int itemHeight
   ) {
      super(minecraft, width, height, top, bottom, itemHeight);
      this.blocks = blocks;
      this.removed = removed;
      this.unselectList = unselectList;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      this.buttons = new Object2ObjectOpenHashMap();
      List<String> ids = new ObjectArrayList();

      for (Block block : this.blocks) {
         String id = PhysicsMod.registeredBlocks.get(block);
         if (id != null) {
            ids.add(id);
         }
      }

      Collections.sort(ids);

      for (String id : ids) {
         if (id.toLowerCase().contains(this.filter.toLowerCase())) {
            Block blockx = PhysicsMod.invRegisteredBlocks.get(id);
            if (blockx != null) {
               BlockState state = blockx.defaultBlockState();
               if (state != null) {
                  SnowObjectSelectionList.MovableBlockEntry entry = new SnowObjectSelectionList.MovableBlockEntry(
                     this, id, PhysicsMod.invRegisteredBlocks.get(id)
                  );
                  this.addEntry(entry);
               }
            }
         }
      }
   }

   public void addBlock(Block block) {
      this.blocks.add(block);
      this.refreshEntries();
   }

   public void removeBlock(Block block) {
      this.blocks.remove(block);
      this.refreshEntries();
   }

   public void setSelected(BaseEntry entry) {
      super.setSelected(entry);
      if (entry != null) {
         this.removeEntry(entry);
         this.blocks.remove((Block)entry.getUserData());
         this.removed.removedBlock((Block)entry.getUserData());
      }
   }

   @Override
   protected int getScrollbarPosition() {
      return this.width - 20 + this.x0;
   }

   @Override
   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   @Override
   public int getRowWidth() {
      return 180;
   }

   public interface BlockRemoved {
      void removedBlock(Block var1);
   }

   public class MovableBlockEntry extends BlockEntry {
      public MovableBlockEntry(LegacyObjectSelectionList objectSelectionList, String text, Block block) {
         Objects.requireNonNull(SnowObjectSelectionList.this);
         super(objectSelectionList, text, block);
      }

      @Override
      public void extractRenderState(
         GuiGraphicsExtractor guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
      ) {
         super.extractRenderState(guiGraphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
         if (this.isMouseOver((double)mouseX, (double)mouseY)) {
            if (SnowObjectSelectionList.this.unselectList) {
               guiGraphics.blitSprite(
                  RenderPipelines.GUI_TEXTURED,
                  SnowObjectSelectionList.UNSELECT_HIGHLIGHTED_SPRITE,
                  SnowObjectSelectionList.this.getRowLeft() - 16,
                  y + (entryHeight - 16) / 2,
                  16,
                  16
               );
            } else {
               guiGraphics.blitSprite(
                  RenderPipelines.GUI_TEXTURED,
                  SnowObjectSelectionList.SELECT_HIGHLIGHTED_SPRITE,
                  SnowObjectSelectionList.this.getScrollbarPosition() - 16,
                  y + (entryHeight - 16) / 2,
                  16,
                  16
               );
            }
         }
      }
   }
}
