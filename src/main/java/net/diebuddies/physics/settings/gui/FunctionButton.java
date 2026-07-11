package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.ux.Animator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Button.Plain;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class FunctionButton extends Plain {
   protected Identifier texture;

   public FunctionButton(int x, int y, int width, int button, Component component, OnPress onPress, Identifier texture) {
      super(x, y, width, button, component, onPress, Button.DEFAULT_NARRATION);
      this.texture = texture;
      ButtonSettings.addCustomButtonStyle(this);
   }

   protected void extractContents(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
      super.extractContents(guiGraphics, i, j, f);
      Animator.drawRect(
         guiGraphics, this.texture, (float)this.getX(), (float)(this.getY() - 1), 20.0F, 20.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, ARGB.white(this.alpha)
      );
   }

   public Identifier getTexture() {
      return this.texture;
   }
}
