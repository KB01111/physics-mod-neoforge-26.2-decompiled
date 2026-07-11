package net.diebuddies.physics.settings.cloth;

import com.google.gson.JsonArray;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import net.diebuddies.compat.Sodium;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.physics.DynamicsWorld;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.GuiPhysicsCustomRenderState;
import net.diebuddies.physics.settings.gui.GuiRenderable;
import net.diebuddies.physics.settings.gui.legacy.LegacyAbstractSelectionList;
import net.diebuddies.physics.settings.mobs.BoundingBoxGetter;
import net.diebuddies.physics.settings.mobs.MobEntry;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ModelPartParent;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.joml.Math;
import org.joml.Matrix4d;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class ClothDisplayScreen extends Screen {
   private static final double ROTATE_SPEED = 6.0;
   private static final double START_ROTATE_SPEED = 25.0;
   private Screen parent;
   private PartSelectionList partList;
   public LegacyAbstractSelectionList<?> activeList;
   private String selectedEntity = "physicsmod:yourself";
   public EntityType<? extends Entity> entityType;
   private Identifier textureLocation;
   private Model model;
   private List<AbstractWidget> bottomWidgets;
   private Component customTitle;
   private Map<String, VerletSimulation> simulations;
   private long lastTime;
   private double timeDelta;
   private static final double FIXED_TIME_STEP = 0.016666666666666666;
   private double rotationSpeed = 25.0;
   private double totalRotation;
   private int entityXPosition;
   private Button rotateLeft;
   private Button rotateRight;
   private double startX;
   private double startY;
   private double startZ;
   private double endX;
   private double endY;
   private double endZ;
   private boolean reloadLater;
   @Nullable
   private Map<String, ConfigCloth.ClothList> playerCopy;

   protected ClothDisplayScreen(Screen parent) {
      super(Component.translatable("physicsmod.menu.cloth.partselection.title"));
      this.bottomWidgets = new ObjectArrayList();
      this.simulations = new Object2ObjectOpenHashMap();
      this.parent = parent;
      this.entityType = EntityTypes.PLAYER;
      Map<String, ConfigCloth.ClothList> toCopy = ConfigCloth.getCustomizationParts("physicsmod:yourself");
      if (toCopy == null) {
         toCopy = new Object2ObjectOpenHashMap();
      }

      this.playerCopy = new Object2ObjectOpenHashMap();

      for (Entry<String, ConfigCloth.ClothList> entry : toCopy.entrySet()) {
         this.playerCopy.put(entry.getKey(), entry.getValue().copy());
      }
   }

   protected void init() {
      super.init();
      this.reloadLater = !ConfigCloth.clothUpToDate;
      ConfigCloth.isChangingPlayer = true;
      PhysicsMod.loadCloth();
      PhysicsMod.resetClothSimulations();
      this.loadModelAndTexture();
      this.loadCloth();
      this.lastTime = System.nanoTime();
      PhysicsMod.createClothDirectory();
      this.entityXPosition = (int)((double)this.width * 0.25);
      this.addRenderableWidget(
         (Button)(
            (Animatable)ButtonSettings.builder(this.entityXPosition - 10, this.height - 57, 20, 20, Component.literal("?"), button -> this.rotationSpeed = 25.0)
         )
      );
      this.addRenderableWidget(
         this.rotateLeft = (Button)((Animatable)ButtonSettings.builder(this.entityXPosition - 35, this.height - 57, 20, 20, Component.literal("<"), button -> {
         }))
      );
      this.addRenderableWidget(
         this.rotateRight = (Button)(
            (Animatable)ButtonSettings.builder(this.entityXPosition + 15, this.height - 57, 20, 20, Component.literal(">"), button -> {
            })
         )
      );
      this.goToCategoryScreen();
   }

   private void goToCategoryScreen() {
      if (this.activeList != null) {
         this.children.remove(this.activeList);
      }

      if (this.reloadLater) {
         this.customTitle = Component.translatable("physicsmod.menu.cloth.downloading.title");
      } else {
         this.customTitle = Component.translatable("physicsmod.menu.cloth.categoryselection.title", new Object[]{getEntityName(this.selectedEntity)});
      }

      this.setBottomWidgets(
         ButtonSettings.builder(
            0,
            0,
            100,
            20,
            Component.translatable("physicsmod.menu.cloth.selection.changeEntity"),
            button -> this.minecraft.setScreenAndShow(new ClothEntitySelectionScreen(this, this.minecraft.options))
         ),
         ButtonSettings.builder(0, 0, 100, 20, CommonComponents.GUI_DONE, button -> this.onClose())
      );
      CategorySelectionList list = new CategorySelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(list);
      this.activeList = list;
   }

   private void goToPartsScreen() {
      if (this.activeList != null) {
         this.children.remove(this.activeList);
      }

      this.customTitle = Component.translatable("physicsmod.menu.cloth.partselection.title", new Object[]{getEntityName(this.selectedEntity)});
      this.setBottomWidgets(
         ButtonSettings.builder(
            0,
            0,
            100,
            20,
            Component.translatable("physicsmod.menu.cloth.selection.changeEntity"),
            button -> this.minecraft.setScreenAndShow(new ClothEntitySelectionScreen(this, this.minecraft.options))
         ),
         ButtonSettings.builder(0, 0, 100, 20, CommonComponents.GUI_DONE, button -> this.onClose())
      );
      this.partList = new PartSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.partList);
      this.activeList = this.partList;
   }

   public void goToClothScreen(String selectedCategory) {
      if (this.activeList != null) {
         this.children.remove(this.activeList);
      }

      this.customTitle = Component.translatable("physicsmod.menu.cloth.clothselection.title", new Object[]{getEntityName(this.selectedEntity)});
      this.setBottomWidgets(
         ButtonSettings.builder(
            0,
            this.height - 27,
            100,
            20,
            Component.translatable("physicsmod.menu.cloth.selection.openFolder"),
            button -> Util.getPlatform().openFile(PhysicsMod.CLOTH_DIRECTORY.toFile())
         ),
         ButtonSettings.builder(0, this.height - 27, 100, 20, Component.translatable("physicsmod.gui.select"), button -> this.goToCategoryScreen())
      );
      ClothSelectionList list = new ClothSelectionList(this, selectedCategory, this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(list);
      this.activeList = list;
   }

   public static String getEntityName(String selectedEntity) {
      if (selectedEntity.equals("physicsmod:yourself")) {
         return Language.getInstance().getOrDefault("physicsmod.menu.cloth.clothselection.yourself");
      } else {
         return selectedEntity.equals("minecraft:player")
            ? Language.getInstance().getOrDefault("physicsmod.menu.cloth.clothselection.allPlayers")
            : selectedEntity.replace("physicsmod:player:", "");
      }
   }

   public void setBottomWidgets(AbstractWidget... newWidgets) {
      for (AbstractWidget widget : this.bottomWidgets) {
         this.removeWidget(widget);
      }

      this.bottomWidgets.clear();
      int width = 100;
      int padding = 10;
      int y = this.height - 27;
      int centerX = this.width / 2;
      int offsetX = centerX - (width * newWidgets.length + (newWidgets.length - 1) * padding) / 2;

      for (AbstractWidget widget : newWidgets) {
         this.bottomWidgets.add(widget);
         widget.setY(y);
         widget.setX(offsetX);
         widget.setWidth(width);
         this.addRenderableWidget(widget);
         offsetX += width + padding;
      }
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.activeList.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      guiGraphics.centeredText(this.font, this.customTitle, this.width / 2, 15, -1);
      if (this.rotateLeft.isHoveredOrFocused()) {
         this.rotationSpeed = -6.0;
      } else if (this.rotateRight.isHoveredOrFocused()) {
         this.rotationSpeed = 6.0;
      }

      this.updateCloth();
      this.renderEntity(guiGraphics);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   public void tick() {
      if (this.reloadLater && ConfigCloth.clothUpToDate) {
         this.init();
      }

      super.tick();
   }

   private void updateCloth() {
      long time = System.nanoTime();
      double passedTime = (double)(System.nanoTime() - this.lastTime) / 1.0E9;
      this.lastTime = time;
      this.timeDelta += passedTime;

      while (this.timeDelta >= 0.016666666666666666) {
         this.timeDelta -= 0.016666666666666666;
         this.rotationSpeed = Math.lerp(this.rotationSpeed, 0.0, 0.06);
         this.rotateEntity();

         for (VerletSimulation simulation : this.simulations.values()) {
            this.rotateCloth(simulation);
            simulation.update(null, 0.016666666666666666);
         }
      }
   }

   private void rotateCloth(VerletSimulation simulation) {
      PoseStack stack = new PoseStack();
      stack.last().pose().rotate(new Quaternionf().rotationXYZ(0.0F, (float)(-java.lang.Math.toRadians(this.totalRotation)), 0.0F));
      simulation.getConstraint(ModelPartConstraint.class).modelPartTransformation(stack.last().pose());
      Matrix4d rotation = new Matrix4d(stack.last().pose());
      ColladaMesh mesh = simulation.cloth.mesh;
      int size = mesh.positions.size();
      List<Vector3f> positions = mesh.positions;
      List<VerletPoint> points = simulation.getPoints();
      Vector3d tmp = new Vector3d();

      for (int i = 0; i < points.size() && i < size; i++) {
         VerletPoint point = points.get(i);
         Vector3f pos = positions.get(i);
         tmp.set((double)pos.x, (double)pos.y, (double)pos.z);
         if (point.locked) {
            rotation.transformPosition(tmp);
            point.position.set(tmp);
         } else if (point.softRestriction != null) {
            rotation.transformPosition(tmp);
            point.softRestriction.set(tmp);
         }
      }
   }

   private void rotateEntity() {
      this.totalRotation = this.totalRotation + this.rotationSpeed;
   }

   private void renderEntity(GuiGraphicsExtractor guiGraphics) {
      GuiRenderable renderable = new GuiRenderable() {
         {
            Objects.requireNonNull(ClothDisplayScreen.this);
         }

         @Override
         public void extractRenderState(SubmitNodeCollector submitNodeCollector) {
            Minecraft.getInstance().gameRenderer.lighting().setupFor(com.mojang.blaze3d.platform.Lighting.Entry.ITEMS_3D);
            Matrix4fStack matrices = RenderSystem.getModelViewStack();
            float scale = (float)ClothDisplayScreen.this.height * 0.215F;
            double xPosition = (double)ClothDisplayScreen.this.entityXPosition;
            double yPosition = (double)ClothDisplayScreen.this.height * 0.5;
            float depth = -100.0F;
            matrices.pushMatrix();
            matrices.scale((float)Minecraft.getInstance().getWindow().getGuiScale());
            matrices.pushMatrix();
            matrices.translate((float)xPosition, (float)yPosition, depth);
            matrices.scale(scale, scale, scale);
            double mobWidth = ClothDisplayScreen.this.endX - ClothDisplayScreen.this.startX;
            double mobHeight = ClothDisplayScreen.this.endY - ClothDisplayScreen.this.startY;
            double mobDepth = ClothDisplayScreen.this.endZ - ClothDisplayScreen.this.startZ;
            float mobScale = -1.0F / (float)java.lang.Math.max(mobWidth, java.lang.Math.max(mobHeight, mobDepth)) * 2.0F;
            matrices.scale(mobScale, mobScale, mobScale);
            matrices.translate(
               (float)(-mobWidth * 0.5 - ClothDisplayScreen.this.startX),
               (float)(-mobHeight * 0.5 + ClothDisplayScreen.this.endY),
               (float)(-mobDepth * 0.5 - ClothDisplayScreen.this.startZ)
            );
            matrices.rotate(
               new Quaternionf()
                  .rotationXYZ(
                     (float)java.lang.Math.toRadians(-25.0),
                     (float)java.lang.Math.toRadians(ClothDisplayScreen.this.totalRotation),
                     (float)java.lang.Math.toRadians(180.0)
                  )
            );
            RenderType renderType = RenderTypes.entityCutout(ClothDisplayScreen.this.textureLocation);
            PoseStack currentPose = new PoseStack();
            currentPose.mulPose(matrices);
            submitNodeCollector.submitCustomGeometry(currentPose, renderType, (pose, buffer) -> {
               try {
                  if (ClothDisplayScreen.this.partList != null && ClothDisplayScreen.this.partList.getHovered() != null) {
                     String selected = (String)ClothDisplayScreen.this.partList.getHovered().getUserData();
                     Iterator i$ = ClothConstants.getModelParts(ClothDisplayScreen.this.model).iterator();

                     while (i$.hasNext()) {
                        ModelPart part = (ModelPart)i$.next();
                        if (selected.equals(((ModelPartParent)part).physicsmod$getName())) {
                           part.visible = true;
                        } else {
                           part.visible = false;
                        }
                     }
                  }

                  ClothConstants.hideProperParts(ClothDisplayScreen.this.selectedEntity, ClothDisplayScreen.this.model);
                  PoseStack stack = new PoseStack();
                  stack.mulPose(pose.pose());
                  ClothDisplayScreen.this.model.renderToBuffer(stack, buffer, 0, OverlayTexture.NO_OVERLAY, -1);
                  Iterator i$ = ClothConstants.getModelParts(ClothDisplayScreen.this.model).iterator();

                  while (i$.hasNext()) {
                     ModelPart part = (ModelPart)i$.next();
                     part.visible = true;
                  }
               } catch (Exception var6x) {
               }
            });
            submitNodeCollector.submitCustomGeometry(currentPose, renderType, (pose, buffer) -> {
               try {
                  if (ClothDisplayScreen.this.partList != null && ClothDisplayScreen.this.partList.getHovered() != null) {
                     String selected = (String)ClothDisplayScreen.this.partList.getHovered().getUserData();
                     Iterator i$ = ClothConstants.getModelParts(ClothDisplayScreen.this.model).iterator();

                     while (i$.hasNext()) {
                        ModelPart part = (ModelPart)i$.next();
                        if (selected.equals(((ModelPartParent)part).physicsmod$getName())) {
                           part.visible = true;
                        } else {
                           part.visible = false;
                        }
                     }
                  }

                  Iterator i$ = ClothConstants.getModelParts(ClothDisplayScreen.this.model).iterator();

                  while (i$.hasNext()) {
                     ModelPart part = (ModelPart)i$.next();
                     part.visible = !part.visible;
                  }

                  ClothConstants.hideProperParts(ClothDisplayScreen.this.selectedEntity, ClothDisplayScreen.this.model);
                  PoseStack stack = new PoseStack();
                  stack.mulPose(pose.pose());
                  ClothDisplayScreen.this.model.renderToBuffer(stack, buffer, 0, OverlayTexture.NO_OVERLAY, ARGB.colorFromFloat(0.175F, 1.0F, 1.0F, 1.0F));
                  Iterator i$x = ClothConstants.getModelParts(ClothDisplayScreen.this.model).iterator();

                  while (i$x.hasNext()) {
                     ModelPart part = (ModelPart)i$x.next();
                     part.visible = true;
                  }
               } catch (Exception var6x) {
               }
            });
            ClothDisplayScreen.this.renderStaticCloth(matrices, submitNodeCollector);
            matrices.popMatrix();
            matrices.pushMatrix();
            matrices.translate((float)xPosition, (float)yPosition, depth);
            matrices.scale(scale, scale, scale);
            matrices.scale(mobScale, mobScale, mobScale);
            matrices.translate(
               (float)(-mobWidth * 0.5 - ClothDisplayScreen.this.startX),
               (float)(-mobHeight * 0.5 + ClothDisplayScreen.this.endY),
               (float)(-mobDepth * 0.5 - ClothDisplayScreen.this.startZ)
            );
            matrices.rotate(new Quaternionf().rotationXYZ((float)java.lang.Math.toRadians(-25.0), 0.0F, (float)java.lang.Math.toRadians(180.0)));
            ClothDisplayScreen.this.renderCloth(matrices, submitNodeCollector);
            matrices.popMatrix();
            matrices.popMatrix();
         }
      };
      guiGraphics.guiRenderState
         .addPicturesInPictureState(
            new GuiPhysicsCustomRenderState(
               renderable, 0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), 1.0F, null
            )
         );
   }

   private void renderCloth(Matrix4fStack matrices, SubmitNodeCollector submitNodeStorage) {
      for (VerletSimulation simulation : this.simulations.values()) {
         simulation.brightness = 15728880;
         if (simulation.cloth.rules.isDynamic()) {
            simulation.render(matrices, submitNodeStorage);
         }
      }
   }

   private void renderStaticCloth(Matrix4fStack matrices, SubmitNodeCollector submitNodeStorage) {
      for (VerletSimulation simulation : this.simulations.values()) {
         int lightCoords = 15728880;
         matrices.pushMatrix();
         simulation.getConstraint(ModelPartConstraint.class).modelPartTransformation(matrices);
         if (!simulation.cloth.rules.isDynamic()) {
            Identifier identifier = simulation.textureID;
            simulation.cloth.mesh.renderSlow(submitNodeStorage, matrices, identifier, lightCoords, ConfigClient.clothSmoothShading);
         }

         if (simulation.cloth.playerMesh != null && this.textureLocation != null) {
            simulation.cloth.playerMesh.renderSlow(submitNodeStorage, matrices, this.textureLocation, lightCoords, false);
         }

         matrices.popMatrix();
      }
   }

   public void loadModelAndTexture() {
      EntityRenderer renderer = PhysicsMod.renderers.get(this.entityType);
      this.textureLocation = MobEntry.getTextureLocation(renderer, this.entityType);
      this.model = MobEntry.getModel(renderer, this.entityType);
      if (this.entityType == EntityTypes.PLAYER) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null) {
            renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            this.textureLocation = MobEntry.getTextureLocation(renderer, this.entityType, player);
            this.model = MobEntry.getModel(renderer, this.entityType, player);
         }
      }

      PhysicsMod.sodiumCatchBoundingBox = true;
      PhysicsMod.sodiumBoundingBox.start.set(Double.MAX_VALUE);
      PhysicsMod.sodiumBoundingBox.end.set(-Double.MAX_VALUE);

      try {
         BoundingBoxGetter boundingBox = StarterClient.sodium ? Sodium.getNewBoundingBoxConsumer() : new BoundingBoxGetter();
         this.model.renderToBuffer(new PoseStack(), boundingBox, 0, OverlayTexture.NO_OVERLAY, -1);
         if (StarterClient.sodium) {
            boundingBox.min = PhysicsMod.sodiumBoundingBox.getMin();
            boundingBox.max = PhysicsMod.sodiumBoundingBox.getMax();
         }

         this.startX = boundingBox.min.x;
         this.endX = boundingBox.max.x;
         this.startY = boundingBox.min.y;
         this.endY = boundingBox.max.y;
         this.startZ = boundingBox.min.z;
         this.endZ = boundingBox.max.z;
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      PhysicsMod.sodiumCatchBoundingBox = false;
   }

   public void loadCloth() {
      Map<String, ConfigCloth.ClothList> customizations = ConfigCloth.getCustomizationParts(this.selectedEntity);
      this.simulations = new Object2ObjectOpenHashMap();
      if (customizations != null) {
         for (Entry<String, ConfigCloth.ClothList> clothParts : customizations.entrySet()) {
            String modelPart = clothParts.getKey();
            ConfigCloth.ClothList clothList = clothParts.getValue();

            for (String clothPart : clothList.getClothPieces()) {
               Cloth cloth = PhysicsMod.cloth.get(clothPart);
               if (cloth != null) {
                  VerletSimulation simulation = new VerletSimulation(new Vector3d(DynamicsWorld.DEFAULT_GRAVITY).negate(), 45, 0.92, new Vector3d(0.0));
                  ModelPartConstraint modelPartConstraint = new ModelPartConstraint(simulation, cloth.rules.getIgnoreParts(), null, modelPart, this.model);
                  modelPartConstraint.setCustomTransformation(
                     matrix -> matrix.mulPose(new Quaternionf().rotationXYZ(0.0F, (float)(-java.lang.Math.toRadians(this.totalRotation)), 0.0F))
                  );
                  simulation.addConstraint(modelPartConstraint);
                  PoseStack modelMatrix = new PoseStack();
                  modelMatrix.last().pose().rotate(new Quaternionf().rotationXYZ(0.0F, (float)(-java.lang.Math.toRadians(this.totalRotation)), 0.0F));
                  modelPartConstraint.modelPartTransformation(modelMatrix.last().pose());
                  Matrix4d partTransformation = new Matrix4d(modelMatrix.last().pose());
                  Identifier texture = cloth.getTexture(null);
                  if (texture == null) {
                     texture = this.textureLocation;
                  }

                  simulation.addCloth(cloth, texture, partTransformation, false);
                  simulation.setTransformation(partTransformation);
                  simulation.setBufferTransformation(partTransformation);
                  simulation.updateOffsets();
                  simulation.calculateNormals();
                  modelPartConstraint.initAsyncData(null, simulation);
                  modelPartConstraint.changeInstantly = true;
                  modelPartConstraint.updateAfter(0.0, simulation);
                  simulation.downloadData();
                  this.simulations.put(modelPart + clothPart, simulation);
               }
            }
         }
      }
   }

   public String getSelectedEntity() {
      return this.selectedEntity;
   }

   public void setSelectedEntity(String selectedEntity) {
      this.selectedEntity = selectedEntity;
   }

   public void onClose() {
      this.minecraft.setScreenAndShow(this.parent);
      ConfigCloth.isChangingPlayer = false;
      Map<String, ConfigCloth.ClothList> toCheck = ConfigCloth.getCustomizationParts("physicsmod:yourself");
      if (toCheck == null) {
         toCheck = new Object2ObjectOpenHashMap();
      }

      if (!toCheck.equals(this.playerCopy)) {
         UUID uuid = ConfigCloth.getMinecraftUUID();
         if (uuid != null) {
            String uuidString = uuid.toString();
            ConfigCloth.setCustomizationParts(uuidString, toCheck);
            JsonArray array = new JsonArray();

            for (Entry<String, ConfigCloth.ClothList> entry : toCheck.entrySet()) {
               for (String part : entry.getValue().getClothPieces()) {
                  array.add(part);
               }
            }
         } else {
            StarterClient.logger.error("Couldn't find player uuid");
         }
      }

      ConfigCloth.save();
      PhysicsMod.resetClothSimulations();
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
