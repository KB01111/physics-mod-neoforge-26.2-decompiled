package net.diebuddies.physics.settings.gui;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4fc;

public record GuiPhysicsEntityRenderState(
   EntityRenderState renderState,
   Matrix3x2f pose,
   Matrix4fc transform,
   int iconSize,
   int x0,
   int y0,
   int x1,
   int y1,
   float scale,
   @Nullable ScreenRectangle scissorArea,
   @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
   public GuiPhysicsEntityRenderState(
      EntityRenderState renderState,
      Matrix3x2f pose,
      Matrix4fc transform,
      int iconSize,
      int x0,
      int y0,
      int x1,
      int y1,
      float scale,
      @Nullable ScreenRectangle scissorArea
   ) {
      this(renderState, pose, transform, iconSize, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
   }
}
