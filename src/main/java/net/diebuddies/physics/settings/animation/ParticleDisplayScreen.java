package net.diebuddies.physics.settings.animation;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.diebuddies.math.Math;
import net.diebuddies.opengl.Box;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.physics.animation.ParticleSpawn;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.GuiPhysicsCustomRenderState;
import net.diebuddies.physics.settings.gui.GuiRenderable;
import net.diebuddies.physics.settings.ux.Animatable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ParticleDisplayScreen extends Screen {
   public static final Identifier STONE_TEXTURE = Identifier.withDefaultNamespace("textures/block/stone.png");
   public Screen parent;
   private double xPosition = 70.0;
   private double animationStart;
   private boolean createdParticles;
   public Animation animation;
   private List<Particle> particles;
   private Camera camera = new Camera();
   private long lastTick = System.currentTimeMillis();

   protected ParticleDisplayScreen(Screen parent, Component component) {
      super(component);
      this.parent = parent;
      this.particles = new ObjectArrayList();
      this.animation = new Animation("default", CurveType.Linear, 0.5F);
   }

   protected void init() {
      super.init();
      this.startAnimation();
      this.addRenderableWidget(
         (Button)(
            (Animatable)ButtonSettings.builder(
               (int)this.xPosition - 30, this.height - 57, 60, 20, Component.translatable("physicsmod.menu.animation.replay"), button -> this.startAnimation()
            )
         )
      );
   }

   public void startAnimation() {
      this.particles.clear();
      this.animationStart = (double)this.animation.speed + 0.5;
      this.createdParticles = false;
   }

   private void createParticles() {
      if (!this.createdParticles) {
         this.createdParticles = true;
         this.particles.clear();

         for (ParticleSpawn spawn : this.animation.particleSpawns) {
            ParticleOptions particleOptions = spawn.particle;
            if (particleOptions != null && (double)Math.random() < spawn.spawnChance) {
               for (int i = 0; i < spawn.amount; i++) {
                  double halfSpread = spawn.spread * 0.5;
                  double px = (double)Math.random() * spawn.spread - halfSpread;
                  double py = (double)Math.random() * spawn.spread - halfSpread;
                  double pz = (double)Math.random() * spawn.spread - halfSpread;

                  try {
                     Particle particle = Minecraft.getInstance().particleEngine.createParticle(particleOptions, px, py, pz, spawn.vx, spawn.vy, spawn.vz);
                     ((ParticleExtension)particle).setPhysics(false);
                     ((ParticleExtension)particle).setFakeLight(true);
                     this.particles.add(particle);
                  } catch (Exception var14) {
                  }
               }

               if (spawn.sound != null) {
                  Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(spawn.sound, 1.0F));
               }
            }
         }
      }
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, -1);
      this.renderAnimation(guiGraphics, delta);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   public void tick() {
      super.tick();
      this.lastTick = System.currentTimeMillis();
      Iterator<Particle> it = this.particles.iterator();

      while (it.hasNext()) {
         Particle particle = it.next();
         if (particle.isAlive()) {
            try {
               particle.tick();
            } catch (Exception var4) {
            }

            if (!particle.isAlive()) {
               it.remove();
            }
         }
      }
   }

   private void renderAnimation(GuiGraphicsExtractor guiGraphics, final float delta) {
      GuiRenderable renderable = new GuiRenderable() {
         {
            Objects.requireNonNull(ParticleDisplayScreen.this);
         }

         @Override
         public void extractRenderState(SubmitNodeCollector submitNodeCollector) {
            float scale = 30.0F;
            Matrix4fStack matrices = RenderSystem.getModelViewStack();
            float realDelta = java.lang.Math.min(1.0F, (float)(System.currentTimeMillis() - ParticleDisplayScreen.this.lastTick) / 50.0F);
            matrices.pushMatrix();
            matrices.scale((float)Minecraft.getInstance().getWindow().getGuiScale());
            matrices.translate((float)ParticleDisplayScreen.this.xPosition, (float)ParticleDisplayScreen.this.height / 2.0F, 100.0F);
            matrices.scale(scale, -scale, scale);
            matrices.pushMatrix();
            matrices.rotate(new Quaternionf().rotationXYZ((float)java.lang.Math.toRadians(25.0), (float)java.lang.Math.toRadians(-25.0), 0.0F));
            Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ITEMS_3D);
            int lightCoords = 15728880;
            ParticleDisplayScreen.this.animationStart = ParticleDisplayScreen.this.animationStart - (double)delta * 0.05;
            float animationSpeed = java.lang.Math.max(ParticleDisplayScreen.this.animation.speed, 0.001F);
            float despawnScale = ParticleDisplayScreen.this.animation
               .getCurve()
               .get((float)(ParticleDisplayScreen.this.animationStart / (double)animationSpeed));
            if (ParticleDisplayScreen.this.animationStart > (double)animationSpeed) {
               despawnScale = 1.0F;
            } else if (ParticleDisplayScreen.this.animationStart <= 0.0) {
               despawnScale = 0.0F;
            }

            float alpha = 1.0F;
            if (ParticleDisplayScreen.this.animation.despawnType == AnimationType.Vanish) {
               alpha = java.lang.Math.min(1.0F, despawnScale);
               despawnScale = 1.0F;
            } else if (ParticleDisplayScreen.this.animation.despawnType == AnimationType.Shrink_and_Vanish) {
               alpha = java.lang.Math.min(1.0F, despawnScale);
            }

            PoseStack currentPose = new PoseStack();
            currentPose.mulPose(matrices);
            RenderType renderType = RenderTypes.entityTranslucent(ParticleDisplayScreen.STONE_TEXTURE);
            float halfScale = despawnScale * 0.5F;
            float finalAlpha = alpha;
            submitNodeCollector.submitCustomGeometry(
               currentPose,
               renderType,
               (pose, buffer) -> {
                  int[] indices = Box.INDICES;
                  float[] positions = Box.POSITIONS;
                  float[] normals = Box.NORMALS;
                  float[] uvs = Box.UVS;

                  for (int i = 0; i < indices.length; i++) {
                     int index = indices[i];
                     float x = positions[index * 3] * halfScale;
                     float y = positions[index * 3 + 1] * halfScale;
                     float z = positions[index * 3 + 2] * halfScale;
                     float nx = normals[index * 3];
                     float ny = normals[index * 3 + 1];
                     float nz = normals[index * 3 + 2];
                     float uvx = uvs[index * 2];
                     float uvy = uvs[index * 2 + 1];
                     buffer.addVertex(pose.pose(), x, y, z)
                        .setColor(1.0F, 1.0F, 1.0F, finalAlpha)
                        .setUv(uvx, uvy)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(lightCoords)
                        .setNormal(nx, ny, nz);
                  }
               }
            );
            if (ParticleDisplayScreen.this.animationStart <= 0.0) {
               ParticleDisplayScreen.this.createParticles();
            }

            matrices.popMatrix();
            renderType = RenderTypes.entityTranslucent(TextureAtlas.LOCATION_PARTICLES);
            currentPose = new PoseStack();
            currentPose.mulPose(matrices);
            submitNodeCollector.submitCustomGeometry(
               currentPose,
               renderType,
               (pose, buffer) -> {
                  ParticleDisplayScreen.CustomQuadParticleRenderState particleState = ParticleDisplayScreen.this.new CustomQuadParticleRenderState(
                     buffer, new Matrix4f(pose.pose())
                  );

                  for (Particle particle : ParticleDisplayScreen.this.particles) {
                     if (particle.isAlive() && particle instanceof SingleQuadParticle quadParticle) {
                        quadParticle.extract(particleState, ParticleDisplayScreen.this.camera, realDelta);
                     }
                  }
               }
            );
            matrices.popMatrix();
         }
      };
      guiGraphics.guiRenderState.addPicturesInPictureState(new GuiPhysicsCustomRenderState(renderable, 0, 0, this.width, this.height, 1.0F, null));
   }

   public void onClose() {
      this.minecraft.setScreenAndShow(this.parent);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }

   private class CustomQuadParticleRenderState extends QuadParticleRenderState {
      private VertexConsumer consumer;
      private Matrix4f transform;

      public CustomQuadParticleRenderState(VertexConsumer consumer, Matrix4f transform) {
         Objects.requireNonNull(ParticleDisplayScreen.this);
         super();
         this.consumer = consumer;
         this.transform = transform;
      }

      public void add(Layer layer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, float q, int r, int s) {
         this.renderRotatedQuad(this.consumer, f, g, h, i, j, k, l, m, n, o, p, q, r, s);
      }

      protected void renderRotatedQuad(
         VertexConsumer vertexConsumer,
         float f,
         float g,
         float h,
         float i,
         float j,
         float k,
         float l,
         float m,
         float n,
         float o,
         float p,
         float q,
         int r,
         int s
      ) {
         Quaternionf quaternionf = new Quaternionf(i, j, k, l);
         this.renderVertex(vertexConsumer, quaternionf, f, g, h, 1.0F, -1.0F, m, o, q, r, s);
         this.renderVertex(vertexConsumer, quaternionf, f, g, h, 1.0F, 1.0F, m, o, p, r, s);
         this.renderVertex(vertexConsumer, quaternionf, f, g, h, -1.0F, 1.0F, m, n, p, r, s);
         this.renderVertex(vertexConsumer, quaternionf, f, g, h, -1.0F, -1.0F, m, n, q, r, s);
      }

      private void renderVertex(
         VertexConsumer vertexConsumer, Quaternionf quaternionf, float f, float g, float h, float i, float j, float k, float l, float m, int n, int o
      ) {
         Vector3f vector3f = new Vector3f(i, j, 0.0F).rotate(quaternionf).mul(k).add(f, g, h);
         vertexConsumer.addVertex(this.transform, vector3f.x(), vector3f.y(), vector3f.z())
            .setColor(n)
            .setUv(l, m)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(o)
            .setNormal(0.0F, -1.0F, 0.0F);
      }
   }
}
