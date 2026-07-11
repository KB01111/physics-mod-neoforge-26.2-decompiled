package net.diebuddies.mixins;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.wind.WeatherDomain;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import physx.common.PxVec3;
import physx.physics.PxRigidDynamic;

@Mixin({ClientLevel.class})
public class MixinClientLevel {
   @Unique
   private final Long2ObjectMap<Holder<Biome>> physicsmod$cachedBiomes = new Long2ObjectOpenHashMap(169);

   @Inject(
      at = {@At("HEAD")},
      method = {"addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"},
      cancellable = true
   )
   private void physicsmod$addParticle(ParticleOptions particleOptions, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info) {
      Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
      if (ConfigClient.serverBlockPhysicsParticles
         && particleOptions instanceof BlockParticleOption blockParticle
         && camera.isInitialized()
         && camera.position().distanceToSqr(x, y, z) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
         BlockState block = blockParticle.getState();
         boolean isBarrier = blockParticle.getType() == ParticleTypes.BLOCK_MARKER;
         boolean isLeaf = block.getBlock() instanceof LeavesBlock || block.is(BlockTags.LEAVES);
         if (isBarrier || isLeaf) {
            return;
         }

         ParticleSpawner.spawnServerBlockPhysicsParticle(
            blockParticle.getState(),
            (ClientLevel)(Object)this,
            x + (double)(Math.random() * 0.1F) - 0.05F,
            y + (double)(Math.random() * 0.1F) - 0.05F,
            z + (double)(Math.random() * 0.1F) - 0.05F,
            vx,
            vy,
            vz
         );
         info.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"addEntity"}
   )
   private void physicsmod$addEntity(Entity entity, CallbackInfo info) {
      if (entity instanceof FallingBlockEntity fallingBlock) {
         PhysicsMod mod = PhysicsMod.getInstance((ClientLevel)(Object)this);
         mod.fallingBlocks.add(fallingBlock.getStartPos());
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"addDestroyBlockEffect"},
      cancellable = true
   )
   private void physicsmod$destroyParticles(BlockPos pos, BlockState state, CallbackInfo info) {
      BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
      if (blockSetting.getType() == BlockPhysicsType.PARTICLES && !state.isAir()) {
         info.cancel();
      } else if (!ConfigClient.minecraftBlockBreakParticles) {
         info.cancel();
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"addBreakingBlockEffect"},
      cancellable = true
   )
   private void physicsmod$crackParticles(BlockPos blockPos, Direction direction, CallbackInfo info) {
      ClientLevel level = (ClientLevel)(Object)this;
      BlockState blockState = level.getBlockState(blockPos);
      if (ConfigClient.crackPhysicsParticles && blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.shouldSpawnTerrainParticles()) {
         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         int blockX = blockPos.getX();
         int blockY = blockPos.getY();
         int blockZ = blockPos.getZ();
         if (camera.isInitialized()
            && camera.position().distanceToSqr((double)blockX, (double)blockY, (double)blockZ)
               < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            float offset = 0.1F;
            VoxelShape shape = blockState.getShape(level, blockPos);
            if (shape.isEmpty()) {
               return;
            }

            AABB aabb = shape.bounds();
            RandomSource random = level.getRandom();
            double posX = (double)blockX + random.nextDouble() * (aabb.maxX - aabb.minX - (double)(offset * 2.0F)) + (double)offset + aabb.minX;
            double posY = (double)blockY + random.nextDouble() * (aabb.maxY - aabb.minY - (double)(offset * 2.0F)) + (double)offset + aabb.minY;
            double posZ = (double)blockZ + random.nextDouble() * (aabb.maxZ - aabb.minZ - (double)(offset * 2.0F)) + (double)offset + aabb.minZ;
            if (direction == Direction.DOWN) {
               posY = (double)blockY + aabb.minY - (double)offset;
            }

            if (direction == Direction.UP) {
               posY = (double)blockY + aabb.maxY + (double)offset;
            }

            if (direction == Direction.NORTH) {
               posZ = (double)blockZ + aabb.minZ - (double)offset;
            }

            if (direction == Direction.SOUTH) {
               posZ = (double)blockZ + aabb.maxZ + (double)offset;
            }

            if (direction == Direction.WEST) {
               posX = (double)blockX + aabb.minX - (double)offset;
            }

            if (direction == Direction.EAST) {
               posX = (double)blockX + aabb.maxX + (double)offset;
            }

            Vec3i normal = direction.getUnitVec3i();
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(blockState).sprite();
            PhysicsMod mod = PhysicsMod.getInstance(level);
            PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
            entity.getTransformation().translation(posX, posY, posZ).scale((double)(Math.random() * 0.06F + 0.04F));
            Model model = entity.models.get(0);
            model.texture = sprite;
            model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
            model.backfaceCulling = true;
            model.mesh = PhysicsMod.brokenBlock.get(0);
            int color = -1;
            BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(blockState, 0);
            if (tintSource != null) {
               color = tintSource.colorInWorld(blockState, level, blockPos);
            }

            if (color == -1) {
               color = -1;
            }

            entity.setColor(color);
            if (blockState.getBlock() == Blocks.CAULDRON || blockState.getBlock() == Blocks.GRASS_BLOCK) {
               entity.setColor(-1);
            }

            IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
            mod.physicsWorld.queue(() -> {
               if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                  float strength = 2.0F;
                  Vector3f speed = new Vector3f((float)normal.getX() * 0.3F, (float)normal.getY() * 0.3F, (float)normal.getZ() * 0.3F);
                  speed.x = speed.x + (Math.random() - 0.5F) * 0.3F;
                  speed.y = speed.y + (Math.random() - 0.5F) * 0.3F;
                  speed.z = speed.z + (Math.random() - 0.5F) * 0.3F;
                  speed.normalize();
                  MemoryStack mem = MemoryStack.stackPush();

                  try {
                     PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
                     rigidBody.setLinearVelocity(velocity);
                  } catch (Throwable var9x) {
                     if (mem != null) {
                        try {
                           mem.close();
                        } catch (Throwable var8x) {
                           var9x.addSuppressed(var8x);
                        }
                     }

                     throw var9x;
                  }

                  if (mem != null) {
                     mem.close();
                  }
               }
            });
            float uo = Math.random() * 3.0F;
            float vo = Math.random() * 3.0F;
            Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0F), sprite.getU((uo + 1.0F) / 4.0F), sprite.getV(vo / 4.0F), sprite.getV((vo + 1.0F) / 4.0F));
            float xScale = customUVs.y - customUVs.x;
            float yScale = customUVs.w - customUVs.z;
            model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0F).scale(xScale, yScale, 0.0F);
            info.cancel();
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tickWeatherEffects"},
      cancellable = true
   )
   private void physicsmod$spawnWeatherParticles(CallbackInfo info) {
      if (ConfigClient.weatherParticles) {
         ClientLevel level = (ClientLevel)(Object)this;
         float precipitationAmount = level.getRainLevel(1.0F);
         if (!(precipitationAmount <= 0.0F)) {
            Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
            int checkRange = 6;
            int camX = Mth.floor(camera.position().x);
            int camY = Mth.floor(camera.position().y);
            int camZ = Mth.floor(camera.position().z);
            int spawnableRain = 0;
            int particlesPerBlock = 0;
            particlesPerBlock += (int)(precipitationAmount * (float)ConfigClient.weatherRainParticleAmount);
            particlesPerBlock += (int)(level.getThunderLevel(1.0F) * (float)ConfigClient.weatherThunderParticleAmount);
            MutableBlockPos mutableBlockPos = new MutableBlockPos();
            this.physicsmod$cachedBiomes.clear();
            int seaLevel = level.getSeaLevel();

            for (int zo = camZ - checkRange; zo <= camZ + checkRange; zo++) {
               for (int xo = camX - checkRange; xo <= camX + checkRange; xo++) {
                  int diffX = xo - camX;
                  int diffZ = zo - camZ;
                  if (diffX * diffX + diffZ * diffZ <= 36) {
                     mutableBlockPos.set(xo, camY, zo);
                     Holder<Biome> biomeHolder = level.getBiome(mutableBlockPos);
                     Biome biome = (Biome)biomeHolder.value();
                     this.physicsmod$cachedBiomes.put(mutableBlockPos.asLong(), biomeHolder);
                     if (biome.getPrecipitationAt(mutableBlockPos, seaLevel) != Precipitation.NONE || biomeHolder.is(Biomes.DESERT)) {
                        int rainToThisHeight = level.getHeight(Types.MOTION_BLOCKING, xo, zo);
                        int rainBottom = camY - checkRange;
                        int rainTop = camY + checkRange;
                        if (rainBottom < rainToThisHeight) {
                           rainBottom = rainToThisHeight;
                        }

                        if (rainTop < rainToThisHeight) {
                           rainTop = rainToThisHeight;
                        }

                        if (rainBottom != rainTop) {
                           int height = rainTop - rainBottom;
                           spawnableRain += height * particlesPerBlock;
                        }
                     }
                  }
               }
            }

            int range = 14;
            PhysicsWorld world = PhysicsMod.getInstance(level).getPhysicsWorld();
            WeatherDomain weatherDomain = world.getWeatherDomain();
            ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
            Map<Identifier, ParticleProvider<?>> provider = ((MixinParticleResourcesAccessor)Minecraft.getInstance().particleEngine.resourceManager)
               .getParticleProviders();
            ParticleProvider<ParticleOptions> rainProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.RAIN_RESOURCE);
            ParticleProvider<ParticleOptions> snowProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.SNOW_RESOURCE);
            ParticleProvider<ParticleOptions> dustProvider = (ParticleProvider<ParticleOptions>)provider.get(WeatherParticlesRegistry.DUST_RESOURCE);

            while (WeatherEffects.aliveParticles < spawnableRain) {
               double xox = (double)Math.random() * 2.0 - 1.0;
               double yo = (double)Math.random() * 2.0 - 1.0;
               double zo = (double)Math.random() * 2.0 - 1.0;
               double distance = xox * xox + yo * yo + zo * zo;
               if (!(distance > 1.0)) {
                  int x = Mth.floor(camera.position().x + xox * (double)range);
                  int y = Mth.floor(camera.position().y + yo * (double)range);
                  int z = Mth.floor(camera.position().z + zo * (double)range);
                  mutableBlockPos.set(x, camY, z);
                  long blockPosUnique = mutableBlockPos.asLong();
                  Holder<Biome> biomeHolder = (Holder<Biome>)(Object)this.physicsmod$cachedBiomes
                     .computeIfAbsent(blockPosUnique, key -> level.getBiome(mutableBlockPos));
                  Biome biome = (Biome)biomeHolder.value();
                  if (biome.getPrecipitationAt(mutableBlockPos, seaLevel) != Precipitation.NONE || biomeHolder.is(Biomes.DESERT)) {
                     int rainToThisHeightx = level.getHeight(Types.MOTION_BLOCKING, x, z);
                     if (y >= rainToThisHeightx) {
                        int rainBottomx = y - range;
                        int rainTopx = y + range;
                        if (rainBottomx < rainToThisHeightx) {
                           rainBottomx = rainToThisHeightx;
                        }

                        if (rainTopx < rainToThisHeightx) {
                           rainTopx = rainToThisHeightx;
                        }

                        if (rainBottomx != rainTopx) {
                           mutableBlockPos.set(x, rainBottomx, z);
                           level.getBlockState(mutableBlockPos).isAir();
                           Vector3f windDirection = weatherDomain.getWindDirection(x, y, z);
                           float forceStrength = weatherDomain.getWindStrengthFast();
                           if (biomeHolder.is(Biomes.DESERT)) {
                              double strength = 0.3;
                              double baseVX = (double)Math.random() * strength - strength * 0.5;
                              double baseVY = -0.05;
                              double baseVZ = (double)Math.random() * strength - strength * 0.5;
                              baseVX += (double)(windDirection.x * forceStrength) * 2.0;
                              baseVZ += (double)(windDirection.z * forceStrength) * 2.0;
                              particleEngine.add(
                                 dustProvider.createParticle(
                                    WeatherEffects.PHYSICS_DUST,
                                    level,
                                    (double)((float)x + Math.random()),
                                    (double)((float)y + Math.random()),
                                    (double)((float)z + Math.random()),
                                    baseVX,
                                    baseVY,
                                    baseVZ,
                                    RandomSource.createThreadLocalInstance()
                                 )
                              );
                           } else if (biome.warmEnoughToRain(mutableBlockPos, seaLevel)) {
                              double strength = 0.13;
                              double baseVX = (double)Math.random() * strength - strength * 0.5;
                              double baseVY = -0.6;
                              double baseVZ = (double)Math.random() * strength - strength * 0.5;
                              baseVX += (double)(windDirection.x * forceStrength);
                              baseVZ += (double)(windDirection.z * forceStrength);
                              particleEngine.add(
                                 rainProvider.createParticle(
                                    WeatherEffects.PHYSICS_RAIN,
                                    level,
                                    (double)((float)x + Math.random()),
                                    (double)((float)y + Math.random()),
                                    (double)((float)z + Math.random()),
                                    baseVX,
                                    baseVY,
                                    baseVZ,
                                    RandomSource.createThreadLocalInstance()
                                 )
                              );
                           } else {
                              double strength = 0.2;
                              double baseVX = (double)Math.random() * strength - strength * 0.5;
                              double baseVY = -0.05;
                              double baseVZ = (double)Math.random() * strength - strength * 0.5;
                              baseVX += (double)(windDirection.x * forceStrength);
                              baseVZ += (double)(windDirection.z * forceStrength);
                              particleEngine.add(
                                 snowProvider.createParticle(
                                    WeatherEffects.PHYSICS_SNOW,
                                    level,
                                    (double)((float)x + Math.random()),
                                    (double)((float)y + Math.random()),
                                    (double)((float)z + Math.random()),
                                    baseVX,
                                    baseVY,
                                    baseVZ,
                                    RandomSource.createThreadLocalInstance()
                                 )
                              );
                           }

                           WeatherEffects.aliveParticles++;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
