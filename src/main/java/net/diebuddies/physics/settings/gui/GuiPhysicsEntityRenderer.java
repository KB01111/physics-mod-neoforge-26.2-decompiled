package net.diebuddies.physics.settings.gui;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

public class GuiPhysicsEntityRenderer extends PictureInPictureRenderer<GuiPhysicsEntityRenderState> implements RendererReset {
   private static final Matrix3x2f IDENTITY_POSE = new Matrix3x2f();
   private static final int ATLAS_SIZE = 128;
   private EntityRenderDispatcher entityRenderDispatcher;
   private int atlasX;
   private int atlasY;

   public GuiPhysicsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      this.entityRenderDispatcher = entityRenderDispatcher;
   }

   public Class<GuiPhysicsEntityRenderState> getRenderStateClass() {
      return GuiPhysicsEntityRenderState.class;
   }

   protected void renderToTexture(GuiPhysicsEntityRenderState guiEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
      Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ITEMS_3D);
      poseStack.pushPose();
      poseStack.mulPose(guiEntityRenderState.transform());

      try {
         this.entityRenderDispatcher.submit(guiEntityRenderState.renderState(), new CameraRenderState(), 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      poseStack.popPose();
   }

   @Override
   public void reset() {
      if (this.texture != null) {
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.texture, GuiRenderer.CLEAR_COLOR, this.depthTexture, 0.0);
         this.atlasX = 0;
         this.atlasY = 0;
      }
   }

   public void prepare(
      GuiPhysicsEntityRenderState pictureInPictureRenderState, GuiRenderState guiRenderState, FeatureRenderDispatcher featureRenderDispatcher, int guiScale
   ) {
      int iconSize = pictureInPictureRenderState.iconSize() * guiScale;
      if (this.atlasX + iconSize >= 128 * guiScale) {
         this.atlasY += iconSize;
         this.atlasX = 0;
         if (this.atlasY + iconSize >= 128 * guiScale) {
            this.atlasX = 128 * guiScale;
            return;
         }
      }

      this.prepareCustomTexturesAndProjection(128 * guiScale, 128 * guiScale);
      RenderSystem.outputColorTextureOverride = this.textureView;
      RenderSystem.outputDepthTextureOverride = this.depthTextureView;
      PoseStack poseStack = new PoseStack();
      poseStack.last().translate((float)(this.atlasX + iconSize / 2), (float)(this.atlasY + iconSize / 2), 0.0F);
      poseStack.last().scale((float)guiScale, (float)guiScale, (float)guiScale);
      poseStack.last().mulPose(new Matrix4f().mul(pictureInPictureRenderState.pose()));
      this.renderToTexture(pictureInPictureRenderState, poseStack, this.submitNodeStorage);
      featureRenderDispatcher.renderAllFeatures(this.submitNodeStorage);
      RenderSystem.outputColorTextureOverride = null;
      RenderSystem.outputDepthTextureOverride = null;
      float umin = (float)this.atlasX / (float)(128 * guiScale);
      float vmin = (float)this.atlasY / (float)(128 * guiScale);
      float umax = umin + (float)iconSize / (float)(128 * guiScale);
      float vmax = vmin + (float)iconSize / (float)(128 * guiScale);
      guiRenderState.addBlitToCurrentLayer(
         new BlitRenderState(
            RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
            TextureSetup.singleTexture(this.textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)),
            IDENTITY_POSE,
            pictureInPictureRenderState.x0(),
            pictureInPictureRenderState.y0(),
            pictureInPictureRenderState.x1(),
            pictureInPictureRenderState.y1(),
            umin,
            umax,
            1.0F - vmin,
            1.0F - vmax,
            -1,
            pictureInPictureRenderState.scissorArea(),
            null
         )
      );
      this.atlasX += iconSize;
   }

   public final void prepareCustomTexturesAndProjection(int width, int height) {
      if (this.texture != null && (this.texture.getWidth(0) != width || this.texture.getHeight(0) != height)) {
         this.texture.close();
         this.texture = null;
         this.textureView.close();
         this.textureView = null;
         this.depthTexture.close();
         this.depthTexture = null;
         this.depthTextureView.close();
         this.depthTextureView = null;
      }

      if (this.texture == null) {
         GpuDevice gpuDevice = RenderSystem.getDevice();
         this.texture = gpuDevice.createTexture(() -> "UI " + this.getTextureLabel() + " texture", 13, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
         this.textureView = gpuDevice.createTextureView(this.texture);
         this.depthTexture = gpuDevice.createTexture(() -> "UI " + this.getTextureLabel() + " depth texture", 9, GpuFormat.D32_FLOAT, width, height, 1, 1);
         this.depthTextureView = gpuDevice.createTextureView(this.depthTexture);
         gpuDevice.createCommandEncoder().clearColorAndDepthTextures(this.texture, GuiRenderer.CLEAR_COLOR, this.depthTexture, 1.0);
      }

      this.projection.setupOrtho(-1000.0F, 1000.0F, (float)width, (float)height, true);
      RenderSystem.setProjectionMatrix(this.projectionMatrixBuffer.getBuffer(this.projection), ProjectionType.ORTHOGRAPHIC);
   }

   protected float getTranslateY(int i, int j) {
      return (float)i / 2.0F;
   }

   protected String getTextureLabel() {
      return "physics mod entity";
   }
}
