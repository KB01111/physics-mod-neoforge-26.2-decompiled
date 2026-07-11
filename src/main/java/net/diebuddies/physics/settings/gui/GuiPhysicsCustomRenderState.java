package net.diebuddies.physics.settings.gui;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;

public record GuiPhysicsCustomRenderState(
   GuiRenderable renderable, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
   public GuiPhysicsCustomRenderState(GuiRenderable renderable, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea) {
      this(renderable, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
   }
}
