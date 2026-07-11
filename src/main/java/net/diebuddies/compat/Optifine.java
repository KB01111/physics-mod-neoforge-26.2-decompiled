package net.diebuddies.compat;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.IntBuffer;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.BasicDrawCall;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.util.ShaderType;
import net.minecraft.world.level.block.state.BlockState;
import net.optifine.shaders.BlockAliases;
import net.optifine.shaders.GlState;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Program;
import net.optifine.shaders.ShaderPackDefault;
import net.optifine.shaders.ShaderPackNone;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersFramebuffer;
import net.optifine.shaders.ShadersTex;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32C;

public class Optifine {
   public static Program compilingProgram;
   public static Program oceanProgram;
   public static Program oceanShadowProgram;
   public static Program liquidProgram;
   public static Program liquidShadowProgram;
   public static Program smokeProgram;
   public static Program smokeShadowProgram;
   public static ShaderType compileStage;
   public static Matrix4f shadowView = new Matrix4f();
   private static Method setData;
   private static Method getProjectionMatrix;
   private static Field activeFramebufferField;
   private static Field dfb;
   private static Field depthTextures;
   private static Field fbWidth;
   private static Field fbHeight;
   private static Field imageTextures;
   private static Constructor<?> vertexFormatConstructor;
   private static RenderPipeline PHYSICS_SMOKE_PIPELINE_OPTIFINE;
   private static RenderPipeline PHYSICS_OCEAN_PIPELINE_OPTIFINE;
   private static boolean init = false;
   private static int[] viewport = new int[4];
   private static int drawFboBoundBefore;
   private static int readFboBoundBefore;
   private static ShadersFramebuffer framebuffer = null;

   public static RenderPipeline getPhysicsVanillaSmokePipeline() {
      if (!initMethods()) {
         return null;
      } else {
         if (PHYSICS_SMOKE_PIPELINE_OPTIFINE == null) {
         }

         return PHYSICS_SMOKE_PIPELINE_OPTIFINE;
      }
   }

   private static VertexFormat createSmokeFormat() {
      if (!initMethods()) {
         return null;
      } else {
         try {
            VertexFormatElement SMOKE_LIGHT = (VertexFormatElement)vertexFormatConstructor.newInstance(6, 0, GpuFormat.RGBA8_UINT, null, 6);
            VertexFormatElement SMOKE_POS = (VertexFormatElement)vertexFormatConstructor.newInstance(7, 0, GpuFormat.RGBA32_FLOAT, null, 7);
            VertexFormatElement var2 = (VertexFormatElement)vertexFormatConstructor.newInstance(8, 0, GpuFormat.RGBA32_FLOAT, null, 8);
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var3) {
            var3.printStackTrace();
         }

         return null;
      }
   }

   public static RenderPipeline getPhysicsVanillaOceanPipeline() {
      if (!initMethods()) {
         return null;
      } else {
         if (PHYSICS_OCEAN_PIPELINE_OPTIFINE == null) {
         }

         return PHYSICS_OCEAN_PIPELINE_OPTIFINE;
      }
   }

   private static VertexFormat createOceanFormat() {
      return !initMethods() ? null : null;
   }

   public static boolean isShadowPass() {
      if (!initMethods()) {
         return false;
      } else {
         try {
            return Shaders.isShadowPass;
         } catch (Exception var1) {
            var1.printStackTrace();
            return false;
         }
      }
   }

   public static boolean isUsingShadersNoInternal() {
      if (!initMethods()) {
         return false;
      } else {
         try {
            IShaderPack pack = Shaders.getShaderPack();
            boolean vanillaPacks = pack == null || pack instanceof ShaderPackDefault || pack instanceof ShaderPackNone;
            return !vanillaPacks;
         } catch (Exception var2) {
            var2.printStackTrace();
            return false;
         }
      }
   }

