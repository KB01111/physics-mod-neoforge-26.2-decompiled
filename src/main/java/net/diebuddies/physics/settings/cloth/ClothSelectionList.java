package net.diebuddies.physics.settings.cloth;

import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.verlet.Cloth;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class ClothSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   private String selectedCategory;
   private ClothDisplayScreen clothDisplay;

   public ClothSelectionList(
      ClothDisplayScreen clothDisplay, String selectedCategory, Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight
   ) {
      super(minecraft, width, height, top, bottom, itemHeight);
      this.selectedCategory = selectedCategory;
      this.clothDisplay = clothDisplay;
      this.x0 = 50;
      this.xOffset = 0;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      String selectedCloth = ConfigCloth.getCategory(this.clothDisplay.getSelectedEntity(), this.selectedCategory);
      LabelEntry empty = new LabelEntry(this, "EMPTY");
      empty.setUserData(ClothSelectionList.ClothObject.EMPTY);
      this.addEntry(empty);

      for (Cloth cloth : PhysicsMod.cloth.values()) {
         if (cloth.rules.getCategory().equals(this.selectedCategory)) {
            String name = cloth.name;
            LabelEntry entry = new LabelEntry(this, name);
            if (cloth.rules.isLocal()) {
               entry.setExtraStyle(ChatFormatting.GOLD);
            }

            this.addEntry(entry);
            if (name.equalsIgnoreCase(selectedCloth)) {
               this.setSelected((BaseEntry)entry);
            }
         }
      }

      if (this.getSelected() == null) {
         this.setSelected((BaseEntry)empty);
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
      Object clothPiece = entry.getUserData();
      if (clothPiece == ClothSelectionList.ClothObject.EMPTY) {
         ConfigCloth.setCategory(this.clothDisplay.getSelectedEntity(), this.selectedCategory, null);
      } else {
         ConfigCloth.setCategory(this.clothDisplay.getSelectedEntity(), this.selectedCategory, (String)clothPiece);
      }

      this.clothDisplay.loadCloth();
   }

   public static enum ClothObject {
      EMPTY;
   }
}
