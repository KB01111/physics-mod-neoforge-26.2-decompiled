package net.diebuddies.render;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.AABBf;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.JsonUnbakedModelHolder;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidDynamic;

public class PhysicsUpdater {
   private static final Map<Block, Direction> textureSides = new Object2ObjectOpenHashMap();
   private Matrix4d tmpMatrix = new Matrix4d();

   public void updatePhysics(PhysicsMod mod, ClientLevel level, Vec3 cameraPos, PhysicsWorld physics) {
      if (mod.updatedLightBlocks.size() > 0) {
         LongIterator it = mod.updatedLightBlocks.iterator();
         if (!StarterClient.disableLightingCache) {
            for (IRigidBody body : physics.getBodies()) {
               PhysicsRenderable entity = body.getEntity();
               long cached = entity.getCachedBrightnessPos();
               if (cached != Long.MAX_VALUE && mod.updatedLightBlocks.contains(cached)) {
                  entity.invalidateBrightness();
               }
            }

            physics.getSmokeDomain().invalidateBrightness(mod.updatedLightBlocks);

            for (Liquid liquid : physics.getLiquids()) {
               liquid.invalidateBrightness(mod.updatedLightBlocks);
            }
         }

         WeatherEffects.invalidateLight = true;
         mod.updatedLightBlocks.clear();
      }

      mod.removeUpdates.clear();

      BlockUpdate blockUpdate;
      while ((blockUpdate = mod.updateQueue.poll()) != null) {
         if (mod.fallingBlocks.isEmpty() || !mod.fallingBlocks.contains(blockUpdate.pos)) {
            mod.removeUpdates.add(blockUpdate);
         }
      }

      mod.fallingBlocks.clear();
      List<PhysicsEntity> newParts = new ObjectArrayList();
      List<PhysicsEntity> newPartsVoxel = new ObjectArrayList();
      double maxActivationDistanceSqr = ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange;
      BlockEntityRenderDispatcher berd = Minecraft.getInstance().getBlockEntityRenderDispatcher();

      for (BlockUpdate bu : mod.removeUpdates) {
         BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer;
         if (bu.blockEntity != null && (renderer = berd.getRenderer(bu.blockEntity)) != null) {
            BlockSetting blockSetting = ConfigBlocks.getBlockSetting(bu.state.getBlock());
            boolean forceBlocky = false;
            if (bu.blockEntity instanceof SignBlockEntity) {
               forceBlocky = true;
            }

            if (cameraPos.distanceToSqr((double)bu.pos.getX(), (double)bu.pos.getY(), (double)bu.pos.getZ()) < maxActivationDistanceSqr
               || ConfigClient.blockPhysicsRange > 319.999) {
               if (blockSetting.getType() == BlockPhysicsType.FRACTURED
                  || blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL
                  || blockSetting.getType() == BlockPhysicsType.BLOCKY) {
                  PhysicsEntity entity = mod.renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, renderer, bu.blockEntity, bu.state, bu.pos);
                  if (entity != null) {
                     physics.addBlockParticle(entity).applyRandomSpawnForces();
                  }

                  if (blockSetting.getType() != BlockPhysicsType.BLOCKY && !forceBlocky) {
                     if (blockSetting.getType() == BlockPhysicsType.FRACTURED_VOXEL) {
                        newPartsVoxel.addAll(this.getBlockData(physics, bu, bu.level));
                     } else if (blockSetting.getType() == BlockPhysicsType.FRACTURED) {
                        newParts.addAll(this.getBlockData(physics, bu, bu.level));
                     }
                  } else {
                     PhysicsEntity blocky = mod.renderBlockIntoEntity(bu.level, PhysicsEntity.Type.BLOCK, bu.state, bu.pos, false);
                     if (blocky != null) {
                        physics.addBlockParticle(blocky).applyRandomSpawnForces();
                     }
                  }
               } else if (blockSetting.getType() == BlockPhysicsType.PARTICLES) {
                  double percent = this.calculateChance(physics.getBodies().size());
                  if (mod.removeUpdates.size() > 8) {
                     percent = Math.min(percent, 0.1);
                  } else if (mod.removeUpdates.size() == 1) {
                     percent = Math.max(0.1, percent);
                  }

                  this.spawnBlockBreakParticles(bu.level, blockSetting, bu.state, bu.pos, percent);
               }
            }
         } else if (bu.state.getBlock() != Blocks.TNT && bu.state.getBlock() != Blocks.PISTON_HEAD && bu.state.getRenderShape() != RenderShape.INVISIBLE) {
            BlockSetting blockSettingx = ConfigBlocks.getBlockSetting(bu.state.getBlock());
            if (cameraPos.distanceToSqr((double)bu.pos.getX(), (double)bu.pos.getY(), (double)bu.pos.getZ()) < maxActivationDistanceSqr
               || ConfigClient.blockPhysicsRange > 319.999) {
               if (blockSettingx.getType() == BlockPhysicsType.FRACTURED) {
                  newParts.addAll(this.getBlockData(physics, bu, bu.level));
               } else if (blockSettingx.getType() == BlockPhysicsType.FRACTURED_VOXEL) {
                  newPartsVoxel.addAll(this.getBlockData(physics, bu, bu.level));
               } else if (blockSettingx.getType() == BlockPhysicsType.BLOCKY) {
                  PhysicsEntity entityx = mod.renderBlockIntoEntity(bu.level, PhysicsEntity.Type.BLOCK, bu.state, bu.pos, false);
                  if (entityx != null) {
                     physics.addBlockParticle(entityx).applyRandomSpawnForces();
                  }
               } else if (blockSettingx.getType() == BlockPhysicsType.PARTICLES) {
                  double percent = this.calculateChance(physics.getBodies().size());
                  if (mod.removeUpdates.size() > 8) {
                     percent = Math.min(percent, 0.1);
                  } else if (mod.removeUpdates.size() == 1) {
                     percent = Math.max(0.1, percent);
                  }

                  this.spawnBlockBreakParticles(bu.level, blockSettingx, bu.state, bu.pos, percent);
               }
            }
         }
      }

