package net.diebuddies.physics.settings;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class GeneralSettingsScreen extends LegacyOptionsSubScreen {
   private static final ProgressOption PHYSICS_MAX_OBJECTS = new ProgressOption(
      "physicsmod.menu.general.maxphysicsobjects",
      10.0,
      30000.0,
      1.0F,
      gameOptions -> (double)ConfigClient.maxPhysicsObjects,
      (gameOptions, value) -> ConfigClient.maxPhysicsObjects = value.intValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.general.maxphysicsobjects", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_PLAYBACK_SPEED = new ProgressOption(
      "physicsmod.menu.general.playbackspeed",
      0.1,
      3.0,
      0.01F,
      gameOptions -> (double)ConfigClient.playbackSpeed,
      (gameOptions, value) -> ConfigClient.playbackSpeed = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.general.playbackspeed", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.general.playbackspeed.info")
   );
   private static final ProgressOption CPU_THREADS = new ProgressOption(
      "physicsmod.menu.general.cputhreads",
      1.0,
      (double)Runtime.getRuntime().availableProcessors(),
      1.0F,
      gameOptions -> (double)ConfigClient.cpuThreads,
      (gameOptions, value) -> ConfigClient.cpuThreads = Math.max(value.intValue(), 1),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.general.cputhreads", Integer.toString((int)option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.general.cputhreads.info")
   );
   private static final CycleOption<Boolean> MINECRAFT_BREAK_PARTICLES = CycleOption.createOnOff(
      "physicsmod.menu.general.minecraftblockbreakparticles",
      gameOptions -> ConfigClient.minecraftBlockBreakParticles,
      (gameOptions, option, value) -> ConfigClient.minecraftBlockBreakParticles = value
   );
   private static final CycleOption<Boolean> PVP_COMPATIBILITY = CycleOption.createOnOff(
         "physicsmod.menu.general.pvpservercompatibility",
         gameOptions -> ConfigClient.pvpServerCompatibility,
         (gameOptions, option, value) -> ConfigClient.pvpServerCompatibility = value
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.general.pvpservercompatibility.info"));
   private static final CycleOption<Boolean> ITEM_BREAK_PHYSICS = CycleOption.createOnOff(
         "physicsmod.menu.general.itembreakphysics",
         gameOptions -> ConfigClient.itemBreakPhysics,
         (gameOptions, option, value) -> ConfigClient.itemBreakPhysics = value
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.general.itembreakphysics.info"));
   private static final CycleOption<Boolean> CRACK_PARTICLES = CycleOption.createOnOff(
         "physicsmod.menu.general.crackphysicsparticles",
         gameOptions -> ConfigClient.crackPhysicsParticles,
         (gameOptions, option, value) -> ConfigClient.crackPhysicsParticles = value
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.general.crackphysicsparticles.info"));
   private static final CycleOption<Boolean> SPRINTING_PARTICLES = CycleOption.createOnOff(
         "physicsmod.menu.general.sprintingphysicsparticles",
         gameOptions -> ConfigClient.sprintingPhysicsParticles,
         (gameOptions, option, value) -> ConfigClient.sprintingPhysicsParticles = value
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.general.sprintingphysicsparticles.info"));
   private static final CycleOption<Boolean> EATING_PARTICLES = CycleOption.createOnOff(
         "physicsmod.menu.general.eatingphysicsparticles",
         gameOptions -> ConfigClient.eatingPhysicsParticles,
         (gameOptions, option, value) -> ConfigClient.eatingPhysicsParticles = value
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.general.eatingphysicsparticles.info"));
   private static final CycleOption<Boolean> SERVER_BLOCK_PARTICLES = CycleOption.createOnOff(
         "physicsmod.menu.general.serverblockPhysicsparticles",
         gameOptions -> ConfigClient.serverBlockPhysicsParticles,
         (gameOptions, option, value) -> ConfigClient.serverBlockPhysicsParticles = value
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.general.serverblockPhysicsparticles.info"));
   private static final ProgressOption PHYSICS_LIFETIME_CRACK = new ProgressOption(
      "physicsmod.menu.general.particlelifetimeparticles",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeParticles,
      (gameOptions, value) -> ConfigClient.particleLifetimeParticles = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.general.particlelifetimeparticles", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_CRACK = new ProgressOption(
      "physicsmod.menu.general.particlelifetimevarianceparticles",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeVarianceParticles,
      (gameOptions, value) -> ConfigClient.particleLifetimeVarianceParticles = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.general.particlelifetimevarianceparticles", String.format("%.2f", option.get(gameOptions)))
   );
   private static final CycleOption<Boolean> PHYSICS_ITEMS = CycleOption.createOnOff(
      "physicsmod.menu.items.itemphysics", gameOptions -> ConfigClient.itemPhysics, (gameOptions, option, value) -> ConfigClient.itemPhysics = value
   );
   private static final ProgressOption PHYSICS_ITEMS_ROTATION = new ProgressOption(
      "physicsmod.menu.items.rotation",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.itemRotationSpeed,
      (gameOptions, value) -> ConfigClient.itemRotationSpeed = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.items.rotation", String.format("%.2f", option.get(gameOptions)))
   );
   private LegacyOptionsList list;

   public GeneralSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.general.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addSmall(PHYSICS_MAX_OBJECTS, MINECRAFT_BREAK_PARTICLES);
      this.list.addBig(PHYSICS_PLAYBACK_SPEED);
      this.list.addBig(PHYSICS_ITEMS);
      this.list.addBig(PHYSICS_ITEMS_ROTATION);
      this.list.addBig(CPU_THREADS);
      this.list.addBig(PVP_COMPATIBILITY);
      this.list.addBig(ITEM_BREAK_PHYSICS);
      this.list.addSmall(CRACK_PARTICLES, SPRINTING_PARTICLES);
      this.list.addSmall(EATING_PARTICLES, SERVER_BLOCK_PARTICLES);
      this.list.addSmall(PHYSICS_LIFETIME_CRACK, PHYSICS_LIFETIME_VARIANCE_CRACK);
      this.children.add(this.list);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80,
            this.height - 27,
            75,
            20,
            Component.translatable("physicsmod.gui.gravity"),
            button -> this.minecraft.setScreenAndShow(new GravityCustomizeSettingsScreen(this, this.minecraft.options))
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
