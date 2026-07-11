package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.PlayerRenderStateExtended;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.ClothRenderState;
import net.diebuddies.physics.verlet.SimulationPlayerExtension;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.physics.verlet.constraints.OceanPhysicsDisplacementConstraint;
import net.diebuddies.physics.verlet.constraints.WorldConstraint;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AvatarRenderer.class})
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<Avatar, AvatarRenderState, PlayerModel> {
   public MixinPlayerRenderer(Context context, PlayerModel entityModel, float f) {
      super(context, entityModel, f);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"extractRenderState"},
      cancellable = true
   )
   private void physicsmod$extendRenderState(Avatar entity, AvatarRenderState entityRenderState, float tickDelta, CallbackInfo info) {
      this.physicsmod$extraxtClothRenderState(entity, entityRenderState, tickDelta);
   }

   @Unique
   private void physicsmod$extraxtClothRenderState(Avatar entity, AvatarRenderState entityRenderState, float tickDelta) {
      ClothRenderState state = new ClothRenderState();
      ((PlayerRenderStateExtended)entityRenderState).physicsmod$setClothRenderState(state);
      boolean canVersionRenderCloth = Minecraft.getInstance().player != entity;
      this.physicsmod$removeOldSimulations(entity);
      if (!PhysicsMod.clothSkipRenderQueue && canVersionRenderCloth && !entityRenderState.isInvisible) {
         if (ConfigClient.capePhysics) {
            if (!ConfigClient.clothForceArmor) {
               state.skipChestEquipment = this.physicsmod$checkSkipArmourRender(entity, entityRenderState.chestEquipment, EquipmentSlot.CHEST);
               state.skipLegsEquipment = this.physicsmod$checkSkipArmourRender(entity, entityRenderState.legsEquipment, EquipmentSlot.LEGS);
               state.skipFeetEquipment = this.physicsmod$checkSkipArmourRender(entity, entityRenderState.feetEquipment, EquipmentSlot.FEET);
               state.skipHeadEquipment = this.physicsmod$checkSkipArmourRender(entity, entityRenderState.headEquipment, EquipmentSlot.HEAD);
            }

            state.hiddenModelParts = ClothConstants.getHiddenParts(entity);
            ItemStack itemStack = entityRenderState.chestEquipment;
            if (itemStack != null && itemStack.is(Items.ELYTRA)) {
               boolean renderElytra = entity != null && !entity.isInvisible() && !ConfigClient.clothForceArmor;
               if (renderElytra && ConfigCloth.hasCategory(entity, "Elytra")) {
                  this.physicsmod$renderPhysicsElytra(entity, entityRenderState, tickDelta);
                  state.skipElytra = true;
               }
            } else if (entityRenderState.showCape) {
               PlayerSkin playerSkin = entityRenderState.skin;
               if (this.physicsmod$hasPhysicsCape(entity)) {
                  state.skipCape = true;
               } else if (playerSkin.cape() != null) {
               }
            }

            this.physicsmod$renderPhysicsCloth(entity, entityRenderState, tickDelta);
         }
      }
   }

   @Unique
   private boolean physicsmod$hasPhysicsCape(Avatar player) {
      return ConfigCloth.hasCategory(player, "Back");
   }

   @Unique
   private void physicsmod$removeOldSimulations(Avatar player) {
      SimulationPlayerExtension extension = (SimulationPlayerExtension)player;
      VerletSimulation elytra = extension.physicsmod$getElytraSimulation();
      VerletSimulation cape = extension.physicsmod$getCapeSimulation();
      Map<String, VerletSimulation> other = extension.physicsmod$getOtherSimulations();
      if (elytra != null && elytra.destroyed) {
         extension.physicsmod$setElytraSimulation(null);
      }

      if (cape != null && cape.destroyed) {
         extension.physicsmod$setCapeSimulation(null);
      }

      Iterator<Entry<String, VerletSimulation>> it = other.entrySet().iterator();

      while (it.hasNext()) {
         Entry<String, VerletSimulation> entry = it.next();
         VerletSimulation simulation = entry.getValue();
         if (simulation.destroyed) {
            it.remove();
         }
      }
   }

   @Unique
   private boolean physicsmod$checkSkipArmourRender(Avatar entity, ItemStack itemStack, EquipmentSlot equipmentSlot) {
      Equippable equippable = (Equippable)itemStack.get(DataComponents.EQUIPPABLE);
      if (equippable != null && equippable.slot() == equipmentSlot) {
         Map<String, ConfigCloth.ClothList> customizationParts = ConfigCloth.getCustomizationParts(entity);
         if (customizationParts != null && !entity.isInvisible()) {
            for (Entry<String, ConfigCloth.ClothList> customizationPart : customizationParts.entrySet()) {
               ConfigCloth.ClothList clothList = customizationPart.getValue();

               for (String clothPiece : clothList.getClothPieces()) {
                  Cloth cloth = PhysicsMod.cloth.get(clothPiece);
                  if (cloth != null && cloth.rules.getHiddenArmorPieces().contains(equipmentSlot.getName())) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Unique
   private void physicsmod$renderPhysicsElytra(Avatar player, AvatarRenderState rs, float dt) {
      if (player.level() instanceof ClientLevel clientLevel) {
         SimulationPlayerExtension extension = (SimulationPlayerExtension)player;
         VerletSimulation sim = extension.physicsmod$getElytraSimulation();
         Cloth cloth = ConfigCloth.getCategory(player, "Elytra");
         int light = rs.lightCoords;
         EntityModel model = ((LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel();
         if (!this.physicsmod$shouldRenderFast(player) && cloth.rules.isDynamic()) {
            if (sim == null) {
               if (!ModelPartConstraint.exists(model, "body")) {
                  return;
               }

               sim = this.physicsmod$buildSimulation(clientLevel, player, rs, model, "body", cloth, cloth.getTexture(player), light);
               extension.physicsmod$setElytraSimulation(sim);
            } else {
               this.physicsmod$updateSimulation(sim, player, rs, model, light);
               if (cloth != sim.cloth) {
                  sim.destroyed = true;
               }
            }

            PhysicsMod.dynamicCloth.computeIfAbsent(PhysicsMod.getRenderPass(), k -> new ObjectArrayList()).add(sim);
         } else {
            ModelPart mp = ModelPartConstraint.getPart(model, "body");
            if (mp != null) {
               PhysicsMod.clothRenderFast
                  .computeIfAbsent(PhysicsMod.getRenderPass(), k -> new ObjectArrayList())
                  .add(new ClothRenderCommand(cloth, player, mp, light));
            }
         }
      }
   }

   @Unique
   private void physicsmod$renderPhysicsCloth(Avatar player, AvatarRenderState renderState, float tickDelta) {
      Map<String, ConfigCloth.ClothList> parts = ConfigCloth.getCustomizationParts(player);
      if (parts != null && !player.isInvisible()) {
         if (player.level() instanceof ClientLevel clientLevel) {
            int light = renderState.lightCoords;
            SimulationPlayerExtension extension = (SimulationPlayerExtension)player;

            for (Entry<String, ConfigCloth.ClothList> e : parts.entrySet()) {
               String modelPart = e.getKey();

               for (String clothId : e.getValue().getClothPieces()) {
                  Cloth cloth = PhysicsMod.cloth.get(clothId);
                  if (cloth != null && !ClothConstants.doesArmorHideCloth(cloth, player) && !ClothConstants.isElytraHidingCloth(cloth, modelPart, player)) {
                     if (!this.physicsmod$shouldRenderFast(player) && cloth.rules.isDynamic()) {
                        String uniqueKey = modelPart + clothId;
                        VerletSimulation sim = extension.physicsmod$getOtherSimulation(uniqueKey);
                        if (sim == null) {
                           if (!ModelPartConstraint.exists(this.getModel(), modelPart)) {
                              continue;
                           }

                           sim = this.physicsmod$buildSimulation(
                              clientLevel, player, renderState, this.getModel(), modelPart, cloth, cloth.getTexture(player), light
                           );
                           extension.physicsmod$putCapeSimulation(uniqueKey, sim);
                        } else {
                           this.physicsmod$updateSimulation(sim, player, renderState, this.getModel(), light);
                           if (cloth != sim.cloth) {
                              sim.destroyed = true;
                           }
                        }

                        PhysicsMod.dynamicCloth.computeIfAbsent(PhysicsMod.getRenderPass(), k -> new ObjectArrayList()).add(sim);
                        if (cloth.playerMesh != null) {
                           ModelPart mp = ModelPartConstraint.getPart(this.getModel(), modelPart);
                           if (mp != null) {
                              PhysicsMod.clothRenderFast
                                 .computeIfAbsent(PhysicsMod.getRenderPass(), k -> new ObjectArrayList())
                                 .add(new ClothRenderCommand(cloth, player, mp, light).setOnlyRenderPlayer(true));
                           }
                        }
                     } else {
                        ModelPart mp = ModelPartConstraint.getPart(this.getModel(), modelPart);
                        if (mp != null) {
                           PhysicsMod.clothRenderFast
                              .computeIfAbsent(PhysicsMod.getRenderPass(), k -> new ObjectArrayList())
                              .add(new ClothRenderCommand(cloth, player, mp, light));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Unique
   private void physicsmod$renderPhysicsCape(Avatar player, AvatarRenderState renderState, float tickDelta) {
      if (player.level() instanceof ClientLevel clientLevel) {
         PlayerSkin skin = renderState.skin;
         SimulationPlayerExtension extension = (SimulationPlayerExtension)player;
         VerletSimulation simulation = extension.physicsmod$getCapeSimulation();
         Cloth cloth = PhysicsMod.defaultCape;
         int light = renderState.lightCoords;
         EntityModel model = ((LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel();
         if (!this.physicsmod$shouldRenderFast(player) && cloth.rules.isDynamic()) {
            if (simulation == null) {
               if (!ModelPartConstraint.exists(model, "body")) {
                  return;
               }

               Identifier tex = skin.cape().texturePath();
               simulation = this.physicsmod$buildSimulation(clientLevel, player, renderState, model, "body", cloth, tex, light);
               extension.physicsmod$setCapeSimulation(simulation);
            } else {
               this.physicsmod$updateSimulation(simulation, player, renderState, model, light);
               if (cloth != simulation.cloth) {
                  simulation.destroyed = true;
               }
            }

            PhysicsMod.dynamicCloth.computeIfAbsent(PhysicsMod.getRenderPass(), key -> new ObjectArrayList()).add(simulation);
         } else {
            ModelPart mp = ModelPartConstraint.getPart(model, "body");
            GpuTextureView tex = Minecraft.getInstance().getTextureManager().getTexture(skin.cape().texturePath()).getTextureView();
            if (mp != null) {
               PhysicsMod.clothRenderFast
                  .computeIfAbsent(PhysicsMod.getRenderPass(), k -> new ObjectArrayList())
                  .add(new ClothRenderCommand(cloth, tex, player, mp, light));
            }
         }
      }
   }

   @Unique
   private boolean physicsmod$shouldRenderFast(LivingEntity e) {
      Camera cam = Minecraft.getInstance().gameRenderer.mainCamera();
      double r = (double)ConfigClient.clothEntityRange;
      return cam.blockPosition().distSqr(e.blockPosition()) > r * r;
   }

   @Unique
   private void physicsmod$updateSimulation(VerletSimulation sim, LivingEntity entity, LivingEntityRenderState rs, Model model, int light) {
      if (!sim.destroyed) {
         sim.active = true;
         sim.brightness = light;
         sim.getConstraint(ModelPartConstraint.class).updateEntityTransformation(sim, entity, rs, model, 1.0F);
      }
   }

   @Unique
   private VerletSimulation physicsmod$buildSimulation(
      ClientLevel level, LivingEntity entity, LivingEntityRenderState rs, Model model, String part, Cloth cloth, Identifier tex, int light
   ) {
      int quality = entity == Minecraft.getInstance().player ? 90 : 45;
      VerletSimulation sim = new VerletSimulation(new Vector3d(ConfigClient.getGravity(level.dimension().identifier())), quality, 0.855);
      ModelPartConstraint mpc = new ModelPartConstraint(sim, cloth.rules.getIgnoreParts(), entity, part, model);
      PoseStack ps = new PoseStack();
      mpc.updateEntityTransformation(sim, entity, rs, model, 1.0F);
      ps.mulPose(mpc.getEntityTransformation());
      mpc.modelPartTransformation(ps.last().pose());
      Matrix4d xf = new Matrix4d(ps.last().pose());
      sim.getConstraints().clear();
      sim.addConstraint(new OceanPhysicsDisplacementConstraint(entity));
      sim.addConstraint(mpc);
      sim.addConstraint(new WorldConstraint(entity));
      sim.brightness = light;
      sim.addCloth(cloth, tex, xf, false);
      sim.setOffset(new Vector3d(entity.getX(), entity.getY(), entity.getZ()).add(sim.getOffset()), false);
      sim.setTransformation(xf);
      sim.setBufferTransformation(xf);
      sim.updateOffsets();
      PhysicsWorld world = PhysicsMod.getInstance(level).physicsWorld;
      mpc.initAsyncData(world, sim);
      mpc.changeInstantly = true;
      mpc.updateAfter(0.0, sim);
      sim.downloadData();
      boolean instant = entity == Minecraft.getInstance().player;
      sim.alwaysFetchInstantly = instant;
      if (instant) {
         world.addVerletSimulation(0, sim);
      } else {
         world.addVerletSimulation(sim);
      }

      return sim;
   }
}
