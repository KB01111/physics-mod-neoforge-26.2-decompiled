package net.diebuddies.physics.settings.cloth;

import java.util.List;
import java.util.Objects;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

public class ClothSettingsScreen extends LegacyOptionsSubScreen {
   private static final CycleOption<Boolean> PHYSICS_ENTITY_CLOTH = CycleOption.createOnOff(
      "physicsmod.menu.cloth.entityclothphysics", gameOptions -> ConfigClient.capePhysics, (gameOptions, option, value) -> {
         ConfigClient.capePhysics = value;
         PhysicsMod.resetClothSimulations();
      }
   );
   private static final ProgressOption PYHSICS_ENTITY_CLOTH_RANGE = new ProgressOption(
      "physicsmod.menu.cloth.entityclothrange",
      0.1,
      120.0,
      0.1F,
      gameOptions -> (double)ConfigClient.clothEntityRange,
      (gameOptions, value) -> ConfigClient.clothEntityRange = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.entityclothrange", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.cloth.entityclothrange.info")
   );
   private static final CycleOption<Boolean> PHYSICS_FISHING_LINE = CycleOption.createOnOff(
      "physicsmod.menu.cloth.fishinglinephysics", gameOptions -> ConfigClient.fishingRodPhysics, (gameOptions, option, value) -> {
         ConfigClient.fishingRodPhysics = value;
         PhysicsMod.resetClothSimulations();
      }
   );
   private static final CycleOption<Boolean> PHYSICS_FORCE_ARMOR = CycleOption.createOnOff(
         "physicsmod.menu.cloth.forcearmor", gameOptions -> ConfigClient.clothForceArmor, (gameOptions, option, value) -> {
            ConfigClient.clothForceArmor = value;
            PhysicsMod.resetClothSimulations();
         }
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.cloth.forcearmor.info"));
   private static final CycleOption<Boolean> PHYSICS_LEASH = CycleOption.createOnOff(
      "physicsmod.menu.cloth.leashphysics", gameOptions -> ConfigClient.leashPhysics, (gameOptions, option, value) -> {
         ConfigClient.leashPhysics = value;
         PhysicsMod.resetClothSimulations();
      }
   );
   private static final CycleOption<Boolean> PHYSICS_BANNER = CycleOption.createOnOff(
      "physicsmod.menu.cloth.bannerphysics", gameOptions -> ConfigClient.bannerPhysics, (gameOptions, option, value) -> {
         ConfigClient.bannerPhysics = value;
         PhysicsMod.resetClothSimulations();
      }
   );
   private static final ProgressOption PHYSICS_BANNER_DISTANCE = new ProgressOption(
      "physicsmod.menu.cloth.bannerdistance",
      5.0,
      200.0,
      0.1F,
      gameOptions -> ConfigClient.bannerPhysicsRange,
      (gameOptions, value) -> ConfigClient.bannerPhysicsRange = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.bannerdistance", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.cloth.bannerdistance.info")
   );
   private static final CycleOption<Boolean> PHYSICS_CLOTH_SMOOTH_SHADING = CycleOption.createOnOff(
      "physicsmod.menu.cloth.smoothshading", gameOptions -> ConfigClient.clothSmoothShading, (gameOptions, option, value) -> {
         ConfigClient.clothSmoothShading = value;
         PhysicsMod.resetClothSimulations();
      }
   );
   private static final ProgressOption CPU_THREADS = new ProgressOption(
      "physicsmod.menu.cloth.clothcputhreads",
      1.0,
      (double)Runtime.getRuntime().availableProcessors(),
      1.0F,
      gameOptions -> (double)ConfigClient.clothThreads,
      (gameOptions, value) -> {
         ConfigClient.clothThreads = Math.max(value.intValue(), 1);
         VerletSimulation.asynchronousWorker.shutdownNow();
         VerletSimulation.asynchronousWorker = VerletSimulation.createClothThreads();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.clothcputhreads", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption LEASH_LENGTH = new ProgressOption(
      "physicsmod.menu.cloth.leashlength",
      0.1,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.leashLength,
      (gameOptions, value) -> ConfigClient.leashLength = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.leashlength", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption FISHING_LINE_LENGTH = new ProgressOption(
      "physicsmod.menu.cloth.fishinglinelength",
      0.1,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.fishingLineLength,
      (gameOptions, value) -> ConfigClient.fishingLineLength = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.cloth.fishinglinelength", String.format("%.2f", option.get(gameOptions)))
   );
   private LegacyOptionsList list;
   private static final int MAX_INFO_WIDTH = 300;
   private int infoOffset = 187;
   private List<FormattedCharSequence> info = Minecraft.getInstance().font.split(Component.translatable("physicsmod.menu.cloth.changesinfo"), 300);

   public ClothSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.cloth.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25) {
         {
            Objects.requireNonNull(ClothSettingsScreen.this);
         }

         @Override
         protected void renderDecorations(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
            int lineY = 0;

            for (FormattedCharSequence sequence : ClothSettingsScreen.this.info) {
               guiGraphics.text(ClothSettingsScreen.this.font, sequence, (this.width - 300) / 2, ClothSettingsScreen.this.infoOffset + lineY, -171);
               lineY += 10;
            }
         }
      };
      this.children.add(this.list);
      this.list.addSmall(PHYSICS_ENTITY_CLOTH, PYHSICS_ENTITY_CLOTH_RANGE);
      this.list.addBig(PHYSICS_FORCE_ARMOR);
      this.info = Minecraft.getInstance().font.split(Component.translatable("physicsmod.menu.cloth.proinfo"), 300);
      this.infoOffset = 87;
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
