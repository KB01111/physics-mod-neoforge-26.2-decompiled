package net.diebuddies.physics.settings.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;

public class ParticleSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public String filter = "";

   public ParticleSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
      super(minecraft, i, j, k, l, m);
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      List<String> ids = new ObjectArrayList();

      for (String id : PhysicsMod.registeredParticles.keySet()) {
         ids.add(id);
      }

      Collections.sort(ids);
      ParticleEntry first = null;

      for (String id : ids) {
         if (id.toLowerCase().contains(this.filter.toLowerCase())) {
            ParticleEntry entry = new ParticleEntry(this, id);
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

   private int getRowBottomCustom(int i) {
      return this.getRowTop(i) + this.itemHeight;
   }
}
