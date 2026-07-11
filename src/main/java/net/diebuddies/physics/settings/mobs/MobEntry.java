package net.diebuddies.physics.settings.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.render.PhysicsFeatureRenderDispatcher;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.GuiPhysicsEntityRenderState;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.model.object.cart.MinecartModel;
import net.minecraft.client.model.object.leash.LeashKnotModel;
import net.minecraft.client.model.object.projectile.ShulkerBulletModel;
import net.minecraft.client.model.object.projectile.TridentModel;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.AxolotlRenderer;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.LeashKnotRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.LlamaRenderer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.MushroomCowRenderer;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.StriderRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.WitherBossRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.client.renderer.entity.state.CowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.client.renderer.feature.CustomFeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer.Group;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.axolotl.Axolotl.Variant;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.cow.CowVariant.ModelType;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class MobEntry extends LabelEntry {
   private static final Identifier DEFAULT_TEXTURE = Identifier.parse("physicsmod:textures/gui/white.png");
   private static final Map<EntityRenderer, Model> models = new Object2ObjectOpenHashMap();
   private String text;
   private EntityType<?> entityType;

   public MobEntry(LegacyObjectSelectionList objectSelectionList, String text) {
      super(objectSelectionList, text);
      this.text = text;
      this.entityType = byString(text).get();
   }

   public void setText(String text) {
      this.text = text;
   }

   @Override
   public void extractRenderState(
      GuiGraphicsExtractor guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      Font font = Minecraft.getInstance().font;
      String text = this.text;
      if (font.width(Component.literal(text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55) {
         String newText = font.plainSubstrByWidth(text, this.objectSelectionList.getRowWidth() - 58);
         if (!text.equalsIgnoreCase(newText)) {
            text = newText + "...";
         }
      }

      MutableComponent label = Component.literal(text);
      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -1);
      } else {
         guiGraphics.centeredText(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, -4013374);
      }

      Matrix3x2fStack matrices = guiGraphics.pose();
      float scale = (float)entryHeight / 2.0F * 0.9F;
      matrices.pushMatrix();
      PhysicsMod.sodiumCatchBoundingBox = true;
      PhysicsMod.sodiumBoundingBox.start.set(Double.MAX_VALUE);
      PhysicsMod.sodiumBoundingBox.end.set(-Double.MAX_VALUE);
      SubmitNodeStorage submits = new SubmitNodeStorage();
      FeatureRendererMap renderers = new FeatureRendererMap();
      renderers.put(ModelFeatureRenderer.TYPE, new ModelFeatureRenderer());
      renderers.put(CustomFeatureRenderer.TYPE, new CustomFeatureRenderer());
      PhysicsFeatureRenderDispatcher renderDispatcher = new PhysicsFeatureRenderDispatcher(renderers) {
         {
            Objects.requireNonNull(MobEntry.this);
         }

         @Override
         public Group createGroup() {
            return new PhysicsFeatureRenderDispatcher.BoundingBoxGroup();
         }
      };

      try {
         EntityRenderer renderer;
         EntityRenderState emptyState;
         if (this.entityType == EntityTypes.PLAYER) {
            emptyState = new AvatarRenderState();
            renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(emptyState);
         } else {
            renderer = PhysicsMod.renderers.get(this.entityType);
            emptyState = renderer.createRenderState();
         }

         emptyState.entityType = this.entityType;
         emptyState.lightCoords = 15728880;
         emptyState.shadowPieces.clear();
         emptyState.outlineColor = 0;
         this.fillRenderStates(emptyState);
         Vector3d min = new Vector3d(Double.MAX_VALUE);
         Vector3d max = new Vector3d(-Double.MAX_VALUE);
         Quaternionf rot = new Quaternionf().rotationXYZ((float)Math.toRadians(-25.0), (float)Math.toRadians(-130.0), 0.0F);
         PoseStack entityTransform = new PoseStack();
         entityTransform.last().rotate(rot);
         renderer.submit(emptyState, entityTransform, submits, new CameraRenderState());
         renderDispatcher.render(submits);
         if (StarterClient.sodium) {
            min = PhysicsMod.sodiumBoundingBox.getMin();
            max = PhysicsMod.sodiumBoundingBox.getMax();
         } else {
            for (Group group : renderDispatcher.getGroups()) {
               PhysicsFeatureRenderDispatcher.BoundingBoxGroup bbGroup = (PhysicsFeatureRenderDispatcher.BoundingBoxGroup)group;
               min.min(bbGroup.boundingBox.min);
               max.max(bbGroup.boundingBox.max);
            }
         }

         double startX = min.x;
         double endX = max.x;
         double startY = min.y;
         double endY = max.y;
         double startZ = min.z;
         double endZ = max.z;
         Vector3d center = max.add(min, new Vector3d()).mul(0.5);
         double mobWidth = endX - startX;
         double mobHeight = endY - startY;
         double mobDepth = endZ - startZ;
         float mobScale = 1.0F / (float)Math.max(mobWidth, Math.max(mobHeight, mobDepth)) * 2.0F;
         Matrix4f transform = new Matrix4f();
         transform.scale(-mobScale * scale, -mobScale * scale, -mobScale * scale);
         transform.translate((float)(-center.x), (float)(-center.y), (float)(-center.z));
         transform.rotate(rot);
         guiGraphics.guiRenderState
            .addPicturesInPictureState(
               new GuiPhysicsEntityRenderState(
                  emptyState, new Matrix3x2f(matrices), transform, entryHeight, x, y, x + entryHeight, y + entryHeight, 1.0F, guiGraphics.scissorStack.peek()
               )
            );
      } catch (Exception var55) {
      } finally {
         try {
            renderDispatcher.close();
         } catch (Exception var54) {
         }
      }

      matrices.popMatrix();
      PhysicsMod.sodiumCatchBoundingBox = false;
   }

   private void fillRenderStates(EntityRenderState emptyState) {
      if (emptyState instanceof CowRenderState cow) {
         cow.variant = new CowVariant(
            new ModelAndTexture(ModelType.NORMAL, Identifier.withDefaultNamespace("entity/cow/cow_temperate")),
            new ResourceTexture(Identifier.withDefaultNamespace("entity/cow/cow_temperate_baby")),
            SpawnPrioritySelectors.EMPTY
         );
      } else if (emptyState instanceof PigRenderState pig) {
         pig.variant = new PigVariant(
            new ModelAndTexture(net.minecraft.world.entity.animal.pig.PigVariant.ModelType.NORMAL, Identifier.withDefaultNamespace("entity/pig/pig_temperate")),
            new ResourceTexture(Identifier.withDefaultNamespace("entity/pig/pig_temperate_baby")),
            SpawnPrioritySelectors.EMPTY
         );
      } else if (emptyState instanceof ChickenRenderState chicken) {
         chicken.variant = new ChickenVariant(
            new ModelAndTexture(
               net.minecraft.world.entity.animal.chicken.ChickenVariant.ModelType.NORMAL, Identifier.withDefaultNamespace("entity/chicken/chicken_temperate")
            ),
            new ResourceTexture(Identifier.withDefaultNamespace("entity/chicken/chicken_temperate_baby")),
            SpawnPrioritySelectors.EMPTY
         );
      } else if (emptyState instanceof AvatarRenderState var5) {
         ;
      }
   }

   public static Model getModel(EntityRenderer renderer, EntityType<?> entityType, Entity entity) {
      if (entityType == EntityTypes.PLAYER) {
         if (entity != null && entity instanceof AbstractClientPlayer player) {
            return player.getSkin().model().name().equalsIgnoreCase("slim")
               ? models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true))
               : models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false));
         } else {
            return models.computeIfAbsent(renderer, key -> new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER), false));
         }
      } else if (renderer instanceof LivingEntityRenderer livingRenderer) {
         return livingRenderer.getModel();
      } else if (renderer instanceof EnderDragonRenderer) {
         return models.computeIfAbsent(renderer, key -> new EnderDragonModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ENDER_DRAGON)));
      } else if (renderer instanceof BoatRenderer) {
         return models.computeIfAbsent(renderer, key -> new BoatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.OAK_BOAT)));
      } else if (renderer instanceof MinecartRenderer) {
         if (entityType == EntityTypes.TNT_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TNT_MINECART)));
         } else if (entityType == EntityTypes.CHEST_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.CHEST_MINECART)));
         } else if (entityType == EntityTypes.COMMAND_BLOCK_MINECART) {
            return models.computeIfAbsent(
               renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.COMMAND_BLOCK_MINECART))
            );
         } else if (entityType == EntityTypes.FURNACE_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.FURNACE_MINECART)));
         } else if (entityType == EntityTypes.HOPPER_MINECART) {
            return models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.HOPPER_MINECART)));
         } else {
            return entityType == EntityTypes.SPAWNER_MINECART
               ? models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SPAWNER_MINECART)))
               : models.computeIfAbsent(renderer, key -> new MinecartModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.MINECART)));
         }
      } else if (renderer instanceof ShulkerBulletRenderer) {
         return models.computeIfAbsent(renderer, key -> new ShulkerBulletModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.SHULKER_BULLET)));
      } else if (renderer instanceof ThrownTridentRenderer) {
         return models.computeIfAbsent(renderer, key -> new TridentModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.TRIDENT)));
      } else if (renderer instanceof WitherSkullRenderer) {
         return models.computeIfAbsent(renderer, key -> new SkullModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.WITHER_SKULL)));
      } else if (renderer instanceof LeashKnotRenderer) {
         return models.computeIfAbsent(renderer, key -> new LeashKnotModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.LEASH_KNOT)));
      } else {
         return renderer instanceof EndCrystalRenderer ? null : null;
      }
   }

   public static Model getModel(EntityRenderer renderer, EntityType<?> entityType) {
      return getModel(renderer, entityType, null);
   }

   public static Identifier getTextureLocation(EntityRenderer renderer, EntityType entityType, Entity entity) {
      if (entityType == EntityTypes.PLAYER) {
         return entity != null && entity instanceof AbstractClientPlayer player
            ? player.getSkin().body().texturePath()
            : Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");
      } else if (entityType == EntityTypes.PIGLIN) {
         return Identifier.withDefaultNamespace("textures/entity/piglin/piglin.png");
      } else if (entityType == EntityTypes.ZOMBIFIED_PIGLIN) {
         return Identifier.withDefaultNamespace("textures/entity/piglin/zombified_piglin.png");
      } else if (entityType == EntityTypes.PIGLIN_BRUTE) {
         return Identifier.withDefaultNamespace("textures/entity/piglin/piglin_brute.png");
      } else if (renderer instanceof RabbitRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/rabbit/brown.png");
      } else if (renderer instanceof AxolotlRenderer) {
         return Identifier.withDefaultNamespace(String.format("textures/entity/axolotl/axolotl_%s.png", Variant.BLUE.getName()));
      } else if (renderer instanceof BeeRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/bee/bee.png");
      } else if (renderer instanceof BoatRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/boat/oak.png");
      } else if (renderer instanceof CatRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/cat/tabby.png");
      } else if (renderer instanceof HorseRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/horse/horse_white.png");
      } else if (renderer instanceof UndeadHorseRenderer) {
         if (entityType == EntityTypes.ZOMBIE_HORSE) {
            return Identifier.withDefaultNamespace("textures/entity/horse/horse_zombie.png");
         } else {
            return entityType == EntityTypes.SKELETON_HORSE
               ? Identifier.withDefaultNamespace("textures/entity/horse/horse_skeleton.png")
               : Identifier.withDefaultNamespace("textures/entity/horse/horse_skeleton.png");
         }
      } else if (renderer instanceof LlamaRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/llama/creamy.png");
      } else if (entityType == EntityTypes.DONKEY) {
         return Identifier.withDefaultNamespace("textures/entity/horse/donkey.png");
      } else if (entityType == EntityTypes.MULE) {
         return Identifier.withDefaultNamespace("textures/entity/horse/mule.png");
      } else if (renderer instanceof FoxRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/fox/fox.png");
      } else if (renderer instanceof GhastRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/ghast/ghast.png");
      } else if (renderer instanceof MushroomCowRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/cow/red_mooshroom.png");
      } else if (renderer instanceof PandaRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/panda/panda.png");
      } else if (renderer instanceof ParrotRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/parrot/parrot_red_blue.png");
      } else if (renderer instanceof ShulkerRenderer) {
         return Identifier.withDefaultNamespace("textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");
      } else if (renderer instanceof StriderRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/strider/strider.png");
      } else if (renderer instanceof TropicalFishRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/fish/tropical_a.png");
      } else if (renderer instanceof VexRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/illager/vex.png");
      } else if (renderer instanceof WitherBossRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/wither/wither.png");
      } else if (renderer instanceof WolfRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/wolf/wolf.png");
      } else if (renderer instanceof ItemFrameRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/wolf/wolf.png");
      } else if (renderer instanceof WitherSkullRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/wither/wither_invulnerable.png");
      } else if (renderer instanceof FrogRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/frog/cold_frog.png");
      } else if (renderer instanceof ChickenRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/chicken/temperate_chicken.png");
      } else if (renderer instanceof CowRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/cow/temperate_cow.png");
      } else if (renderer instanceof PigRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/pig/temperate_pig.png");
      } else if (renderer instanceof ZombieRenderer) {
         return Identifier.withDefaultNamespace("textures/entity/zombie/zombie.png");
      } else {
         Identifier texture = null;

         try {
            LivingEntityRenderState renderState = (LivingEntityRenderState)renderer.createRenderState(entity, 0.0F);
            texture = ((LivingEntityRenderer)renderer).getTextureLocation(renderState);
         } catch (Exception var5) {
         }

         if (texture == null) {
            texture = getBackupTextureLocation(renderer, entityType);
         }

         return texture == null ? DEFAULT_TEXTURE : texture;
      }
   }

   public static Identifier getTextureLocation(EntityRenderer renderer, EntityType entityType) {
      return getTextureLocation(renderer, entityType, null);
   }

   private static Identifier getBackupTextureLocation(EntityRenderer renderer, EntityType entityType) {
      Field[] fields = renderer.getClass().getDeclaredFields();

      for (Field field : fields) {
         if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(Identifier.class)) {
            try {
               field.setAccessible(true);
               Identifier resource = (Identifier)field.get(null);
               if (resource != null) {
                  String file = resource.getPath();
                  if (file.endsWith(".png") || file.endsWith(".jpg") || file.endsWith(".tga") || file.endsWith(".jpeg")) {
                     return resource;
                  }
               }
            } catch (IllegalAccessException | IllegalArgumentException var9) {
               var9.printStackTrace();
            }
         }
      }

      return null;
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.text);
   }

   public static Optional<EntityType<?>> byString(String id) {
      return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(id));
   }
}
