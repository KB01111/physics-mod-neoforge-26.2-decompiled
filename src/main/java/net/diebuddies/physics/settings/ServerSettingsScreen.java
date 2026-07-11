package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigServer;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ServerSettingsScreen extends LegacyOptionsSubScreen {
   private static final CycleOption<Boolean> SERVER_COLLAPSE = CycleOption.createOnOff(
      "physicsmod.menu.collapse.collapse", gameOptions -> ConfigServer.collapse, (gameOptions, option, value) -> ConfigServer.collapse = value
   );
   private static final CycleOption<Boolean> SERVER_DROP_BLOCKS = CycleOption.createOnOff(
      "physicsmod.menu.collapse.dropblocks", gameOptions -> ConfigServer.dropBlocks, (gameOptions, option, value) -> ConfigServer.dropBlocks = value
   );
   private static final ProgressOption SERVER_COLLAPSE_SPEED = new ProgressOption(
      "physicsmod.menu.collapse.collapsespeed",
      0.0,
      500.0,
      1.0F,
      gameOptions -> (double)ConfigServer.collapseSpeed,
      (gameOptions, value) -> ConfigServer.collapseSpeed = value.intValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.collapse.collapsespeed", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption SERVER_COLLAPSE_LIMIT = new ProgressOption(
      "Collapse Limit",
      1.0,
      5000.0,
      1.0F,
      gameOptions -> (double)ConfigServer.maxCollapseObjects,
      (gameOptions, value) -> ConfigServer.maxCollapseObjects = value.intValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.collapse.maxcollapseobjects", Integer.toString((int)option.get(gameOptions)))
   );
   private LegacyOptionsList list;

   public ServerSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.collapse.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addSmall(SERVER_COLLAPSE, SERVER_DROP_BLOCKS);
      this.list.addSmall(SERVER_COLLAPSE_SPEED, SERVER_COLLAPSE_LIMIT);
      this.children.add(this.list);
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.onClose()));
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }

   @Override
   public void onClose() {
      ConfigServer.save();
      super.onClose();
   }
}
