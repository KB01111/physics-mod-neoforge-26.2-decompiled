package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3i;

public class FallingBlocks {
   public Set<Vector3i> alreadyChecked;
   public Set<Vector3i> fallen;
   public Set<Vector3i> toCheck;
   public Level level;
   public Player player;
   public int ticks;

   public FallingBlocks(Level level, Player player) {
      this.player = player;
      this.level = level;
      this.alreadyChecked = new ObjectOpenHashSet();
      this.fallen = new ObjectOpenHashSet();
      this.toCheck = new ObjectOpenHashSet();
      this.ticks = 0;
   }
}
