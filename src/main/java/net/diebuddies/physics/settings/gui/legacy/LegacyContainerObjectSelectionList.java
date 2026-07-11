package net.diebuddies.physics.settings.gui.legacy;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority;
import org.jetbrains.annotations.Nullable;

public abstract class LegacyContainerObjectSelectionList<E extends LegacyAbstractSelectionList.LegacyEntry<E>> extends LegacyAbstractSelectionList<E> {
   private boolean hasFocus;

   public LegacyContainerObjectSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
      super(minecraft, i, j, k, l, m);
   }

   public void setFocused(boolean bl) {
      this.hasFocus = bl;
      if (this.hasFocus) {
         this.ensureVisible(this.getFocused());
      }
   }

   @Override
   public NarrationPriority narrationPriority() {
      return this.hasFocus ? NarrationPriority.FOCUSED : super.narrationPriority();
   }

   @Override
   protected boolean isSelectedItem(int i) {
      return false;
   }

   public void updateNarration(NarrationElementOutput narrationElementOutput) {
   }

   public abstract static class LegacyEntry<E extends LegacyContainerObjectSelectionList.LegacyEntry<E>>
      extends LegacyAbstractSelectionList.LegacyEntry<E>
      implements ContainerEventHandler {
      @Nullable
      private GuiEventListener focused;
      @Nullable
      private NarratableEntry lastNarratable;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean bl) {
         this.dragging = bl;
      }

      public void setFocused(@Nullable GuiEventListener guiEventListener) {
         this.focused = guiEventListener;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }

      public abstract List<? extends NarratableEntry> narratables();
   }
}
