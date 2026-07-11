package net.diebuddies.physics.settings.vines;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.blocks.BlockSelectionList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.vines.BlockFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BlockSearchScreen extends LegacyOptionsSubScreen {
   private static String searchText = "";
   private BlockSelectionList list;
   private BlockOption option;
   private boolean canBeNull;
   private BlockFilter filter;

   public BlockSearchScreen(Screen parent, BlockOption option, BlockFilter filter, boolean canBeNull) {
      super(parent, null, Component.translatable("physicsmod.menu.blocks.search.title"));
      this.filter = filter;
      this.option = option;
      this.canBeNull = canBeNull;
   }

   protected void init() {
      this.list = new BlockSelectionList(this.minecraft, this.filter, this.width, this.height, 32, this.height - 32, 25);
      this.addRenderableWidget(this.list);
      int offset = this.canBeNull ? 0 : 45;
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
               this.option.setBlock(((BlockEntry)this.list.getSelected()).getText());
               this.minecraft.setScreenAndShow(this.lastScreen);
            }
         })
      );
      if (this.canBeNull) {
         this.addRenderableWidget(
            ButtonSettings.builder(this.width / 2 + 95, this.height - 27, 80, 20, Component.translatable("physicsmod.gui.noblock"), button -> {
               this.option.setBlock(null);
               this.minecraft.setScreenAndShow(this.lastScreen);
            })
         );
      }
   }

   private void checkSearchText(String searchText, EditBox search) {
      BlockSearchScreen.searchText = searchText;
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
