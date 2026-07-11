package net.diebuddies.physics.settings;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

public class LiquidSettingsScreen extends LegacyOptionsSubScreen {
   private LegacyOptionsList list;
   private static final CycleOption<Boolean> PHYSICS_LIQUIDS = CycleOption.createOnOff(
      "physicsmod.menu.liquid.liquidphysics", gameOptions -> ConfigClient.liquidPhysics, (gameOptions, option, value) -> {
         ConfigClient.liquidPhysics = value;
         if (!ConfigClient.liquidPhysics) {
            Iterator i$ = PhysicsMod.getInstances().values().iterator();

            while (i$.hasNext()) {
               PhysicsMod mod = (PhysicsMod)i$.next();

               for (Liquid liquid : new ObjectArrayList<>(mod.getPhysicsWorld().getLiquids())) {
                  mod.getPhysicsWorld().removeLiquid(liquid);
               }
            }
         }
      }
   );
   private final CycleOption<Boolean> PHYSICS_CUDA_LIQUIDS = CycleOption.createOnOff(
         "physicsmod.menu.liquid.cudaliquids", gameOptions -> ConfigClient.cudaLiquids, (gameOptions, option, value) -> {
            if (((CycleOption)option).active) {
               ConfigClient.cudaLiquids = value;
               Iterator i$ = PhysicsMod.getInstances().values().iterator();

               while (i$.hasNext()) {
                  PhysicsMod mod = (PhysicsMod)i$.next();
                  mod.getPhysicsWorld().destroy();
               }

               PhysicsMod.getInstances().clear();
               StarterClient.createPhysicsCooking(ConfigClient.useCuda());
               this.list.children().clear();
               Minecraft.getInstance().setScreenAndShow(new LiquidSettingsScreen(this.lastScreen, this.options));
            }
         }
      )
      .setTooltip(
         minecraft -> graphicsStatus -> !StarterClient.cudaAvailable
                  ? Component.translatable("physicsmod.menu.liquid.cudaliquids.error")
                  : Component.translatable("physicsmod.menu.liquid.cudaliquids.info")
      );
   private static final ProgressOption PHYSICS_LIQUID_PARTICLE_SIZE = new ProgressOption(
      "physicsmod.menu.liquid.size",
      0.05,
      0.5,
      0.01F,
      gameOptions -> (double)ConfigClient.liquidParticleSize,
      (gameOptions, value) -> {
         ConfigClient.liquidParticleSize = value.floatValue();
         Iterator i$ = PhysicsMod.getInstances().values().iterator();

         while (i$.hasNext()) {
            PhysicsMod mod = (PhysicsMod)i$.next();
            mod.getPhysicsWorld().destroy();
         }

         PhysicsMod.getInstances().clear();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.size", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.liquid.size.info")
   );
   private static final ProgressOption PHYSICS_LIQUID_AMOUNT = new ProgressOption(
      "physicsmod.menu.liquid.amount",
      1.0,
      64.0,
      1.0F,
      gameOptions -> (double)ConfigClient.liquidAmount,
      (gameOptions, value) -> ConfigClient.liquidAmount = Math.max(value.intValue(), 1),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.amount", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIQUID_MAX_PARTICLES = new ProgressOption(
      "physicsmod.menu.liquid.maxpersource", 1.0, 200000.0, 1.0F, gameOptions -> (double)ConfigClient.liquidMaxParticles, (gameOptions, value) -> {
         ConfigClient.liquidMaxParticles = Math.max(value.intValue(), 1);
         Iterator i$ = PhysicsMod.getInstances().values().iterator();

         while (i$.hasNext()) {
            PhysicsMod mod = (PhysicsMod)i$.next();

            for (Liquid liquid : new ObjectArrayList<>(mod.getPhysicsWorld().getLiquids())) {
               mod.getPhysicsWorld().removeLiquid(liquid);
            }
         }
      }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.maxpersource", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_CUDA_LIQUID_PARTICLE_SIZE = new ProgressOption(
      "physicsmod.menu.liquid.size",
      0.05,
      0.5,
      0.01F,
      gameOptions -> (double)ConfigClient.cudaLiquidsParticleSize,
      (gameOptions, value) -> {
         ConfigClient.cudaLiquidsParticleSize = value.floatValue();
         Iterator i$ = PhysicsMod.getInstances().values().iterator();

         while (i$.hasNext()) {
            PhysicsMod mod = (PhysicsMod)i$.next();
            mod.getPhysicsWorld().destroy();
         }

         PhysicsMod.getInstances().clear();
      },
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.size", String.format("%.2f", option.get(gameOptions))),
      minecraft -> Component.translatable("physicsmod.menu.liquid.size.info")
   );
   private static final ProgressOption PHYSICS_CUDA_LIQUID_AMOUNT = new ProgressOption(
      "physicsmod.menu.liquid.amount",
      1.0,
      64.0,
      1.0F,
      gameOptions -> (double)ConfigClient.liquidCudaAmount,
      (gameOptions, value) -> ConfigClient.liquidCudaAmount = Math.max(value.intValue(), 1),
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.amount", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_CUDA_LIQUID_MAX_PARTICLES = new ProgressOption(
      "physicsmod.menu.liquid.maxpersource", 1.0, 200000.0, 1.0F, gameOptions -> (double)ConfigClient.liquidCudaMaxParticles, (gameOptions, value) -> {
         ConfigClient.liquidCudaMaxParticles = Math.max(value.intValue(), 1);
         Iterator i$ = PhysicsMod.getInstances().values().iterator();

         while (i$.hasNext()) {
            PhysicsMod mod = (PhysicsMod)i$.next();

            for (Liquid liquid : new ObjectArrayList<>(mod.getPhysicsWorld().getLiquids())) {
               mod.getPhysicsWorld().removeLiquid(liquid);
            }
         }
      }, (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.maxpersource", Integer.toString((int)option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_CUDA_LIQUID = new ProgressOption(
      "physicsmod.menu.liquid.particlelifetimeliquids",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeLiquidsCuda,
      (gameOptions, value) -> ConfigClient.particleLifetimeLiquidsCuda = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.particlelifetimeliquids", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_CUDA_LIQUID = new ProgressOption(
      "physicsmod.menu.liquid.particlelifetimevarianceliquids",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeVarianceLiquidsCuda,
      (gameOptions, value) -> ConfigClient.particleLifetimeVarianceLiquidsCuda = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.particlelifetimevarianceliquids", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_LIQUID = new ProgressOption(
      "physicsmod.menu.liquid.particlelifetimeliquids",
      0.0,
      100.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeLiquids,
      (gameOptions, value) -> ConfigClient.particleLifetimeLiquids = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.particlelifetimeliquids", String.format("%.2f", option.get(gameOptions)))
   );
   private static final ProgressOption PHYSICS_LIFETIME_VARIANCE_LIQUID = new ProgressOption(
      "physicsmod.menu.liquid.particlelifetimevarianceliquids",
      0.0,
      30.0,
      0.1F,
      gameOptions -> ConfigClient.particleLifetimeVarianceLiquids,
      (gameOptions, value) -> ConfigClient.particleLifetimeVarianceLiquids = value,
      (gameOptions, option) -> option.customFormat("physicsmod.menu.liquid.particlelifetimevarianceliquids", String.format("%.2f", option.get(gameOptions)))
   );
   private static final int MAX_INFO_WIDTH = 300;
   private List<FormattedCharSequence> info = Minecraft.getInstance().font.split(Component.translatable("physicsmod.menu.liquid.warning"), 300);

   public LiquidSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.liquid.title"));
   }