   public static void setModelViewMatrix(Matrix4f modelViewMatrix) {
      if (initMethods()) {
         try {
            Shaders.setModelViewMatrix(modelViewMatrix);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setProjectionMatrix(Matrix4f projectionMatrix) {
      if (initMethods()) {
         try {
            Shaders.setProjectionMatrix(projectionMatrix);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setColorModulator(float[] colors) {
      if (initMethods()) {
         try {
            Shaders.setColorModulator(colors);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setDynamicTransforms(GpuBufferSlice slice) {
      if (initMethods()) {
         try {
            Shaders.setDynamicTransforms(slice);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setChunkSectionInfo(GpuBufferSlice slice) {
      if (initMethods()) {
         try {
            Shaders.setChunkSectionInfo(slice);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setGpuBufferSliceData(GpuBufferSlice slice, Object data) {
      if (initMethods()) {
         try {
            setData.invoke(slice, data);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }
   }

   public static void setTextureMatrix(Matrix4f textureMatrix) {
      if (initMethods()) {
         try {
            Shaders.setTextureMatrix(textureMatrix);
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void setNormalMatrix(Matrix3f normal) {
      if (initMethods()) {
         try {
            if (Shaders.uniform_normalMatrix.isDefined()) {
               Shaders.uniform_normalMatrix.setValue(normal);
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static boolean bindPBRTexture(BasicDrawCall drawCall) {
      if (!initMethods()) {
         return false;
      } else {
         boolean isDefaultTexture = false;

         try {
            if (drawCall.texture != null) {
               ShadersTex.bindTexture((GlTexture)drawCall.texture.texture());
            } else {
               isDefaultTexture = true;
               ShadersTex.bindTexture(Shaders.defaultTexture);
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }

         return isDefaultTexture;
      }
   }

   public static int getMaterialID(BlockState state) {
      if (!initMethods()) {
         return -1;
      } else {
         try {
            return BlockAliases.getAliasBlockId(state);
         } catch (Exception var2) {
            var2.printStackTrace();
            return -1;
         }
      }
   }

   public static int getRenderType(BlockState state) {
      if (!initMethods()) {
         return -1;
      } else {
         try {
            return BlockAliases.getRenderType(state);
         } catch (Exception var2) {
            var2.printStackTrace();
            return -1;
         }
      }
   }

   public static void useEntityShader() {
      if (initMethods()) {
         Shaders.useProgram(Shaders.ProgramEntities);
      }
   }

   public static boolean useOceanShader() {
      if (initMethods() && oceanProgram.getId() > 0) {
         Shaders.useProgram(oceanProgram);
         return true;
      } else {
         return false;
      }
   }

   public static boolean useOceanShadowShader() {
      if (initMethods() && oceanShadowProgram.getId() > 0) {
         boolean before = Shaders.isShadowPass;
         Shaders.isShadowPass = false;
         Shaders.useProgram(oceanShadowProgram);
         Shaders.isShadowPass = before;
         return true;
      } else {
         return false;
      }
   }

   public static boolean useLiquidShader() {
      if (initMethods() && liquidProgram.getId() > 0) {
         Shaders.useProgram(liquidProgram);
         return true;
      } else {
         return false;
      }
   }

   public static boolean supportsLiquidShader() {
      return !initMethods() ? false : liquidProgram.getId() > 0;
   }

   public static boolean useLiquidShadowShader() {
      if (initMethods() && liquidShadowProgram.getId() > 0) {
         boolean before = Shaders.isShadowPass;
         Shaders.isShadowPass = false;
         Shaders.useProgram(liquidShadowProgram);
         Shaders.isShadowPass = before;
         return true;
      } else {
         return false;
      }
   }

   public static boolean useSmokeShader() {
      if (initMethods() && smokeProgram.getId() > 0) {
         Shaders.useProgram(smokeProgram);
         return true;
      } else {
         return false;
      }
   }

   public static boolean useSmokeShadowShader() {
      if (initMethods() && smokeShadowProgram.getId() > 0) {
         boolean before = Shaders.isShadowPass;
         Shaders.isShadowPass = false;
         Shaders.useProgram(smokeShadowProgram);
         Shaders.isShadowPass = before;
         return true;
      } else {
         return false;
      }
   }

   public static boolean supportsSmokeShadow() {
      return initMethods() && smokeShadowProgram.getId() > 0;
   }

   public static void useWaterShader() {
      if (initMethods()) {
         Shaders.useProgram(Shaders.ProgramWater);
      }
   }

   public static void setShadowMatrices(MainRenderer mainRenderer) {
      if (initMethods()) {
         try {
            Matrix4f projection = (Matrix4f)getProjectionMatrix.invoke(null);
            mainRenderer.setViewAndProjectionMatrix(shadowView, projection);
         } catch (InvocationTargetException | IllegalAccessException var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void resetLastProgram() {
      if (initMethods()) {
         Shaders.activeProgram = Shaders.ProgramNone;
         Shaders.activeProgramID = 0;
      }
   }

   public static void storeFBOAndViewport() {
      if (initMethods()) {
         drawFboBoundBefore = GL32C.glGetInteger(36006);
         readFboBoundBefore = GL32C.glGetInteger(36010);
         GL32C.glGetIntegerv(2978, viewport);
      }
   }

   public static void restoreFBOAndViewport() {
      if (initMethods()) {
         GlStateManager._glBindFramebuffer(36009, drawFboBoundBefore);
         GlStateManager._glBindFramebuffer(36008, readFboBoundBefore);
         GlStateManager._viewport(viewport[0], viewport[1], viewport[2], viewport[3]);
      }
   }

   public static void disableShaders() {
      if (initMethods()) {
         framebuffer = GlState.getFramebuffer();

         try {
            activeFramebufferField.set(null, null);
         } catch (IllegalAccessException | IllegalArgumentException var1) {
            var1.printStackTrace();
         }
      }
   }

   public static int viewportWidth() {
      return viewport[2];
   }

   public static int viewportHeight() {
      return viewport[3];
   }

   public static void restoreShaders() {
      if (initMethods()) {
         try {
            activeFramebufferField.set(null, framebuffer);
         } catch (IllegalAccessException | IllegalArgumentException var1) {
            var1.printStackTrace();
         }
      }
   }

   public static GpuTexture getActiveDepthTexture() {
      if (!initMethods()) {
         return null;
      } else {
         try {
            ShadersFramebuffer shadersFramebuffer = (ShadersFramebuffer)dfb.get(null);
            IntBuffer dt = (IntBuffer)depthTextures.get(shadersFramebuffer);
            int depthId = dt.get(0);
            int width = fbWidth.getInt(shadersFramebuffer);
            int height = fbHeight.getInt(shadersFramebuffer);
            return new GlTexture(
               15, "dummy optifine depth", GpuFormat.D32_FLOAT, width, height, 1, 1, depthId, ((GlDevice)RenderSystem.getDevice().backend).frameBufferCache()
            );
         } catch (Exception var5) {
            var5.printStackTrace();
            return null;
         }
      }
   }

   public static void setImageTexture(int unit, int texture) {
      if (initMethods()) {
         try {
            int[] cache = (int[])imageTextures.get(null);
            cache[unit] = texture;
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }
   }

   private static boolean initMethods() {
      if (!StarterClient.optifabric) {
         return false;
      } else if (init) {
         return true;
      } else {
         init = true;

         try {
            Class<?> bufferSlice = GpuBufferSlice.class;
            setData = bufferSlice.getMethod("setData", Object.class);
            Class<?> renderSystem = RenderSystem.class;
            getProjectionMatrix = renderSystem.getMethod("getProjectionMatrix");
            Class<?> glState = GlState.class;
            activeFramebufferField = glState.getDeclaredField("activeFramebuffer");
            activeFramebufferField.setAccessible(true);
            Class<?> vertexFormatElement = VertexFormatElement.class;
            vertexFormatConstructor = vertexFormatElement.getConstructor(int.class, int.class, GpuFormat.class, String.class, int.class);
            dfb = Shaders.class.getDeclaredField("dfb");
            dfb.setAccessible(true);
            depthTextures = ShadersFramebuffer.class.getDeclaredField("depthTextures");
            depthTextures.setAccessible(true);
            fbWidth = ShadersFramebuffer.class.getDeclaredField("width");
            fbWidth.setAccessible(true);
            fbHeight = ShadersFramebuffer.class.getDeclaredField("height");
            fbHeight.setAccessible(true);
            imageTextures = GlStateManager.class.getDeclaredField("IMAGE_TEXTURES");
            imageTextures.setAccessible(true);
         } catch (SecurityException | NoSuchFieldException | NoSuchMethodException var4) {
            var4.printStackTrace();
         }

         return true;
      }
   }
}
