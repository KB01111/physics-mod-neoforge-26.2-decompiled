package net.diebuddies.physics.settings.gui;

import java.util.Objects;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class LabelOption extends LegacyOption {
   private String value;
   private int inactiveColor = 10526880;
   public LabelOption.LabelComponent label;

   public LabelOption(String value) {
      super(value);
      this.value = value;
   }

   @Override
   public AbstractWidget createButton(Options options, int x, int y, int width) {
      return this.label = new LabelOption.LabelComponent(x, y, width, 20, Component.literal(this.value));
   }

   public void setInactiveColor(int inactiveColor) {
      this.inactiveColor = inactiveColor;
   }

   public class LabelComponent extends AbstractWidget {
      public LabelComponent(int x, int y, int width, int height, Component component) {
         Objects.requireNonNull(LabelOption.this);
         super(x, y, width, height, component);
      }

      public void updateWidgetNarration(NarrationElementOutput narration) {
      }

      public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
         Minecraft minecraft = Minecraft.getInstance();
         Font font = minecraft.font;
         int color = this.active ? 16777215 : LabelOption.this.inactiveColor;
         guiGraphics.centeredText(
            font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color | Mth.ceil(this.alpha * 255.0F) << 24
         );
      }
   }
}
