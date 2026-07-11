package net.diebuddies.physics;

import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import de.fabmax.physxjni.Platform;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.diebuddies.bridge.FabricAPI;
import net.diebuddies.bridge.FabricAPIServer;
import net.diebuddies.bridge.KeyBindingsRegistry;
import net.diebuddies.bridge.ModLoaderFunctions;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.compat.Iris;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigCloth;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.config.ConfigSnow;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform.Architecture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import physx.PxTopLevelFunctions;
import physx.common.PxCudaContextManager;
import physx.common.PxCudaContextManagerDesc;
import physx.common.PxCudaTopLevelFunctions;
import physx.common.PxDefaultAllocator;
import physx.common.PxErrorCallback;
import physx.common.PxErrorCallbackImpl;
import physx.common.PxErrorCodeEnum;
import physx.common.PxFoundation;
import physx.common.PxTolerancesScale;
import physx.cooking.PxConvexMeshCookingTypeEnum;
import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;

public class StarterClient {
   public static final MemoryStack memoryStack = MemoryStack.create(5242880);
   public static final Logger logger = LogManager.getLogger("Physics Mod");
   public static boolean optifabric;
   private static boolean iris;
   public static boolean sodium;
   public static boolean voxy;
   public static boolean replay;
   public static boolean flashback;
   public static boolean farsight;
   public static boolean immersivePortals;
   public static boolean valkyrienSkies;
   public static boolean soundPhysicsRemastered;
   public static boolean emf;
   public static boolean vivecraft;
   public static boolean disableLightingCache;
   public static boolean entityCulling;
   public static volatile boolean newUpdateAvailable;
   public static final boolean PRO_VERSION = false;
   public static boolean cudaAvailable = false;
   public static PxPhysics physics;
   public static PxTolerancesScale tolerances;
   public static PxCookingParams cookingParams;
   public static PxMaterial defaultMaterial;
   public static PxMaterial smokeMaterial;
   public static PxMaterial throwableMaterial;
   public static PxFoundation foundation;
   public static PxCudaContextManager cudaManager;
   public static int physxVersion;
   public static volatile String updateMessage = "";
   public static volatile String customMessage = "";

