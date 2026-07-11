package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatternLayers.Layer;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class BannerConstraint implements VerletConstraint {
   private List<ModelCube> partsToCheck;
   private Matrix4d transformation = new Matrix4d();
   private Matrix4d invTransformation = new Matrix4d();
   private VerletHelper helper = new VerletHelper();
   private Vector3d invPoint = new Vector3d();
   private Vector2f[] tmpUV;
   private Matrix4f[] textureMatrices;
   private BannerPatternLayers patterns;
   private DyeColor baseColor;
   private SpriteGetter materialSet;
   private BlockPos blockPos;
   private BlockState blockState;
   private Vector3f scratchPosition = new Vector3f();

   public BannerConstraint(
      VerletSimulation simulation,
      BannerModel bannerModel,
      BannerPatternLayers patterns,
      DyeColor baseColor,
      BlockPos pos,
      BlockState state,
      SpriteGetter materialSet
   ) {
      this.materialSet = materialSet;
      this.patterns = patterns;
      this.baseColor = baseColor;
      this.blockPos = pos;
      this.blockState = state;
      List<VerletConstraint> constraints = simulation.getConstraints();

      for (int i = 0; i < constraints.size(); i++) {
         if (constraints.get(i) instanceof RenderConstraint) {
            constraints.remove(i--);
         }
      }

      this.partsToCheck = new ObjectArrayList();
      bannerModel.allParts().forEach(part -> this.partsToCheck.add(new ModelCube(part)));
      this.calculateTransformation(simulation, 1.0F);
      int capeXPoints = 9;
      int capeYPoints = 17;
      double distance = 0.15000000001500002;
      VerletPoint[][] points = new VerletPoint[capeXPoints][capeYPoints];
      float uvXOff = 0.015625F;
      float uvYOff = 0.015625F;
      float uvXMod = 0.3125F;
      float uvYMod = 0.625F;

      for (int y = 0; y < points[0].length; y++) {
         for (int x = 0; x < points.length; x++) {
            Vector3d position = new Vector3d(
               (double)x * distance - (double)capeXPoints * 0.5 * distance + distance * 0.5, (double)y * distance, -0.08928571428571429
            );
            this.transformation.transformPosition(position);
            VerletPoint point = new VerletPoint(position);
            point.uv.set((float)x / (float)(points.length - 1) * uvXMod + uvXOff, (float)y / (float)(points[0].length - 1) * uvYMod + uvYOff);
            if (y == 0) {
               point.locked = true;
            }

            points[x][y] = point;
            simulation.addPoint(points[x][y]);
         }
      }

      for (int x = 0; x < points.length; x++) {
         for (int y = 0; y < points[0].length; y++) {
            if (x < points.length - 1) {
               simulation.addStick(new VerletStick(points[x][y], points[x + 1][y]));
            }

            if (y < points[0].length - 1) {
               simulation.addStick(new VerletStick(points[x][y], points[x][y + 1]));
            }

            if (x < points.length - 1 && y < points[0].length - 1) {
               simulation.addQuad(new VerletQuad(points[x][y + 1], points[x + 1][y + 1], points[x + 1][y], points[x][y]));
               simulation.addStick(new VerletStick(points[x][y], points[x + 1][y + 1]));
               simulation.addStick(new VerletStick(points[x + 1][y], points[x][y + 1]));
            }
         }
      }

      simulation.calculateNormals();
      simulation.downloadData();
      this.calculateTransformation(simulation, 1.0F);
      List<VerletQuad> quads = simulation.getQuads();
      int drawCalls = Math.min(17, patterns.layers().size() + 1);
      int size = quads.size();
      this.tmpUV = new Vector2f[drawCalls * size * 4];
      this.textureMatrices = new Matrix4f[drawCalls];

      for (int ix = 0; ix < this.tmpUV.length; ix++) {
         this.tmpUV[ix] = new Vector2f();
      }

      for (int ix = 0; ix < 17 && ix < patterns.layers().size() + 1; ix++) {
         SpriteId bannerMaterial = null;
         if (ix == 0) {
            bannerMaterial = Sheets.BANNER_BASE;
         } else {
            Layer layer = (Layer)patterns.layers().get(ix - 1);
            bannerMaterial = Sheets.getBannerSprite(layer.pattern());
         }

         if (bannerMaterial == null) {
            this.textureMatrices[ix] = new Matrix4f();
         } else {
            TextureAtlasSprite sprite = materialSet.get(bannerMaterial);
            float minU = sprite.getU0();
            float maxU = sprite.getU1();
            float minV = sprite.getV0();
            float maxV = sprite.getV1();
            float xScale = maxU - minU;
            float yScale = maxV - minV;
            this.textureMatrices[ix] = new Matrix4f().translate(minU, minV, 0.0F).scale(xScale, yScale, 0.0F);

            for (int j = 0; j < quads.size(); j++) {
               VerletQuad quad = quads.get(j);
               this.remap(quad.point1.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4]);
               this.remap(quad.point2.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4 + 1]);
               this.remap(quad.point3.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4 + 2]);
               this.remap(quad.point4.uv, minU, maxU, minV, maxV, this.tmpUV[size * ix * 4 + j * 4 + 3]);
            }
         }
      }
   }

   private void calculateTransformation(VerletSimulation simulation, float tickDelta) {
      Vector3d offset = simulation.getOffset();
      Matrix4d test = new Matrix4d();
      if (offset != null) {
         test.translate((double)this.blockPos.getX() - offset.x, (double)this.blockPos.getY() - offset.y, (double)this.blockPos.getZ() - offset.z);
      } else {
         test.translate((double)this.blockPos.getX(), (double)this.blockPos.getY(), (double)this.blockPos.getZ());
      }

      if (this.blockState.getBlock() instanceof BannerBlock) {
         test.translate(0.5, 0.5, 0.5);
         float blockRotation = (float)(-(Integer)this.blockState.getValue(BannerBlock.ROTATION) * 360) / 16.0F;
         test.rotate(Axis.YP.rotationDegrees(blockRotation));
      } else {
         test.translate(0.5, -0.16666667F, 0.5);
         float blockRotation = -((Direction)this.blockState.getValue(WallBannerBlock.FACING)).toYRot();
         test.rotate(Axis.YP.rotationDegrees(blockRotation));
         test.translate(0.0, -0.3125, -0.4375);
      }

      test.scale(0.6666667, -0.6666667, -0.6666667);
      if (simulation.getOffset() == null) {
         long gameTime = 0L;
         float n = ((float)Math.floorMod((long)(this.blockPos.getX() * 7 + this.blockPos.getY() * 9 + this.blockPos.getZ() * 13) + gameTime, 100L) + tickDelta)
            / 100.0F;
         float xRot = (-0.0125F + 0.01F * Mth.cos((double)((float) (Math.PI * 2) * n))) * (float) Math.PI;
         double yPos = -32.0;
         test.translate(0.0, yPos / 16.0, 0.0);
         if (xRot != 0.0F) {
            test.rotate(Axis.XP.rotation(xRot));
         }
      }

      this.transformation.set(test);
      this.transformation.invert(this.invTransformation);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      for (int i = 0; i < this.partsToCheck.size(); i++) {
         ModelCube cube = this.partsToCheck.get(i);
         cube.pose = cube.part.storePose();
         cube.updateHitbox();
      }

      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
      this.doCollisionCheck(percent, simulation);
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   private void doCollisionCheck(double percent, VerletSimulation simulation) {
   }

   public void translateAndRotate(PoseStack poseStack, PartPose pose) {
      poseStack.translate((double)pose.x() / 16.0, (double)pose.y() / 16.0, (double)pose.z() / 16.0);
      if (pose.zRot() != 0.0F) {
         poseStack.mulPose(Axis.ZP.rotation(pose.zRot()));
      }

      if (pose.yRot() != 0.0F) {
         poseStack.mulPose(Axis.YP.rotation(pose.yRot()));
      }

      if (pose.xRot() != 0.0F) {
         poseStack.mulPose(Axis.XP.rotation(pose.xRot()));
      }
   }

   @Override
   public void render(Matrix4fStack matrixStack, double renderPercent, SubmitNodeCollector submitNodeStorage, VerletSimulation simulation) {
      int brightness = simulation.brightness;
      List<VerletQuad> quads = simulation.getQuads();
      int size = quads.size();
      if (simulation.getQuads().size() > 0) {
         List<VerletPoint> points = simulation.getPoints();

         for (int i = 0; i < points.size(); i++) {
            VerletPoint point = points.get(i);
            point.updateRenderPosition(renderPercent);
            point.renderPosition.add(simulation.viewDelta);
         }

         for (int i = 0; i < 17 && i < this.patterns.layers().size() + 1; i++) {
            int color;
            SpriteId bannerMaterial;
            if (i == 0) {
               color = this.baseColor.getTextureDiffuseColor();
               bannerMaterial = Sheets.BANNER_BASE;
            } else {
               Layer layer = (Layer)this.patterns.layers().get(i - 1);
               color = layer.color().getTextureDiffuseColor();
               bannerMaterial = Sheets.getBannerSprite(layer.pattern());
            }

            if (bannerMaterial != null) {
               TextureAtlasSprite sprite = this.materialSet.get(bannerMaterial);
               int drawCallIndex = i * size * 4;
               RenderType renderType;
               if (!StarterClient.iris() || !Iris.isExtending()) {
                  renderType = PhysicsShaders.PHYSICS_BANNER_RENDER.apply(sprite.atlasLocation());
               } else if (i == 0) {
                  renderType = PhysicsShaders.PHYSICS_CLOTH_RENDER_IRIS.apply(sprite.atlasLocation());
               } else {
                  renderType = PhysicsShaders.PHYSICS_BANNER_RENDER_IRIS.apply(sprite.atlasLocation());
               }

               PoseStack currentPose = new PoseStack();
               currentPose.mulPose(matrixStack);
               Matrix4f modelMatrix = currentPose.last().pose();
               submitNodeStorage.submitCustomGeometry(
                  currentPose,
                  renderType,
                  (pose, vertexConsumer) -> {
                     for (int j = 0; j < quads.size(); j++) {
                        VerletQuad quad = quads.get(j);
                        int multiple = j * 4;
                        int uvIndex = drawCallIndex + multiple;
                        if (ConfigClient.clothSmoothShading) {
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point1.renderPosition,
                              this.tmpUV[uvIndex],
                              quad.point1.bufferNormal,
                              brightness,
                              color
                           );
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point2.renderPosition,
                              this.tmpUV[uvIndex + 1],
                              quad.point2.bufferNormal,
                              brightness,
                              color
                           );
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point3.renderPosition,
                              this.tmpUV[uvIndex + 2],
                              quad.point3.bufferNormal,
                              brightness,
                              color
                           );
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point4.renderPosition,
                              this.tmpUV[uvIndex + 3],
                              quad.point4.bufferNormal,
                              brightness,
                              color
                           );
                        } else {
                           this.bufferVertex(
                              modelMatrix, vertexConsumer, renderPercent, quad.point1.renderPosition, this.tmpUV[uvIndex], quad.bufferNormal, brightness, color
                           );
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point2.renderPosition,
                              this.tmpUV[uvIndex + 1],
                              quad.bufferNormal,
                              brightness,
                              color
                           );
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point3.renderPosition,
                              this.tmpUV[uvIndex + 2],
                              quad.bufferNormal,
                              brightness,
                              color
                           );
                           this.bufferVertex(
                              modelMatrix,
                              vertexConsumer,
                              renderPercent,
                              quad.point4.renderPosition,
                              this.tmpUV[uvIndex + 3],
                              quad.bufferNormal,
                              brightness,
                              color
                           );
                        }
                     }
                  }
               );
            }
         }
      }
   }

   private void remap(Vector2f uv, float minU, float maxU, float minV, float maxV, Vector2f dst) {
      dst.set(net.diebuddies.math.Math.remap(uv.x, 0.0F, 1.0F, minU, maxU), net.diebuddies.math.Math.remap(uv.y, 0.0F, 1.0F, minV, maxV));
   }

   private void bufferVertex(
      Matrix4f modelMatrix, VertexConsumer bufferbuilder, double renderPercent, Vector3d position, Vector2f uv, Vector3d normal, int brightness, int color
   ) {
      modelMatrix.transformPosition((float)position.x, (float)position.y, (float)position.z, this.scratchPosition);
      bufferbuilder.addVertex(
         this.scratchPosition.x,
         this.scratchPosition.y,
         this.scratchPosition.z,
         color,
         uv.x,
         uv.y,
         OverlayTexture.NO_OVERLAY,
         brightness,
         (float)normal.x,
         (float)normal.y,
         (float)normal.z
      );
   }
}
