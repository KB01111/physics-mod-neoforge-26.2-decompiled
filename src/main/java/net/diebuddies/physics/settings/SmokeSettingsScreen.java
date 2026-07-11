package net.diebuddies.physics.settings;

import java.util.Iterator;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.render.SmokeVolumeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class SmokeSettingsScreen extends LegacyOptionsSubScreen {
   private LegacyOptionsList list;
   private static final CycleOption<Boolean> PHYSICS_SMOKE = CycleOption.createOnOff(
      "physicsmod.menu.smoke.smokephysics", gameOptions -> ConfigClient.smokePhysics, (gameOptions, option, value) -> {
         ConfigClient.smokePhysics = value;
         Iterator i$ = PhysicsMod.getInstances().values().iterator();

         while (i$.hasNext()) {
            PhysicsMod mod = (PhysicsMod)i$.next();
            mod.getPhysicsWorld().getSmokeDomain().clearParticles();
         }
      }
   );
   private final CycleOption<Boolean> PHYSICS_VOLUMETRIC_SMOKE = CycleOption.createOnOff(
         "physicsmod.menu.smoke.smokevolumetricphysics", gameOptions -> ConfigClient.smokeVolumetricPhysics, (gameOptions, option, value) -> {
            if (((CycleOption)option).active) {
               ConfigClient.smokeVolumetricPhysics = value;
               Iterator i$ = PhysicsMod.getInstances().values().iterator();

               while (i$.hasNext()) {
                  PhysicsMod mod = (PhysicsMod)i$.next();
                  mod.getPhysicsWorld().destroy();
               }

               PhysicsMod.getInstances().clear();
               this.list.children().clear();
               Minecraft.getInstance().setScreenAndShow(new SmokeSettingsScreen(this.lastScreen, this.options));
            }
         }
      )
      .setTooltip(
         minecraft -> graphicsStatus -> !SmokeVolumeRenderer.supportsVolumetricSmoke()
                  ? Component.translatable("physicsmod.menu.smoke.smokevolumetricphysics.error")
                  : Component.translatable("physicsmod.menu.smoke.smokevolumetricphysics.info")
      );
   private final CycleOption<SmokeSettingsScreen.SmokeQuality> PHYSICS_VOLUMETRIC_SMOKE_QUALITY = CycleOption.create(
      "physicsmod.menu.smoke.smokequality",
      SmokeSettingsScreen.SmokeQuality.values(),
      model -> Component.translatable(model.toString()),
      gameOptions -> {
         int val = ConfigClient.smokeVolumeQuality;
         return val >= SmokeSettingsScreen.SmokeQuality.values().length
            ? SmokeSettingsScreen.SmokeQuality.values()[0]
            : SmokeSettingsScreen.SmokeQuality.values()[val];
      },
      (gameOptions, option, model) -> {
         SmokeSettingsScreen.SmokeQuality type = model;
         ConfigClient.smokeVolumeQuality = type.ordinal();
      }
   );
   private final CycleOption<Boolean> PHYSICS_CUDA_SMOKE = CycleOption.createOnOff(
         "physicsmod.menu.smoke.cudasmoke", gameOptions -> ConfigClient.cudaSmoke, (gameOptions, option, value) -> {
            if (((CycleOption)option).active) {
               ConfigClient.cudaSmoke = value;
               Iterator i$ = PhysicsMod.getInstances().values().iterator();

               while (i$.hasNext()) {
                  PhysicsMod mod = (PhysicsMod)i$.next();
                  mod.getPhysicsWorld().destroy();
               }

               PhysicsMod.getInstances().clear();
               StarterClient.createPhysicsCooking(ConfigClient.useCuda());
               this.list.children().clear();
               Minecraft.getInstance().setScreenAndShow(new SmokeSettingsScreen(this.lastScreen, this.options));
            }
         }
      )
      .setTooltip(
         minecraft -> graphicsStatus -> !StarterClient.cudaAvailable
                  ? Component.translatable("physicsmod.menu.smoke.cudasmoke.error")
                  : Component.translatable("physicsmod.menu.smoke.cudasmoke.info")
      );
   private static final ProgressOption PHYSICS_SMOKE_DISTANCE = new ProgressOption(
      "physicsmod.menu.smoke.smokephysicsrange",
      1.0,
      400.0,
      0.1F,
      gameOptions -> ConfigClient.smokePhysicsRange,
      (gameOptions, value) -> ConfigClient.smokePhysicsRange = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.smokephysicsrange", String.format("%.0f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_SMOKE_BRIGHTNESS = new ProgressOption(
      "physicsmod.menu.smoke.smokedensity",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeDensity,
      (gameOptions, value) -> ConfigClient.smokeDensity = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.smokedensity", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_EMBER_PARTICLES_AMOUNT = new ProgressOption(
      "physicsmod.menu.smoke.emberamount",
      0.0,
      3.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeEmberParticlesAmount,
      (gameOptions, value) -> ConfigClient.smokeEmberParticlesAmount = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.emberamount", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_SMOKE_PUFFINESS = new ProgressOption(
      "physicsmod.menu.smoke.puffiness",
      0.0,
      1.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeVolumeShadowStrength,
      (gameOptions, value) -> ConfigClient.smokeVolumeShadowStrength = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.puffiness", String.format("%.2f", option.get(gameOptions)))
   );
   private static final CycleOption<Boolean> PHYSICS_SMOKE_SHADOW = CycleOption.createOnOff(
      "physicsmod.menu.smoke.shadow", gameOptions -> ConfigClient.smokeVolumeShadow, (gameOptions, option, value) -> ConfigClient.smokeVolumeShadow = value
   );
   private static final ProgressOption PHYSICS_SMOKE_PARTICLE_LIMIT = new ProgressOption(
      "physicsmod.menu.smoke.smokeparticlelimit",
      1.0,
      ConfigClient.cudaSmoke() ? 200000.0 : 40000.0,
      0.1F,
      gameOptions -> ConfigClient.cudaSmoke() ? (double)ConfigClient.smokeParticleLimitCuda : (double)ConfigClient.smokeParticleLimit,
      (gameOptions, value) -> {
         if (ConfigClient.cudaSmoke()) {
            ConfigClient.smokeParticleLimitCuda = value.intValue();
            Iterator i$ = PhysicsMod.getInstances().values().iterator();

            while (i$.hasNext()) {
               PhysicsMod mod = (PhysicsMod)i$.next();
               mod.getPhysicsWorld().destroy();
            }

            PhysicsMod.getInstances().clear();
         } else {
            ConfigClient.smokeParticleLimit = value.intValue();
            Iterator i$ = PhysicsMod.getInstances().values().iterator();

            while (i$.hasNext()) {
               PhysicsMod mod = (PhysicsMod)i$.next();
               mod.getPhysicsWorld().getSmokeDomain().clearParticles();
            }
         }
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.smokeparticlelimit", String.format("%.0f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_SMOKE = new ProgressOption(
      "physicsmod.menu.smoke.particlelifetimesmoke", 0.0, 300.0, 0.1F, gameOptions -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            return ConfigClient.particleLifetimeSmokeVolumetric;
         } else {
            return ConfigClient.cudaSmoke() ? ConfigClient.particleLifetimeSmokeCuda : ConfigClient.particleLifetimeSmoke;
         }
      }, (gameOptions, value) -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            ConfigClient.particleLifetimeSmokeVolumetric = value;
         } else if (ConfigClient.cudaSmoke()) {
            ConfigClient.particleLifetimeSmokeCuda = value;
         } else {
            ConfigClient.particleLifetimeSmoke = value;
         }
      }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particlelifetimesmoke", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_SMOKE = new ProgressOption(
      "physicsmod.menu.smoke.particlelifetimevariancesmoke", 0.0, 30.0, 0.1F, gameOptions -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            return ConfigClient.particleLifetimeVarianceSmokeVolumetric;
         } else {
            return ConfigClient.cudaSmoke() ? ConfigClient.particleLifetimeVarianceSmokeCuda : ConfigClient.particleLifetimeVarianceSmoke;
         }
      }, (gameOptions, value) -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            ConfigClient.particleLifetimeVarianceSmokeVolumetric = value;
         } else if (ConfigClient.cudaSmoke()) {
            ConfigClient.particleLifetimeVarianceSmokeCuda = value;
         } else {
            ConfigClient.particleLifetimeVarianceSmoke = value;
         }
      }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particlelifetimevariancesmoke", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_DESPAWN_TIME_SMOKE = new ProgressOption(
      "physicsmod.menu.smoke.particledespawntimesmoke",
      0.0,
      100.0,
      0.1F,
      gameOptions -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            return ConfigClient.particleDespawnTimeSmokeVolumetric;
         } else {
            return ConfigClient.cudaSmoke() ? ConfigClient.particleDespawnTimeSmokeCuda : ConfigClient.particleDespawnTimeSmoke;
         }
      },
      (gameOptions, value) -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            ConfigClient.particleDespawnTimeSmokeVolumetric = value;
         } else if (ConfigClient.cudaSmoke()) {
            ConfigClient.particleDespawnTimeSmokeCuda = value;
         } else {
            ConfigClient.particleDespawnTimeSmoke = value;
         }
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particledespawntimesmoke", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.smoke.particledespawntimesmoke.info")
   );
   private static final ProgressOption PHYSICS_DESPAWN_TIME_VARIANCE_SMOKE = new ProgressOption(
      "physicsmod.menu.smoke.particledespawntimevariancesmoke", 0.0, 30.0, 0.1F, gameOptions -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            return ConfigClient.particleDespawnTimeVarianceSmokeVolumetric;
         } else {
            return ConfigClient.cudaSmoke() ? ConfigClient.particleDespawnTimeVarianceSmokeCuda : ConfigClient.particleDespawnTimeVarianceSmoke;
         }
      }, (gameOptions, value) -> {
         if (ConfigClient.areVolumetricSmokePhysicsEnabled()) {
            ConfigClient.particleDespawnTimeVarianceSmokeVolumetric = value;
         } else if (ConfigClient.cudaSmoke()) {
            ConfigClient.particleDespawnTimeVarianceSmokeCuda = value;
         } else {
            ConfigClient.particleDespawnTimeVarianceSmoke = value;
         }
      }, (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.particledespawntimevariancesmoke", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_COLOR_RED = new ProgressOption(
      "physicsmod.menu.smoke.red",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeColorRed,
      (gameOptions, value) -> ConfigClient.smokeColorRed = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.red", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_COLOR_GREEN = new ProgressOption(
      "physicsmod.menu.smoke.green",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeColorGreen,
      (gameOptions, value) -> ConfigClient.smokeColorGreen = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.green", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_COLOR_BLUE = new ProgressOption(
      "physicsmod.menu.smoke.blue",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeColorBlue,
      (gameOptions, value) -> ConfigClient.smokeColorBlue = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blue", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_VOLUME_COLOR_RED = new ProgressOption(
      "physicsmod.menu.smoke.red",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeVolumeColorRed,
      (gameOptions, value) -> ConfigClient.smokeVolumeColorRed = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.red", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_VOLUME_COLOR_GREEN = new ProgressOption(
      "physicsmod.menu.smoke.green",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeVolumeColorGreen,
      (gameOptions, value) -> ConfigClient.smokeVolumeColorGreen = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.green", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_VOLUME_COLOR_BLUE = new ProgressOption(
      "physicsmod.menu.smoke.blue",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeVolumeColorBlue,
      (gameOptions, value) -> ConfigClient.smokeVolumeColorBlue = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blue", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_DENSE_COLOR_RED = new ProgressOption(
      "physicsmod.menu.smoke.red",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeDenseColorRed,
      (gameOptions, value) -> ConfigClient.smokeDenseColorRed = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.red", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_DENSE_COLOR_GREEN = new ProgressOption(
      "physicsmod.menu.smoke.green",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeDenseColorGreen,
      (gameOptions, value) -> ConfigClient.smokeDenseColorGreen = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.green", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_DENSE_COLOR_BLUE = new ProgressOption(
      "physicsmod.menu.smoke.blue",
      0.0,
      2.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeDenseColorBlue,
      (gameOptions, value) -> ConfigClient.smokeDenseColorBlue = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blue", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_SMOKE_FIRE = new ProgressOption(
      "physicsmod.menu.smoke.fire",
      0.0,
      3.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeFire,
      (gameOptions, value) -> ConfigClient.smokeFire = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.fire", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_VOLUME_SMOKE_FIRE = new ProgressOption(
      "physicsmod.menu.smoke.fire",
      0.0,
      1.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeVolumeFire,
      (gameOptions, value) -> ConfigClient.smokeVolumeFire = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.fire", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_VOLUME_SMOKE_RADIUS = new ProgressOption(
      "physicsmod.menu.smoke.radius",
      0.0,
      5.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeVolumeRadius,
      (gameOptions, value) -> ConfigClient.smokeVolumeRadius = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.radius", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_SMOKE_CAMPFIRE = new ProgressOption(
      "physicsmod.menu.smoke.campfire",
      0.0,
      3.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeCampfire,
      (gameOptions, value) -> ConfigClient.smokeCampfire = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.campfire", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_SMOKE_BLAZE = new ProgressOption(
      "physicsmod.menu.smoke.blaze",
      0.0,
      3.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeBlaze,
      (gameOptions, value) -> ConfigClient.smokeBlaze = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.blaze", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_SMOKE_OTHER = new ProgressOption(
      "physicsmod.menu.smoke.other",
      0.0,
      3.0,
      0.01F,
      gameOptions -> (double)ConfigClient.smokeOther,
      (gameOptions, value) -> ConfigClient.smokeOther = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.smoke.other", String.format("%.2f", option.get(gameOptions)))
   );
   private static final int MAX_INFO_WIDTH = 350;
   private List<FormattedCharSequence> info = Minecraft.getInstance().font.split(Component.translatable("physicsmod.menu.smoke.warning"), 350);
   private LabelOption denseColor;
   private LabelOption color;

   public SmokeSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.smoke.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.renderBackgroundWhenIngame = false;
      this.children.add(this.list);
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> this.onClose()));
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80,
            this.height - 27,
            75,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
                  Language.getInstance().getOrDefault("physicsmod.menu.smoke.reset"),
                  this,
                  widget -> this.addRenderableWidget(widget),
                  widget -> this.removeWidget(widget),
                  response -> {
                     if (response == PopupWidget.PopupResponse.YES) {
                        ConfigClient.resetSmokeSettings();
                        this.list.children().clear();
                        this.minecraft.setScreenAndShow(new SmokeSettingsScreen(this.lastScreen, this.options));
                        Iterator i$ = PhysicsMod.getInstances().values().iterator();

                        while (i$.hasNext()) {
                           PhysicsMod mod = (PhysicsMod)i$.next();
                           mod.getPhysicsWorld().getSmokeDomain().clearParticles();
                        }
                     } else {
                        this.list.children().clear();
                        this.minecraft.setScreenAndShow(new SmokeSettingsScreen(this.lastScreen, this.options));
                     }
                  }
               )
         )
      );
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
      int lineY = 0;

      for (FormattedCharSequence sequence : this.info) {
         guiGraphics.text(this.font, sequence, (this.width - 350) / 2, 8 + lineY, -171);
         lineY += 10;
      }

      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      if (this.color != null && this.color.label != null) {
         if ((PHYSICS_COLOR_RED.widget == null || !PHYSICS_COLOR_RED.widget.isHoveredOrFocused())
            && (PHYSICS_COLOR_GREEN.widget == null || !PHYSICS_COLOR_GREEN.widget.isHoveredOrFocused())
            && (PHYSICS_COLOR_BLUE.widget == null || !PHYSICS_COLOR_BLUE.widget.isHoveredOrFocused())) {
            this.color.label.active = true;
         } else {
            this.color.label.active = false;
            int r = Math.max(0, Math.min((int)(PHYSICS_COLOR_RED.widget.getValue() * 255.0), 255));
            int g = Math.max(0, Math.min((int)(PHYSICS_COLOR_GREEN.widget.getValue() * 255.0), 255));
            int b = Math.max(0, Math.min((int)(PHYSICS_COLOR_BLUE.widget.getValue() * 255.0), 255));
            int hexColor = (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0;
            this.color.setInactiveColor(hexColor);
         }
      }

      if (this.denseColor != null && this.denseColor.label != null) {
         if ((PHYSICS_DENSE_COLOR_RED.widget == null || !PHYSICS_DENSE_COLOR_RED.widget.isHoveredOrFocused())
            && (PHYSICS_DENSE_COLOR_GREEN.widget == null || !PHYSICS_DENSE_COLOR_GREEN.widget.isHoveredOrFocused())
            && (PHYSICS_DENSE_COLOR_BLUE.widget == null || !PHYSICS_DENSE_COLOR_BLUE.widget.isHoveredOrFocused())) {
            this.denseColor.label.active = true;
         } else {
            this.denseColor.label.active = false;
            int rd = Math.max(0, Math.min((int)(PHYSICS_DENSE_COLOR_RED.widget.getValue() * 255.0), 255));
            int gd = Math.max(0, Math.min((int)(PHYSICS_DENSE_COLOR_GREEN.widget.getValue() * 255.0), 255));
            int bd = Math.max(0, Math.min((int)(PHYSICS_DENSE_COLOR_BLUE.widget.getValue() * 255.0), 255));
            int hexDenseColor = (rd & 0xFF) << 16 | (gd & 0xFF) << 8 | (bd & 0xFF) << 0;
            this.denseColor.setInactiveColor(hexDenseColor);
         }
      }
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }

   @Override
   public void onClose() {
      ConfigClient.save();
      super.onClose();
   }

   public static enum SmokeQuality {
      Low("physicsmod.enum.smokequality.low"),
      Medium("physicsmod.enum.smokequality.medium"),
      High("physicsmod.enum.smokequality.high");

      private String translationId;

      private SmokeQuality(String translationId) {
         this.translationId = translationId;
      }

      @Override
      public String toString() {
         return this.translationId;
      }
   }
}
