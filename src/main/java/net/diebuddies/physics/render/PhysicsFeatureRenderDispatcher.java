package net.diebuddies.physics.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.compat.Sodium;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer.Group;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.PreparedRenderType.Texture;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;

public class PhysicsFeatureRenderDispatcher implements AutoCloseable {
   private final ModelManager modelManager;
   private final AtlasManager atlasManager;
   private final Font font;
   private final GameRenderState gameRenderState;
   private final FeatureRendererMap featureRenderers;
   private final List<Group> groups;

   public PhysicsFeatureRenderDispatcher(FeatureRendererMap featureRenderers) {
      Minecraft minecraft = Minecraft.getInstance();
      this.modelManager = minecraft.getModelManager();
      this.atlasManager = minecraft.getAtlasManager();
      this.font = minecraft.font;
      this.gameRenderState = minecraft.gameRenderer.gameRenderState();
      this.featureRenderers = featureRenderers;
      this.groups = new ObjectArrayList();
   }

   public void render(SubmitNodeStorage submitNodeStorage) {
      Minecraft minecraft = Minecraft.getInstance();
      Map<FeatureRendererType<?>, List<SubmitNode>> allSubmits = new Object2ObjectOpenHashMap();
      submitNodeStorage.drainPhases(
         phase -> phase.sortInto((submit, strictlyOrdered) -> allSubmits.computeIfAbsent(submit.featureType(), key -> new ObjectArrayList()).add(submit))
      );
      FeatureFrameContext context = new FeatureFrameContext(
         this.gameRenderState.optionsRenderState,
         this.font,
         this.modelManager.getBlockStateModelSet(),
         minecraft.getBlockColors(),
         minecraft.getTextureManager(),
         this.atlasManager,
         minecraft.gameRenderer.lightmap(),
         null
      );

      for (FeatureRenderer<?> renderer : this.featureRenderers.values()) {
         renderer.beginPrepare(context);
      }

      for (Entry<FeatureRendererType<?>, List<SubmitNode>> entry : allSubmits.entrySet()) {
         FeatureRendererType<?> type = entry.getKey();
         List<SubmitNode> submits = entry.getValue();
         FeatureRenderer<?> renderer = this.featureRenderers.get(type);
         if (renderer != null) {
            if (renderer instanceof RenderTypeFeatureRenderer) {
               RenderTypeFeatureRenderer renderTypeRenderer = (RenderTypeFeatureRenderer)renderer;
               renderTypeRenderer.currentGroup = this.createGroup();

               try {
                  ReflectionsForge.buildGroup.invoke(renderTypeRenderer, context, submits);
               } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var12) {
                  var12.printStackTrace();
               }

               renderTypeRenderer.groups.add(renderTypeRenderer.currentGroup);
               this.groups.add(renderTypeRenderer.currentGroup);
               renderTypeRenderer.currentGroup = null;
            }

            renderer.finishPrepare(context);
         }
      }

      for (FeatureRenderer<?> renderer : this.featureRenderers.values()) {
         renderer.finishPrepare(context);
      }
   }

   public Group createGroup() {
      return new PhysicsFeatureRenderDispatcher.BlockEntityGroup();
   }

   public List<Group> getGroups() {
      return this.groups;
   }

   @Override
   public void close() {
      this.featureRenderers.close();
   }

   public static class BlockEntityGroup extends Group {
      private Map<RenderType, BlockEntityVertexConsumer> renderTypes = new Object2ObjectOpenHashMap();
      public static BlockEntityVertexConsumer currentConsumer;

      public BlockEntityGroup() {
         super(null, false);
      }

      public VertexConsumer getVertexBuilder(RenderType renderType) {
         PreparedRenderType prepared = renderType.prepare();
         GpuTextureView currentTexture = null;

         for (Texture texture : prepared.textures()) {
            if (texture.name().equalsIgnoreCase("Sampler0")) {
               currentTexture = texture.textureView();
            }
         }

         GpuTextureView texturex = currentTexture != null
            ? currentTexture
            : Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.WHITE_TEXTURE).getTextureView();
         TextureHelper.setLoadedTexture(texturex);
         return currentConsumer = this.renderTypes
            .computeIfAbsent(renderType, key -> StarterClient.sodium ? Sodium.getNewBlockConsumer(texturex) : new BlockEntityVertexConsumer(texturex));
      }

      public Map<RenderType, BlockEntityVertexConsumer> getBakedRenderTypeModels() {
         return this.renderTypes;
      }
   }

   public static class BoundingBoxGroup extends Group {
      public BoundingBoxGetter boundingBox = new BoundingBoxGetter();

      public BoundingBoxGroup() {
         super(null, false);
         this.boundingBox = StarterClient.sodium ? Sodium.getNewBoundingBoxConsumer() : new BoundingBoxGetter();
      }

      public VertexConsumer getVertexBuilder(RenderType renderType) {
         return this.boundingBox;
      }
   }

   public static class DummyGroup extends Group {
      private Map<RenderType, DummyVertexConsumer> renderTypes = new Object2ObjectOpenHashMap();

      public DummyGroup() {
         super(null, false);
      }

      public VertexConsumer getVertexBuilder(RenderType renderType) {
         PreparedRenderType prepared = renderType.prepare();
         GpuTextureView currentTexture = null;

         for (Texture texture : prepared.textures()) {
            if (texture.name().equalsIgnoreCase("Sampler0")) {
               currentTexture = texture.textureView();
            }
         }

         PhysicsMod mod = PhysicsMod.getCurrentInstance();
         if (mod != null) {
            mod.cubifyTranslucent = renderType.pipeline().getColorTargetState().blendFunction().orElse(null) == BlendFunction.TRANSLUCENT;
         }

         GpuTextureView texturex = currentTexture != null
            ? currentTexture
            : Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.WHITE_TEXTURE).getTextureView();
         TextureHelper.setLoadedTexture(texturex);
         return this.renderTypes
            .computeIfAbsent(renderType, key -> StarterClient.sodium ? Sodium.getNewDummyConsumer(texturex) : new DummyVertexConsumer(texturex));
      }
   }
}
