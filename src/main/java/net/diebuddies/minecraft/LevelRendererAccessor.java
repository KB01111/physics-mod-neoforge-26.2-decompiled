package net.diebuddies.minecraft;

import net.diebuddies.render.MainRenderer;
import net.minecraft.client.multiplayer.ClientLevel;

public interface LevelRendererAccessor {
   MainRenderer physicsmod$getMainRenderer();

   ClientLevel physicsmod$getLevel();
}
