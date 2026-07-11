package net.diebuddies.render.shader;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.render.SimpleColorDepthRenderTarget;
import net.diebuddies.render.SimpleColorRenderTarget;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.GaussianDepthBlurUniform;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

public class GaussianDepthBlurEffect {
   private final Vector2f vertical = new Vector2f(0.0F, 1.0F);
   private final Vector2f horizontal = new Vector2f(1.0F, 0.0F);
   private static final Map<GpuFormat, RenderPipeline> formatPipelines = new Object2ObjectOpenHashMap();
   public static final RenderPipeline PHYSICS_GAUSSIAN_DEPTH_BLUR_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withBindGroupLayout(PhysicsBindGroupLayouts.GAUSSIAN_DEPTH_BLUR)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/gaussian"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/gaussian"))
         .withLocation("pipeline/physics_gaussian_depth_blur")
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );

   public void render(Matrix4f projectionMatrix, float blurWorldDistance, SimpleColorDepthRenderTarget image, SimpleColorRenderTarget scratch) {
      GpuBufferSlice verticalUniform = this.uploadBlurUniform(projectionMatrix, blurWorldDistance, this.vertical, image.getWidth(), image.getHeight());
      if (verticalUniform != null) {
         GpuFormat format = scratch.getFormat();
         RenderPipeline pipeline = formatPipelines.get(format);
         if (pipeline == null) {
            pipeline = RenderPipelines.register(
               RenderPipeline.builder(new Snippet[0])
                  .withBindGroupLayout(PhysicsBindGroupLayouts.GAUSSIAN_DEPTH_BLUR)
                  .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/gaussian"))
                  .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/gaussian"))
                  .withLocation("pipeline/physics_gaussian_depth_blur")
                  .withCull(false)
                  .withVertexBinding(0, DefaultVertexFormat.POSITION)
                  .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                  .withColorTargetState(0, new ColorTargetState(Optional.empty(), format, 15))
                  .build()
            );
            formatPipelines.put(format, pipeline);
         }

         CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
         RenderPass pass = encoder.createRenderPass(
            () -> "Physics Mod Gaussian Depth Blur Vertical", scratch.textureView(), Optional.of(new Vector4f(0.0F)), null, OptionalDouble.empty()
         );

         try {
            pass.setPipeline(pipeline);
            pass.bindTexture("imageMap", image.colorView(), MainRenderer.NEAREST_CLAMP_SAMPLER_NO_MIPMAP);
            pass.setUniform("PhysicsLiquidGaussianBlur", verticalUniform);
            pass.draw(3, 1, 0, 0);
         } catch (Throwable var16) {
            if (pass != null) {
               try {
                  pass.close();
               } catch (Throwable var14) {
                  var16.addSuppressed(var14);
               }
            }

            throw var16;
         }

         if (pass != null) {
            pass.close();
         }

         GpuBufferSlice horizontalUniform = this.uploadBlurUniform(projectionMatrix, blurWorldDistance, this.horizontal, image.getWidth(), image.getHeight());
         if (horizontalUniform != null) {
            RenderPass passx = encoder.createRenderPass(
               () -> "Physics Mod Gaussian Depth Blur Horizontal", image.colorView(), Optional.of(new Vector4f(0.0F)), null, OptionalDouble.empty()
            );

            try {
               passx.setPipeline(pipeline);
               passx.bindTexture("imageMap", scratch.textureView(), MainRenderer.NEAREST_CLAMP_SAMPLER_NO_MIPMAP);
               passx.setUniform("PhysicsLiquidGaussianBlur", horizontalUniform);
               passx.draw(3, 1, 0, 0);
            } catch (Throwable var15) {
               if (passx != null) {
                  try {
                     passx.close();
                  } catch (Throwable var13) {
                     var15.addSuppressed(var13);
                  }
               }

               throw var15;
            }

            if (passx != null) {
               passx.close();
            }
         }
      }
   }

   @Nullable
   private GpuBufferSlice uploadBlurUniform(Matrix4f projectionMatrix, float blurWorldDistance, Vector2f offset, int width, int height) {
      float m22 = projectionMatrix.m22();
      float m32 = projectionMatrix.m32();
      boolean reverseZ = !StarterClient.iris() || Iris.isUsingReverseZ();
      boolean zZeroToOne = RenderSystem.getDevice().getDeviceInfo().isZZeroToOne();
      float nearNdc = reverseZ ? 1.0F : (zZeroToOne ? 0.0F : -1.0F);
      float farNdc = reverseZ ? (zZeroToOne ? 0.0F : -1.0F) : 1.0F;
      float near = m32 / (m22 + nearNdc);
      float far = m32 / (m22 + farNdc);
      GaussianDepthBlurUniform blurUniform = new GaussianDepthBlurUniform(
         offset.x,
         offset.y,
         1.0F / (float)width,
         1.0F / (float)height,
         near,
         far,
         projectionMatrix.m00(),
         blurWorldDistance,
         zZeroToOne ? 1 : 0,
         reverseZ ? 1 : 0
      );
      DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
      GpuBufferSlice[] slices = dynamicUniforms.physicsmod$writeGaussianDepthBlurUniforms(blurUniform);
      return slices.length > 0 ? slices[0] : null;
   }

   public void destroy() {
   }

   public void resize(int width, int height) {
   }
}
