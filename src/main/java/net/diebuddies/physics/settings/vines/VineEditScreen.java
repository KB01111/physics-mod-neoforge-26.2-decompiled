package net.diebuddies.physics.settings.vines;

import java.util.List;
import net.diebuddies.config.ConfigVines;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.DynamicSettingEnum;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VineEditScreen extends LegacyOptionsSubScreen {
   private LegacyOptionsList list;
   private DynamicSettingEnum type;
   private DynamicSetting setting;
   private Block block = Blocks.WEEPING_VINES;

   public VineEditScreen(Screen parent, Options options, DynamicSetting setting, Block block) {
      super(parent, options, Component.translatable(setting != null ? "physicsmod.menu.dynamicblocks.edit.title" : "physicsmod.menu.dynamicblocks.add.title"));
      this.setting = setting;
      if (this.setting == null) {
         this.setting = new VineSetting();
         this.block = this.setting.defaultBlock();
      } else {
         this.block = block;
      }

      for (DynamicSettingEnum ds : DynamicSettingEnum.values()) {
         if (ds.getType() == this.setting.getClass()) {
            this.type = ds;
         }
      }
   }

   protected void init() {
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 80, this.height - 27, 75, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreenAndShow(this.lastScreen)
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> {
         if (this.block != null) {
            ConfigVines.configSettings.put(this.block, this.setting);
         }

         this.minecraft.setScreenAndShow(this.lastScreen);
      }));
      this.generateOptions();
   }

   private void generateOptions() {
      if (this.list != null) {
         this.children.remove(this.list);
      }

      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.list);
      CycleOption<DynamicSettingEnum> dynamicType = CycleOption.create(
         "physicsmod.menu.dynamicblocks.edit.type", DynamicSettingEnum.values(), model -> Component.literal(model.name()), gameOptions -> {
            int val = this.type.ordinal();
            return val >= DynamicSettingEnum.values().length ? DynamicSettingEnum.values()[0] : DynamicSettingEnum.values()[val];
         }, (gameOptions, option, model) -> {
            if (this.type != model) {
               this.type = model;

               try {
                  this.setting = (DynamicSetting)this.type.getType().getDeclaredConstructor().newInstance();
                  this.block = this.setting.defaultBlock();
               } catch (Exception var5) {
                  var5.printStackTrace();
               }

               this.generateOptions();
            }
         }
      );
      BlockOption blockOption = new BlockOption(
         Language.getInstance().getOrDefault("physicsmod.menu.dynamicblocks.edit.block"),
         PhysicsMod.registeredBlocks.get(this.block),
         false,
         this,
         block -> this.block = PhysicsMod.invRegisteredBlocks.get(block)
      );
      blockOption.setFilter(this.setting);
      List<LegacyOption> options = AdjustableUtil.generateOptions(this, this.setting);
      this.list.addBig(dynamicType);
      this.list.addBig(blockOption);

      for (int i = 0; i < options.size(); i++) {
         if (options.get(i) instanceof BlockOption) {
            this.list.addBig(options.remove(i--));
         }
      }

      this.list.addSmall(options.toArray(new LegacyOption[options.size()]));
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
