package net.diebuddies.physics.ragdoll;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.diebuddies.physics.PhysicsEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.ambient.BatModel;
import net.minecraft.client.model.animal.allay.AllayModel;
import net.minecraft.client.model.animal.armadillo.ArmadilloModel;
import net.minecraft.client.model.animal.armadillo.BabyArmadilloModel;
import net.minecraft.client.model.animal.axolotl.AdultAxolotlModel;
import net.minecraft.client.model.animal.axolotl.BabyAxolotlModel;
import net.minecraft.client.model.animal.bee.BeeModel;
import net.minecraft.client.model.animal.camel.BabyCamelModel;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.client.model.animal.chicken.AdultChickenModel;
import net.minecraft.client.model.animal.chicken.BabyChickenModel;
import net.minecraft.client.model.animal.dolphin.DolphinModel;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.animal.equine.BabyDonkeyModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.animal.feline.AbstractFelineModel;
import net.minecraft.client.model.animal.feline.AdultFelineModel;
import net.minecraft.client.model.animal.fish.CodModel;
import net.minecraft.client.model.animal.fish.PufferfishBigModel;
import net.minecraft.client.model.animal.fish.PufferfishMidModel;
import net.minecraft.client.model.animal.fish.PufferfishSmallModel;
import net.minecraft.client.model.animal.fish.SalmonModel;
import net.minecraft.client.model.animal.fish.TropicalFishLargeModel;
import net.minecraft.client.model.animal.fish.TropicalFishSmallModel;
import net.minecraft.client.model.animal.fox.AdultFoxModel;
import net.minecraft.client.model.animal.fox.BabyFoxModel;
import net.minecraft.client.model.animal.frog.FrogModel;
import net.minecraft.client.model.animal.frog.TadpoleModel;
import net.minecraft.client.model.animal.ghast.HappyGhastModel;
import net.minecraft.client.model.animal.goat.BabyGoatModel;
import net.minecraft.client.model.animal.goat.GoatModel;
import net.minecraft.client.model.animal.golem.CopperGolemModel;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.model.animal.golem.SnowGolemModel;
import net.minecraft.client.model.animal.llama.BabyLlamaModel;
import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.model.animal.nautilus.NautilusModel;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.animal.rabbit.AdultRabbitModel;
import net.minecraft.client.model.animal.rabbit.BabyRabbitModel;
import net.minecraft.client.model.animal.sheep.BabySheepModel;
import net.minecraft.client.model.animal.sheep.SheepModel;
import net.minecraft.client.model.animal.sniffer.SnifferModel;
import net.minecraft.client.model.animal.squid.SquidModel;
import net.minecraft.client.model.animal.turtle.BabyTurtleModel;
import net.minecraft.client.model.animal.wolf.AdultWolfModel;
import net.minecraft.client.model.animal.wolf.BabyWolfModel;
import net.minecraft.client.model.monster.creaking.CreakingModel;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.model.monster.enderman.EndermanModel;
import net.minecraft.client.model.monster.endermite.EndermiteModel;
import net.minecraft.client.model.monster.ghast.GhastModel;
import net.minecraft.client.model.monster.guardian.GuardianModel;
import net.minecraft.client.model.monster.hoglin.BabyHoglinModel;
import net.minecraft.client.model.monster.hoglin.HoglinModel;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.model.monster.phantom.PhantomModel;
import net.minecraft.client.model.monster.piglin.AbstractPiglinModel;
import net.minecraft.client.model.monster.piglin.BabyPiglinModel;
import net.minecraft.client.model.monster.piglin.BabyZombifiedPiglinModel;
import net.minecraft.client.model.monster.ravager.RavagerModel;
import net.minecraft.client.model.monster.silverfish.SilverfishModel;
import net.minecraft.client.model.monster.skeleton.BoggedModel;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.model.monster.strider.AdultStriderModel;
import net.minecraft.client.model.monster.strider.BabyStriderModel;
import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.model.monster.warden.WardenModel;
import net.minecraft.client.model.monster.witch.WitchModel;
import net.minecraft.client.model.monster.wither.WitherBossModel;
import net.minecraft.client.model.monster.zombie.BabyDrownedModel;
import net.minecraft.client.model.monster.zombie.BabyZombieModel;
import net.minecraft.client.model.monster.zombie.BabyZombieVillagerModel;
import net.minecraft.client.model.monster.zombie.DrownedModel;
import net.minecraft.client.model.monster.zombie.ZombieVillagerModel;
import net.minecraft.client.model.npc.BabyVillagerModel;
import net.minecraft.client.model.npc.VillagerModel;
import net.minecraft.client.model.object.armorstand.ArmorStandModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SimpleEquipmentLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.ArmadilloRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.CamelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.minecraft.client.renderer.entity.state.GoatRenderState;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.illager.AbstractIllager.IllagerArmPose;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class VanillaRagdollHook implements RagdollHook {
   @Override
   public void map(Ragdoll ragdoll, Entity entity, EntityModel model, EntityRenderState renderState) {
      if (model instanceof CreakingModel creaking) {
         Map<String, RagdollMapper.ModelPartIndex> indices = RagdollMapper.getModelPartIndices(model);
         int headOffset = indices.get("head").index;
         int bodyOffset = indices.get("body").index;
         int rightArmOffset = indices.get("right_arm").index;
         int leftArmOffset = indices.get("left_arm").index;
         int rightLegOffset = indices.get("right_leg").index;
         int leftLegOffset = indices.get("left_leg").index;
         ragdoll.addConnection(headOffset, bodyOffset);
         ragdoll.addConnection(rightArmOffset, bodyOffset);
         ragdoll.addConnection(leftArmOffset, bodyOffset);
         ragdoll.addConnection(rightLegOffset, bodyOffset);
         ragdoll.addConnection(leftLegOffset, bodyOffset);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof BabyPiglinModel || model instanceof BabyZombifiedPiglinModel) {
         Map<String, RagdollMapper.ModelPartIndex> indices = RagdollMapper.getModelPartIndices(model);
         int headOffset = indices.get("head").index;
         int bodyOffset = indices.get("body").index;
         int rightArmOffset = indices.get("right_arm").index;
         int leftArmOffset = indices.get("left_arm").index;
         int rightLegOffset = indices.get("right_leg").index;
         int leftLegOffset = indices.get("left_leg").index;
         int leftEarOffset = indices.get("left_ear_r1").index;
         int rightEarOffset = indices.get("right_ear_r1").index;
         ragdoll.addConnection(leftEarOffset, headOffset);
         ragdoll.addConnection(rightEarOffset, headOffset);
         ragdoll.addConnection(headOffset, bodyOffset);
         ragdoll.addConnection(rightArmOffset, bodyOffset);
         ragdoll.addConnection(leftArmOffset, bodyOffset);
         ragdoll.addConnection(rightLegOffset, bodyOffset);
         ragdoll.addConnection(leftLegOffset, bodyOffset);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof AbstractPiglinModel piglin) {
         Map<String, RagdollMapper.ModelPartIndex> indices = RagdollMapper.getModelPartIndices(model);
         int headOffset = indices.get("head").index;
         int bodyOffset = indices.get("body").index;
         int rightArmOffset = indices.get("right_arm").index;
         int leftArmOffset = indices.get("left_arm").index;
         int rightLegOffset = indices.get("right_leg").index;
         int leftLegOffset = indices.get("left_leg").index;
         int leftEarOffset = indices.get("left_ear").index;
         int rightEarOffset = indices.get("right_ear").index;
         ragdoll.addConnection(leftEarOffset, headOffset);
         ragdoll.addConnection(rightEarOffset, headOffset);
         ragdoll.addConnection(headOffset, bodyOffset);
         ragdoll.addConnection(rightArmOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(leftArmOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(rightLegOffset, bodyOffset);
         ragdoll.addConnection(leftLegOffset, bodyOffset);
         int leftPantsOffset = indices.get("left_pants").index;
         int rightPantsOffset = indices.get("right_pants").index;
         int leftSleeveOffset = indices.get("left_sleeve").index;
         int rightSleeveOffset = indices.get("right_sleeve").index;
         int jacketOffset = indices.get("jacket").index;
         ragdoll.addConnection(leftPantsOffset, leftLegOffset, true, true);
         ragdoll.addConnection(rightPantsOffset, rightLegOffset, true, true);
         ragdoll.addConnection(leftSleeveOffset, leftArmOffset, true, true);
         ragdoll.addConnection(rightSleeveOffset, rightArmOffset, true, true);
         ragdoll.addConnection(jacketOffset, bodyOffset, true, true);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof VexModel vex) {
         int headOffset = 0;
         int bodyOffset = 1;
         int clothOffset = 2;
         int rightArmOffset = 3;
         int leftArmOffset = 4;
         int rightWingOffset = 5;
         int leftWingOffset = 6;
         ragdoll.addConnection(headOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(clothOffset, bodyOffset, true);
         ragdoll.addConnection(rightArmOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(leftArmOffset, bodyOffset).stopCollision = true;
         ragdoll.addConnection(rightWingOffset, bodyOffset, true);
         ragdoll.addConnection(leftWingOffset, bodyOffset, true);
         RagdollMapper.getCuboids(ragdoll, vex.root(), new RagdollMapper.Counter());
      } else if (model instanceof HumanoidModel) {
         Map<String, RagdollMapper.ModelPartIndex> indices = RagdollMapper.getModelPartIndices(model);
         int headOffset = indices.get("head").index;
         int bodyOffset = indices.get("body").index;
         int rightArmOffset = indices.get("right_arm").index;
         int leftArmOffset = indices.get("left_arm").index;
         int rightLegOffset = indices.get("right_leg").index;
         int leftLegOffset = indices.get("left_leg").index;
         int hatOffset = 0;
         if (!(model instanceof BabyDrownedModel) && !(model instanceof BabyZombieModel)) {
            hatOffset = indices.get("hat").index;
         } else {
            hatOffset = headOffset++;
         }

         ragdoll.addConnection(headOffset, bodyOffset);
         ragdoll.addConnection(rightArmOffset, bodyOffset);
         ragdoll.addConnection(leftArmOffset, bodyOffset);
         ragdoll.addConnection(rightLegOffset, bodyOffset);
         ragdoll.addConnection(leftLegOffset, bodyOffset);
         if (model instanceof PlayerModel playerModel) {
            AvatarRenderState playerState = (AvatarRenderState)renderState;

            try {
               int leftPantsOffset = indices.get("left_pants").index;
               int rightPantsOffset = indices.get("right_pants").index;
               int leftSleeveOffset = indices.get("left_sleeve").index;
               int rightSleeveOffset = indices.get("right_sleeve").index;
               int jacketOffset = indices.get("jacket").index;
               if (playerState.showLeftPants) {
                  ragdoll.addConnection(leftPantsOffset, leftLegOffset, true, true);
               }

               if (playerState.showRightPants) {
                  ragdoll.addConnection(rightPantsOffset, rightLegOffset, true, true);
               }

               if (playerState.showLeftSleeve) {
                  ragdoll.addConnection(leftSleeveOffset, leftArmOffset, true, true);
               }

               if (playerState.showRightSleeve) {
                  ragdoll.addConnection(rightSleeveOffset, rightArmOffset, true, true);
               }

               if (playerState.showJacket) {
                  ragdoll.addConnection(jacketOffset, bodyOffset, true, true);
               }

               if (playerState.showHat) {
                  ragdoll.addConnection(hatOffset, headOffset, true, true);
               }
            } catch (Exception var36) {
            }
         } else if (model instanceof ArmorStandModel) {
            try {
               int rightBodyStickOffset = indices.get("right_body_stick").index;
               int leftBodyStickOffset = indices.get("left_body_stick").index;
               int shoulderStickOffset = indices.get("shoulder_stick").index;
               int basePlateOffset = indices.get("base_plate").index;
               ragdoll.addConnection(rightBodyStickOffset, bodyOffset, true);
               ragdoll.addConnection(leftBodyStickOffset, bodyOffset, true);
               ragdoll.addConnection(shoulderStickOffset, bodyOffset, true);
            } catch (Exception var35) {
            }
         } else if (model instanceof BoggedModel) {
            boolean sheared = false;
            if (entity instanceof Bogged bogged) {
               sheared = bogged.isSheared();
            }

            PhysicsEntity bow = null;

            for (int i = 0; i < ragdoll.bodies.size(); i++) {
               PhysicsEntity b = ragdoll.bodies.get(i);
               if (b.feature instanceof ItemInHandLayer) {
                  bow = ragdoll.bodies.remove(i);
                  break;
               }
            }

            int count = RagdollMapper.countModelParts(entity, model);
            if (sheared) {
               if (count < ragdoll.bodies.size()) {
                  ragdoll.addOverlayConnections(true, count * 2, 0, 2);
               }
            } else {
               ragdoll.addConnection(13, 0, true, true);
               ragdoll.addConnection(14, 7, true, true);
               ragdoll.addConnection(15, 8, true, true);
               ragdoll.addConnection(16, 9, true, true);
               ragdoll.addConnection(17, 10, true, true);
               ragdoll.addConnection(18, 11, true, true);
               ragdoll.addConnection(19, 12, true, true);
               ragdoll.addConnection(1, 0, true, true);
               ragdoll.addConnection(2, 0, true, true);
               ragdoll.addConnection(3, 0, true, true);
               ragdoll.addConnection(4, 0, true, true);
               ragdoll.addConnection(5, 0, true, true);
               ragdoll.addConnection(6, 0, true, true);
            }

            ragdoll.addConnection(hatOffset, headOffset, true, true);
            if (bow != null) {
               ragdoll.bodies.add(bow);
               ragdoll.addConnection(ragdoll.bodies.size() - 1, rightArmOffset, true, true);
            }
         } else if (!(model instanceof SkeletonModel)) {
            if (model instanceof DrownedModel) {
               int countx = RagdollMapper.countModelParts(entity, model);
               ragdoll.addConnection(hatOffset, headOffset, true);
               if (ragdoll.bodies.size() > countx * 2) {
                  ragdoll.addOverlayConnections(true, 14, 5, 2);
                  int base = 7;
                  int spike1 = 8;
                  int spike2 = 9;
                  int spike3 = 10;
                  int base2 = 11;
                  ragdoll.addConnection(base2, base, true);
                  ragdoll.addConnection(spike1, base, true);
                  ragdoll.addConnection(spike2, base, true);
                  ragdoll.addConnection(spike3, base, true);
               } else if (countx < ragdoll.bodies.size()) {
                  ragdoll.addOverlayConnections(true);
               }
            } else if (model instanceof EndermanModel) {
               ragdoll.addConnection(hatOffset, headOffset, true, true);
               ragdoll.addOverlayConnections(true);
            } else if (model instanceof ZombieVillagerModel) {
               ragdoll.addConnection(hatOffset, headOffset, true, true);
               if (model instanceof BabyZombieVillagerModel) {
                  int noseOffset = indices.get("nose").index;
                  ragdoll.addConnection(noseOffset, headOffset, true);
               }

               int countx = RagdollMapper.countModelParts(entity, model);
               if (countx < ragdoll.bodies.size()) {
                  boolean hasHat = ragdoll.bodies.size() % countx != 0;
                  int overlays = (int)Math.ceil((double)ragdoll.bodies.size() / (double)countx);
                  int offset = 0;

                  for (int ix = 1; ix < overlays; ix++) {
                     offset += countx;
                     if (ix == 1 && hasHat) {
                        offset -= 4;
                     } else {
                        ragdoll.addConnection(headOffset + offset, headOffset, true, true);
                        ragdoll.addConnection(headOffset + 1 + offset, headOffset, true, true);
                        ragdoll.addConnection(hatOffset + offset, headOffset, true, true);
                        ragdoll.addConnection(hatOffset + 1 + offset, headOffset, true, true);
                     }

                     ragdoll.addConnection(bodyOffset + offset, bodyOffset, true, true);
                     ragdoll.addConnection(bodyOffset + 1 + offset, bodyOffset, true, true);
                     ragdoll.addConnection(leftArmOffset + offset, leftArmOffset, true, true);
                     ragdoll.addConnection(rightArmOffset + offset, rightArmOffset, true, true);
                     ragdoll.addConnection(rightLegOffset + offset, rightLegOffset, true, true);
                     ragdoll.addConnection(leftLegOffset + offset, leftLegOffset, true, true);
                  }
               }
            }
         } else {
            PhysicsEntity bow = null;

            for (int ix = 0; ix < ragdoll.bodies.size(); ix++) {
               PhysicsEntity b = ragdoll.bodies.get(ix);
               if (b.feature instanceof ItemInHandLayer) {
                  bow = ragdoll.bodies.remove(ix);
                  break;
               }
            }

            ragdoll.addConnection(hatOffset, headOffset, true, true);
            int countx = RagdollMapper.countModelParts(entity, model);
            if (countx < ragdoll.bodies.size()) {
               ragdoll.addOverlayConnections(true, countx * 2, 0, 2);
            }

            if (bow != null) {
               ragdoll.bodies.add(bow);
               ragdoll.addConnection(ragdoll.bodies.size() - 1, rightArmOffset, true, true);
            }
         }

         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof QuadrupedModel animal) {
         Map<String, RagdollMapper.ModelPartIndex> indicesx = RagdollMapper.getModelPartIndices(model);
         int headOffsetx = indicesx.get("head").index;
         if (model instanceof BabyGoatModel) {
            GoatRenderState goatState = (GoatRenderState)renderState;
            int headMainOffset = indicesx.get("HeadMain").index;
            ragdoll.addConnection(headOffsetx, headMainOffset, true, true);
            headOffsetx = headMainOffset;
            int leftEar = indicesx.get("left_ear").index;
            int rightEar = indicesx.get("right_ear").index;
            ragdoll.addConnection(leftEar, headMainOffset, true, true);
            ragdoll.addConnection(rightEar, headMainOffset, true, true);
            if (goatState.hasLeftHorn) {
               ragdoll.addConnection(indicesx.get("left_horn").index, headMainOffset, true);
            }

            if (goatState.hasRightHorn) {
               ragdoll.addConnection(indicesx.get("right_horn").index, headMainOffset, true);
            }
         } else if (model instanceof GoatModel) {
            GoatRenderState goatStatex = (GoatRenderState)renderState;
            headOffsetx += 2;
            ragdoll.addConnection(headOffsetx - 2, headOffsetx, true, true);
            ragdoll.addConnection(headOffsetx - 1, headOffsetx, true, true);
            if (goatStatex.hasLeftHorn) {
               ragdoll.addConnection(indicesx.get("left_horn").index, headOffsetx, true);
            }

            if (goatStatex.hasRightHorn) {
               ragdoll.addConnection(indicesx.get("right_horn").index, headOffsetx, true);
            }

            ragdoll.addConnection(indicesx.get("nose").index, headOffsetx, true, true);
         }

         int bodyOffsetx = indicesx.get("body").index;
         int rightArmOffsetx = indicesx.get("right_front_leg").index;
         int leftArmOffsetx = indicesx.get("left_front_leg").index;
         int rightLegOffsetx = indicesx.get("right_hind_leg").index;
         int leftLegOffsetx = indicesx.get("left_hind_leg").index;
         ragdoll.addConnection(headOffsetx, bodyOffsetx);
         if (model instanceof BabyTurtleModel) {
            ragdoll.addConnection(rightArmOffsetx, bodyOffsetx, true);
            ragdoll.addConnection(leftArmOffsetx, bodyOffsetx, true);
            ragdoll.addConnection(rightLegOffsetx, bodyOffsetx, true);
            ragdoll.addConnection(leftLegOffsetx, bodyOffsetx, true);
            ragdoll.bodies.get(bodyOffsetx).backfaceCulling(true);
         } else {
            ragdoll.addConnection(rightArmOffsetx, bodyOffsetx);
            ragdoll.addConnection(leftArmOffsetx, bodyOffsetx);
            ragdoll.addConnection(rightLegOffsetx, bodyOffsetx);
            ragdoll.addConnection(leftLegOffsetx, bodyOffsetx);
         }

         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
         if (model instanceof BabySheepModel && RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof AdultChickenModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int beakOffset = indicesxx.get("beak").index;
         int wattleOffset = indicesxx.get("red_thing").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int rightLegOffsetxx = indicesxx.get("right_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_leg").index;
         int rightWingOffset = indicesxx.get("right_wing").index;
         int leftWingOffset = indicesxx.get("left_wing").index;
         ragdoll.addConnection(beakOffset, headOffsetxx, true);
         ragdoll.addConnection(wattleOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxx);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof BabyChickenModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int bodyOffsetxx = indicesxx.get("body").index;
         int rightLegOffsetxx = indicesxx.get("right_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_leg").index;
         int rightWingOffset = indicesxx.get("right_wing").index;
         int leftWingOffset = indicesxx.get("left_wing").index;
         ragdoll.addConnection(bodyOffsetxx + 1, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx, true);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx + 1, bodyOffsetxx, true);
         ragdoll.addConnection(leftLegOffsetxx + 1, bodyOffsetxx, true);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxx, true);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxx, true);
      } else if (model instanceof AdultWolfModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int rightFrontLegOffset = indicesxx.get("right_front_leg").index;
         int leftFrontLegOffset = indicesxx.get("left_front_leg").index;
         int rightHindLegOffset = indicesxx.get("right_hind_leg").index;
         int leftHindLegOffset = indicesxx.get("left_hind_leg").index;
         int neckOffset = indicesxx.get("upper_body").index;
         int tailOffset = indicesxx.get("tail").index;
         ragdoll.addConnection(headOffsetxx, neckOffset);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
         ragdoll.addConnection(neckOffset, bodyOffsetxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof BabyWolfModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int rightEarOffset = indicesxx.get("right_ear").index;
         int leftEarOffset = indicesxx.get("left_ear").index;
         int rightFrontLegOffset = indicesxx.get("right_front_leg").index;
         int leftFrontLegOffset = indicesxx.get("left_front_leg").index;
         int rightHindLegOffset = indicesxx.get("right_hind_leg").index;
         int leftHindLegOffset = indicesxx.get("left_hind_leg").index;
         int tailOffset = indicesxx.get("tail").index;
         ragdoll.addConnection(rightEarOffset, headOffsetxx);
         ragdoll.addConnection(leftEarOffset, headOffsetxx);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof SquidModel) {
         ragdoll.addConnection(0, 4);
         ragdoll.addConnection(1, 4);
         ragdoll.addConnection(2, 4);
         ragdoll.addConnection(3, 4);
         ragdoll.addConnection(5, 4);
         ragdoll.addConnection(6, 4);
         ragdoll.addConnection(7, 4);
         ragdoll.addConnection(8, 4);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof CreeperModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int rightArmOffsetxx = indicesxx.get("right_front_leg").index;
         int leftArmOffsetxx = indicesxx.get("left_front_leg").index;
         int rightLegOffsetxx = indicesxx.get("right_hind_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_hind_leg").index;
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof DolphinModel) {
         int bodyOffsetxx = 0;
         int headOffsetxx = 1;
         int noseOffset = 2;
         int leftFinOffset = 3;
         int rightFinOffset = 4;
         int tailOffset = 5;
         int tailFinOffset = 6;
         int backFinOffset = 7;
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(leftFinOffset, bodyOffsetxx, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxx, true);
         ragdoll.addConnection(backFinOffset, bodyOffsetxx, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxx);
         ragdoll.addConnection(tailFinOffset, tailOffset);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof GhastModel) {
         ragdoll.addConnection(0, 5);
         ragdoll.addConnection(1, 5);
         ragdoll.addConnection(2, 5);
         ragdoll.addConnection(3, 5);
         ragdoll.addConnection(4, 5);
         ragdoll.addConnection(6, 5);
         ragdoll.addConnection(7, 5);
         ragdoll.addConnection(8, 5);
         ragdoll.addConnection(9, 5);
      } else if (model instanceof HappyGhastModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int bodyOffsetxx = indicesxx.get("body").index;
         ragdoll.addConnection(indicesxx.get("tentacle0").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle1").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle2").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle3").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle4").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle5").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle6").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle7").index, bodyOffsetxx);
         ragdoll.addConnection(indicesxx.get("tentacle8").index, bodyOffsetxx);
         if (indicesxx.get("inner_body") != null) {
            int innerBodyOffset = indicesxx.get("inner_body").index;
            ragdoll.addConnection(innerBodyOffset, bodyOffsetxx, true, true);
         }
      } else if (model instanceof IronGolemModel) {
         int headOffsetxx = 0;
         int noseOffset = 1;
         int rightArmOffsetxx = 2;
         int leftLegOffsetxx = 3;
         int leftArmOffsetxx = 4;
         int rightLegOffsetxx = 5;
         int bodyOffsetxx = 6;
         int lowerBodyOffset = 7;
         ragdoll.addConnection(headOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(leftArmOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(rightArmOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(leftLegOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(rightLegOffsetxx, lowerBodyOffset);
         ragdoll.addConnection(bodyOffsetxx, lowerBodyOffset, true);
      } else if (model instanceof SpiderModel) {
         int headOffsetxx = 0;
         int rightFrontLegOffset = 1;
         int rightHindLegOffset = 2;
         int leftMiddleFrontLegOffset = 3;
         int body0Offset = 4;
         int body1Offset = 5;
         int leftHindLegOffset = 6;
         int rightMiddleHindLegOffset = 7;
         int rightMiddleFrontLegOffset = 8;
         int leftMiddleHindLegOffset = 9;
         int leftFrontLegOffset = 10;
         ragdoll.addConnection(headOffsetxx, body0Offset);
         ragdoll.addConnection(body1Offset, body0Offset);
         ragdoll.addConnection(rightFrontLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(rightHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftMiddleFrontLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(rightMiddleHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(rightMiddleFrontLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftMiddleHindLegOffset, body0Offset).stopCollision = true;
         ragdoll.addConnection(leftFrontLegOffset, body0Offset).stopCollision = true;
      } else if (model instanceof SnowGolemModel) {
         int headOffsetxx = 0;
         int rightArmOffsetxx = 1;
         int upperBodyOffset = 2;
         int leftArmOffsetxx = 3;
         int lowerBodyOffset = 4;
         int pumpkinOffset = 5;
         ragdoll.addConnection(headOffsetxx, upperBodyOffset);
         ragdoll.addConnection(rightArmOffsetxx, upperBodyOffset);
         ragdoll.addConnection(leftArmOffsetxx, upperBodyOffset);
         ragdoll.addConnection(upperBodyOffset, lowerBodyOffset);
         if (ragdoll.bodies.size() == 6) {
            ragdoll.addConnection(pumpkinOffset, headOffsetxx, true);
         }
      } else if (model instanceof GuardianModel) {
         int headOffsetxx = 0;
         int spike0 = 21;
         int spike1 = 5;
         int spike2 = 6;
         int spike3 = 7;
         int spike4 = 8;
         int spike5 = 9;
         int spike6 = 10;
         int spike7 = 16;
         int spike8 = 17;
         int spike9 = 18;
         int spike10 = 19;
         int spike11 = 20;
         int eye = 11;
         int tail0 = 12;
         int tail1 = 13;
         int tail2 = 14;
         int tail3 = 15;
         ragdoll.addConnection(tail0, headOffsetxx, true);
         ragdoll.addConnection(tail1, headOffsetxx, true);
         ragdoll.addConnection(tail2, headOffsetxx, true);
         ragdoll.addConnection(tail3, headOffsetxx, true);
         ragdoll.addConnection(spike0, headOffsetxx, true);
         ragdoll.addConnection(spike1, headOffsetxx, true);
         ragdoll.addConnection(spike2, headOffsetxx, true);
         ragdoll.addConnection(spike3, headOffsetxx, true);
         ragdoll.addConnection(spike4, headOffsetxx, true);
         ragdoll.addConnection(spike5, headOffsetxx, true);
         ragdoll.addConnection(spike6, headOffsetxx, true);
         ragdoll.addConnection(spike7, headOffsetxx, true);
         ragdoll.addConnection(spike8, headOffsetxx, true);
         ragdoll.addConnection(spike9, headOffsetxx, true);
         ragdoll.addConnection(spike10, headOffsetxx, true);
         ragdoll.addConnection(spike11, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 1, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 2, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 3, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx + 4, headOffsetxx, true);
         ragdoll.addConnection(eye, headOffsetxx, true);
      } else if (model instanceof WitchModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int noseOffset = indicesxx.get("nose").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int hatOffsetx = indicesxx.get("hat").index;
         int hat2Offset = indicesxx.get("hat2").index;
         int hat3Offset = indicesxx.get("hat3").index;
         int hat4Offset = indicesxx.get("hat4").index;
         int rightLegOffsetxx = indicesxx.get("right_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_leg").index;
         int armsOffset = indicesxx.get("arms").index;
         int jacketOffsetx = indicesxx.get("jacket").index;
         int moleOffset = indicesxx.get("mole").index;
         ragdoll.addConnection(moleOffset, headOffsetxx, true);
         ragdoll.addConnection(hat2Offset, headOffsetxx, true);
         ragdoll.addConnection(hat3Offset, headOffsetxx, true);
         ragdoll.addConnection(hat4Offset, headOffsetxx, true);
         ragdoll.addConnection(hatOffsetx, headOffsetxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(jacketOffsetx, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 1, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 2, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(armsOffset, bodyOffsetxx);
      } else if (model instanceof BabyVillagerModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int noseOffset = indicesxx.get("nose").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int hatOffsetx = indicesxx.get("hat").index;
         int hatRimOffset = indicesxx.get("hat_rim").index;
         int rightLegOffsetxx = indicesxx.get("right_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_leg").index;
         int armsOffset = indicesxx.get("right_hand").index;
         int arms2Offset = indicesxx.get("middlearm_r1").index;
         int jacketOffsetx = indicesxx.get("bb_main").index;
         ragdoll.addConnection(hatOffsetx, headOffsetxx, true);
         ragdoll.addConnection(hatRimOffset, headOffsetxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(jacketOffsetx, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 1, bodyOffsetxx, true);
         ragdoll.addConnection(arms2Offset, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx).stopCollision = true;
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx).stopCollision = true;
         ragdoll.addConnection(armsOffset, bodyOffsetxx);
         int countxx = RagdollMapper.countModelParts(entity, model);
         if (countxx < ragdoll.bodies.size()) {
            boolean hasHat = ragdoll.bodies.size() % countxx != 0;
            int overlays = (int)Math.ceil((double)ragdoll.bodies.size() / (double)countxx);
            int offset = 0;

            for (int ixx = 1; ixx < overlays; ixx++) {
               offset += countxx;
               if (ixx == 1 && hasHat) {
                  offset -= 4;
               } else {
                  ragdoll.addConnection(hatOffsetx + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(hatRimOffset + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(noseOffset + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(headOffsetxx + offset, headOffsetxx, true, true);
               }

               ragdoll.addConnection(bodyOffsetxx + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(jacketOffsetx + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(armsOffset + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(armsOffset + 1 + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(arms2Offset + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(rightLegOffsetxx + offset, rightLegOffsetxx, true, true);
               ragdoll.addConnection(leftLegOffsetxx + offset, leftLegOffsetxx, true, true);
            }
         }
      } else if (model instanceof VillagerModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int noseOffset = indicesxx.get("nose").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int hatOffsetx = indicesxx.get("hat").index;
         int hatRimOffset = indicesxx.get("hat_rim").index;
         int rightLegOffsetxx = indicesxx.get("right_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_leg").index;
         int armsOffset = indicesxx.get("arms").index;
         int jacketOffsetx = indicesxx.get("jacket").index;
         ragdoll.addConnection(hatOffsetx, headOffsetxx, true);
         ragdoll.addConnection(hatRimOffset, headOffsetxx, true);
         ragdoll.addConnection(noseOffset, headOffsetxx, true);
         ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
         ragdoll.addConnection(jacketOffsetx, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 1, bodyOffsetxx, true);
         ragdoll.addConnection(armsOffset + 2, bodyOffsetxx, true);
         ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx).stopCollision = true;
         ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx).stopCollision = true;
         ragdoll.addConnection(armsOffset, bodyOffsetxx);
         int countxx = RagdollMapper.countModelParts(entity, model);
         if (countxx < ragdoll.bodies.size()) {
            boolean hasHat = ragdoll.bodies.size() % countxx != 0;
            int overlays = (int)Math.ceil((double)ragdoll.bodies.size() / (double)countxx);
            int offset = 0;

            for (int ixx = 1; ixx < overlays; ixx++) {
               offset += countxx;
               if (ixx == 1 && hasHat) {
                  offset -= 4;
               } else {
                  ragdoll.addConnection(hatOffsetx + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(hatRimOffset + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(noseOffset + offset, headOffsetxx, true, true);
                  ragdoll.addConnection(headOffsetxx + offset, headOffsetxx, true, true);
               }

               ragdoll.addConnection(bodyOffsetxx + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(jacketOffsetx + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(armsOffset + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(armsOffset + 1 + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(armsOffset + 2 + offset, bodyOffsetxx, true, true);
               ragdoll.addConnection(rightLegOffsetxx + offset, rightLegOffsetxx, true, true);
               ragdoll.addConnection(leftLegOffsetxx + offset, leftLegOffsetxx, true, true);
            }
         }
      } else if (model instanceof IllagerModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxx = indicesxx.get("head").index;
         int noseOffset = indicesxx.get("nose").index;
         int bodyOffsetxx = indicesxx.get("body").index;
         int rightLegOffsetxx = indicesxx.get("right_leg").index;
         int leftLegOffsetxx = indicesxx.get("left_leg").index;
         IllagerRenderState illagerState = (IllagerRenderState)renderState;
         if (illagerState.armPose == IllagerArmPose.CROSSED) {
            int armsOffset = indicesxx.get("arms").index;
            int leftShoulderOffset = indicesxx.get("left_shoulder").index;
            ragdoll.addConnection(noseOffset, headOffsetxx, true);
            ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(armsOffset, bodyOffsetxx, true);
            ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(armsOffset + 1, bodyOffsetxx, true);
            ragdoll.addConnection(leftShoulderOffset, bodyOffsetxx, true);
            ragdoll.addConnection(bodyOffsetxx + 1, bodyOffsetxx, true);
         } else {
            int rightArmOffsetxx = indicesxx.get("right_arm").index;
            int leftArmOffsetxx = indicesxx.get("left_arm").index;
            ragdoll.addConnection(noseOffset, headOffsetxx, true);
            ragdoll.addConnection(headOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(leftLegOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(rightLegOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxx);
            ragdoll.addConnection(bodyOffsetxx + 1, bodyOffsetxx, true);
         }

         if (RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof BabyStriderModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int rightLegOffsetxxx = indicesxxx.get("right_leg").index;
         int leftLegOffsetxxx = indicesxxx.get("left_leg").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(rightLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof AdultStriderModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int rightLegOffsetxxx = indicesxxx.get("right_leg").index;
         int leftLegOffsetxxx = indicesxxx.get("left_leg").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(rightLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(indicesxxx.get("right_top_bristle").index, bodyOffsetxxx, true);
         ragdoll.addConnection(indicesxxx.get("right_bottom_bristle").index, bodyOffsetxxx, true);
         ragdoll.addConnection(indicesxxx.get("left_top_bristle").index, bodyOffsetxxx, true);
         ragdoll.addConnection(indicesxxx.get("left_bottom_bristle").index, bodyOffsetxxx, true);
         ragdoll.addConnection(indicesxxx.get("right_middle_bristle").index, bodyOffsetxxx, true);
         ragdoll.addConnection(indicesxxx.get("left_middle_bristle").index, bodyOffsetxxx, true);
         ragdoll.bodies.get(bodyOffsetxxx).backfaceCulling(false);
      } else if (model instanceof RavagerModel) {
         int rightFrontLegOffset = 0;
         int rightHindLegOffset = 1;
         int leftHindLegOffset = 2;
         int neckOffset = 3;
         int headOffsetxxx = 4;
         int headChildOffset = 5;
         int rightHornOffset = 6;
         int mouthOffset = 7;
         int leftHornOffset = 8;
         int bodyOffsetxxx = 9;
         int bodyChildOffset = 10;
         int leftFrontLegOffset = 11;
         ragdoll.addConnection(bodyChildOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(neckOffset, headOffsetxxx, true);
         ragdoll.addConnection(mouthOffset, headOffsetxxx, true);
         ragdoll.addConnection(rightHornOffset, headOffsetxxx, true);
         ragdoll.addConnection(leftHornOffset, headOffsetxxx, true);
         ragdoll.addConnection(headChildOffset, headOffsetxxx, true);
      } else if (model instanceof BatModel) {
         int headOffsetxxx = 0;
         int rightEarOffset = 1;
         int leftEarOffset = 2;
         int bodyOffsetxxx = 3;
         int bodyChildOffset = 4;
         int rightWingOffset = 5;
         int rightWingTipOffset = 6;
         int leftWingOffset = 7;
         int leftWingTipOffset = 8;
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(bodyChildOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(leftEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(rightWingTipOffset, rightWingOffset, true);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftWingTipOffset, leftWingOffset, true);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxxx);
      } else if (model instanceof BeeModel) {
         int frontLegsOffset = 0;
         int rightWingOffset = 1;
         int leftWingOffset = 2;
         int middleLegsOffset = 3;
         int leftAntennaOffset = 4;
         int rightAntennaOffset = 5;
         int stingerOffset = 6;
         int bodyOffsetxxx = 7;
         int backLegsOffset = 8;
         ragdoll.addConnection(frontLegsOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(middleLegsOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(rightAntennaOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(stingerOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(leftAntennaOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(backLegsOffset, bodyOffsetxxx, true);
      } else if (model instanceof AdultRabbitModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxx = indicesxxx.get("head").index;
         int rightFrontLegOffset = indicesxxx.get("right_front_leg").index;
         int tailOffset = indicesxxx.get("tail").index;
         int leftHaunchOffset = indicesxxx.get("left_haunch").index;
         int rightHaunchOffset = indicesxxx.get("right_haunch").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         int rightEarOffset = indicesxxx.get("right_ear").index;
         int leftFrontLegOffset = indicesxxx.get("left_front_leg").index;
         int leftEarOffset = indicesxxx.get("left_ear").index;
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(leftEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(rightHaunchOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftHaunchOffset, bodyOffsetxxx);
      } else if (model instanceof BabyRabbitModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxx = indicesxxx.get("head").index;
         int rightFrontLegOffset = indicesxxx.get("right_front_leg_r1").index;
         int tailOffset = indicesxxx.get("tail_r1").index;
         int leftHaunchOffset = indicesxxx.get("left_haunch").index;
         int rightHaunchOffset = indicesxxx.get("right_haunch").index;
         int bodyOffsetxxx = indicesxxx.get("body_r1").index;
         int rightEarOffset = indicesxxx.get("right_ear").index;
         int leftFrontLegOffset = indicesxxx.get("left_front_leg_r1").index;
         int leftEarOffset = indicesxxx.get("left_ear").index;
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(leftEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxxx, true);
         ragdoll.addConnection(rightHaunchOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftHaunchOffset, bodyOffsetxxx);
      } else if (model instanceof WitherBossModel) {
         int shouldersOffset = 0;
         int ribcageOffset = 1;
         int tailOffset = 5;
         int leftHeadOffset = 6;
         int rightHeadOffset = 7;
         int centerHeadOffset = 8;
         ragdoll.addConnection(tailOffset, ribcageOffset);
         ragdoll.addConnection(shouldersOffset, ribcageOffset, true);
         ragdoll.addConnection(leftHeadOffset, ribcageOffset);
         ragdoll.addConnection(rightHeadOffset, ribcageOffset);
         ragdoll.addConnection(centerHeadOffset, ribcageOffset);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof AbstractFelineModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxx = indicesxxx.get("head").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         int rightArmOffsetxx = indicesxxx.get("right_front_leg").index;
         int leftArmOffsetxx = indicesxxx.get("left_front_leg").index;
         int rightLegOffsetxxx = indicesxxx.get("right_hind_leg").index;
         int leftLegOffsetxxx = indicesxxx.get("left_hind_leg").index;
         int upperTailOffset = indicesxxx.get("tail1").index;
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxxx);
         ragdoll.addConnection(rightLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(upperTailOffset, bodyOffsetxxx);
         if (model instanceof AdultFelineModel) {
            int lowerTailOffset = indicesxxx.get("tail2").index;
            ragdoll.addConnection(lowerTailOffset, upperTailOffset);
         }

         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof AdultFoxModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxx = indicesxxx.get("head").index;
         int noseOffsetx = indicesxxx.get("nose").index;
         int rightEarOffset = indicesxxx.get("right_ear").index;
         int leftEarOffset = indicesxxx.get("left_ear").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         int tailOffset = indicesxxx.get("tail").index;
         int leg1Offset = indicesxxx.get("right_hind_leg").index;
         int leg2Offset = indicesxxx.get("left_hind_leg").index;
         int leg3Offset = indicesxxx.get("right_front_leg").index;
         int leg4Offset = indicesxxx.get("left_front_leg").index;
         ragdoll.addConnection(noseOffsetx, headOffsetxxx, true);
         ragdoll.addConnection(rightEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(leftEarOffset, headOffsetxxx, true);
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxxx);
         ragdoll.addConnection(leg1Offset, bodyOffsetxxx);
         ragdoll.addConnection(leg2Offset, bodyOffsetxxx);
         ragdoll.addConnection(leg3Offset, bodyOffsetxxx);
         ragdoll.addConnection(leg4Offset, bodyOffsetxxx);
      } else if (model instanceof BabyFoxModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxx = indicesxxx.get("head").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         int tailOffset = indicesxxx.get("tail").index;
         int leg1Offset = indicesxxx.get("right_hind_leg").index;
         int leg2Offset = indicesxxx.get("left_hind_leg").index;
         int leg3Offset = indicesxxx.get("right_front_leg").index;
         int leg4Offset = indicesxxx.get("left_front_leg").index;
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxxx);
         ragdoll.addConnection(leg1Offset, bodyOffsetxxx);
         ragdoll.addConnection(leg2Offset, bodyOffsetxxx);
         ragdoll.addConnection(leg3Offset, bodyOffsetxxx);
         ragdoll.addConnection(leg4Offset, bodyOffsetxxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof SilverfishModel) {
         int segment2Offset = 0;
         int segment1Offset = 1;
         int segment0Offset = 2;
         int layer0Offset = 3;
         int layer1Offset = 4;
         int layer2Offset = 5;
         int segment6Offset = 6;
         int segment5Offset = 7;
         int segment4Offset = 8;
         int segment3Offset = 9;
         ragdoll.addConnection(segment0Offset, segment1Offset);
         ragdoll.addConnection(segment1Offset, segment2Offset);
         ragdoll.addConnection(segment2Offset, segment3Offset);
         ragdoll.addConnection(segment3Offset, segment4Offset);
         ragdoll.addConnection(segment4Offset, segment5Offset);
         ragdoll.addConnection(segment5Offset, segment6Offset);
         ragdoll.addConnection(layer0Offset, segment2Offset, true);
         ragdoll.addConnection(layer1Offset, segment4Offset, true);
         ragdoll.addConnection(layer2Offset, segment1Offset, true);
      } else if (model instanceof EndermiteModel) {
         int segment2Offset = 0;
         int segment1Offset = 1;
         int segment0Offset = 2;
         int segment3Offset = 3;
         ragdoll.addConnection(segment0Offset, segment1Offset);
         ragdoll.addConnection(segment1Offset, segment2Offset);
         ragdoll.addConnection(segment2Offset, segment3Offset);
      } else if (model instanceof ParrotModel) {
         int headOffsetxxx = 0;
         int beak1Offset = 1;
         int beak2Offset = 2;
         int featherOffset = 3;
         int head2Offset = 4;
         int leftLegOffsetxxx = 5;
         int rightWingOffset = 6;
         int rightLegOffsetxxx = 7;
         int tailOffset = 8;
         int leftWingOffset = 9;
         int bodyOffsetxxx = 10;
         ragdoll.addConnection(beak1Offset, headOffsetxxx, true);
         ragdoll.addConnection(beak2Offset, headOffsetxxx, true);
         ragdoll.addConnection(featherOffset, headOffsetxxx, true);
         ragdoll.addConnection(head2Offset, headOffsetxxx, true);
         ragdoll.addConnection(headOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(rightLegOffsetxxx, bodyOffsetxxx);
         ragdoll.addConnection(rightWingOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftWingOffset, bodyOffsetxxx);
         ragdoll.addConnection(tailOffset, bodyOffsetxxx);
      } else if (model instanceof BabyDonkeyModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxx = indicesxxx.get("head").index;
         int neckOffset = indicesxxx.get("neck").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         int rightHindLegOffset = indicesxxx.get("right_hind_leg").index;
         int leftHindLegOffset = indicesxxx.get("left_hind_leg").index;
         int rightFrontLegOffset = indicesxxx.get("right_front_leg").index;
         int leftFrontLegOffset = indicesxxx.get("left_front_leg").index;
         int leftEarOffset = indicesxxx.get("left_ear").index;
         int rightEarOffset = indicesxxx.get("right_ear").index;
         int tail = indicesxxx.get("tail").index;
         ragdoll.addConnection(neckOffset, bodyOffsetxxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(headOffsetxxx, neckOffset, true);
         ragdoll.addConnection(leftEarOffset, neckOffset, true);
         ragdoll.addConnection(rightEarOffset, neckOffset, true);
         ragdoll.addConnection(tail, bodyOffsetxxx, true);
      } else if (model instanceof AbstractEquineModel horse) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxx = RagdollMapper.getModelPartIndices(model);
         EquineRenderState equineState = (EquineRenderState)renderState;
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
         boolean hasSaddle = equineState.saddle != ItemStack.EMPTY;
         int neckOffset = indicesxxx.get("head_parts").index;
         int bodyOffsetxxx = indicesxxx.get("body").index;
         int rightHindLegOffset = indicesxxx.get("right_hind_leg").index;
         int leftHindLegOffset = indicesxxx.get("left_hind_leg").index;
         int rightFrontLegOffset = indicesxxx.get("right_front_leg").index;
         int leftFrontLegOffset = indicesxxx.get("left_front_leg").index;
         ragdoll.addConnection(neckOffset, bodyOffsetxxx);
         ragdoll.addConnection(rightHindLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftHindLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(rightFrontLegOffset, bodyOffsetxxx);
         ragdoll.addConnection(leftFrontLegOffset, bodyOffsetxxx);
         int leftEarOffset = indicesxxx.get("left_ear").index;
         int rightEarOffset = indicesxxx.get("right_ear").index;
         int headOffsetxxx = indicesxxx.get("head").index;
         int tail = indicesxxx.get("tail").index;
         ragdoll.addConnection(headOffsetxxx, neckOffset, true);
         ragdoll.addConnection(leftEarOffset, neckOffset, true);
         ragdoll.addConnection(rightEarOffset, neckOffset, true);
         ragdoll.addConnection(tail, bodyOffsetxxx);
         if (!equineState.isBaby) {
            int mane = indicesxxx.get("mane").index;
            int upperMouth = indicesxxx.get("upper_mouth").index;
            if (hasSaddle) {
               int leftSaddleMouthOffset = indicesxxx.get("left_saddle_mouth").index;
               int mouthSaddleWrapOffset = indicesxxx.get("mouth_saddle_wrap").index;
               int rightSaddleLineOffset = indicesxxx.get("right_saddle_line").index;
               int rightSaddleMouthOffset = indicesxxx.get("right_saddle_mouth").index;
               int leftSaddleLineOffset = indicesxxx.get("left_saddle_line").index;
               int saddleOffset = indicesxxx.get("saddle").index;
               int headSaddleOffset = indicesxxx.get("head_saddle").index;
               ragdoll.addConnection(leftSaddleMouthOffset, neckOffset, true);
               ragdoll.addConnection(mouthSaddleWrapOffset, neckOffset, true);
               ragdoll.addConnection(rightSaddleMouthOffset, neckOffset, true);
               ragdoll.addConnection(headSaddleOffset, neckOffset, true);
               ragdoll.addConnection(saddleOffset, bodyOffsetxxx, true);
            }

            ragdoll.addConnection(mane, neckOffset, true);
            ragdoll.addConnection(upperMouth, neckOffset, true);
         }

         int countxx = RagdollMapper.countModelParts(entity, model);
         if (countxx < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true, Math.max(2, ragdoll.bodies.size() / countxx));
         }
      } else if (model instanceof LlamaModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxxx = indicesxxxx.get("head").index;
         int neckOffsetx = headOffsetxxxx + 1;
         int earLeftOffset = headOffsetxxxx + 2;
         int earRightOffset = headOffsetxxxx + 3;
         int bodyOffsetxxxx = indicesxxxx.get("body").index;
         int rightFrontLegOffsetx = indicesxxxx.get("right_front_leg").index;
         int rightHindLegOffsetx = indicesxxxx.get("right_hind_leg").index;
         int leftHindLegOffsetx = indicesxxxx.get("left_hind_leg").index;
         int leftFrontLegOffsetx = indicesxxxx.get("left_front_leg").index;
         int rightChestOffset = indicesxxxx.get("right_chest").index;
         int leftChestOffset = indicesxxxx.get("left_chest").index;
         if (model instanceof BabyLlamaModel) {
            int swap = neckOffsetx;
            neckOffsetx = headOffsetxxxx;
            headOffsetxxxx = swap;
         }

         ragdoll.addConnection(headOffsetxxxx, neckOffsetx, true);
         ragdoll.addConnection(earLeftOffset, neckOffsetx, true);
         ragdoll.addConnection(earRightOffset, neckOffsetx, true);
         ragdoll.addConnection(neckOffsetx, bodyOffsetxxxx);
         ragdoll.addConnection(rightFrontLegOffsetx, bodyOffsetxxxx);
         ragdoll.addConnection(rightHindLegOffsetx, bodyOffsetxxxx);
         ragdoll.addConnection(leftHindLegOffsetx, bodyOffsetxxxx);
         ragdoll.addConnection(leftFrontLegOffsetx, bodyOffsetxxxx);
         if (RagdollMapper.countModelParts(entity, model) < ragdoll.bodies.size()) {
            ragdoll.addOverlayConnections(true);
         }
      } else if (model instanceof BabyCamelModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int rightFrontLegOffsetxx = indicesxxxxx.get("right_front_leg").index;
         int leftFrontLegOffsetxx = indicesxxxxx.get("left_front_leg").index;
         int rightHindLegOffsetxx = indicesxxxxx.get("right_hind_leg").index;
         int leftHindLegOffsetxx = indicesxxxxx.get("left_hind_leg").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int headOffsetxxxxx = indicesxxxxx.get("head").index;
         int neck1Offset = headOffsetxxxxx + 1;
         int neck2Offset = headOffsetxxxxx + 2;
         int earRightOffsetx = indicesxxxxx.get("right_ear").index;
         int earLeftOffsetx = indicesxxxxx.get("left_ear").index;
         int tailOffset = indicesxxxxx.get("tail").index;
         ragdoll.addConnection(headOffsetxxxxx, neck2Offset, true);
         ragdoll.addConnection(earLeftOffsetx, neck2Offset, true);
         ragdoll.addConnection(earRightOffsetx, neck2Offset, true);
         ragdoll.addConnection(neck1Offset, neck2Offset, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(neck2Offset, bodyOffsetxxxxx);
         ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightHindLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxxx);
      } else if (model instanceof CamelModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         CamelRenderState camelRenderState = (CamelRenderState)renderState;
         Equippable equippable = (Equippable)camelRenderState.saddle.get(DataComponents.EQUIPPABLE);
         boolean saddle = equippable != null && !equippable.assetId().isEmpty();
         boolean reins = camelRenderState.isRidden;
         int rightFrontLegOffsetxx = indicesxxxxx.get("right_front_leg").index;
         int leftFrontLegOffsetxx = indicesxxxxx.get("left_front_leg").index;
         int rightHindLegOffsetxx = indicesxxxxx.get("right_hind_leg").index;
         int leftHindLegOffsetxx = indicesxxxxx.get("left_hind_leg").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int headOffsetxxxxx = indicesxxxxx.get("head").index;
         int neck1Offset = headOffsetxxxxx + 1;
         int neck2Offset = headOffsetxxxxx + 2;
         int earRightOffsetx = indicesxxxxx.get("right_ear").index;
         int earLeftOffsetx = indicesxxxxx.get("left_ear").index;
         int humpOffset = indicesxxxxx.get("hump").index;
         int tailOffset = indicesxxxxx.get("tail").index;
         if (saddle) {
            int reinOffset = reins ? 3 : 0;
            int saddleHump1Offset = 27 + reinOffset;
            int saddleHump2Offset = 28 + reinOffset;
            int saddleBodyOffset = 29 + reinOffset;
            int bridleOffset = 19;
            ragdoll.addConnection(bridleOffset, neck2Offset, true);
            ragdoll.addConnection(bridleOffset + 1, neck2Offset, true);
            ragdoll.addConnection(bridleOffset + 2, neck2Offset, true);
            ragdoll.addConnection(bridleOffset + 3, neck2Offset, true);
            ragdoll.addConnection(bridleOffset + 4, neck2Offset, true);
            ragdoll.addConnection(saddleHump1Offset, bodyOffsetxxxxx, true);
            ragdoll.addConnection(saddleHump2Offset, bodyOffsetxxxxx, true);
            ragdoll.addConnection(saddleBodyOffset, bodyOffsetxxxxx, true);
         }

         ragdoll.addConnection(headOffsetxxxxx, neck2Offset, true);
         ragdoll.addConnection(earLeftOffsetx, neck2Offset, true);
         ragdoll.addConnection(earRightOffsetx, neck2Offset, true);
         ragdoll.addConnection(neck1Offset, neck2Offset, true);
         ragdoll.addConnection(humpOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(neck2Offset, bodyOffsetxxxxx);
         ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightHindLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.removeUnused();
      } else if (model instanceof HoglinModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxxxx = indicesxxxxx.get("head").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int rightArmOffsetxx = indicesxxxxx.get("right_front_leg").index;
         int leftArmOffsetxx = indicesxxxxx.get("left_front_leg").index;
         int rightLegOffsetxxx = indicesxxxxx.get("right_hind_leg").index;
         int leftLegOffsetxxx = indicesxxxxx.get("left_hind_leg").index;
         ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightLegOffsetxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxxxx);
         int rightEarOffsetx = indicesxxxxx.get("right_ear").index;
         int leftEarOffsetx = indicesxxxxx.get("left_ear").index;
         if (!(model instanceof BabyHoglinModel)) {
            int rightHornOffset = indicesxxxxx.get("right_horn").index;
            int leftHornOffset = indicesxxxxx.get("left_horn").index;
            int maneOffset = indicesxxxxx.get("mane").index;
            ragdoll.addConnection(maneOffset, bodyOffsetxxxxx, true);
            ragdoll.addConnection(rightHornOffset, headOffsetxxxxx, true);
            ragdoll.addConnection(leftHornOffset, headOffsetxxxxx, true);
         }

         ragdoll.addConnection(rightEarOffsetx, headOffsetxxxxx);
         ragdoll.addConnection(leftEarOffsetx, headOffsetxxxxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof SalmonModel) {
         int headOffsetxxxxx = 0;
         int leftFinOffset = 1;
         int bodyBackOffset = 2;
         int topBackFinOffset = 3;
         int backFinOffset = 4;
         int rightFinOffset = 5;
         int bodyFrontOffset = 6;
         int topFrontFinOffset = 7;
         ragdoll.addConnection(headOffsetxxxxx, bodyFrontOffset);
         ragdoll.addConnection(bodyBackOffset, bodyFrontOffset);
         ragdoll.addConnection(topFrontFinOffset, bodyFrontOffset, true);
         ragdoll.addConnection(leftFinOffset, bodyFrontOffset, true);
         ragdoll.addConnection(rightFinOffset, bodyFrontOffset, true);
         ragdoll.addConnection(topBackFinOffset, bodyBackOffset, true);
         ragdoll.addConnection(backFinOffset, bodyBackOffset, true);
      } else if (model instanceof AdultAxolotlModel || model instanceof BabyAxolotlModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int bodyGillsOffset = bodyOffsetxxxxx + 1;
         int headOffsetxxxxx = indicesxxxxx.get("head").index;
         int topGillsOffset = indicesxxxxx.get("top_gills").index;
         int leftGillsOffset = indicesxxxxx.get("left_gills").index;
         int rightGillsOffset = indicesxxxxx.get("right_gills").index;
         int rightFrontLegOffsetxx = indicesxxxxx.get("right_front_leg").index;
         int rightHindLegffset = indicesxxxxx.get("right_hind_leg").index;
         int tailOffset = indicesxxxxx.get("tail").index;
         int leftHindLegOffsetxx = indicesxxxxx.get("left_hind_leg").index;
         int leftFrontLegOffsetxx = indicesxxxxx.get("left_front_leg").index;
         ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(bodyGillsOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topGillsOffset, headOffsetxxxxx, true);
         ragdoll.addConnection(leftGillsOffset, headOffsetxxxxx, true);
         ragdoll.addConnection(rightGillsOffset, headOffsetxxxxx, true);
         ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightHindLegffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxxx, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxxxxx, true);
         if (model instanceof BabyAxolotlModel) {
            ragdoll.bodies.get(bodyOffsetxxxxx).backfaceCulling(true);
         }
      } else if (model instanceof PhantomModel) {
         int bodyOffsetxxxxx = 0;
         int headOffsetxxxxx = 1;
         int rightWingBaseOffset = 2;
         int rightWingTipOffset = 3;
         int tailBaseOffset = 4;
         int tailTipOffset = 5;
         int leftWingBaseOffset = 6;
         int leftWingTipOffset = 7;
         ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightWingBaseOffset, bodyOffsetxxxxx);
         ragdoll.addConnection(tailBaseOffset, bodyOffsetxxxxx);
         ragdoll.addConnection(leftWingBaseOffset, bodyOffsetxxxxx);
         ragdoll.addConnection(rightWingTipOffset, rightWingBaseOffset, true);
         ragdoll.addConnection(tailTipOffset, tailBaseOffset, true);
         ragdoll.addConnection(leftWingTipOffset, leftWingBaseOffset, true);
      } else if (model instanceof CodModel) {
         int headOffsetxxxxx = 0;
         int noseOffsetx = 1;
         int leftFinOffset = 2;
         int topFinOffset = 3;
         int rightFinOffset = 4;
         int bodyOffsetxxxxx = 5;
         int tailFinOffset = 6;
         ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(noseOffsetx, headOffsetxxxxx, true);
         ragdoll.addConnection(tailFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxxx, true);
      } else if (model instanceof PufferfishSmallModel) {
         int rightEyeOffset = 0;
         int leftFinOffset = 1;
         int rightFinOffset = 2;
         int leftEyeOffset = 3;
         int bodyOffsetxxxxx = 4;
         int backFinOffset = 5;
         ragdoll.addConnection(rightEyeOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftEyeOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(backFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxxx, true);
      } else if (model instanceof PufferfishMidModel) {
         int leftBlueFinOffset = 0;
         int topBackFinOffset = 1;
         int leftBackFinOffset = 2;
         int leftFrontFinOffset = 3;
         int bottomFrontFinOffset = 4;
         int rightFrontFinOffset = 5;
         int rightBackFinOffset = 6;
         int bodyOffsetxxxxx = 7;
         int topFrontFinOffset = 8;
         int bottomBackFinOffset = 9;
         int rightBlueFinOffset = 10;
         ragdoll.addConnection(leftBlueFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(bottomFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(bottomBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightBlueFinOffset, bodyOffsetxxxxx, true);
      } else if (model instanceof PufferfishBigModel) {
         int leftBlueFinOffset = 0;
         int topBackFinOffset = 1;
         int leftBackFinOffset = 2;
         int leftFrontFinOffset = 3;
         int bottomFrontFinOffset = 4;
         int bodyOffsetxxxxx = 5;
         int rightFrontFinOffset = 6;
         int rightBackFinOffset = 7;
         int topFrontFinOffset = 8;
         int bottomBackFinOffset = 9;
         int rightBlueFinOffset = 10;
         int rightBlueBackFinOffset = 11;
         int topBlueFrontFinOffset = 12;
         ragdoll.addConnection(leftBlueFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(leftFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(bottomFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(bottomBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightBlueBackFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(topBlueFrontFinOffset, bodyOffsetxxxxx, true);
         ragdoll.addConnection(rightBlueFinOffset, bodyOffsetxxxxx, true);
      } else if (model instanceof TropicalFishLargeModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int leftFinOffset = indicesxxxxx.get("left_fin").index;
         int topFinOffset = indicesxxxxx.get("top_fin").index;
         int tailOffset = indicesxxxxx.get("tail").index;
         int rightFinOffset = indicesxxxxx.get("right_fin").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int bottomFinOffset = indicesxxxxx.get("bottom_fin").index;
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(topFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(bottomFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addOverlayConnections(true);
      } else if (model instanceof TropicalFishSmallModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int leftFinOffset = indicesxxxxx.get("left_fin").index;
         int topFinOffset = indicesxxxxx.get("top_fin").index;
         int tailOffset = indicesxxxxx.get("tail").index;
         int rightFinOffset = indicesxxxxx.get("right_fin").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         ragdoll.addConnection(leftFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(topFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(tailOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addConnection(rightFinOffset, bodyOffsetxxxxx, true, true);
         ragdoll.addOverlayConnections(true);
      } else if (model instanceof TadpoleModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int tailOffset = indicesxxxxx.get("tail").index;
         ragdoll.addConnection(tailOffset, bodyOffsetxxxxx, true, true);
      } else if (model instanceof FrogModel frog) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int leftLeg = indicesxxxxx.get("left_leg").index;
         int leftFoot = indicesxxxxx.get("left_foot").index;
         int rightLeg = indicesxxxxx.get("right_leg").index;
         int rightFoot = indicesxxxxx.get("right_foot").index;
         int body = indicesxxxxx.get("body").index;
         int bodyCroakingBody = indicesxxxxx.get("croaking_body").index;
         int head = indicesxxxxx.get("head").index;
         int head2 = head + 1;
         int rightEye = indicesxxxxx.get("right_eye").index;
         int leftEye = indicesxxxxx.get("left_eye").index;
         int rightArm = indicesxxxxx.get("right_arm").index;
         int rightHand = indicesxxxxx.get("right_hand").index;
         int tongue = indicesxxxxx.get("tongue").index;
         int leftArm = indicesxxxxx.get("left_arm").index;
         int leftHand = indicesxxxxx.get("left_hand").index;
         ragdoll.addConnection(leftFoot, leftLeg, true, true);
         ragdoll.addConnection(rightFoot, rightLeg, true, true);
         ragdoll.addConnection(leftLeg, body);
         ragdoll.addConnection(rightLeg, body);
         ragdoll.addConnection(head2, body, true);
         ragdoll.addConnection(rightEye, body, true);
         ragdoll.addConnection(leftEye, body, true);
         ragdoll.addConnection(rightArm, body);
         ragdoll.addConnection(leftArm, body);
         ragdoll.addConnection(rightHand, rightArm, true, true);
         ragdoll.addConnection(leftHand, leftArm, true, true);
         ragdoll.addConnection(tongue, body, true, true);
         ragdoll.addConnection(head, body, true, true);
         FrogRenderState frogState = (FrogRenderState)renderState;
         if (frogState.croakAnimationState.isStarted()) {
            ragdoll.addConnection(bodyCroakingBody, body, true);
         }

         RagdollMapper.getCuboids(ragdoll, frog.root(), new RagdollMapper.Counter());
      } else if (model instanceof AllayModel allay) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int head = indicesxxxxx.get("head").index;
         int body = indicesxxxxx.get("body").index;
         int body2 = body + 1;
         int rightArm = indicesxxxxx.get("right_arm").index;
         int leftArm = indicesxxxxx.get("left_arm").index;
         int rightWing = indicesxxxxx.get("right_wing").index;
         int leftWing = indicesxxxxx.get("left_wing").index;
         ragdoll.addConnection(head, body);
         ragdoll.addConnection(rightArm, body);
         ragdoll.addConnection(leftArm, body);
         ragdoll.addConnection(body2, body, true, true);
         ragdoll.addConnection(rightWing, body, true, true);
         ragdoll.addConnection(leftWing, body, true, true);
         RagdollMapper.getCuboids(ragdoll, allay.root(), new RagdollMapper.Counter());
      } else if (model instanceof WardenModel warden) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int bone = 0;
         int rightLeg = indicesxxxxx.get("right_leg").index;
         int leftLeg = indicesxxxxx.get("left_leg").index;
         int body = indicesxxxxx.get("body").index;
         int head = indicesxxxxx.get("head").index;
         int rightTendril = indicesxxxxx.get("right_tendril").index;
         int leftTendril = indicesxxxxx.get("left_tendril").index;
         int rightArm = indicesxxxxx.get("right_arm").index;
         int leftArm = indicesxxxxx.get("left_arm").index;
         int rightRibcage = indicesxxxxx.get("right_ribcage").index;
         int leftRibcage = indicesxxxxx.get("left_ribcage").index;
         ragdoll.addConnection(rightTendril, head, true, true);
         ragdoll.addConnection(leftTendril, head, true, true);
         ragdoll.addConnection(head, body);
         ragdoll.addConnection(rightArm, body);
         ragdoll.addConnection(leftArm, body);
         ragdoll.addConnection(rightLeg, body);
         ragdoll.addConnection(leftLeg, body);
         ragdoll.addConnection(leftRibcage, body, true, true);
         ragdoll.addConnection(rightRibcage, body, true, true);
         ragdoll.removeUnused();
      } else if (model instanceof EnderDragonModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int head = indicesxxxxx.get("head").index;

         for (int ixx = 0; ixx < 4; ixx++) {
            ragdoll.addConnection(indicesxxxxx.get("neck" + Integer.toString(ixx + 1)).index, indicesxxxxx.get("neck" + Integer.toString(ixx)).index);
         }

         int jaw = indicesxxxxx.get("jaw").index;
         int body = indicesxxxxx.get("body").index;
         int leftWing = indicesxxxxx.get("left_wing").index;
         int leftWingTip = indicesxxxxx.get("left_wing_tip").index;
         int leftFrontLeg = indicesxxxxx.get("left_front_leg").index;
         int leftFrontLegTip = indicesxxxxx.get("left_front_leg_tip").index;
         int leftFrontFoot = indicesxxxxx.get("left_front_foot").index;
         int leftHindLeg = indicesxxxxx.get("left_hind_leg").index;
         int leftHindLegTip = indicesxxxxx.get("left_hind_leg_tip").index;
         int leftHindFoot = indicesxxxxx.get("left_hind_foot").index;
         int rightWing = indicesxxxxx.get("right_wing").index;
         int rightWingTip = indicesxxxxx.get("right_wing_tip").index;
         int rightFrontLeg = indicesxxxxx.get("right_front_leg").index;
         int rightFrontLegTip = indicesxxxxx.get("right_front_leg_tip").index;
         int rightFrontFoot = indicesxxxxx.get("right_front_foot").index;
         int rightHindLeg = indicesxxxxx.get("right_hind_leg").index;
         int rightHindLegTip = indicesxxxxx.get("right_hind_leg_tip").index;
         int rightHindFoot = indicesxxxxx.get("right_hind_foot").index;
         ragdoll.addConnection(jaw, head, true);
         ragdoll.addConnection(head, indicesxxxxx.get("neck4").index);
         ragdoll.addConnection(indicesxxxxx.get("neck0").index, body);
         ragdoll.addConnection(rightWing, body);
         ragdoll.addConnection(leftWing, body);
         ragdoll.addConnection(rightWingTip, rightWing);
         ragdoll.addConnection(leftWingTip, leftWing);
         ragdoll.addConnection(rightFrontLeg, body);
         ragdoll.addConnection(rightHindLeg, body);
         ragdoll.addConnection(leftFrontLeg, body);
         ragdoll.addConnection(leftHindLeg, body);
         ragdoll.addConnection(rightFrontLegTip, rightFrontLeg);
         ragdoll.addConnection(rightHindLegTip, rightHindLeg);
         ragdoll.addConnection(leftFrontLegTip, leftFrontLeg);
         ragdoll.addConnection(leftHindLegTip, leftHindLeg);
         ragdoll.addConnection(rightFrontFoot, rightFrontLegTip);
         ragdoll.addConnection(rightHindFoot, rightHindLegTip);
         ragdoll.addConnection(leftFrontFoot, leftFrontLegTip);
         ragdoll.addConnection(leftHindFoot, leftHindLegTip);

         for (int ixx = 0; ixx < 11; ixx++) {
            ragdoll.addConnection(indicesxxxxx.get("tail" + Integer.toString(ixx + 1)).index, indicesxxxxx.get("tail" + Integer.toString(ixx)).index);
         }

         ragdoll.addConnection(indicesxxxxx.get("tail0").index, body);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof SnifferModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int rightFrontLegOffsetxx = indicesxxxxx.get("right_front_leg").index;
         int rightHindLegOffsetxx = indicesxxxxx.get("right_hind_leg").index;
         int rightMidLegOffset = indicesxxxxx.get("right_mid_leg").index;
         int leftHindLegOffsetxx = indicesxxxxx.get("left_hind_leg").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int headOffsetxxxxx = indicesxxxxx.get("head").index;
         int noseOffsetx = indicesxxxxx.get("nose").index;
         int lowerBeakOffset = indicesxxxxx.get("lower_beak").index;
         int rightEarOffsetx = indicesxxxxx.get("right_ear").index;
         int leftEarOffsetx = indicesxxxxx.get("left_ear").index;
         int leftMidLegOffset = indicesxxxxx.get("left_mid_leg").index;
         int leftFrontLegOffsetxx = indicesxxxxx.get("left_front_leg").index;
         ragdoll.addConnection(rightEarOffsetx, headOffsetxxxxx, true);
         ragdoll.addConnection(leftEarOffsetx, headOffsetxxxxx, true);
         ragdoll.addConnection(noseOffsetx, headOffsetxxxxx, true);
         ragdoll.addConnection(lowerBeakOffset, headOffsetxxxxx, true);
         ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightHindLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightMidLegOffset, bodyOffsetxxxxx);
         ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftMidLegOffset, bodyOffsetxxxxx);
         ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      } else if (model instanceof ArmadilloModel) {
         ArmadilloRenderState armadilloState = (ArmadilloRenderState)renderState;
         boolean inShell = armadilloState.isHidingInShell;
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         if (inShell) {
            int cubeOffset = indicesxxxxx.get("cube").index;
            int rightFrontLegOffsetxx = 0;
            int headOffsetxxxxx = 2;
            int rightEarOffsetx = 3;
            int leftEarOffsetx = 4;
            int leftFrontLegOffsetxx = 5;
            ragdoll.addConnection(rightEarOffsetx, cubeOffset, true, true);
            ragdoll.addConnection(leftEarOffsetx, cubeOffset, true, true);
            ragdoll.addConnection(headOffsetxxxxx, cubeOffset, true, true);
            ragdoll.addConnection(rightFrontLegOffsetxx, cubeOffset);
            ragdoll.addConnection(leftFrontLegOffsetxx, cubeOffset);
         } else {
            boolean isBaby = model instanceof BabyArmadilloModel;
            int rightFrontLegOffsetxx = indicesxxxxx.get("right_front_leg").index;
            int leftFrontLegOffsetxx = indicesxxxxx.get("left_front_leg").index;
            int headOffsetxxxxx = indicesxxxxx.get("head_cube").index;
            int rightEarOffsetx = indicesxxxxx.get(isBaby ? "right_ear" : "right_ear_cube").index;
            int leftEarOffsetx = indicesxxxxx.get(isBaby ? "left_ear" : "left_ear_cube").index;
            int rightHindLegOffsetxx = indicesxxxxx.get("right_hind_leg").index;
            int leftHindLegOffsetxx = indicesxxxxx.get("left_hind_leg").index;
            int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
            int tailOffset = indicesxxxxx.get("tail").index;
            ragdoll.addConnection(rightEarOffsetx, headOffsetxxxxx, true);
            ragdoll.addConnection(leftEarOffsetx, headOffsetxxxxx, true);
            ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
            ragdoll.addConnection(rightFrontLegOffsetxx, bodyOffsetxxxxx);
            ragdoll.addConnection(rightHindLegOffsetxx, bodyOffsetxxxxx);
            ragdoll.addConnection(leftHindLegOffsetxx, bodyOffsetxxxxx);
            ragdoll.addConnection(leftFrontLegOffsetxx, bodyOffsetxxxxx);
            ragdoll.addConnection(tailOffset, bodyOffsetxxxxx);
            RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
         }
      } else if (model instanceof CopperGolemModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int headOffsetxxxxx = indicesxxxxx.get("head").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int rightArmOffsetxx = indicesxxxxx.get("right_arm").index;
         int leftArmOffsetxx = indicesxxxxx.get("left_arm").index;
         int rightLegOffsetxxx = indicesxxxxx.get("right_leg").index;
         int leftLegOffsetxxx = indicesxxxxx.get("left_leg").index;
         ragdoll.addConnection(headOffsetxxxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightArmOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftArmOffsetxx, bodyOffsetxxxxx);
         ragdoll.addConnection(rightLegOffsetxxx, bodyOffsetxxxxx);
         ragdoll.addConnection(leftLegOffsetxxx, bodyOffsetxxxxx);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
         ragdoll.addOverlayConnections(true);
      } else if (model instanceof NautilusModel) {
         Map<String, RagdollMapper.ModelPartIndex> indicesxxxxx = RagdollMapper.getModelPartIndices(model);
         int shellOffset = indicesxxxxx.get("shell").index;
         int bodyOffsetxxxxx = indicesxxxxx.get("body").index;
         int lowerMouthOffset = indicesxxxxx.get("lower_mouth").index;
         int upperMouthOffset = indicesxxxxx.get("upper_mouth").index;
         int innerMouthOffset = indicesxxxxx.get("inner_mouth").index;
         ragdoll.addConnection(bodyOffsetxxxxx, shellOffset, true);
         ragdoll.addConnection(lowerMouthOffset, shellOffset, true, true);
         ragdoll.addConnection(upperMouthOffset, shellOffset, true, true);
         ragdoll.addConnection(innerMouthOffset, shellOffset, true, true);
         RagdollMapper.getCuboids(ragdoll, model.root(), new RagdollMapper.Counter());
      }
   }

   @Override
   public void filterCuboidsFromEntities(List<PhysicsEntity> blockifiedEntity, Entity entity, EntityModel model) {
      boolean ragdollsEnabled = RagdollMapper.areRagdollsEnabled(entity);
      if (model instanceof IronGolemModel) {
         while (blockifiedEntity.size() > 8) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof SpiderModel) {
         while (blockifiedEntity.size() > 11) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof AdultStriderModel) {
         while (blockifiedEntity.size() > 9) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (model instanceof WitherBossModel) {
         while (blockifiedEntity.size() > 9) {
            blockifiedEntity.remove(blockifiedEntity.size() - 1);
         }
      } else if (!(model instanceof BabySheepModel)) {
         if (model instanceof SheepModel) {
            while (blockifiedEntity.size() > 6) {
               blockifiedEntity.remove(blockifiedEntity.size() - 1);
            }
         } else if (entity instanceof EnderDragon) {
            while (blockifiedEntity.size() > 65) {
               blockifiedEntity.remove(blockifiedEntity.size() - 1);
            }
         } else if (model instanceof TropicalFishLargeModel
            || model instanceof TropicalFishSmallModel
            || model instanceof SkeletonModel
            || model instanceof HorseModel
            || model instanceof LlamaModel
            || model instanceof DrownedModel
            || model instanceof IllagerModel
            || model instanceof VillagerModel
            || model instanceof EndermanModel) {
            int count = RagdollMapper.countModelParts(entity, model);
            if (!ragdollsEnabled) {
               while (blockifiedEntity.size() > count) {
                  blockifiedEntity.remove(blockifiedEntity.size() - 1);
               }
            }
         } else if (model instanceof PhantomModel) {
            int count = RagdollMapper.countModelParts(entity, model);

            while (blockifiedEntity.size() > count) {
               blockifiedEntity.remove(blockifiedEntity.size() - 1);
            }
         } else if (model instanceof NautilusModel) {
            Iterator<PhysicsEntity> it = blockifiedEntity.iterator();

            while (it.hasNext()) {
               PhysicsEntity physicsEntity = it.next();
               if (physicsEntity.feature instanceof SimpleEquipmentLayer) {
                  it.remove();
               }
            }
         }
      }

      Iterator<PhysicsEntity> it = blockifiedEntity.iterator();

      while (it.hasNext()) {
         PhysicsEntity physicsEntity = it.next();
         if (physicsEntity.feature instanceof HumanoidArmorLayer
            || physicsEntity.feature instanceof CustomHeadLayer
            || physicsEntity.feature instanceof WingsLayer
            || physicsEntity.feature instanceof ItemInHandLayer
            || physicsEntity.feature instanceof ArrowLayer
            || physicsEntity.feature instanceof Deadmau5EarsLayer
            || physicsEntity.feature instanceof CapeLayer
            || physicsEntity.feature instanceof SpinAttackEffectLayer
            || physicsEntity.feature instanceof ParrotOnShoulderLayer
            || physicsEntity.feature instanceof BeeStingerLayer) {
            it.remove();
         }
      }
   }
}
