package net.diebuddies.physics.settings.animation;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.EditButton;
import net.diebuddies.physics.settings.gui.RemoveButton;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

public class AnimationEditList extends LegacyObjectSelectionList<BaseEntry> {
   private Long2ObjectMap<Animation> settings;
   private Map<BaseEntry, Button> buttons1 = new Object2ObjectOpenHashMap();
   private Map<BaseEntry, Button> buttons2 = new Object2ObjectOpenHashMap();

   public AnimationEditList(Minecraft minecraft, int width, int height, int top, int bottom, int itemHeight) {
      super(minecraft, width, height, top, bottom, itemHeight);
      this.settings = ConfigAnimations.animations;
      this.refreshEntries();
      if (this.getEntry(0) != null) {
         this.setSelected(this.getEntry(0));
      }
   }

   public void refreshEntries() {
      this.clearEntries();
      this.buttons1 = new Object2ObjectOpenHashMap();
      this.buttons2 = new Object2ObjectOpenHashMap();
      ObjectIterator var1 = this.settings.long2ObjectEntrySet().iterator();

      while (var1.hasNext()) {
         it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<Animation> entry = (it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<Animation>)var1.next();
         long identifier = entry.getLongKey();
         LabelEntry labelEntry = new LabelEntry(this, ((Animation)entry.getValue()).name);
         labelEntry.setUserData(Long.valueOf(identifier));
         this.addEntry(labelEntry);
      }
   }

   public void addSetting(long identifier, Animation animation) {
      this.settings.put(identifier, animation);
      this.refreshEntries();
   }

   @Override
   public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
      return super.mouseClicked(mouseButtonEvent, bl) | this.listButtons(null, mouseButtonEvent, bl, 0.0F, false);
   }

   public boolean listButtons(GuiGraphicsExtractor guiGraphics, MouseButtonEvent mouseButtonEvent, boolean bl, float tickDelta, boolean render) {
      boolean clicked = false;

      for (int i = 0; i < this.getItemCount(); i++) {
         int entryY = this.getRowTop(i);
         int p = this.getRowBottomCustom(i);
         if (p >= this.y0 && entryY <= this.y1) {
            int entryHeight = this.itemHeight - 4;
            BaseEntry entry = this.getEntry(i);
            Button editButton = this.buttons1
               .computeIfAbsent(
                  entry,
                  key -> new EditButton(
                        this.getRowRight() + 3,
                        entryY,
                        entryHeight,
                        entryHeight - 1,
                        Component.literal(""),
                        source -> {
                           Long identifier = (Long)entry.getUserData();
                           if (identifier != null) {
                              this.minecraft
                                 .setScreenAndShow(new AnimationEditScreen(this.minecraft.gui.screen(), (Animation)this.settings.get(identifier), identifier));
                           }
                        }
                     )
               );
            editButton.setX(this.getRowRight() + 3);
            editButton.setY(entryY);
            Button removeButton = this.buttons2
               .computeIfAbsent(
                  entry, key -> new RemoveButton(this.getRowRight() + 26, entryY, entryHeight, entryHeight - 1, Component.literal(""), source -> {
                        this.removeEntry(entry);
                        this.settings.remove((Long)entry.getUserData());
                        ConfigAnimations.save();
                     })
               );
            removeButton.setX(this.getRowRight() + 26);
            removeButton.setY(entryY);
            if (!render) {
               if (editButton.mouseClicked(mouseButtonEvent, bl)) {
                  return true;
               }

               if (removeButton.mouseClicked(mouseButtonEvent, bl)) {
                  return true;
               }
            } else {
               removeButton.extractRenderState(guiGraphics, (int)mouseButtonEvent.x(), (int)mouseButtonEvent.y(), tickDelta);
               editButton.extractRenderState(guiGraphics, (int)mouseButtonEvent.x(), (int)mouseButtonEvent.y(), tickDelta);
            }
         }
      }

      return clicked;
   }

   @Override
   protected void renderList(GuiGraphicsExtractor guiGraphics, int x, int scrollAmount, int mouseX, int mouseY, float tickDelta) {
      super.renderList(guiGraphics, x, scrollAmount, mouseX, mouseY, tickDelta);
      this.listButtons(guiGraphics, new MouseButtonEvent((double)mouseX, (double)mouseY, new MouseButtonInfo(0, 0)), false, tickDelta, true);
   }

   private int getRowBottomCustom(int i) {
      return this.getRowTop(i) + this.itemHeight;
   }

   @Override
   protected int getScrollbarPosition() {
      return this.width - 20;
   }

   @Override
   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   @Override
   public int getRowWidth() {
      return 220;
   }

   public void setSelected(BaseEntry entry) {
      super.setSelected(entry);
   }
}
