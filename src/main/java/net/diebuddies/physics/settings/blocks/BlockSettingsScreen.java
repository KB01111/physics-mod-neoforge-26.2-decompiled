package net.diebuddies.physics.settings.blocks;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.AnimationOption;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BlockSettingsScreen extends LegacyOptionsSubScreen {
   CycleOption<BlockSettingsScreen.TypeWrapper> PHYSICS_BLOCK = CycleOption.create(
      "physicsmod.menu.block.blockphysics",
      BlockSettingsScreen.TypeWrapper.values(),
      model -> Component.translatable(BlockPhysicsType.values()[BlockSettingsScreen.TypeWrapper.unwrapMainRuleValue(model.ordinal())].toString()),
      gameOptions -> BlockSettingsScreen.TypeWrapper.values()[BlockSettingsScreen.TypeWrapper.wrapMainRuleValue(ConfigClient.blockSetting.type.ordinal())],
      (gameOptions, option, model) -> {
         BlockSettingsScreen.TypeWrapper type = model;
         ConfigClient.blockSetting.type = BlockPhysicsType.values()[BlockSettingsScreen.TypeWrapper.unwrapMainRuleValue(type.ordinal())];
      }
   );
   private static final ProgressOption PHYSICS_LIFETIME_BLOCKS = new ProgressOption(
      "physicsmod.menu.block.lifetime",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.blockSetting.lifetime,
      (gameOptions, value) -> ConfigClient.blockSetting.lifetime = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.block.lifetime", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_BLOCKS = new ProgressOption(
      "physicsmod.menu.block.lifetimevariance",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.blockSetting.lifetimeVariance,
      (gameOptions, value) -> ConfigClient.blockSetting.lifetimeVariance = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.block.lifetimevariance", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_BLOCKS_RANGE = new ProgressOption(
      "physicsmod.menu.block.blockphysicsrange",
      10.0,
      320.0,
      0.1F,
      gameOptions -> ConfigClient.blockPhysicsRange,
      (gameOptions, value) -> ConfigClient.blockPhysicsRange = value,
      (gameOptions, option) -> {
         double val = option.get(gameOptions);
         return option.customFormat(
            "physicsmod.menu.block.blockphysicsrange",
            val >= 319.999
               ? Language.getInstance().getOrDefault("physicsmod.menu.block.blockphysicsrange.max")
               : String.format("%.2f", option.get(gameOptions))
         );
      },
      minecraft -> Component.translatable("physicsmod.menu.block.blockphysicsrange.info")
   );
   private static final ProgressOption PHYSICS_BLOCK_SCALE = new ProgressOption(
      "physicsmod.menu.block.scale",
      0.05,
      4.0,
      0.01F,
      gameOptions -> ConfigClient.blockSetting.scale,
      (gameOptions, value) -> ConfigClient.blockSetting.scale = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.block.scale", String.format("%.2f", option.get(gameOptions)))
   );
   private LegacyOptionsList list;

   public BlockSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.blocks.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      Animation animation = ConfigClient.blockSetting.animation;
      long id = -1L;
      ObjectIterator option = ConfigAnimations.animations.long2ObjectEntrySet().iterator();

      while (option.hasNext()) {
         Entry<Animation> entry = (Entry<Animation>)option.next();
         if (((Animation)entry.getValue()).equals(animation)) {
            id = entry.getLongKey();
            break;
         }
      }

      LegacyOption optionx = new AnimationOption(
         Language.getInstance().getOrDefault("physicsmod.animation"),
         id,
         this,
         particleChange -> ConfigClient.blockSetting.animation = (Animation)ConfigAnimations.animations.get(Long.parseLong((String)particleChange)),
         "default",
         false
      );
      this.list.addSmall(this.PHYSICS_BLOCK, PHYSICS_BLOCKS_RANGE);
      this.list.addSmall(PHYSICS_LIFETIME_BLOCKS, PHYSICS_BLOCK_SCALE);
      this.list.addBig(PHYSICS_LIFETIME_VARIANCE_BLOCKS);
      this.list.addBig(optionx);
      this.children.add(this.list);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80,
            this.height - 27,
            75,
            20,
            Component.translatable("physicsmod.gui.customize"),
            button -> this.minecraft.setScreenAndShow(new BlockCustomizeScreen(this, this.minecraft.options))
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> this.onClose()));
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   @Override
   public void onClose() {
      ConfigClient.save();
      super.onClose();
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }

   public static enum TypeWrapper {
      FRACTURED,
      BLOCKY,
      PARTICLES,
      OFF,
      FRACTURED_VOXEL;

      private static int wrapMainRuleValue(int ordinal) {
         return ordinal > 3 ? ordinal - 1 : ordinal;
      }

      private static int unwrapMainRuleValue(int ordinal) {
         return ordinal > 3 ? ordinal + 1 : ordinal;
      }
   }
}
