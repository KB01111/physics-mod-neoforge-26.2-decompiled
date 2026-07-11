package net.diebuddies.physics.settings.animation;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

public class AnimationSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public String filter = "";
   private boolean addParentEntry;

   public AnimationSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m, boolean addParentEntry) {
      super(minecraft, i, j, k, l, m);
      this.addParentEntry = addParentEntry;
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      LabelEntry first = null;
      String parent = Language.getInstance().getOrDefault("physicsmod.prop.mainrule");
      if (parent.contains(this.filter.toLowerCase()) && this.addParentEntry) {
         LabelEntry listEntry = new LabelEntry(this, parent);
         listEntry.setUserData(Long.valueOf(-1L));
         this.addEntry(listEntry);
         first = listEntry;
      }

      ObjectIterator var9 = ConfigAnimations.animations.long2ObjectEntrySet().iterator();

      while (var9.hasNext()) {
         it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<Animation> entry = (it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<Animation>)var9.next();
         long id = entry.getLongKey();
         Animation animation = (Animation)entry.getValue();
         if (animation.name.contains(this.filter.toLowerCase())) {
            LabelEntry listEntry = new LabelEntry(this, animation.name);
            listEntry.setUserData(Long.valueOf(id));
            this.addEntry(listEntry);
            if (first == null) {
               first = listEntry;
            }
         }
      }

      if (first != null) {
         this.ensureVisible(first);
      }
   }
}
