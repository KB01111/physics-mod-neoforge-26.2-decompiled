package net.diebuddies.physics.verlet;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

public class ClothRenderState {
   public boolean skipChestEquipment;
   public boolean skipLegsEquipment;
   public boolean skipFeetEquipment;
   public boolean skipHeadEquipment;
   public boolean skipElytra;
   public boolean skipCape;
   public List<String> hiddenModelParts = new ObjectArrayList();
}
