package net.diebuddies.physics.settings.gui;

import java.util.function.Consumer;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TextOption extends LegacyOption {
   private String name;
   private String value;
   private Consumer<String> consumer;
   private EditBox search;

   public TextOption(String name, String value, Consumer<String> consumer) {
      super(name);
      this.name = name;
      this.value = value;
      this.consumer = consumer;
   }

   @Override
   public AbstractWidget createButton(Options options, int x, int y, int width) {
      this.search = new EditBox(Minecraft.getInstance().font, x, y, width, 20, Component.literal(this.value));
      if (this.value != null && !this.value.isEmpty()) {
         this.search.setValue(this.value);
      } else {
         this.search.setSuggestion("<" + this.name + ">");
      }

      this.search.setResponder(changedText -> {
         if (changedText.isEmpty()) {
            this.search.setSuggestion("<" + this.name + ">");
         } else {
            this.search.setSuggestion("");
         }

         this.consumer.accept(changedText);
      });
      return this.search;
   }

   public void setText(String text) {
      if (this.search != null) {
         if (text.isEmpty()) {
            this.search.setSuggestion("<" + this.name + ">");
         } else {
            this.search.setSuggestion("");
         }

         this.search.setValue(text);
      }
   }

   public String getText() {
      return this.search != null ? this.search.getValue() : "";
   }
}