      double chance = this.calculateChance(physics.getBodies().size());
      int qsize = newParts.size() + newPartsVoxel.size();
      if (qsize == 1) {
         chance = 1.0;
      } else if (qsize > 10) {
         chance = Math.min(chance, 0.3);
      }

      this.addPhysicsBlocks(newParts, chance, physics, false);
      this.addPhysicsBlocks(newPartsVoxel, chance, physics, true);

      while (!mod.entityBlocks.isEmpty()) {
         PhysicsEntity particle = mod.entityBlocks.poll();
         if (!particle.noVolume) {
            physics.addBlockParticle(particle).applyRandomSpawnForces();
         }
      }

      while (!mod.ragdolls.isEmpty()) {
         Ragdoll ragdoll = mod.ragdolls.poll();
         physics.addRagdoll(ragdoll);
      }

      while (!mod.blockUpdates.isEmpty()) {
         BlockPos pos = mod.blockUpdates.poll();
         physics.queue(() -> physics.blockUpdate(pos));
      }

      while (!mod.explosions.isEmpty()) {
         physics.applyExplosion(mod.explosions.poll());
      }
   }

   private void addPhysicsBlocks(List<PhysicsEntity> newParts, double chance, PhysicsWorld physics, boolean voxel) {
      for (PhysicsEntity particle : newParts) {
         double volume = (double)particle.getVolume();
         List<Mesh> mesh = PhysicsMod.brokenBlock;
         List<Mesh> physicsMesh = null;
         if ((double)net.diebuddies.math.Math.random() < chance) {
            if (!(chance < 0.5) && !((double)physics.getBodies().size() > (double)ConfigClient.maxPhysicsObjects * 0.4)) {
               int index = this.randomFractureIndex(PhysicsMod.brokenBlocksLots.size());
               if (voxel) {
                  mesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLotsVoxel, index, PhysicsMod.brokenBlock);
                  physicsMesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLotsVoxelPhysics, index, null);
               } else {
                  mesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLots, index, PhysicsMod.brokenBlock);
               }
            } else {
               int index = this.randomFractureIndex(PhysicsMod.brokenBlocksLittle.size());
               if (voxel) {
                  mesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLittleVoxel, index, PhysicsMod.brokenBlock);
                  physicsMesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLittleVoxelPhysics, index, null);
               } else {
                  mesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLittle, index, PhysicsMod.brokenBlock);
               }
            }

            if (volume < 0.05) {
               mesh = PhysicsMod.brokenBlock;
               physicsMesh = null;
            } else if (volume < 0.9) {
               int indexx = this.randomFractureIndex(PhysicsMod.brokenBlocksLittle.size());
               if (voxel) {
                  mesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLittleVoxel, indexx, PhysicsMod.brokenBlock);
                  physicsMesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLittleVoxelPhysics, indexx, null);
               } else {
                  mesh = this.getFractureMeshes(PhysicsMod.brokenBlocksLittle, indexx, PhysicsMod.brokenBlock);
               }
            }

            physics.addBlockParticle(mesh, physicsMesh, particle);
         }
      }
   }

   private int randomFractureIndex(int size) {
      return size <= 0 ? 0 : net.diebuddies.math.Math.randomInt(size);
   }

   private List<Mesh> getFractureMeshes(List<List<Mesh>> meshes, int index, List<Mesh> fallback) {
      if (meshes.isEmpty()) {
         return fallback;
      }

      int safeIndex = Math.min(index, meshes.size() - 1);
      List<Mesh> selected = meshes.get(safeIndex);
      return selected != null && !selected.isEmpty() ? selected : fallback;
   }

   private List<PhysicsEntity> getBlockData(PhysicsWorld physics, BlockUpdate update, ClientLevel level) {
      List<PhysicsEntity> particles = new ObjectArrayList();
      BlockPos pos = update.pos;
      BlockState state = update.state;
      BlockStateModel bakedModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
      List<BlockStateModelPart> parts = new ObjectArrayList();
      bakedModel.collectParts(new XoroshiroRandomSource(42L), parts);

      for (BlockStateModelPart part : parts) {
         JsonUnbakedModelHolder unbakedModel = PhysicsMod.loadedModels.get(part);
         if (state.getBlock() == Blocks.BROWN_MUSHROOM_BLOCK || state.getBlock() == Blocks.RED_MUSHROOM_BLOCK || state.getBlock() == Blocks.MUSHROOM_STEM) {
            unbakedModel = null;
         }

         if (unbakedModel != null) {
            this.addParticles(particles, unbakedModel, level, update);
            if (particles.size() == 0 && bakedModel != null) {
               PhysicsEntity entity = PhysicsMod.getInstance(level)
                  .renderBlockIntoEntity(PhysicsEntity.Type.BLOCK, bakedModel, update.state, update.pos, false);
               if (entity != null) {
                  physics.addBlockParticle(entity).applyRandomSpawnForces();
               }
            }
         } else {
            PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.BLOCK, update.state);
            Minecraft minecraft = Minecraft.getInstance();
            ModelManager ren = minecraft.getModelManager();
            BlockStateModel model = ren.getBlockStateModelSet().get(state);
            Vec3 blockOffset = update.state.getOffset(update.pos);
            particle.getTransformation()
               .translation((double)pos.getX() + 0.5 + blockOffset.x, (double)pos.getY() + 0.5 + blockOffset.y, (double)pos.getZ() + 0.5 + blockOffset.z);
            BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
            particle.getTransformation().scale((double)((float)blockSetting.getScale()));
            particle.models.get(0).texture = model.particleMaterial().sprite();
            particle.models.get(0).textureID = Minecraft.getInstance()
               .getTextureManager()
               .getTexture(model.particleMaterial().sprite().atlasLocation())
               .getTextureView();
            BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(update.state, 0);
            int color = -1;
            if (tintSource != null) {
               color = tintSource.colorInWorld(update.state, level, update.pos);
            }

            if (color == -1) {
               color = -1;
            }

            particle.setColor(color);
            if (update.state.getBlock() == Blocks.CAULDRON || update.state.getBlock() == Blocks.GRASS_BLOCK) {
               particle.setColor(-1);
            }

            particles.add(particle);
         }
      }

      return particles;
   }

   public List<CuboidModelElement> getBlockModelElements(ResolvedModel model) {
      return (List<CuboidModelElement>)(model.getTopGeometry() instanceof UnbakedCuboidGeometry simpleGeometry
         ? simpleGeometry.elements()
         : new ObjectArrayList());
   }

   private void addParticles(List<PhysicsEntity> particles, JsonUnbakedModelHolder unbakedModel, ClientLevel level, BlockUpdate update) {
      BlockState state = update.state;
      BlockPos pos = update.pos;
      Vec3 blockOffset = state.getOffset(pos);
      Direction textureDirection = textureSides.getOrDefault(state.getBlock(), Direction.DOWN);

      for (CuboidModelElement element : this.getBlockModelElements(unbakedModel.model)) {
         PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.BLOCK, state);
         if (element.from().x() != 0.0F
            || element.from().y() != 0.0F
            || element.from().z() != 0.0F
            || element.to().x() != 16.0F
            || element.to().y() != 16.0F
            || element.to().z() != 16.0F) {
            particle.rescale = new AABBf(
               new Vector3f(element.from().x() / 16.0F, element.from().y() / 16.0F, element.from().z() / 16.0F),
               new Vector3f(element.to().x() / 16.0F, element.to().y() / 16.0F, element.to().z() / 16.0F)
            );
         }

         particle.shade(element.shade());
         Minecraft minecraft = Minecraft.getInstance();
         ModelManager ren = minecraft.getModelManager();
         BlockStateModel model = ren.getBlockStateModelSet().get(state);
         Matrix4f m = unbakedModel.transformation;
         Matrix4d modelTransformation = new Matrix4d();
         modelTransformation.set(m);
         Matrix4d transformation = new Matrix4d();
         transformation.mul(modelTransformation);
         if (element.rotation() != null) {
            transformation.translate(
               (double)element.rotation().origin().x() - 0.5, (double)element.rotation().origin().y() - 0.5, (double)element.rotation().origin().z() - 0.5
            );
            transformation.mul(this.tmpMatrix.set(element.rotation().transform()));
            transformation.translate(
               -((double)element.rotation().origin().x() - 0.5),
               -((double)element.rotation().origin().y() - 0.5),
               -((double)element.rotation().origin().z() - 0.5)
            );
         }

         transformation.m30(transformation.m30() + (double)pos.getX() + 0.5 + blockOffset.x);
         transformation.m31(transformation.m31() + (double)pos.getY() + 0.5 + blockOffset.y);
         transformation.m32(transformation.m32() + (double)pos.getZ() + 0.5 + blockOffset.z);
         particle.getTransformation().set(transformation);
         BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
         particle.getTransformation().scale((double)((float)blockSetting.getScale()));
         particle.models.get(0).texture = model.particleMaterial().sprite();
         if (element.faces().values().size() > 0) {
            ResolvedModel unbaked = unbakedModel.model;
            CuboidFace face = (CuboidFace)element.faces().get(textureDirection);
            if (face == null && element.faces().size() > 0) {
               face = (CuboidFace)element.faces().values().iterator().next();
            }

            TextureSlots textureSlots = unbaked.getTopTextureSlots();
            Material material = textureSlots.getMaterial(face.texture());
            if (material != null) {
               TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(material.sprite());
               particle.models.get(0).texture = sprite;
               if (face.texture().startsWith("#overlay")) {
                  continue;
               }
            }
         }

         if (particle.models.get(0).texture != null) {
            particle.models.get(0).textureID = Minecraft.getInstance()
               .getTextureManager()
               .getTexture(particle.models.get(0).texture.atlasLocation())
               .getTextureView();
         }

         BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(update.state, 0);
         int color = -1;
         if (tintSource != null) {
            color = tintSource.colorInWorld(state, level, pos);
         }

         if (color == -1) {
            color = -1;
         }

         particle.setColor(color);
         if (update.state.getBlock() == Blocks.CAULDRON || update.state.getBlock() == Blocks.GRASS_BLOCK) {
            particle.setColor(-1);
         }

         particles.add(particle);
      }
   }

   private double calculateChance(int count) {
      double chance = 1.0;
      if ((double)count > (double)ConfigClient.maxPhysicsObjects * 0.4) {
         chance = 0.3;
         if ((double)count > (double)ConfigClient.maxPhysicsObjects * 0.7) {
            chance = 0.05;
         }
      }

      if (count > ConfigClient.maxPhysicsObjects) {
         chance = 0.0;
      }

      return chance;
   }

   private void spawnBlockBreakParticles(ClientLevel level, BlockSetting setting, BlockState state, BlockPos pos, double spawnRate) {
      VoxelShape voxelShape = state.getShape(level, pos);
      voxelShape.forAllBoxes(
         (minX, minY, minZ, maxX, maxY, maxZ) -> {
            double width = Math.min(1.0, maxX - minX);
            double height = Math.min(1.0, maxY - minY);
            double depth = Math.min(1.0, maxZ - minZ);
            int stepX = Math.max(2, Mth.ceil(width / 0.25));
            int stepY = Math.max(2, Mth.ceil(height / 0.25));
            int stepZ = Math.max(2, Mth.ceil(depth / 0.25));

            for (int xp = 0; xp < stepX; xp++) {
               for (int yp = 0; yp < stepY; yp++) {
                  for (int zp = 0; zp < stepZ; zp++) {
                     if (!((double)net.diebuddies.math.Math.random() > spawnRate)) {
                        double xSpeed = ((double)xp + 0.5) / (double)stepX;
                        double ySpeed = ((double)yp + 0.5) / (double)stepY;
                        double zSpeed = ((double)zp + 0.5) / (double)stepZ;
                        double xPos = xSpeed * width + minX;
                        double yPos = ySpeed * height + minY;
                        double zPos = zSpeed * depth + minZ;
                        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(state).sprite();
                        PhysicsMod mod = PhysicsMod.getInstance(level);
                        PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.BLOCK, state);
                        entity.getTransformation()
                           .translation((double)pos.getX() + xPos, (double)pos.getY() + yPos, (double)pos.getZ() + zPos)
                           .scale(((double)net.diebuddies.math.Math.random() * 0.06 + 0.07) * setting.getScale());
                        Model model = entity.models.get(0);
                        model.texture = sprite;
                        model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
                        model.backfaceCulling = true;
                        model.mesh = PhysicsMod.brokenBlock.get(0);
                        BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
                        int color = -1;
                        if (tintSource != null) {
                           color = tintSource.colorAsTerrainParticle(state, level, pos);
                        }

                        entity.setColor(color);
                        if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
                           entity.setColor(-1);
                        }

                        IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
                        mod.physicsWorld.queue(() -> {
                           if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                              float strength = 3.0F;
                              Vector3f speed = new Vector3f(0.0F, 0.2F, 0.0F);
                              speed.x = speed.x + (net.diebuddies.math.Math.random() - 0.5F) * 0.4F;
                              speed.y = speed.y + (net.diebuddies.math.Math.random() - 0.5F) * 0.4F;
                              speed.z = speed.z + (net.diebuddies.math.Math.random() - 0.5F) * 0.4F;
                              speed.normalize();
                              MemoryStack mem = MemoryStack.stackPush();

                              try {
                                 PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                                 rigidBody.setLinearVelocity(velocity);
                              } catch (Throwable var8x) {
                                 if (mem != null) {
                                    try {
                                       mem.close();
                                    } catch (Throwable var7x) {
                                       var8x.addSuppressed(var7x);
                                    }
                                 }

                                 throw var8x;
                              }

                              if (mem != null) {
                                 mem.close();
                              }
                           }
                        });
                        float uo = net.diebuddies.math.Math.random() * 3.0F;
                        float vo = net.diebuddies.math.Math.random() * 3.0F;
                        Vector4f customUVs = new Vector4f(
                           sprite.getU(uo / 4.0F), sprite.getU((uo + 1.0F) / 4.0F), sprite.getV(vo / 4.0F), sprite.getV((vo + 1.0F) / 4.0F)
                        );
                        float xScale = customUVs.y - customUVs.x;
                        float yScale = customUVs.w - customUVs.z;
                        model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0F).scale(xScale, yScale, 0.0F);
                     }
                  }
               }
            }
         }
      );
   }

   static {
      textureSides.put(Blocks.ACACIA_LOG, Direction.WEST);
      textureSides.put(Blocks.OAK_LOG, Direction.WEST);
      textureSides.put(Blocks.BIRCH_LOG, Direction.WEST);
      textureSides.put(Blocks.JUNGLE_LOG, Direction.WEST);
      textureSides.put(Blocks.DARK_OAK_LOG, Direction.WEST);
      textureSides.put(Blocks.SPRUCE_LOG, Direction.WEST);
      textureSides.put(Blocks.CHERRY_LOG, Direction.WEST);
      textureSides.put(Blocks.MANGROVE_LOG, Direction.WEST);
      textureSides.put(Blocks.PALE_OAK_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_ACACIA_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_OAK_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_BIRCH_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_JUNGLE_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_DARK_OAK_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_SPRUCE_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_CHERRY_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_MANGROVE_LOG, Direction.WEST);
      textureSides.put(Blocks.STRIPPED_PALE_OAK_LOG, Direction.WEST);
   }
}
