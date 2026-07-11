package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SoundSettingsScreen extends LegacyOptionsSubScreen {
   private static final ProgressOption IMPACT_VOLUME = new ProgressOption(
      "physicsmod.menu.sound.soundvolume",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.impactVolume,
      (gameOptions, value) -> ConfigClient.impactVolume = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.sound.soundvolume", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.sound.soundvolume.info")
   );
   private static final ProgressOption WIND_VOLUME = new ProgressOption(
      "physicsmod.menu.sound.windvolume",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.windVolume,
      (gameOptions, value) -> ConfigClient.windVolume = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.sound.windvolume", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.sound.windvolume.info")
   );
   private static final ProgressOption OCEAN_SPLASH_VOLUME = new ProgressOption(
      "physicsmod.menu.sound.oceansplashvolume",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.oceanSplashVolume,
      (gameOptions, value) -> ConfigClient.oceanSplashVolume = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.sound.oceansplashvolume", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.sound.oceansplashvolume.info")
   );
   private LegacyOptionsList list;

   public SoundSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.sound.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addBig(IMPACT_VOLUME);
      this.list.addBig(WIND_VOLUME);
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
      ConfigClient.save();
      super.onClose();
   }
}
