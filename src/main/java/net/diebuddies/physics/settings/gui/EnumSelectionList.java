package net.diebuddies.physics.settings.gui;

import java.util.Locale;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

public class EnumSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public String filter = "";
   private Enum<?> selectedEnum;

   public EnumSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m, Enum<?> selectedEnum) {
      super(minecraft, i, j, k, l, m);
      this.selectedEnum = selectedEnum;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      LabelEntry first = null;
      Language language = Language.getInstance();
      String filterLower = this.filter.toLowerCase(Locale.ROOT);

      for (Enum<?> value : this.selectedEnum.getDeclaringClass().getEnumConstants()) {
         String label = language.getOrDefault(value.toString());
         if (label.toLowerCase(Locale.ROOT).contains(filterLower)) {
            LabelEntry entry = new LabelEntry(this, label);
            entry.setUserData(value);
            this.addEntry(entry);
            if (first == null) {
               first = entry;
            }
         }
      }

      if (first != null) {
         this.ensureVisible(first);
      }
   }
}
