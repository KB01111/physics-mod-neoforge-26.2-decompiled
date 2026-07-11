package net.diebuddies.minecraft;

import net.diebuddies.math.Math;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidDynamic;

public class ParticleSpawner {
   public static void spawnEatingPhysicsParticle(ItemStack itemStack, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
      ItemStackRenderState scratchRenderState = new ItemStackRenderState();
      Minecraft.getInstance().getItemModelResolver().updateForTopItem(scratchRenderState, itemStack, ItemDisplayContext.GROUND, level, null, 0);
      TextureAtlasSprite sprite = scratchRenderState.pickParticleMaterial(RandomSource.create()).sprite();
      PhysicsMod mod = PhysicsMod.getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
      entity.getTransformation().translation(x, y, z).scale((double)(Math.random() * 0.06F + 0.04F));
      Model model = entity.models.get(0);
      model.texture = sprite;
      model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
      model.backfaceCulling = true;
      model.mesh = PhysicsMod.brokenBlock.get(0);
      entity.physicsGroup = 16;
      entity.physicsMask = 19;
      IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
      mod.physicsWorld.queue(() -> {
         if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
            MemoryStack mem = MemoryStack.stackPush();

            try {
               PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)vx, (float)vy * 1.3F + 1.0F, (float)vz);
               rigidBody.setLinearVelocity(velocity);
               PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0F, Math.random() * 4.0F, Math.random() * 4.0F);
               rigidBody.setAngularVelocity(avelocity);
            } catch (Throwable var12x) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var11) {
                     var12x.addSuppressed(var11);
                  }
               }

               throw var12x;
            }

            if (mem != null) {
               mem.close();
            }
         }
      });
      float uo = Math.random() * 3.0F;
      float vo = Math.random() * 3.0F;
      Vector4f customUVs = new Vector4f(sprite.getU(uo / 4.0F), sprite.getU(uo / 4.0F), sprite.getV(vo / 4.0F), sprite.getV(vo / 4.0F));
      float xScale = customUVs.y - customUVs.x;
      float yScale = customUVs.w - customUVs.z;
      model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0F).scale(xScale, yScale, 0.0F);
   }

   public static void spawnSprintingPhysicsParticle(BlockState state, BlockPos pos, ClientLevel level, double x, double y, double z) {
      TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(state).sprite();
      PhysicsMod mod = PhysicsMod.getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
      entity.getTransformation().translation(x, y, z).scale((double)(Math.random() * 0.06F + 0.11F));
      Model model = entity.models.get(0);
      model.texture = sprite;
      model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
      model.backfaceCulling = true;
      model.mesh = PhysicsMod.brokenBlock.get(0);
      entity.physicsGroup = 16;
      entity.physicsMask = 19;
      int color = -1;
      BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
      if (tintSource != null) {
         color = tintSource.colorAsTerrainParticle(state, level, pos);
      }

      if (color == -1) {
         color = -1;
      }

      entity.setColor(color);
      if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
         entity.setColor(-1);
      }

      IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
      mod.physicsWorld.queue(() -> {
         if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
            float strength = 3.0F;
            Vector3f speed = new Vector3f(0.0F, 0.3F, 0.0F);
            speed.x = speed.x + (Math.random() - 0.5F) * 0.4F;
            speed.y = speed.y + Math.random() * 0.4F;
            speed.z = speed.z + (Math.random() - 0.5F) * 0.4F;
            speed.normalize();
            MemoryStack mem = MemoryStack.stackPush();

            try {
               PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
               rigidBody.setLinearVelocity(velocity);
               PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0F, Math.random() * 4.0F, Math.random() * 4.0F);
               rigidBody.setAngularVelocity(avelocity);
            } catch (Throwable var8) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var7x) {
                     var8.addSuppressed(var7x);
                  }
               }

               throw var8;
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
   }

   public static void spawnBloodPhysicsParticle(ClientLevel level, double x, double y, double z) {
      PhysicsMod mod = PhysicsMod.getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
      entity.getTransformation().translation(x, y, z).scale((double)(Math.random() * 0.03F + 0.08F));
      Model model = entity.models.get(0);
      model.textureID = Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.WHITE_TEXTURE).getTextureView();
      model.backfaceCulling = true;
      model.mesh = PhysicsMod.brokenBlock.get(0);
      float bloodModifier = Math.random() * 0.2F + 0.8F;
      entity.setColor(Pack.color(0.1F * bloodModifier, 0.1F * bloodModifier, 0.77F * bloodModifier));
      IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
      mod.physicsWorld.queue(() -> {
         if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
            Vector3f speed = new Vector3f();
            speed.x = Math.random() - 0.5F;
            speed.y = Math.random() - 0.5F;
            speed.z = Math.random() - 0.5F;
            float strength = Math.random() * 2.0F + 1.0F;
            if (speed.lengthSquared() > 0.0F) {
               speed.normalize();
            }

            MemoryStack mem = MemoryStack.stackPush();

            try {
               PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
               rigidBody.setLinearVelocity(velocity);
               PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0F, Math.random() * 4.0F, Math.random() * 4.0F);
               rigidBody.setAngularVelocity(avelocity);
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
   }

   public static void spawnOceanPhysicsParticle(ClientLevel level, double x, double y, double z, double vx, double vz) {
      PhysicsMod mod = PhysicsMod.getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
      entity.getTransformation().translation(x, y, z).scale((double)(Math.random() * 0.05F + 0.11F));
      Model model = entity.models.get(0);
      model.textureID = Minecraft.getInstance().getTextureManager().getTexture(PhysicsMod.WHITE_TEXTURE).getTextureView();
      model.backfaceCulling = true;
      model.mesh = PhysicsMod.brokenBlock.get(0);
      entity.physicsGroup = 16;
      entity.physicsMask = 19;
      entity.setColor(-1);
      IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
      mod.physicsWorld.queue(() -> {
         if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
            Vector3f speed = new Vector3f((float)vx, 0.0F, (float)vz);
            speed.x = speed.x * Math.random();
            speed.y = 0.0F;
            speed.z = speed.z + Math.random();
            float strength = Math.random() * 2.0F + 1.0F;
            if (speed.lengthSquared() > 0.0F) {
               speed.normalize();
            }

            MemoryStack mem = MemoryStack.stackPush();

            try {
               PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, speed.x * strength, speed.y * strength, speed.z * strength);
               rigidBody.setLinearVelocity(velocity);
               PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0F, Math.random() * 4.0F, Math.random() * 4.0F);
               rigidBody.setAngularVelocity(avelocity);
            } catch (Throwable var12x) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var11x) {
                     var12x.addSuppressed(var11x);
                  }
               }

               throw var12x;
            }

            if (mem != null) {
               mem.close();
            }
         }
      });
   }

   public static void spawnServerBlockPhysicsParticle(BlockState state, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
      TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockStateModelSet().getParticleMaterial(state).sprite();
      PhysicsMod mod = PhysicsMod.getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
      entity.getTransformation().translation(x, y, z).scale((double)(Math.random() * 0.06F + 0.11F));
      Model model = entity.models.get(0);
      model.texture = sprite;
      model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
      model.backfaceCulling = true;
      model.mesh = PhysicsMod.brokenBlock.get(0);
      entity.physicsGroup = 16;
      entity.physicsMask = 19;
      int color = -1;
      BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, 0);
      if (tintSource != null) {
         color = tintSource.colorAsTerrainParticle(state, level, BlockPos.containing(x, y, z));
      }

      if (color == -1) {
         color = -1;
      }

      entity.setColor(color);
      if (state.getBlock() == Blocks.CAULDRON || state.getBlock() == Blocks.GRASS_BLOCK) {
         entity.setColor(-1);
      }

      IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
      mod.physicsWorld.queue(() -> {
         if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
            float strength = 2.0F;
            MemoryStack mem = MemoryStack.stackPush();

            try {
               PxVec3 velocity = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)vx * strength, (float)vy * strength, (float)vz * strength);
               rigidBody.setLinearVelocity(velocity);
               PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0F, Math.random() * 4.0F, Math.random() * 4.0F);
               rigidBody.setAngularVelocity(avelocity);
            } catch (Throwable var13) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var12x) {
                     var13.addSuppressed(var12x);
                  }
               }

               throw var13;
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
   }

   public static void spawnItemPhysicsParticle(
      TextureAtlasSprite sprite, ClientLevel level, double x, double y, double z, float size, float depthScale, float uvx, float uvy, Matrix4f transformation
   ) {
      PhysicsMod mod = PhysicsMod.getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.PARTICLE, null);
      entity.getTransformation().set(transformation).translate(x, y, z).scale((double)size, (double)size, (double)(depthScale * size));
      Model model = entity.models.get(0);
      model.texture = sprite;
      model.textureID = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation()).getTextureView();
      model.backfaceCulling = true;
      model.mesh = PhysicsMod.brokenBlock.get(0);
      entity.physicsGroup = 16;
      entity.physicsMask = 19;
      IRigidBody body = mod.physicsWorld.addBlockParticle(entity);
      mod.physicsWorld
         .queue(
            () -> {
               if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                  float strength = 2.0F;
                  MemoryStack mem = MemoryStack.stackPush();

                  try {
                     PxVec3 velocity = PxVec3.createAt(
                        mem,
                        MemoryStack::nmalloc,
                        (Math.random() - 0.5F) * strength,
                        (Math.random() - 0.5F) * strength * 1.6F,
                        (Math.random() - 0.5F) * strength
                     );
                     rigidBody.setLinearVelocity(velocity);
                     PxVec3 avelocity = PxVec3.createAt(mem, MemoryStack::nmalloc, Math.random() * 4.0F, Math.random() * 4.0F, Math.random() * 4.0F);
                     rigidBody.setAngularVelocity(avelocity);
                  } catch (Throwable var7) {
                     if (mem != null) {
                        try {
                           mem.close();
                        } catch (Throwable var6x) {
                           var7.addSuppressed(var6x);
                        }
                     }

                     throw var7;
                  }

                  if (mem != null) {
                     mem.close();
                  }
               }
            }
         );
      Vector4f customUVs = new Vector4f(sprite.getU(uvx), sprite.getU(uvx), sprite.getV(uvy), sprite.getV(uvy));
      float xScale = customUVs.y - customUVs.x;
      float yScale = customUVs.w - customUVs.z;
      model.textureMatrix = new Matrix4f().translate(customUVs.x, customUVs.z, 0.0F).scale(xScale, yScale, 0.0F);
   }
}