   protected void init() {
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25) {
         {
            Objects.requireNonNull(LiquidSettingsScreen.this);
         }

         @Override
         protected void renderDecorations(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
            int yOffset = 137;
            if (ConfigClient.cudaLiquids()) {
               int var5 = 137;
            }

            super.renderDecorations(guiGraphics, mouseX, mouseY);
         }
      };
      this.list.addBig(PHYSICS_LIQUIDS);
      this.list.addBig(this.PHYSICS_CUDA_LIQUIDS);
      this.PHYSICS_CUDA_LIQUIDS.setActive(StarterClient.cudaAvailable);
      if (ConfigClient.cudaLiquids()) {
         this.list.addBig(PHYSICS_CUDA_LIQUID_PARTICLE_SIZE);
         this.list.addBig(PHYSICS_CUDA_LIQUID_AMOUNT);
         this.list.addBig(PHYSICS_CUDA_LIQUID_MAX_PARTICLES);
         this.list.addBig(PHYSICS_LIFETIME_CUDA_LIQUID);
         this.list.addBig(PHYSICS_LIFETIME_VARIANCE_CUDA_LIQUID);
      } else {
         this.list.addBig(PHYSICS_LIQUID_PARTICLE_SIZE);
         this.list.addBig(PHYSICS_LIQUID_AMOUNT);
         this.list.addBig(PHYSICS_LIQUID_MAX_PARTICLES);
         this.list.addBig(PHYSICS_LIFETIME_LIQUID);
         this.list.addBig(PHYSICS_LIFETIME_VARIANCE_LIQUID);
      }

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
