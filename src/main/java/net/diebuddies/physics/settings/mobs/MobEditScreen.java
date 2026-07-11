package net.diebuddies.physics.settings.mobs;

import java.util.List;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MobEditScreen extends LegacyOptionsSubScreen {
   private LegacyOptionsList list;
   private String mob;
   private MobSetting setting;

   public MobEditScreen(Screen parent, Options options, String mob) {
      super(parent, options, Component.translatable("physicsmod.menu.mobs.edit.title", new Object[]{mob}));
      this.mob = mob;
      this.setting = ConfigMobs.getMobSetting(MobEntry.byString(mob).get()).copy();
   }

   protected void init() {
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 105, this.height - 27, 100, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreenAndShow(this.lastScreen)
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> {
         this.minecraft.setScreenAndShow(this.lastScreen);
         ConfigMobs.customizedMobs.put(this.mob, this.setting);
         ConfigMobs.save();
      }));
      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.list);
      List<LegacyOption> options = AdjustableUtil.generateOptions(this, this.setting);
      this.list.addSmall(options.toArray(new LegacyOption[options.size()]));
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
