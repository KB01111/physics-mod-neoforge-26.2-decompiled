package net.diebuddies.physics.settings.snow;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.diebuddies.config.ConfigSnow;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.TitleWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Block;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public class SnowCustomizationScreen extends LegacyOptionsSubScreen {
   private static String searchText = "";
   private SnowObjectSelectionList activeList;
   private SnowObjectSelectionList inactiveList;
   private int topAndBottomSize = 32;

   public SnowCustomizationScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.snow.customize.title"));
   }

   protected void init() {
      Set<Block> inactiveBlockList = new ObjectLinkedOpenHashSet();
      Set<Block> activeBlockList = ConfigSnow.activeBlocks;

      for (Block block : PhysicsMod.registeredBlocks.keySet()) {
         if (!activeBlockList.contains(block)) {
            inactiveBlockList.add(block);
         }
      }

      List<String> ids = new ObjectArrayList();

      for (String id : PhysicsMod.registeredBlocks.values()) {
         ids.add(id);
      }

      Collections.sort(ids);
      this.activeList = new SnowObjectSelectionList(this.minecraft, activeBlockList, blockx -> {
         inactiveBlockList.add(blockx);
         this.inactiveList.refreshEntries();
      }, false, this.width / 2 + 1, this.height, this.topAndBottomSize, this.height - this.topAndBottomSize, 25);
      this.inactiveList = new SnowObjectSelectionList(this.minecraft, inactiveBlockList, blockx -> {
         activeBlockList.add(blockx);
         this.activeList.refreshEntries();
      }, true, this.width / 2 + 1, this.height, this.topAndBottomSize, this.height - this.topAndBottomSize, 25);
      this.inactiveList.setLeftPos(this.width / 2);
      this.inactiveList.setX(this.width / 2);
      this.addRenderableWidget(this.activeList);
      this.addRenderableWidget(this.inactiveList);
      int var10003 = this.width / 2 - 160;
      EditBox search = new EditBox(Minecraft.getInstance().font, var10003, this.height - 27, 100, 20, Component.literal(""));
      search.setValue(searchText);
      this.checkSearchText(searchText, search);
      search.setResponder(changedText -> this.checkSearchText(changedText, search));
      this.addRenderableWidget(search);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 + 60,
            this.height - 27,
            100,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
                  Language.getInstance().getOrDefault("physicsmod.menu.snow.customize.reset"),
                  this,
                  widget -> this.addRenderableWidget(widget),
                  widget -> this.removeWidget(widget),
                  response -> {
                     if (response == PopupWidget.PopupResponse.YES) {
                        ConfigSnow.loadDefaultConfigSettings();
                        this.applySnowSettings();
                        this.minecraft.setScreenAndShow(new SnowCustomizationScreen(this.lastScreen, this.options));
                     }
                  }
               )
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> {
         this.applySnowSettings();
         this.minecraft.setScreenAndShow(this.lastScreen);
      }));
      this.addRenderableWidget(new TitleWidget(0, 0, this.width / 2, this.height, Component.translatable("physicsmod.menu.snow.customize.active")));
      this.addRenderableWidget(
         new TitleWidget(this.width / 2, 0, this.width / 2, this.height, Component.translatable("physicsmod.menu.snow.customize.inactive"))
      );
   }

   private void checkSearchText(String searchText, EditBox search) {
      SnowCustomizationScreen.searchText = searchText;
      if (searchText.isEmpty()) {
         search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.search"));
      } else {
         search.setSuggestion("");
      }

      this.activeList.filter = searchText;
      this.activeList.refreshEntries();
      this.inactiveList.filter = searchText;
      this.inactiveList.refreshEntries();
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      int gradientSize = 2;
      int yStart = this.topAndBottomSize;
      int yEnd = this.height - this.topAndBottomSize;
      int xCenter = this.width / 2;
      this.fillGradientHorizontal(guiGraphics, xCenter - gradientSize, yStart, xCenter, yEnd, ARGB.color(0, 0, 0, 0), ARGB.color(255, 0, 0, 0));
      this.fillGradientHorizontal(guiGraphics, xCenter, yStart, xCenter + gradientSize, yEnd, ARGB.color(255, 0, 0, 0), ARGB.color(0, 0, 0, 0));
   }

   private void fillGradientHorizontal(GuiGraphicsExtractor guiGraphics, int x0, int y0, int x1, int y1, int color1, int color2) {
      guiGraphics.guiRenderState
         .addGuiElement(
            new SnowCustomizationScreen.HorizontalColoredRectangleRenderState(
               RenderPipelines.GUI,
               TextureSetup.noTexture(),
               new Matrix3x2f(guiGraphics.pose()),
               x0,
               y0,
               x1,
               y1,
               color1,
               color2,
               guiGraphics.scissorStack.peek()
            )
         );
   }

   private void applySnowSettings() {
      ConfigSnow.save();
      Minecraft.getInstance().levelExtractor.allChanged();
   }

   @Override
   public void onClose() {
      super.onClose();
      this.applySnowSettings();
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }

   public static record HorizontalColoredRectangleRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2fc pose,
      int x0,
      int y0,
      int x1,
      int y1,
      int col1,
      int col2,
      @Nullable ScreenRectangle scissorArea,
      @Nullable ScreenRectangle bounds
   ) implements GuiElementRenderState {
      public HorizontalColoredRectangleRenderState(
         RenderPipeline pipeline,
         TextureSetup textureSetup,
         Matrix3x2fc pose,
         int x0,
         int y0,
         int x1,
         int y1,
         int col1,
         int col2,
         @Nullable ScreenRectangle scissorArea
      ) {
         this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, getBounds(x0, y0, x1, y1, pose, scissorArea));
      }

      public void buildVertices(VertexConsumer vertexConsumer) {
         vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y0()).setColor(this.col1());
         vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x0(), (float)this.y1()).setColor(this.col1());
         vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y1()).setColor(this.col2());
         vertexConsumer.addVertexWith2DPose(this.pose(), (float)this.x1(), (float)this.y0()).setColor(this.col2());
      }

      @Nullable
      private static ScreenRectangle getBounds(int x0, int y0, int x1, int y1, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
         ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
         return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
      }
   }
}
