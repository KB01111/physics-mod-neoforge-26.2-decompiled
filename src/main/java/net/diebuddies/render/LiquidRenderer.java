package net.diebuddies.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.joml.Matrix4f;

public class LiquidRenderer {
   private MainRenderer mainRenderer;

   public LiquidRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void render(ClientLevel level, ChunkSectionLayer chunkSectionLayer, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
   }
}