   public static void onInitializeClient(IEventBus modEventBus, ModContainer modContainer) {
      SharedConstants.IS_RUNNING_IN_IDE = false;
      boolean updateNotifications = ConfigClient.showUpdateNotifications;
      Thread checkVersion = new Thread(
         () -> {
            String currentPhysicsModVersion = ModLoaderFunctions.getModID();
            String url = "https://minecraftphysicsmod.com/versions?format=rss";

            try {
               String currentMCVersion = DetectedVersion.tryDetectVersion().id();
               String text = getText(url);
               Document doc = convertStringToXMLDocument(text);
               NodeList entries = doc.getElementsByTagName("item");

               for (int i = 0; i < entries.getLength(); i++) {
                  Node node = entries.item(i);

                  try {
                     Element element = (Element)node;
                     String title = element.getElementsByTagName("title").item(0).getTextContent();
                     if (title.startsWith("Custom_Message:")) {
                        customMessage = title.replaceFirst("Custom_Message:", "");
                     } else {
                        String[] split = title.split(":");
                        String type = split[0];
                        String mcVersion = split[1];
                        String physicsVersion = split[2];
                        if (type.equalsIgnoreCase(ModLoaderFunctions.getModloader())
                           && mcVersion.equals(currentMCVersion)
                           && !currentPhysicsModVersion.equals(physicsVersion)
                           && updateNotifications) {
                           updateMessage = "{\"text\":\"NEW PHYSICS VERSION AVAILABLE! CLICK HERE!\",\"bold\":true,\"underlined\":true,\"color\":\"red\",\"click_event\":{\"action\":\"open_url\",\"url\":\"https://minecraftphysicsmod.com/\"}}";
                           newUpdateAvailable = true;
                        }
                     }
                  } catch (Exception var15) {
                  }
               }
            } catch (Exception var16) {
               var16.printStackTrace();
            }
         }
      );
      checkVersion.setName("Version Check Thread");
      checkVersion.setDaemon(true);
      checkVersion.start();
      if (ModLoaderFunctions.isModLoaded("optifabric") || ModLoaderFunctions.isModLoaded("optifine")) {
         optifabric = true;
      }

      iris = ModLoaderFunctions.isModLoaded("iris") || ModLoaderFunctions.isModLoaded("oculus");
      sodium = ModLoaderFunctions.isModLoaded("sodium") || iris || ModLoaderFunctions.isModLoaded("rubidium") || ModLoaderFunctions.isModLoaded("embeddium");
      replay = ModLoaderFunctions.isModLoaded("replaymod");
      flashback = ModLoaderFunctions.isModLoaded("flashback");
      immersivePortals = ModLoaderFunctions.isModLoaded("immersive_portals");
      soundPhysicsRemastered = ModLoaderFunctions.isModLoaded("sound_physics_remastered");
      farsight = ModLoaderFunctions.isModLoaded("farsight");
      valkyrienSkies = ModLoaderFunctions.isModLoaded("valkyrienskies");
      emf = ModLoaderFunctions.isModLoaded("entity_model_features");
      vivecraft = ModLoaderFunctions.isModLoaded("vivecraft");
      voxy = ModLoaderFunctions.isModLoaded("voxy");
      entityCulling = ModLoaderFunctions.isModLoaded("entityculling");
      if (soundPhysicsRemastered) {
         ContactSimulationCallback.RESET_SOUNDS_PER_TICK_EVERY_X_TICKS = 20;
      }

      ConfigClient.init();
      ConfigMobs.init();
      ConfigBlocks.init();
      ConfigCloth.init();
      ConfigSnow.init();
      PhysicsShaders.init();
      if (iris) {
         Iris.init();
      }

      WeatherParticlesRegistry.register(modEventBus);
      KeyBindingsRegistry.register(modEventBus);
      modContainer.registerExtensionPoint(
         IConfigScreenFactory.class, (IConfigScreenFactory)(container, modListScreen) -> new PhysicsSettingsScreen(modListScreen)
      );
      physxVersion = PxTopLevelFunctions.getPHYSICS_VERSION();
      final PxDefaultAllocator allocator = new PxDefaultAllocator();
      final PxErrorCallback errorCb = new PxErrorCallbackImpl() {
         @Override
         public void reportError(PxErrorCodeEnum code, String message, String file, int line) {
            StarterClient.logger.error(code + ": " + message);
            Thread.dumpStack();
         }
      };
      foundation = PxTopLevelFunctions.CreateFoundation(physxVersion, allocator, errorCb);
      tolerances = new PxTolerancesScale();
      physics = PxTopLevelFunctions.CreatePhysics(physxVersion, foundation, tolerances);
      cudaAvailable = false;
      createPhysicsCooking(ConfigClient.useCuda());
      defaultMaterial = physics.createMaterial(1.0F, 1.0F, 0.0F);
      smokeMaterial = physics.createMaterial(0.0F, 0.0F, 0.95F);
      throwableMaterial = physics.createMaterial(1.0F, 1.0F, 0.75F);
      FabricAPI.CLIENT_STOPPING.register(new FabricAPI.ClientStopping() {
         @Override
         public void onClientStopping(Minecraft client) {
            ObjectIterator var2 = PhysicsMod.getInstances().values().iterator();

            while (var2.hasNext()) {
               PhysicsMod mod = (PhysicsMod)var2.next();
               mod.physicsWorld.destroy();
            }

            PhysicsMod.getInstances().clear();

            for (Cloth cloth : PhysicsMod.cloth.values()) {
               cloth.destroy();
            }

            if (PhysicsMod.defaultCape != null) {
               PhysicsMod.defaultCape.destroy();
            }

            StarterClient.defaultMaterial.release();
            StarterClient.smokeMaterial.release();
            StarterClient.throwableMaterial.release();
            StarterClient.physics.release();
            if (StarterClient.cudaManager != null) {
               StarterClient.cudaManager.release();
            }

            StarterClient.cookingParams.destroy();
            StarterClient.tolerances.destroy();
            StarterClient.foundation.release();
            errorCb.destroy();
            allocator.destroy();
            PhysicsMod.foamTexture.close();
         }
      });
      ServerPhysicsMod server = new ServerPhysicsMod();
      FabricAPIServer.START_WORLD_TICK.register(server);
      FabricAPIServer.AFTER.register(server);
   }

