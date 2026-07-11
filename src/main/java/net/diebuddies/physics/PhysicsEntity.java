package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.AABBf;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Vector3f;

public class PhysicsEntity {
   private final Matrix4d transformation;
   public List<Model> models = new ObjectArrayList(1);
   public PhysicsEntity.Type type;
   public float time;
   public AABBf rescale;
   public final Vector3f pivot = new Vector3f();
   private int color = -1;
   public final Vector3f enlargeHitbox = new Vector3f(1.0F);
   public byte physicsGroup = 2;
   public byte physicsMask = 23;
   public Object info;
   public SoundType sound;
   public long lastSoundTime = 0L;
   public RenderLayer feature = null;
   public float lifetime;
   public float lifetimeVariance;
   private Animation animation;
   public boolean noVolume;
   public boolean staticPhysics = false;
   private boolean dead;
   public List<PhysicsEntity> children = new ObjectArrayList();

   public PhysicsEntity(PhysicsEntity.Type type, Object info) {
      this.info = info;
      this.animation = ConfigAnimations.DEFAULT_ANIMATION;
      if (info != null) {
         if (info instanceof BlockState state) {
            this.sound = state.getSoundType();
            if (type == PhysicsEntity.Type.BLOCK || type == PhysicsEntity.Type.VINE) {
               BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
               this.animation = blockSetting.getAnimation();
               this.lifetime = (float)blockSetting.getLifetime();
               this.lifetimeVariance = (float)blockSetting.getLifetimeVariance();
            }
         } else if (info instanceof EntityType<?> entityType && type == PhysicsEntity.Type.MOB) {
            MobSetting mobSetting = ConfigMobs.getMobSetting(entityType);
            this.animation = mobSetting.getAnimation();
            this.lifetime = (float)mobSetting.getLifetime();
            this.lifetimeVariance = (float)mobSetting.getLifetimeVariance();
         }
      }

      this.transformation = new Matrix4d();
      this.models.add(new Model());
      this.type = type;
   }

   public Matrix4d getTransformation() {
      return this.transformation;
   }

   public void destroy() {
      this.destroyModels();
   }

   public void destroyModels() {
      if (this.models != null) {
         for (int i = 0; i < this.models.size(); i++) {
            Model model = this.models.get(i);
            model.freeRenderData();
         }
      }
   }

   public double getDespawnSpeed() {
      return (double)this.animation.speed;
   }

   public float getVolume() {
      if (this.rescale == null) {
         return 1.0F;
      } else {
         Vector3f max = this.rescale.end;
         Vector3f min = this.rescale.start;
         return (max.x - min.x) * (max.y - min.y) * (max.z - min.z);
      }
   }

   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   public Animation getAnimation() {
      return this.animation;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public int getBGRA() {
      return this.color;
   }

   public void backfaceCulling(boolean value) {
      for (Model model : this.models) {
         model.backfaceCulling = value;
      }
   }

   public void shade(boolean value) {
      for (Model model : this.models) {
         model.shade = value;
      }
   }

   public static enum Type {
      MOB,
      BLOCK,
      VINE,
      ITEM,
      PARTICLE,
      LIQUID,
      SMOKE,
      SMOKE_CUDA,
      SMOKE_VOLUMETRIC,
      OTHER;
   }
}
