package net.diebuddies.physics.settings.mobs;

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

public class MobSettingsScreen extends LegacyOptionsSubScreen {
   CycleOption<MobSettingsScreen.TypeWrapper> PHYSICS_MOB_CYCLING = CycleOption.create(
      "physicsmod.menu.mob.mobphysics",
      MobSettingsScreen.TypeWrapper.values(),
      model -> Component.translatable(MobPhysicsType.values()[MobSettingsScreen.TypeWrapper.unwrapMainRuleValue(model.ordinal())].toString()),
      gameOptions -> MobSettingsScreen.TypeWrapper.values()[MobSettingsScreen.TypeWrapper.wrapMainRuleValue(ConfigClient.mobSetting.type.ordinal())],
      (gameOptions, option, model) -> {
         MobSettingsScreen.TypeWrapper type = model;
         ConfigClient.mobSetting.type = MobPhysicsType.values()[MobSettingsScreen.TypeWrapper.unwrapMainRuleValue(type.ordinal())];
      }
   );
   private static final ProgressOption PHYSICS_LIFETIME_MOBS = new ProgressOption(
      "physicsmod.menu.mob.lifetime",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.mobSetting.lifetime,
      (gameOptions, value) -> ConfigClient.mobSetting.lifetime = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.lifetime", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE = new ProgressOption(
      "physicsmod.menu.mob.lifetimevariance",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.mobSetting.lifetimeVariance,
      (gameOptions, value) -> ConfigClient.mobSetting.lifetimeVariance = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.lifetimevariance", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_RAGDOLL_BREAK_FORCE = new ProgressOption(
      "physicsmod.menu.mob.ragdollbreakforce",
      0.0,
      4.0,
      0.01F,
      gameOptions -> (double)ConfigClient.jointBreakForce,
      (gameOptions, value) -> ConfigClient.jointBreakForce = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.ragdollbreakforce", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.mob.ragdollbreak.info")
   );
   private static final ProgressOption PHYSICS_RAGDOLL_BREAK_BLOOD = new ProgressOption(
      "physicsmod.menu.mob.ragdollbreakblood",
      0.0,
      5.0,
      0.01F,
      gameOptions -> (double)ConfigClient.jointBlood,
      (gameOptions, value) -> ConfigClient.jointBlood = value.floatValue(),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.ragdollbreakblood", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.mob.ragdollbreak.info")
   );
   private static final ProgressOption PHYSICS_RAGDOLL_LIMIT = new ProgressOption(
      "physicsmod.menu.mob.ragdolllimit",
      1.0,
      256.0,
      1.0F,
      gameOptions -> (double)ConfigClient.mobRagdollLimit,
      (gameOptions, value) -> ConfigClient.mobRagdollLimit = Math.max(value.intValue(), 1),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.mob.ragdolllimit", Integer.toString((int)option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.mob.ragdolllimit.info")
   );
   private LegacyOptionsList list;

   public MobSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.mobs.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      Animation animation = ConfigClient.mobSetting.animation;
      long id = -1L;
      ObjectIterator option = ConfigAnimations.animations.long2ObjectEntrySet().iterator();

      while (option.hasNext()) {
         Entry<Animation> entry = (Entry<Animation>)option.next();
         if (((Animation)entry.getValue()).equals(animation)) {
            id = entry.getLongKey();
            break;
         }
      }

      LegacyOption optionx = new AnimationOption(Language.getInstance().getOrDefault("physicsmod.animation"), id, this, particleChange -> {
         ConfigClient.mobSetting.animation = (Animation)ConfigAnimations.animations.get(Long.parseLong((String)particleChange));
         ConfigClient.save();
      }, "default", false);
      this.list.addSmall(this.PHYSICS_MOB_CYCLING, PHYSICS_LIFETIME_MOBS);
      this.list.addSmall(PHYSICS_LIFETIME_VARIANCE, optionx);
      this.list.addBig(PHYSICS_RAGDOLL_BREAK_FORCE);
      this.list.addBig(PHYSICS_RAGDOLL_BREAK_BLOOD);
      this.list.addBig(PHYSICS_RAGDOLL_LIMIT);
      this.children.add(this.list);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80,
            this.height - 27,
            75,
            20,
            Component.translatable("physicsmod.gui.customize"),
            button -> this.minecraft.setScreenAndShow(new MobCustomizeScreen(this, this.minecraft.options))
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

   public static enum TypeWrapper {
      RAGDOLL,
      BLOCKY,
      FRACTURED,
      BLOODY,
      OFF,
      RAGDOLL_BREAKING,
      RAGDOLL_BREAKING_BLOOD;

      private static int wrapMainRuleValue(int ordinal) {
         return ordinal > 4 ? ordinal - 1 : ordinal;
      }

      private static int unwrapMainRuleValue(int ordinal) {
         return ordinal > 4 ? ordinal + 1 : ordinal;
      }
   }
}
