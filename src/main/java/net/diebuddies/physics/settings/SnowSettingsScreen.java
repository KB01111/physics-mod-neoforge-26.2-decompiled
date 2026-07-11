package net.diebuddies.physics.settings;

import java.util.List;
import java.util.Objects;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.snow.SnowCustomizationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class SnowSettingsScreen extends LegacyOptionsSubScreen {
   private boolean thicknessChanged = false;
   private final CycleOption<Boolean> PHYSICS_SNOW = CycleOption.createOnOff(
      "physicsmod.menu.snow.snowphysics", gameOptions -> ConfigClient.snowPhysics, (gameOptions, option, value) -> {
         ConfigClient.snowPhysics = value;
         Minecraft.getInstance().levelExtractor.allChanged();
      }
   );
   private final ProgressOption PHYSICS_SNOW_THICKNESS = new ProgressOption(
      "physicsmod.menu.snow.snowthickness",
      0.0,
      0.5,
      0.01F,
      gameOptions -> (double)ConfigClient.snowThickness,
      (gameOptions, value) -> {
         ConfigClient.snowThickness = value.floatValue();
         this.thicknessChanged = true;
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.snowthickness", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.snow.snowthickness.info")
   );
   private final CycleOption<Boolean> PHYSICS_SNOW_TRACKS = CycleOption.createOnOff(
      "physicsmod.menu.snow.snowtracks", gameOptions -> ConfigClient.snowTracks, (gameOptions, option, value) -> ConfigClient.snowTracks = value
   );
   private final ProgressOption PHYSICS_SNOW_TRACK_DISTANCE = new ProgressOption(
      "physicsmod.menu.snow.snowtrackdistance",
      4.0,
      240.0,
      0.1F,
      gameOptions -> ConfigClient.snowTrackDistance,
      (gameOptions, value) -> ConfigClient.snowTrackDistance = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.snowtrackdistance", String.format("%.0f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.snow.snowtrackdistance.info")
   );
   private final ProgressOption PHYSICS_SNOW_TRACK_ENTITIES = new ProgressOption(
      "physicsmod.menu.snow.snowtrackentities",
      1.0,
      60.0,
      1.0F,
      gameOptions -> (double)ConfigClient.snowTrackEntities,
      (gameOptions, value) -> ConfigClient.snowTrackEntities = value.intValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.snowtrackentities", String.format("%.0f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.snow.snowtrackentities.info")
   );
   private final CycleOption<Boolean> PHYSICS_GRASSY_SNOW = CycleOption.createOnOff(
         "physicsmod.menu.snow.grasssnowy", gameOptions -> ConfigClient.grassSnowy, (gameOptions, option, value) -> {
            ConfigClient.grassSnowy = value;
            Minecraft.getInstance().levelExtractor.allChanged();
         }
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.snow.grasssnowy.info"));
   private final CycleOption<Boolean> PHYSICS_SNOW_SMOOTH_SHADING = CycleOption.createOnOff(
         "physicsmod.menu.snow.snowsmoothshading", gameOptions -> ConfigClient.snowSmoothShading, (gameOptions, option, value) -> {
            ConfigClient.snowSmoothShading = value;
            Minecraft.getInstance().levelExtractor.allChanged();
         }
      )
      .setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.snow.snowsmoothshading.info"));
   private final CycleOption<SnowSettingsScreen.SnowType> PHYSICS_SNOW_TYPE = CycleOption.create(
      "physicsmod.menu.snow.snowtype", SnowSettingsScreen.SnowType.values(), model -> Component.translatable(model.toString()), gameOptions -> {
         int val = ConfigClient.snowType;
         return val >= SnowSettingsScreen.SnowType.values().length ? SnowSettingsScreen.SnowType.values()[0] : SnowSettingsScreen.SnowType.values()[val];
      }, (gameOptions, option, model) -> {
         SnowSettingsScreen.SnowType type = model;
         ConfigClient.snowType = type.ordinal();
         Minecraft.getInstance().levelExtractor.allChanged();
      }
   );
   private final CycleOption<SnowSettingsScreen.SnowQuality> PHYSICS_SNOW_QUALITY = CycleOption.create(
      "physicsmod.menu.snow.snowquality", SnowSettingsScreen.SnowQuality.values(), model -> Component.translatable(model.toString()), gameOptions -> {
         int val = ConfigClient.snowQuality;
         return val >= SnowSettingsScreen.SnowQuality.values().length
            ? SnowSettingsScreen.SnowQuality.values()[0]
            : SnowSettingsScreen.SnowQuality.values()[val];
      }, (gameOptions, option, model) -> {
         SnowSettingsScreen.SnowQuality type = model;
         ConfigClient.snowQuality = type.ordinal();
         Minecraft.getInstance().levelExtractor.allChanged();
      }
   );
   private final ProgressOption PHYSICS_SNOW_LOD = new ProgressOption(
      "physicsmod.menu.snow.levelofdetail",
      0.5,
      10.0,
      0.01F,
      gameOptions -> (double)ConfigClient.snowLOD,
      (gameOptions, value) -> {
         ConfigClient.snowLOD = value.floatValue();
         this.thicknessChanged = true;
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.snow.levelofdetail", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.snow.levelofdetail.info")
   );
   private static final int MAX_INFO_WIDTH = 300;
   private LegacyOptionsList list;
   private List<FormattedCharSequence> info = Minecraft.getInstance().font.split(Component.translatable("physicsmod.menu.snow.warning"), 300);

   public SnowSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.snow.title"));
      this.PHYSICS_SNOW_QUALITY.setTooltip(minecraft -> graphicsStatus -> Component.translatable("physicsmod.menu.snow.snowquality.info"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25) {
         {
            Objects.requireNonNull(SnowSettingsScreen.this);
         }

         @Override
         protected void renderDecorations(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
            int lineY = 0;

            for (FormattedCharSequence sequence : SnowSettingsScreen.this.info) {
               guiGraphics.text(SnowSettingsScreen.this.font, sequence, (this.width - 300) / 2, 187 + lineY, -171);
               lineY += 10;
            }

            super.renderDecorations(guiGraphics, mouseX, mouseY);
         }
      };
      this.list.addSmall(this.PHYSICS_SNOW, this.PHYSICS_SNOW_TRACKS);
      this.list.addBig(this.PHYSICS_SNOW_THICKNESS);
      this.list.addBig(this.PHYSICS_SNOW_LOD);
      this.list.addSmall(this.PHYSICS_SNOW_TRACK_ENTITIES, this.PHYSICS_SNOW_TRACK_DISTANCE);
      this.list.addSmall(this.PHYSICS_SNOW_TYPE, this.PHYSICS_SNOW_SMOOTH_SHADING);
      this.list.addSmall(this.PHYSICS_SNOW_QUALITY, this.PHYSICS_GRASSY_SNOW);
      this.children.add(this.list);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 130,
            this.height - 27,
            80,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
                  Language.getInstance().getOrDefault("physicsmod.menu.snow.reset"),
                  this,
                  widget -> this.addRenderableWidget(widget),
                  widget -> this.removeWidget(widget),
                  response -> {
                     if (response == PopupWidget.PopupResponse.YES) {
                        ConfigClient.resetSnowSettings();
                        this.list.children().clear();
                        this.minecraft.setScreenAndShow(new SnowSettingsScreen(this.lastScreen, this.options));
                        Minecraft.getInstance().levelExtractor.allChanged();
                     } else {
                        this.list.children().clear();
                        this.minecraft.setScreenAndShow(new SnowSettingsScreen(this.lastScreen, this.options));
                     }
                  }
               )
         )
      );
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 40,
            this.height - 27,
            80,
            20,
            Component.translatable("physicsmod.gui.customize"),
            button -> this.minecraft.setScreenAndShow(new SnowCustomizationScreen(this, this.minecraft.options))
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 50, this.height - 27, 80, 20, CommonComponents.GUI_DONE, button -> this.onClose()));
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
      if (this.thicknessChanged) {
         Minecraft.getInstance().levelExtractor.allChanged();
      }
   }

   public static enum SnowQuality {
      Medium("physicsmod.enum.snowquality.medium"),
      High("physicsmod.enum.snowquality.high");

      private String translationId;

      private SnowQuality(String translationId) {
         this.translationId = translationId;
      }

      @Override
      public String toString() {
         return this.translationId;
      }
   }

   public static enum SnowType {
      Round("physicsmod.enum.snowtype.round"),
      Cube("physicsmod.enum.snowtype.cube");

      private String translationId;

      private SnowType(String translationId) {
         this.translationId = translationId;
      }

      @Override
      public String toString() {
         return this.translationId;
      }
   }
}
