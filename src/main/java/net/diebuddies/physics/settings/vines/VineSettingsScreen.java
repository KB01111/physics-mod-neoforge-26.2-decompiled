package net.diebuddies.physics.settings.vines;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigVines;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class VineSettingsScreen extends LegacyOptionsSubScreen {
   private static final CycleOption<Boolean> PHYSICS_VINES = CycleOption.createOnOff(
      "physicsmod.menu.dynamicblocks.dynamicblockphysics", gameOptions -> ConfigClient.vinePhysics, (gameOptions, option, value) -> {
         ConfigClient.vinePhysics = value;
         Minecraft.getInstance().levelExtractor.allChanged();
      }
   );
   private static final ProgressOption PHYSICS_VINE_RANGE = new ProgressOption(
      "physicsmod.menu.dynamicblocks.range",
      20.0,
      400.0,
      1.0F,
      gameOptions -> ConfigClient.vineRange,
      (gameOptions, value) -> ConfigClient.vineRange = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.range", String.format("%.0f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_MAX_LOADED_BLOCKS = new ProgressOption(
      "physicsmod.menu.dynamicblocks.maxloadedblocks",
      1.0,
      400.0,
      1.0F,
      gameOptions -> (double)ConfigClient.maxLoadedDynamicBlocks,
      (gameOptions, value) -> ConfigClient.maxLoadedDynamicBlocks = value.intValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.maxloadedblocks", String.format("%.0f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.dynamicblocks.maxloadedblocks.info")
   );
   private static final ProgressOption PHYSICS_LIFETIME_VINES = new ProgressOption(
      "physicsmod.menu.dynamicblocks.lifetime",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeVines,
      (gameOptions, value) -> ConfigClient.particleLifetimeVines = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.lifetime", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_VINES = new ProgressOption(
      "physicsmod.menu.dynamicblocks.lifetimevariance",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeVarianceVines,
      (gameOptions, value) -> ConfigClient.particleLifetimeVarianceVines = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.dynamicblocks.lifetimevariance", String.format("%.2f", option.get(gameOptions)))
   );
   private LegacyOptionsList list;

   public VineSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.dynamicblocks.title"));
   }

   protected void init() {
      ConfigVines.init();
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addBig(PHYSICS_VINES);
      this.list.addBig(PHYSICS_VINE_RANGE);
      this.list.addBig(PHYSICS_MAX_LOADED_BLOCKS);
      this.list.addBig(PHYSICS_LIFETIME_VINES);
      this.list.addBig(PHYSICS_LIFETIME_VARIANCE_VINES);
      this.children.add(this.list);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80,
            this.height - 27,
            75,
            20,
            Component.translatable("physicsmod.gui.pro"),
            button -> Util.getPlatform().openUri("https://minecraftphysicsmod.com/pro")
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> this.onClose()));
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
      ConfigClient.save();
      super.onClose();
   }
}
