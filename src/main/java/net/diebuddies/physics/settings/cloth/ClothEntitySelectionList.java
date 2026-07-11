package net.diebuddies.physics.settings.cloth;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.diebuddies.physics.settings.mobs.MobEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;

public class ClothEntitySelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public ClothEntitySelectionList(Minecraft minecraft, int i, int j, int k, int l, int m) {
      super(minecraft, i, j, k, l, m);
      this.refreshEntries();
   }

   public void refreshEntries() {
      this.clearEntries();
      List<String> ids = new ObjectArrayList();

      for (EntityType<?> type : PhysicsMod.renderers.keySet()) {
         ids.add(EntityType.getKey(type).toString());
      }

      Collections.sort(ids);
      MobEntry yourself = new MobEntry(this, "minecraft:player");
      yourself.setUserData("physicsmod:yourself");
      yourself.setText(ClothDisplayScreen.getEntityName("physicsmod:yourself"));
      this.addEntry(yourself);
      MobEntry allPlayers = new MobEntry(this, "minecraft:player");
      allPlayers.setUserData("minecraft:player");
      allPlayers.setText(ClothDisplayScreen.getEntityName("minecraft:player"));
      this.addEntry(allPlayers);

      for (String id : ConfigCloth.getEntityCustomizations().keySet()) {
         if (id.startsWith("physicsmod:player:")) {
            MobEntry otherPlayer = new MobEntry(this, "minecraft:player");
            otherPlayer.setUserData(id);
            otherPlayer.setText(ClothDisplayScreen.getEntityName(id));
            this.addEntry(otherPlayer);
         }
      }

      this.ensureVisible(yourself);
   }

   @Override
   protected int getScrollbarPosition() {
      return this.width - 20;
   }
}