   public static boolean iris() {
      GpuDevice device = RenderSystem.tryGetDevice();
      boolean glDevice = true;
      if (device != null) {
         glDevice = device.backend instanceof GlDevice;
      }

      return iris && glDevice;
   }

   public static void createPhysicsCooking(boolean cudaEnabled) {
      if (cookingParams != null) {
         if (cudaManager != null && cudaEnabled) {
            return;
         }

         if (cudaManager == null && !cudaEnabled) {
            return;
         }
      }

      if (cookingParams != null) {
         cookingParams.destroy();
      }

      cookingParams = new PxCookingParams(tolerances);
      cookingParams.setConvexMeshCookingType(PxConvexMeshCookingTypeEnum.eQUICKHULL);
      cookingParams.setSuppressTriangleMeshRemapTable(true);
      if (cudaManager != null) {
         cudaManager.release();
      }

      if (cudaEnabled) {
         cudaManager = createCudaManager();
         if (!cudaManager.contextIsValid()) {
            cudaManager.release();
            cudaManager = null;
            logger.error("failed creating valid CUDA context");
         } else {
            cookingParams.setBuildGPUData(true);
         }
      } else {
         cudaManager = null;
      }
   }

   private static boolean isCudaAvailable() {
      if (Platform.getPlatform() != Platform.MACOS && Platform.getPlatform() != Platform.MACOS_ARM64 && Platform.getPlatform() != Platform.LINUX) {
         Architecture arch = org.lwjgl.system.Platform.getArchitecture();
         return arch != Architecture.X64 && arch != Architecture.X86 ? false : PxCudaTopLevelFunctions.GetSuggestedCudaDeviceOrdinal(foundation) >= 0;
      } else {
         return false;
      }
   }

   private static PxCudaContextManager createCudaManager() {
      if (!isCudaAvailable()) {
         System.err.println("CUDA is not available or disabled on this platform");
         return null;
      } else {
         MemoryStack mem = MemoryStack.stackPush();

         PxCudaContextManager var6;
         label49: {
            try {
               PxCudaContextManagerDesc desc = PxCudaContextManagerDesc.createAt(mem, MemoryStack::nmalloc);
               PxCudaContextManager cudaMgr = PxCudaTopLevelFunctions.CreateCudaContextManager(foundation, desc);
               if (cudaMgr != null && cudaMgr.contextIsValid()) {
                  var6 = cudaMgr;
                  break label49;
               }

               System.err.println("Failed creating CUDA context, no CUDA capable GPU?");
               var6 = null;
            } catch (Throwable var5) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (mem != null) {
               mem.close();
            }

            return var6;
         }

         if (mem != null) {
            mem.close();
         }

         return var6;
      }
   }

   private static Document convertStringToXMLDocument(String xmlString) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = null;

      try {
         builder = factory.newDocumentBuilder();
         return builder.parse(new InputSource(new StringReader(xmlString)));
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static String getText(String urlString) throws Exception {
      URL url = new URL(urlString);
      URLConnection con = url.openConnection();
      con.setReadTimeout(10000);
      con.setConnectTimeout(10000);
      String text = "";

      String line;
      try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
         while ((line = in.readLine()) != null) {
            text = text + line + "\n";
         }
      }

      return text;
   }
}
