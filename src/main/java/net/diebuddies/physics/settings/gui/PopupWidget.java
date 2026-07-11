package net.diebuddies.physics.settings.gui;

import java.util.List;
import net.diebuddies.physics.settings.ButtonSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class PopupWidget extends AbstractWidget {
   private static final int MAX_INFO_WIDTH = 300;
   private String title;
   public List<FormattedCharSequence> info;

   public PopupWidget(String title, int x, int y, int width, int height, Component component) {
      super(x, y, width, height, component);
      this.title = title;
      this.info = Minecraft.getInstance().font.split(Component.literal(title), 300);
   }

   public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      guiGraphics.fill(this.getX(), this.getY(), this.width, this.height, -1090519040);
      int lineY = 0;

      for (FormattedCharSequence sequence : this.info) {
         int var10003 = this.width / 2;
         guiGraphics.centeredText(Minecraft.getInstance().font, sequence, var10003, this.height / 2 - 25 + lineY, -171);
         lineY += 10;
      }
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
   }

   public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
      return true;
   }

   public static void create(
      String title,
      Screen screen,
      PopupWidget.WidgetCreator creator,
      PopupWidget.WidgetRemover remover,
      Component yes,
      Component no,
      PopupWidget.PopupCallback callback
   ) {
      PopupWidget.PopupPress yesPress = new PopupWidget.PopupPress(remover, callback, PopupWidget.PopupResponse.YES);
      PopupWidget.PopupPress noPress = new PopupWidget.PopupPress(remover, callback, PopupWidget.PopupResponse.NO);
      PopupWidget background;
      creator.addWidget(background = new PopupWidget(title, 0, 0, screen.width, screen.height, Component.literal("")));
      int yOffset = (background.info.size() - 1) * 10;
      Button noButton;
      creator.addWidget(noButton = ButtonSettings.builder(screen.width / 2 - 90, screen.height / 2 + 5 + yOffset, 80, 20, no, noPress));
      Button yesButton;
      creator.addWidget(yesButton = ButtonSettings.builder(screen.width / 2 + 10, screen.height / 2 + 5 + yOffset, 80, 20, yes, yesPress));
      screen.children.remove(background);
      screen.children.remove(noButton);
      screen.children.remove(yesButton);
      screen.children.add(0, background);
      screen.children.add(0, noButton);
      screen.children.add(0, yesButton);
      yesPress.background = background;
      yesPress.noButton = noButton;
      yesPress.yesButton = yesButton;
      noPress.background = background;
      noPress.noButton = noButton;
      noPress.yesButton = yesButton;
   }

   public static void create(
      String title, Screen screen, PopupWidget.WidgetCreator creator, PopupWidget.WidgetRemover remover, PopupWidget.PopupCallback callback
   ) {
      create(title, screen, creator, remover, CommonComponents.GUI_YES, CommonComponents.GUI_NO, callback);
   }

   public interface PopupCallback {
      void popupClosed(PopupWidget.PopupResponse var1);
   }

   private static class PopupPress implements OnPress {
      public AbstractWidget background;
      public AbstractWidget noButton;
      public AbstractWidget yesButton;
      private PopupWidget.WidgetRemover remover;
      private PopupWidget.PopupCallback callback;
      private PopupWidget.PopupResponse response;

      public PopupPress(PopupWidget.WidgetRemover remover, PopupWidget.PopupCallback callback, PopupWidget.PopupResponse response) {
         this.remover = remover;
         this.callback = callback;
         this.response = response;
      }

      public void onPress(Button button) {
         this.remover.removeWidget(this.background);
         this.remover.removeWidget(this.noButton);
         this.remover.removeWidget(this.yesButton);
         this.callback.popupClosed(this.response);
      }
   }

   public static enum PopupResponse {
      YES,
      NO;
   }

   public interface WidgetCreator {
      void addWidget(AbstractWidget var1);
   }

   public interface WidgetRemover {
      void removeWidget(AbstractWidget var1);
   }
}
