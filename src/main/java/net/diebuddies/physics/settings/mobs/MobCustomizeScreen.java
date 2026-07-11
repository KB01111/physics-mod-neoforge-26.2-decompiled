package net.diebuddies.physics.settings.mobs;

import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.TitleWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class MobCustomizeScreen extends LegacyOptionsSubScreen {
   private static String searchText = "";
   private MobSelectionList list;

   public MobCustomizeScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.mobs.customize.title"));
   }

   protected void init() {
      this.list = new MobSelectionList(
         this.minecraft,
         this.width,
         this.height,
         32,
         this.height - 32,
         25,
         block -> this.minecraft.setScreenAndShow(new MobEditScreen(this, this.options, block))
      );
      this.addRenderableWidget(this.list);
      int var10003 = this.width / 2 - 160;
      EditBox search = new EditBox(Minecraft.getInstance().font, var10003, this.height - 27, 100, 20, Component.literal(""));
      search.setValue(searchText);
      this.checkSearchText(searchText, search);
      search.setResponder(changedText -> this.checkSearchText(changedText, search));
      this.addRenderableWidget(search);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 50, this.height - 27, 100, 20, CommonComponents.GUI_DONE, button -> this.minecraft.setScreenAndShow(this.lastScreen)
         )
      );
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 + 60,
            this.height - 27,
            100,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
                  Language.getInstance().getOrDefault("physicsmod.menu.mobs.customize.reset"),
                  this,
                  widget -> this.addRenderableWidget(widget),
                  widget -> this.removeWidget(widget),
                  response -> {
                     if (response == PopupWidget.PopupResponse.YES) {
                        ConfigMobs.resetMobs();
                        this.list.children().clear();
                        this.minecraft.setScreenAndShow(new MobSettingsScreen(this.lastScreen, this.options));
                     }
                  }
               )
         )
      );
      this.addRenderableWidget(new TitleWidget(this));
   }

   private void checkSearchText(String searchText, EditBox search) {
      MobCustomizeScreen.searchText = searchText;
      if (searchText.isEmpty()) {
         search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.search"));
      } else {
         search.setSuggestion("");
      }

      this.list.filter = searchText;
      this.list.refreshEntries();
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
