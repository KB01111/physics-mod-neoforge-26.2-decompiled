package net.diebuddies.physics.settings;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsSubScreen;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.joml.Vector3f;

public class GravityCustomizeSettingsScreen extends LegacyOptionsSubScreen {
   private static String searchText = "";
   private LegacyOptionsList list;

   public GravityCustomizeSettingsScreen(Screen parent, Options options) {
      super(parent, options, Component.translatable("physicsmod.menu.gravity.customize.title"));
   }

   protected void init() {
      int var10003 = this.width / 2 - 160;
      EditBox search = new EditBox(Minecraft.getInstance().font, var10003, this.height - 27, 100, 20, Component.literal(""));
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
                  Language.getInstance().getOrDefault("physicsmod.menu.gravity.customize.reset"),
                  this,
                  widget -> this.addRenderableWidget(widget),
                  widget -> this.removeWidget(widget),
                  response -> {
                     if (response == PopupWidget.PopupResponse.YES) {
                        ConfigClient.resetGravities();
                        this.list.children().clear();
                        this.minecraft.setScreenAndShow(new GravityCustomizeSettingsScreen(this.lastScreen, this.options));
                     }
                  }
               )
         )
      );
   }

   private void checkSearchText(String searchText, EditBox search) {
      GravityCustomizeSettingsScreen.searchText = searchText;
      if (searchText.isEmpty()) {
         search.setSuggestion(Language.getInstance().getOrDefault("physicsmod.gui.search"));
      } else {
         search.setSuggestion("");
      }

      this.updateGravities(searchText);
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   public void updateGravities(String matcher) {
      if (this.list != null) {
         this.children.remove(this.list);
      }

      this.list = new LegacyOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.list);
      List<String> ids = new ObjectArrayList();

      for (String id : ConfigClient.customizedGravities.keySet()) {
         ids.add(id);
      }

      Collections.sort(ids);

      for (String id : ids) {
         if (id.toLowerCase().contains(matcher.toLowerCase())) {
            ProgressOption gravityX = new ProgressOption(
               "physicsmod.menu.gravity.gravityx", -50.0, 50.0, 0.1F, gameOptions -> (double)ConfigClient.getGravity(id).x, (gameOptions, value) -> {
                  Vector3f val = ConfigClient.getGravity(id);
                  val.x = value.floatValue();
                  ConfigClient.setGravity(id, val);
                  ConfigClient.save();
               }, (gameOptions, option) -> option.customFormat("physicsmod.menu.gravity.gravityx", String.format("%.2f", option.get(gameOptions)))
            );
            ProgressOption gravityY = new ProgressOption(
               "physicsmod.menu.gravity.gravityy", -50.0, 50.0, 0.1F, gameOptions -> (double)ConfigClient.getGravity(id).y, (gameOptions, value) -> {
                  Vector3f val = ConfigClient.getGravity(id);
                  val.y = value.floatValue();
                  ConfigClient.setGravity(id, val);
                  ConfigClient.save();
               }, (gameOptions, option) -> option.customFormat("physicsmod.menu.gravity.gravityy", String.format("%.2f", option.get(gameOptions)))
            );
            ProgressOption gravityZ = new ProgressOption(
               "physicsmod.menu.gravity.gravityz", -50.0, 50.0, 0.1F, gameOptions -> (double)ConfigClient.getGravity(id).z, (gameOptions, value) -> {
                  Vector3f val = ConfigClient.getGravity(id);
                  val.z = value.floatValue();
                  ConfigClient.setGravity(id, val);
                  ConfigClient.save();
               }, (gameOptions, option) -> option.customFormat("physicsmod.menu.gravity.gravityz", String.format("%.2f", option.get(gameOptions)))
            );
            ProgressOption buoyancyX = new ProgressOption(
               "physicsmod.menu.gravity.buoyancyx", -50.0, 50.0, 0.1F, gameOptions -> (double)ConfigClient.getBuoyancy(id).x, (gameOptions, value) -> {
                  Vector3f val = ConfigClient.getBuoyancy(id);
                  val.x = value.floatValue();
                  ConfigClient.setBuoyancy(id, val);
                  ConfigClient.save();
               }, (gameOptions, option) -> option.customFormat("physicsmod.menu.gravity.buoyancyx", String.format("%.2f", option.get(gameOptions)))
            );
            ProgressOption buoyancyY = new ProgressOption(
               "physicsmod.menu.gravity.buoyancyy", -50.0, 50.0, 0.1F, gameOptions -> (double)ConfigClient.getBuoyancy(id).y, (gameOptions, value) -> {
                  Vector3f val = ConfigClient.getBuoyancy(id);
                  val.y = value.floatValue();
                  ConfigClient.setBuoyancy(id, val);
                  ConfigClient.save();
               }, (gameOptions, option) -> option.customFormat("physicsmod.menu.gravity.buoyancyy", String.format("%.2f", option.get(gameOptions)))
            );
            ProgressOption buoyancyZ = new ProgressOption(
               "physicsmod.menu.gravity.buoyancyz", -50.0, 50.0, 0.1F, gameOptions -> (double)ConfigClient.getBuoyancy(id).z, (gameOptions, value) -> {
                  Vector3f val = ConfigClient.getBuoyancy(id);
                  val.z = value.floatValue();
                  ConfigClient.setBuoyancy(id, val);
                  ConfigClient.save();
               }, (gameOptions, option) -> option.customFormat("physicsmod.menu.gravity.buoyancyz", String.format("%.2f", option.get(gameOptions)))
            );
            this.list.addBig(new LabelOption(id));
            this.list.addSmall(gravityX, buoyancyX);
            this.list.addSmall(gravityY, buoyancyY);
            this.list.addSmall(gravityZ, buoyancyZ);
         }
      }
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
