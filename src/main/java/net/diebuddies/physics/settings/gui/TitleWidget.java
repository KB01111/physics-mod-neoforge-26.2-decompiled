package net.diebuddies.physics.settings.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TitleWidget extends AbstractWidget {
   public TitleWidget(Screen screen) {
      super(0, 0, screen.width, screen.height, screen.getTitle());
      this.active = false;
   }

   public TitleWidget(int x, int y, int width, int height, Component title) {
      super(x, y, width, height, title);
      this.active = false;
   }

   public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      guiGraphics.centeredText(Minecraft.getInstance().font, this.message, this.width / 2 + this.getX(), 15, -1);
   }

   public void updateWidgetNarration(NarrationElementOutput narration) {
   }
}
