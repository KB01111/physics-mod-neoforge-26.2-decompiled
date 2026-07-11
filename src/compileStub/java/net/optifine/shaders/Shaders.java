package net.optifine.shaders;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Shaders {
   public static boolean isShadowPass;
   public static Program ProgramEntities;
   public static Program ProgramWater;
   public static Program ProgramNone;
   public static Program activeProgram;
   public static int activeProgramID;
   public static int defaultTexture;
   public static ShadersFramebuffer dfb;
   public static Uniform uniform_normalMatrix;

   public static IShaderPack getShaderPack() {
      return null;
   }

   public static void setModelViewMatrix(Matrix4f modelViewMatrix) {
   }

   public static void setProjectionMatrix(Matrix4f projectionMatrix) {
   }

   public static void setColorModulator(float[] colors) {
   }

   public static void setDynamicTransforms(GpuBufferSlice slice) {
   }

   public static void setChunkSectionInfo(GpuBufferSlice slice) {
   }

   public static void setTextureMatrix(Matrix4f textureMatrix) {
   }

   public static void useProgram(Program program) {
   }

   public static class Uniform {
      public boolean isDefined() {
         return false;
      }

      public void setValue(Matrix3f normal) {
      }
   }
}
