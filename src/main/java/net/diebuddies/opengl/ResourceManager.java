package net.diebuddies.opengl;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.diebuddies.render.shader.ShaderResourceProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

public class ResourceManager {
   private static final int THREAD_POOL_SIZE = 1;
   private Object2ObjectMap<Identifier, LoadableTexture> textures;
   private ExecutorService asynchronousLoadingExecutor;
   private List<Future<Runnable>> tasks = new ObjectArrayList();
   private Set<LoadableTexture> destructionQueue;

   public ResourceManager() {
      this.textures = new Object2ObjectOpenHashMap();
      this.asynchronousLoadingExecutor = Executors.newFixedThreadPool(1);
      this.destructionQueue = new ObjectOpenHashSet();
   }

   public void update() {
      for (int i = 0; i < this.tasks.size(); i++) {
         Future<Runnable> result = this.tasks.get(i);
         if (result.isDone()) {
            try {
               Runnable event = result.get();
               if (event != null) {
                  event.run();
               }
            } catch (InterruptedException var8) {
               var8.printStackTrace();
            } catch (ExecutionException var9) {
               var9.printStackTrace();
            } finally {
               this.tasks.remove(i--);
            }
         }
      }

      Iterator<LoadableTexture> it = this.destructionQueue.iterator();

      while (it.hasNext()) {
         LoadableTexture dummy = it.next();
         if (dummy.isLoaded()) {
            dummy.close();
            it.remove();
         }
      }
   }

   public boolean isLoading() {
      return !this.tasks.isEmpty();
   }

   private void doAsynchronous(Callable<Runnable> runnable) {
      if (!this.asynchronousLoadingExecutor.isShutdown()) {
         this.tasks.add(this.asynchronousLoadingExecutor.submit(runnable));
      }
   }

   public LoadableTexture loadTexture(final Identifier path) {
      final LoadableTexture dummy = new LoadableTexture();
      this.textures.put(path, dummy);
      this.doAsynchronous(new Callable<Runnable>() {
         public Runnable call() throws Exception {
            try {
               Runnable var3;
               try (InputStream stream = ResourceManager.this.processResourceAsStream(path)) {
                  final NativeImage img = NativeImage.read(stream);
                  var3 = new Runnable() {
                     @Override
                     public void run() {
                        GpuDevice device = RenderSystem.getDevice();
                        GpuTexture texture = device.createTexture(path.toString(), 5, GpuFormat.RGBA8_UNORM, img.getWidth(), img.getHeight(), 1, 1);
                        device.createCommandEncoder().writeToTexture(texture, img);
                        dummy.setTexture(texture);
                        dummy.setTextureView(device.createTextureView(texture));
                        img.close();
                     }
                  };
               }

               return var3;
            } catch (IOException var6) {
               var6.printStackTrace();
               return null;
            }
         }
      });
      return dummy;
   }

   public LoadableTexture getTexture(Identifier path) {
      LoadableTexture texture = (LoadableTexture)this.textures.get(path);
      return texture == null ? this.loadTexture(path) : texture;
   }

   public void destroy() {
      this.asynchronousLoadingExecutor.shutdown();
      long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5L);

      while (!this.tasks.isEmpty() && System.nanoTime() < deadline) {
         this.update();

         try {
            Thread.sleep(1L);
         } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
            break;
         }
      }

      try {
         if (!this.asynchronousLoadingExecutor.awaitTermination(5L, TimeUnit.SECONDS)) {
            this.asynchronousLoadingExecutor.shutdownNow();
         }
      } catch (InterruptedException var5) {
         this.asynchronousLoadingExecutor.shutdownNow();
         Thread.currentThread().interrupt();
      }

      this.update();
      ObjectIterator e = this.textures.values().iterator();

      while (e.hasNext()) {
         LoadableTexture texture = (LoadableTexture)e.next();
         texture.close();
      }

      this.textures.clear();
   }

   public void destroyTexture(Identifier imageLocation) {
      LoadableTexture dummy = (LoadableTexture)this.textures.remove(imageLocation);
      if (dummy != null) {
         if (dummy.isLoaded()) {
            dummy.close();
         } else {
            this.destructionQueue.add(dummy);
         }
      }
   }

   private boolean isResourceUrlValid(String string, @Nullable URL url) throws IOException {
      return url != null && (url.getProtocol().equals("jar") || this.validatePath(new File(url.getFile()), string));
   }

   private boolean validatePath(File file, String string) throws IOException {
      String canonicalPath = file.getCanonicalPath();
      return canonicalPath.endsWith(string);
   }

   private InputStream processResourceAsStream(Identifier Identifier) {
      String path = this.createPath(Identifier);

      try {
         URL url = ShaderResourceProvider.class.getResource(path);
         return this.isResourceUrlValid(path, url) ? url.openStream() : ShaderResourceProvider.class.getResourceAsStream(path);
      } catch (IOException var4) {
         return ShaderResourceProvider.class.getResourceAsStream(path);
      }
   }

   private String createPath(Identifier Identifier) {
      return "/" + PackType.CLIENT_RESOURCES.getDirectory() + "/physicsmod/" + Identifier.getPath();
   }

   public int getLoadedTexturesSize() {
      return this.textures.size();
   }
}
