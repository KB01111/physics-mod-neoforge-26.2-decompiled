package net.diebuddies.physics.smoke.volumetric;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBClearTexture;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.ARBShaderImageLoadStore;
import org.lwjgl.opengl.ARBTextureStorage;
import org.lwjgl.opengl.EXTTextureStorage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GL44C;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class GLCompat {
   private static volatile GLCompat.Features cached;
   private static IntBuffer zeroIntCache;
   private static int zeroIntCacheElems;

   private GLCompat() {
   }

   public static GLCompat.Features features() {
      GLCompat.Features f = cached;
      if (f != null) {
         return f;
      } else {
         synchronized (GLCompat.class) {
            if (cached == null) {
               cached = queryFeatures(GL.getCapabilities());
            }

            return cached;
         }
      }
   }

   private static GLCompat.Features queryFeatures(GLCapabilities caps) {
      return new GLCompat.Features(
         caps.OpenGL42 || caps.GL_ARB_texture_storage || caps.GL_EXT_texture_storage,
         caps.OpenGL43 || caps.GL_ARB_compute_shader,
         caps.OpenGL42 || caps.GL_ARB_shader_image_load_store,
         caps.OpenGL43 || caps.GL_ARB_shader_storage_buffer_object,
         caps.OpenGL44 || caps.GL_ARB_clear_texture
      );
   }

   public static void texStorage2D(int target, int levels, int internalFormat, int width, int height) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL42) {
         GL42C.glTexStorage2D(target, levels, internalFormat, width, height);
      } else if (caps.GL_ARB_texture_storage) {
         ARBTextureStorage.glTexStorage2D(target, levels, internalFormat, width, height);
      } else if (caps.GL_EXT_texture_storage) {
         EXTTextureStorage.glTexStorage2DEXT(target, levels, internalFormat, width, height);
      } else {
         GLCompat.PixelFormatType fmt = pixelFormatForInternalFormat(internalFormat);

         for (int level = 0; level < levels; level++) {
            int w = Math.max(1, width >> level);
            int h = Math.max(1, height >> level);
            GL11C.glTexImage2D(target, level, internalFormat, w, h, 0, fmt.format, fmt.type, (ByteBuffer)null);
         }
      }
   }

   public static void texStorage3D(int target, int levels, int internalFormat, int width, int height, int depth) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL42) {
         GL42C.glTexStorage3D(target, levels, internalFormat, width, height, depth);
      } else if (caps.GL_ARB_texture_storage) {
         ARBTextureStorage.glTexStorage3D(target, levels, internalFormat, width, height, depth);
      } else if (caps.GL_EXT_texture_storage) {
         EXTTextureStorage.glTexStorage3DEXT(target, levels, internalFormat, width, height, depth);
      } else {
         GLCompat.PixelFormatType fmt = pixelFormatForInternalFormat(internalFormat);

         for (int level = 0; level < levels; level++) {
            int w = Math.max(1, width >> level);
            int h = Math.max(1, height >> level);
            int d = Math.max(1, depth >> level);
            GL12C.glTexImage3D(target, level, internalFormat, w, h, d, 0, fmt.format, fmt.type, (ByteBuffer)null);
         }
      }
   }

   public static void clearR32UITexture3D(int texture, int width, int height, int depth) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL44) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            GL44C.glClearTexImage(texture, 0, 36244, 5125, stack.ints(0));
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var11.addSuppressed(var9);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }
      } else if (caps.GL_ARB_clear_texture) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            ARBClearTexture.glClearTexImage(texture, 0, 36244, 5125, stack.ints(0));
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var8) {
                  var10.addSuppressed(var8);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }
      } else {
         IntBuffer zeros = zeroInts(width * height * depth);
         zeros.limit(width * height * depth);
         GL11C.glBindTexture(32879, texture);
         GL12C.glTexSubImage3D(32879, 0, 0, 0, 0, width, height, depth, 36244, 5125, zeros);
         GL11C.glBindTexture(32879, 0);
      }
   }

   public static void clearR32UIBuffer(int target, int bufferHandle, int elementCount) {
      GLCapabilities caps = GL.getCapabilities();
      GL15C.glBindBuffer(target, bufferHandle);
      if (caps.OpenGL43) {
         GL43C.glClearBufferData(target, 33334, 36244, 5125, (ByteBuffer)null);
      } else {
         IntBuffer zeros = zeroInts(elementCount);
         zeros.limit(elementCount);
         GL15C.glBufferSubData(target, 0L, zeros);
      }

      GL15C.glBindBuffer(target, 0);
   }

   public static boolean bindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL42) {
         GL42C.glBindImageTexture(unit, texture, level, layered, layer, access, format);
         return true;
      } else if (caps.GL_ARB_shader_image_load_store) {
         ARBShaderImageLoadStore.glBindImageTexture(unit, texture, level, layered, layer, access, format);
         return true;
      } else {
         return false;
      }
   }

   public static boolean memoryBarrier(int barriers) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL42) {
         GL42C.glMemoryBarrier(barriers);
         return true;
      } else if (caps.GL_ARB_shader_image_load_store) {
         ARBShaderImageLoadStore.glMemoryBarrier(barriers);
         return true;
      } else {
         return false;
      }
   }

   public static boolean dispatchCompute(int x, int y, int z) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL43) {
         GL43C.glDispatchCompute(x, y, z);
         return true;
      } else if (caps.GL_ARB_compute_shader) {
         ARBComputeShader.glDispatchCompute(x, y, z);
         return true;
      } else {
         return false;
      }
   }

   public static String buildComputeShaderHeader(boolean needsSsbo, boolean needsImageLoadStore) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL43) {
         return "#version 430 core\n";
      } else if (!caps.GL_ARB_compute_shader) {
         throw new IllegalStateException("Compute shader requested, but GL_ARB_compute_shader is not supported.");
      } else {
         StringBuilder sb = new StringBuilder(192);
         sb.append("#version 420 core\n");
         sb.append("#extension GL_ARB_compute_shader : require\n");
         if (needsSsbo) {
            if (!caps.GL_ARB_shader_storage_buffer_object) {
               throw new IllegalStateException("Compute shader requires GL_ARB_shader_storage_buffer_object, but it is not supported.");
            }

            sb.append("#extension GL_ARB_shader_storage_buffer_object : require\n");
         }

         if (needsImageLoadStore) {
            if (!caps.OpenGL42 && !caps.GL_ARB_shader_image_load_store) {
               throw new IllegalStateException("Compute shader requires shader image load/store, but it is not supported.");
            }

            sb.append("#extension GL_ARB_shader_image_load_store : require\n");
         }

         return sb.toString();
      }
   }

   private static IntBuffer zeroInts(int elements) {
      if (elements <= 0) {
         return BufferUtils.createIntBuffer(0);
      } else if (zeroIntCache != null && zeroIntCacheElems >= elements) {
         zeroIntCache.clear();
         return zeroIntCache;
      } else {
         if (zeroIntCache != null) {
            MemoryUtil.memFree(zeroIntCache);
         }

         zeroIntCache = MemoryUtil.memCallocInt(elements);
         zeroIntCacheElems = elements;
         return zeroIntCache;
      }
   }

   private static GLCompat.PixelFormatType pixelFormatForInternalFormat(int internalFormat) {
      return switch (internalFormat) {
         case 32856 -> new GLCompat.PixelFormatType(6408, 5121);
         case 33321 -> new GLCompat.PixelFormatType(6403, 5121);
         case 33325 -> new GLCompat.PixelFormatType(6403, 5131);
         case 33334 -> new GLCompat.PixelFormatType(36244, 5125);
         case 34842 -> new GLCompat.PixelFormatType(6408, 5131);
         default -> throw new IllegalArgumentException("Unsupported internalFormat mapping: 0x" + Integer.toHexString(internalFormat));
      };
   }

   public static final class Features {
      public final boolean textureStorage;
      public final boolean compute;
      public final boolean imageLoadStore;
      public final boolean shaderStorageBuffer;
      public final boolean clearTexture;
      public final boolean supportsSmokeComputePipeline;

      private Features(boolean textureStorage, boolean compute, boolean imageLoadStore, boolean shaderStorageBuffer, boolean clearTexture) {
         this.textureStorage = textureStorage;
         this.compute = compute;
         this.imageLoadStore = imageLoadStore;
         this.shaderStorageBuffer = shaderStorageBuffer;
         this.clearTexture = clearTexture;
         this.supportsSmokeComputePipeline = compute && imageLoadStore && shaderStorageBuffer;
      }
   }

   private static final class PixelFormatType {
      final int format;
      final int type;

      PixelFormatType(int format, int type) {
         this.format = format;
         this.type = type;
      }
   }
}
