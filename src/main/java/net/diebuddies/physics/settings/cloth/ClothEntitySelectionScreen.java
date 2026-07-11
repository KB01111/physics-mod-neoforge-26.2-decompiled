package net.diebuddies.physics.settings.cloth;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.TitleWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class ClothEntitySelectionScreen extends LegacyOptionsSubScreen {
   private ClothEntitySelectionList list;
   private Button select;

   public ClothEntitySelectionScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.mobs.customize.title"));
   }

   protected void init() {
      this.list = new ClothEntitySelectionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.addRenderableWidget(this.list);
      int var10003 = this.width / 2 - 160;
      EditBox search = new EditBox(Minecraft.getInstance().font, var10003, this.height - 27, 100, 20, Component.literal(""));
      search.setValue("");
      search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.customplayer"));
      search.setResponder(changedText -> this.checkSearchText(changedText, search));
      this.addRenderableWidget(search);
      this.addRenderableWidget(
         this.select = ButtonSettings.builder(this.width / 2 - 50, this.height - 27, 100, 20, Component.translatable("physicsmod.gui.select"), button -> {
            ClothDisplayScreen clothDisplay = (ClothDisplayScreen)this.lastScreen;
            if (search.getValue().isEmpty()) {
               if (this.list.getSelected() != null) {
                  String selected = (String)this.list.getSelected().getUserData();
                  clothDisplay.setSelectedEntity(selected);
               }
            } else {
               clothDisplay.setSelectedEntity("physicsmod:player:" + search.getValue());
            }

            this.minecraft.setScreenAndShow(this.lastScreen);
         })
      );
      this.addRenderableWidget(new TitleWidget(this));
   }

   private void checkSearchText(String searchText, EditBox search) {
      if (searchText.isEmpty()) {
         search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.customplayer"));
         this.select.setMessage(Component.translatable("physicsmod.gui.select"));
      } else {
         search.setSuggestion("");
         this.select.setMessage(Component.translatable("physicsmod.gui.selectx", new Object[]{searchText}));
      }
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
