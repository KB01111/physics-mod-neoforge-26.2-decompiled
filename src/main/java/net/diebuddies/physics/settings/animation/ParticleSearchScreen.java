package net.diebuddies.physics.settings.animation;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.ParticleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ParticleSearchScreen extends LegacyOptionsSubScreen {
   private static String searchText = "";
   private ParticleSelectionList list;
   private ParticleOption option;

   public ParticleSearchScreen(Screen parent, ParticleOption option) {
      super(parent, null, Component.translatable("physicsmod.menu.particle.search.title"));
      this.option = option;
   }

   protected void init() {
      this.list = new ParticleSelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.addRenderableWidget(this.list);
      int offset = 45;
      int var10003 = this.width / 2 - 175 + offset;
      EditBox search = new EditBox(Minecraft.getInstance().font, var10003, this.height - 27, 80, 20, Component.literal(""));
      search.setValue(searchText);
      this.checkSearchText(searchText, search);
      search.setResponder(changedText -> this.checkSearchText(changedText, search));
      this.addRenderableWidget(search);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 85 + offset, this.height - 27, 80, 20, CommonComponents.GUI_CANCEL, button -> this.minecraft.setScreenAndShow(this.lastScreen)
         )
      );
      this.addRenderableWidget(
         ButtonSettings.builder(this.width / 2 + 5 + offset, this.height - 27, 80, 20, Component.translatable("physicsmod.gui.select"), button -> {
            if (this.list.getSelected() != null) {
               this.option.setParticle((String)this.list.getSelected().getUserData());
               this.minecraft.setScreenAndShow(this.lastScreen);
            }
         })
      );
   }

   private void checkSearchText(String searchText, EditBox search) {
      ParticleSearchScreen.searchText = searchText;
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
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
