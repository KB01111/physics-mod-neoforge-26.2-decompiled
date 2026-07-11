package net.diebuddies.physics.settings.cloth;

import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;

public abstract class BaseEntry extends LegacyObjectSelectionList.Entry<BaseEntry> {
   protected final LegacyObjectSelectionList objectSelectionList;
   private Object object;

   protected BaseEntry(LegacyObjectSelectionList objectSelectionList, Object object) {
      this.objectSelectionList = objectSelectionList;
      this.object = object;
   }

   public boolean isSelected() {
      return this.objectSelectionList.getSelected() == this;
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
      if (!this.isSelected()) {
         this.objectSelectionList.setSelected(this);
         return true;
      } else {
         return false;
      }
   }

   public Object getUserData() {
      return this.object;
   }

   public void setUserData(Object object) {
      this.object = object;
   }
}
