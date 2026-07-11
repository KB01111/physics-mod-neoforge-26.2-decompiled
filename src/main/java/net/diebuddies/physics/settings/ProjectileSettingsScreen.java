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
import net.minecraft.util.Util;

public class ProjectileSettingsScreen extends LegacyOptionsSubScreen {
   private static final CycleOption<Boolean> PHYSICS_SNOWBALL_SHADE = CycleOption.createOnOff(
      "physicsmod.menu.items.snowballshade", gameOptions -> ConfigClient.snowballShade, (gameOptions, option, value) -> ConfigClient.snowballShade = value
   );
   private static final CycleOption<ProjectileSettingsScreen.Model> PHYSICS_SNOWBALL_MODEL = CycleOption.create(
      "physicsmod.menu.items.snowballmodel", ProjectileSettingsScreen.Model.values(), model -> Component.translatable(model.toString()), gameOptions -> {
         int val = ConfigClient.snowballModel;
         return val >= ProjectileSettingsScreen.Model.values().length
            ? ProjectileSettingsScreen.Model.values()[0]
            : ProjectileSettingsScreen.Model.values()[val];
      }, (gameOptions, option, model) -> {
         ProjectileSettingsScreen.Model type = model;
         ConfigClient.snowballModel = type.ordinal();
      }
   );
   private static final CycleOption<ProjectileSettingsScreen.Impact> PHYSICS_SNOWBALL_IMPACT = CycleOption.create(
      "physicsmod.menu.items.snowballimpact",
      ProjectileSettingsScreen.Impact.values(),
      model -> Component.translatable(model.toString()),
      gameOptions -> {
         int val = ConfigClient.snowballImpact;
         return val >= ProjectileSettingsScreen.Impact.values().length
            ? ProjectileSettingsScreen.Impact.values()[0]
            : ProjectileSettingsScreen.Impact.values()[val];
      },
      (gameOptions, option, model) -> {
         ProjectileSettingsScreen.Impact type = model;
         ConfigClient.snowballImpact = type.ordinal();
      }
   );
   private static final CycleOption<Boolean> PHYSICS_ENDERPEARL_SHADE = CycleOption.createOnOff(
      "physicsmod.menu.items.enderpearlshade",
      gameOptions -> ConfigClient.enderpearlShade,
      (gameOptions, option, value) -> ConfigClient.enderpearlShade = value
   );
   private static final CycleOption<ProjectileSettingsScreen.Model> PHYSICS_ENDERPEARL_MODEL = CycleOption.create(
      "physicsmod.menu.items.enderpearlmodel", ProjectileSettingsScreen.Model.values(), model -> Component.translatable(model.toString()), gameOptions -> {
         int val = ConfigClient.enderpearlModel;
         return val >= ProjectileSettingsScreen.Model.values().length
            ? ProjectileSettingsScreen.Model.values()[0]
            : ProjectileSettingsScreen.Model.values()[val];
      }, (gameOptions, option, model) -> {
         ProjectileSettingsScreen.Model type = model;
         ConfigClient.enderpearlModel = type.ordinal();
      }
   );
   private static final CycleOption<ProjectileSettingsScreen.Impact> PHYSICS_ENDERPEARL_IMPACT = CycleOption.create(
      "physicsmod.menu.items.enderpearlimpact",
      ProjectileSettingsScreen.Impact.values(),
      model -> Component.translatable(model.toString()),
      gameOptions -> {
         int val = ConfigClient.enderpearlImpact;
         return val >= ProjectileSettingsScreen.Impact.values().length
            ? ProjectileSettingsScreen.Impact.values()[0]
            : ProjectileSettingsScreen.Impact.values()[val];
      },
      (gameOptions, option, model) -> {
         ProjectileSettingsScreen.Impact type = model;
         ConfigClient.enderpearlImpact = type.ordinal();
      }
   );
   private static final CycleOption<Boolean> PHYSICS_EGG_SHADE = CycleOption.createOnOff(
      "physicsmod.menu.items.eggshade", gameOptions -> ConfigClient.eggShade, (gameOptions, option, value) -> ConfigClient.eggShade = value
   );
   private static final CycleOption<ProjectileSettingsScreen.Model> PHYSICS_EGG_MODEL = CycleOption.create(
      "physicsmod.menu.items.eggmodel", ProjectileSettingsScreen.Model.values(), model -> Component.translatable(model.toString()), gameOptions -> {
         int val = ConfigClient.eggModel;
         return val >= ProjectileSettingsScreen.Model.values().length
            ? ProjectileSettingsScreen.Model.values()[0]
            : ProjectileSettingsScreen.Model.values()[val];
      }, (gameOptions, option, model) -> {
         ProjectileSettingsScreen.Model type = model;
         ConfigClient.eggModel = type.ordinal();
      }
   );
   private static final CycleOption<ProjectileSettingsScreen.Impact> PHYSICS_EGG_IMPACT = CycleOption.create(
      "physicsmod.menu.items.eggimpact",
      ProjectileSettingsScreen.Impact.values(),
      model -> Component.translatable(model.toString()),
      gameOptions -> {
         int val = ConfigClient.eggImpact;
         return val >= ProjectileSettingsScreen.Impact.values().length
            ? ProjectileSettingsScreen.Impact.values()[0]
            : ProjectileSettingsScreen.Impact.values()[val];
      },
      (gameOptions, option, model) -> {
         ProjectileSettingsScreen.Impact type = model;
         ConfigClient.eggImpact = type.ordinal();
      }
   );
   private static final ProgressOption PHYSICS_LIFETIME_ITEMS = new ProgressOption(
      "physicsmod.menu.items.particlelifetimeitems",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeItems,
      (gameOptions, value) -> ConfigClient.particleLifetimeItems = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.items.particlelifetimeitems", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_ITEMS = new ProgressOption(
      "physicsmod.menu.items.particlelifetimevarianceitems",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeVarianceItems,
      (gameOptions, value) -> ConfigClient.particleLifetimeVarianceItems = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.items.particlelifetimevarianceitems", String.format("%.2f", option.get(gameOptions)))
   );
   private LegacyOptionsList list;

   public ProjectileSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.items.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
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

   public static enum Impact {
      Shatter("physicsmod.enum.impact.shatter"),
      Bounce("physicsmod.enum.impact.bounce"),
      Disappear("physicsmod.enum.impact.disappear");

      private String translationId;

      private Impact(String translationId) {
         this.translationId = translationId;
      }

      @Override
      public String toString() {
         return this.translationId;
      }
   }

   public static enum Model {
      Voxel("physicsmod.enum.model.voxel"),
      Round("physicsmod.enum.model.round"),
      Classic("physicsmod.enum.model.classic");

      private String translationId;

      private Model(String translationId) {
         this.translationId = translationId;
      }

      @Override
      public String toString() {
         return this.translationId;
      }
   }
}
