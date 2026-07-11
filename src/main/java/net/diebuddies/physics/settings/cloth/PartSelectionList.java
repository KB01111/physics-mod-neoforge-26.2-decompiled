package net.diebuddies.physics.settings.cloth;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;

public class PartSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   private ClothDisplayScreen clothDisplay;

   public PartSelectionList(ClothDisplayScreen clothDisplay, Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
      super(minecraft, width, height, top, bottom, itemHeight);
      this.clothDisplay = clothDisplay;
      this.x0 = 50;
      this.xOffset = 0;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      ObjectListIterator var1 = ClothConstants.getModelParts(this.clothDisplay.entityType).iterator();

      while (var1.hasNext()) {
         ModelPart part = (ModelPart)var1.next();
         String name = ((ModelPartParent)part).physicsmod$getName();
         if (name != null) {
            LabelEntry entry = new LabelEntry(this, name);
            this.addEntry(entry);
         }
      }
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
      return 160;
   }

   public void setSelected(BaseEntry entry) {
      super.setSelected(entry);
      if (entry != null) {
         this.clothDisplay.goToClothScreen((String)entry.getUserData());
      }
   }
}
