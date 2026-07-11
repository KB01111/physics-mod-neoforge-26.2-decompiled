package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record BlitFloatRenderState(
   RenderPipeline pipeline,
   TextureSetup textureSetup,
   Matrix3x2f pose,
   float x0,
   float y0,
   float x1,
   float y1,
   float u0,
   float u1,
   float v0,
   float v1,
   int color,
   @Nullable ScreenRectangle scissorArea,
   @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
   public BlitFloatRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      float x0,
      float y0,
      float x1,
      float y1,
      float u0,
      float u1,
      float v0,
      float v1,
      int color,
      @Nullable ScreenRectangle scissorArea
   ) {
      this(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea, getBounds((int)x0, (int)y0, (int)x1, (int)y1, pose, scissorArea));
   }

   public void buildVertices(VertexConsumer vertexConsumer) {
      vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0()).setUv(this.u0(), this.v0()).setColor(this.color());
      vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setUv(this.u0(), this.v1()).setColor(this.color());
      vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setUv(this.u1(), this.v1()).setColor(this.color());
      vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setUv(this.u1(), this.v0()).setColor(this.color());
   }

   @Nullable
   private static ScreenRectangle getBounds(int i, int j, int k, int l, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {
      ScreenRectangle screenRectangle2 = new ScreenRectangle(i, j, k - i, l - j).transformMaxBounds(matrix3x2f);
      return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
   }
}
