package net.diebuddies.physics;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import net.diebuddies.bridge.ModLoaderFunctions;
import net.diebuddies.compat.EMF;
import net.diebuddies.compat.Flashback;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.compat.Replay;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.RayIntersection;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.model.ColladaParser;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.ragdoll.RagdollMapper;
import net.diebuddies.physics.render.BlockEntityVertexConsumer;
import net.diebuddies.physics.render.PhysicsFeatureRenderDispatcher;
import net.diebuddies.physics.render.PhysicsSubmitNodeStorage;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.ClothRules;
import net.diebuddies.physics.verlet.ClothTexture;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.vines.DynamicLoader;
import net.diebuddies.render.util.PhysicsRenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.geom.ModelPart.Polygon;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.BlockModelFeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.MovingBlockFeatureRenderer;
import net.minecraft.client.renderer.feature.TextFeatureRenderer;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer.Group;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import physx.common.PxVec3;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;

public class PhysicsMod {
   public static final Path CLOTH_DIRECTORY = ModLoaderFunctions.getGameDir().resolve("cloth_local");
   public static final Path CLOTH_SYNCHRONIZED_DIRECTORY = ModLoaderFunctions.getGameDir().resolve(".physics_mod_cache/cloth");
   public static final Identifier WHITE_TEXTURE = Identifier.parse("physicsmod:textures/gui/white.png");
   public static final Identifier BLACK_TEXTURE = Identifier.parse("physicsmod:textures/gui/black.png");
   public static final Identifier SNOWBALL_TEXTURE = Identifier.parse("physicsmod:textures/items/snowball.png");
   public static final Identifier ENDERPEARL_TEXTURE = Identifier.parse("physicsmod:textures/items/enderpearl.png");
   public static final Identifier EGG_TEXTURE = Identifier.parse("physicsmod:textures/items/egg.png");
   public static final Identifier SMOKE_TEXTURE = Identifier.parse("physicsmod:textures/smoke/smoke.png");
   public static final Identifier PUDDLE_TEXTURE = Identifier.parse("physicsmod:textures/ocean/puddle.png");
   private static GpuBufferSlice shaderLights;
   public static Object2ObjectMap<ClientLevel, PhysicsMod> instances = new Object2ObjectOpenHashMap();
   private static PhysicsMod currentInstance;
   public static final Map<EntityType<?>, EntityRenderer<?, ?>> renderers = new Object2ObjectOpenHashMap();
   public static final Map<Block, String> registeredBlocks = new Object2ObjectOpenHashMap();
   public static final Map<String, Block> invRegisteredBlocks = new Object2ObjectOpenHashMap();
   public static final Map<String, ParticleOptions> registeredParticles = new Object2ObjectAVLTreeMap();
   public static final Map<ParticleOptions, String> invRegisteredParticles = new Object2ObjectOpenHashMap();
   public static final Map<String, SoundEvent> registeredSounds = new Object2ObjectAVLTreeMap();
   public static final Map<SoundEvent, String> invRegisteredSounds = new Object2ObjectOpenHashMap();
   public static boolean clothSkipRenderQueue = true;
   public static boolean stopOceanDisplacement = false;
   public static boolean sodiumCatch;
   public static boolean sodiumCatchBoundingBox;
   public static AABB3D sodiumBoundingBox = new AABB3D(new Vector3d(), new Vector3d());
   public static boolean clothSmootShadingIrisFix;
   public static Map<BlockStateModelPart, JsonUnbakedModelHolder> loadedModels = new ConcurrentHashMap<>();
   public static Map<PhysicsMod.RenderPass, List<VerletSimulation>> dynamicCloth = new Object2ObjectOpenHashMap();
   public static Map<PhysicsMod.RenderPass, List<ClothRenderCommand>> clothRenderFast = new Object2ObjectOpenHashMap();
   public static boolean reloadCloth;
   public static Matrix4f itemBreakTransformation;
   public static boolean disabledEMFModel;
   public static boolean smokeParticlePrevention;
   public PhysicsWorld physicsWorld;
   public boolean init = false;
   public ConcurrentLinkedQueue<PhysicsEntity> entityBlocks = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<BlockPos> blockUpdates = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<Explosion> explosions = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<Ragdoll> ragdolls = new ConcurrentLinkedQueue<>();
   public ConcurrentLinkedQueue<Ragdoll> sodiumRemoveRagdolls = new ConcurrentLinkedQueue<>();
   public Set<BlockPos> fallingBlocks = new ObjectOpenHashSet();
   public ConcurrentLinkedQueue<BlockUpdate> updateQueue = new ConcurrentLinkedQueue<>();
   public List<PhysicsEntity> blockifiedEntity = new ObjectArrayList();
   public PhysicsEntity itemStackEntity;
   public Set<BlockUpdate> removeUpdates = new ObjectOpenHashSet();
   public Set<Integer> alreadyBlockified = new ObjectOpenHashSet();
   public LongSet updatedLightBlocks = new LongOpenHashSet();
   public PoseStack localPivotMatrix = new PoseStack();
   public EntityRenderer cubifyEntityRenderer;
   public Entity cubifyEntity;
   public boolean cubifyTranslucent;
   public static volatile boolean destroyNextTick;
   public long time;
   public boolean blockify;
   public Entity blockifyEntity;
   public RenderLayer blockifyFeature;
   public static final List<List<Mesh>> brokenBlocksLittle = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLots = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLittleVoxel = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLotsVoxel = new ObjectArrayList();
   public static final List<List<Mesh>> brokenBlocksLittleVoxelPhysics = createPhysicsMeshes(brokenBlocksLittle, brokenBlocksLittleVoxel);
   public static final List<List<Mesh>> brokenBlocksLotsVoxelPhysics = createPhysicsMeshes(brokenBlocksLots, brokenBlocksLotsVoxel);
   public static final List<Mesh> brokenBlock = readBlock("assets/physicsmod/models/fractures/physics_simple.obj");
   public static final List<Mesh> snowballMesh = new ObjectArrayList();
   public static final List<List<Mesh>> snowballMeshFractured = new ObjectArrayList();
   public static final List<Mesh> enderpearlMesh = new ObjectArrayList();
   public static final List<List<Mesh>> enderpearlMeshFractured = new ObjectArrayList();
   public static final List<Mesh> eggMesh = new ObjectArrayList();
   public static final List<List<Mesh>> eggMeshFractured = new ObjectArrayList();
   public static final Mesh smoke = readBlock("assets/physicsmod/models/smoke/smoke.obj").get(0);
   public static final Mesh liquid = readBlock("assets/physicsmod/models/liquid/liquid.obj").get(0);
   public static Cloth defaultCape;
   public static GpuTextureView foamTexture;
   public static Map<String, Cloth> cloth;
   private static final Direction[] DIRECTIONS = Direction.values();

   public static PhysicsMod.RenderPass getRenderPass() {
      if (StarterClient.iris() && Iris.isExtending() && Iris.isShadowPass()) {
         return PhysicsMod.RenderPass.SHADOW;
      } else {
         return StarterClient.optifabric && Optifine.isUsingShadersNoInternal() && Optifine.isShadowPass()
            ? PhysicsMod.RenderPass.SHADOW
            : PhysicsMod.RenderPass.SOLID;
      }
   }

   private static List<List<Mesh>> createPhysicsMeshes(List<List<Mesh>> physicsReferences, List<List<Mesh>> visuals) {
      List<List<Mesh>> results = new ObjectArrayList();

      for (int i = 0; i < physicsReferences.size(); i++) {
         List<Mesh> pr = physicsReferences.get(i);
         List<Mesh> vs = visuals.get(i);
         List<Mesh> physicsMeshes = new ObjectArrayList();

         for (int j = 0; j < pr.size(); j++) {
            Mesh physicsMesh = new Mesh(false);
            Mesh physicsReference = pr.get(j);
            Mesh visual = vs.get(j);
            Vector3f difference = physicsReference.offset.sub(visual.offset, new Vector3f());

            for (Vector3f position : physicsReference.positions) {
               physicsMesh.positions.add(new Vector3f(position).add(difference));
            }

            physicsMeshes.add(physicsMesh);
         }

         results.add(physicsMeshes);
      }

      return results;
   }

   private static List<Mesh> readBlock(String asset) {
      List<Mesh> meshes = new ObjectArrayList();
      List<Vector3i> indices = new ObjectArrayList();
      List<Vector3i> indicesQuads = new ObjectArrayList();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(asset)))) {
         String line = "";
         Mesh mesh = null;
         int ov = 0;
         int ot = 0;
         int on = 0;

         while ((line = reader.readLine()) != null) {
            if (line.startsWith("o")) {
               if (mesh != null) {
                  ov += mesh.positions.size();
                  ot += mesh.uvs.size();
                  on += mesh.normals.size();
                  mesh = unrollMeshIndices(mesh, indices, indicesQuads);
                  indices.clear();
                  indicesQuads.clear();
                  meshes.add(mesh);
               }

               mesh = new Mesh(false);
            } else if (line.startsWith("vt")) {
               String[] data = line.split(" ");
               mesh.uvs.add(new Vector2f(Float.parseFloat(data[1]), Float.parseFloat(data[2])));
            } else if (line.startsWith("vn")) {
               String[] data = line.split(" ");
               mesh.normals.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
            } else if (line.startsWith("v")) {
               String[] data = line.split(" ");
               mesh.positions.add(new Vector3f(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
            } else if (line.startsWith("f")) {
               String[] data = line.split(" ");
               boolean quads = data.length == 5;

               for (int i = 1; i < data.length; i++) {
                  String[] idata = data[i].split("/");
                  if (quads) {
                     indicesQuads.add(new Vector3i(Integer.parseInt(idata[0]) - ov, Integer.parseInt(idata[1]) - ot, Integer.parseInt(idata[2]) - on));
                  } else {
                     indices.add(new Vector3i(Integer.parseInt(idata[0]) - ov, Integer.parseInt(idata[1]) - ot, Integer.parseInt(idata[2]) - on));
                  }
               }

               if (quads) {
                  int index = indicesQuads.size() - 4;
                  indices.add(indicesQuads.get(index));
                  indices.add(indicesQuads.get(index + 1));
                  indices.add(indicesQuads.get(index + 2));
                  indices.add(indicesQuads.get(index));
                  indices.add(indicesQuads.get(index + 2));
                  indices.add(indicesQuads.get(index + 3));
               }
            }
         }

         if (mesh != null) {
            mesh = unrollMeshIndices(mesh, indices, indicesQuads);
            meshes.add(mesh);
         }

         for (Mesh m : meshes) {
            m.calculateOffset(true);
            m.calculatePBRData(true);
         }
      } catch (IOException var16) {
         var16.printStackTrace();
      }

      return meshes;
   }

   private static Mesh unrollMeshIndices(Mesh mesh, List<Vector3i> indices, List<Vector3i> indicesQuads) {
      Mesh unrolledMesh = new Mesh(false);
      if (indicesQuads.size() > 0) {
         indices.clear();

         for (int i = 0; i < indicesQuads.size() / 4; i++) {
            int indexQuads = i * 6;
            unrolledMesh.indicesQuads.add(indexQuads);
            unrolledMesh.indicesQuads.add(indexQuads + 1);
            unrolledMesh.indicesQuads.add(indexQuads + 2);
            unrolledMesh.indicesQuads.add(indexQuads + 5);
            int indexTriangles = i * 4;
            indices.add(indicesQuads.get(indexTriangles));
            indices.add(indicesQuads.get(indexTriangles + 1));
            indices.add(indicesQuads.get(indexTriangles + 2));
            indices.add(indicesQuads.get(indexTriangles));
            indices.add(indicesQuads.get(indexTriangles + 2));
            indices.add(indicesQuads.get(indexTriangles + 3));
         }
      }

      for (int i = 0; i < indices.size(); i++) {
         Vector3i index = indices.get(i);
         int ipos = index.x - 1;
         int iuv = index.y - 1;
         int inormal = index.z - 1;
         unrolledMesh.positions.add(new Vector3f((Vector3fc)mesh.positions.get(ipos)));
         unrolledMesh.uvs.add(new Vector2f((Vector2fc)mesh.uvs.get(iuv)));
         unrolledMesh.normals.add(new Vector3f((Vector3fc)mesh.normals.get(inormal)));
         unrolledMesh.indices.add(i);
      }

      return unrolledMesh;
   }

   public static void createClothDirectory() {
   }

   public static void loadCloth() {
      if (foamTexture == null) {
         foamTexture = PhysicsRenderUtil.load3DTexture("assets/physicsmod/textures/ocean/foam.dat", 256, 256, 16);
      }

      if (defaultCape == null) {
         try (BufferedReader reader = new BufferedReader(
               new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream("assets/physicsmod/cloth/default.dae"))
            )) {
            ColladaMesh mesh = ColladaParser.loadStaticModel(reader);
            mesh.flipUVs();
            defaultCape = new Cloth("Vanilla Cape", mesh, null, null, new ClothRules());
         } catch (Exception var5) {
            System.err.println("Couldn't load default cape model");
            var5.printStackTrace();
         }
      }

      if (cloth != null) {
         for (Cloth cape : cloth.values()) {
            cape.destroy();
         }
      }

      cloth = new Object2ObjectLinkedOpenHashMap();
      loadClothFromDirectory(CLOTH_SYNCHRONIZED_DIRECTORY, false);
   }

   private static void loadClothFromDirectory(Path clothDirectory, boolean local) {
      try {
         Collection<Path> files = Files.list(clothDirectory).filter(PhysicsMod::isValidCloth).collect(Collectors.toList());
         List<Cloth> sortedCloth = new ObjectArrayList();

         for (Path path : files) {
            File model = path.toFile();
            String name = path.getFileName().toString();
            if (name.contains(".")) {
               name = name.substring(0, name.lastIndexOf(46));
            }

            String pathNoExtension = path.toFile().getAbsolutePath().substring(0, path.toFile().getAbsolutePath().length() - 3);
            String texture = pathNoExtension.concat("png");
            File rules = new File(pathNoExtension.concat("rules"));

            try (
               FileReader fileReader = new FileReader(model);
               BufferedReader reader = new BufferedReader(fileReader);
               FileInputStream textureInput = new FileInputStream(new File(texture));
            ) {
               Map<String, ColladaMesh> meshes = ColladaParser.loadMultipleStaticModel(reader);
               ColladaMesh playerMesh = meshes.remove("Player");
               ColladaMesh clothMesh = meshes.values().iterator().next();
               STBImage.stbi_set_flip_vertically_on_load(true);
               NativeImage clothImg = NativeImage.read(textureInput);
               STBImage.stbi_set_flip_vertically_on_load(false);
               Identifier textureIdentifier = Identifier.fromNamespaceAndPath("physicsmod", "cloth_" + name.replace(" ", "_").toLowerCase());
               ClothTexture clothTexture = new ClothTexture(clothImg, textureIdentifier.toString());
               Minecraft.getInstance().getTextureManager().register(textureIdentifier, clothTexture);
               sortedCloth.add(new Cloth(name, clothMesh, playerMesh, textureIdentifier, ClothRules.load(rules, local)));
            } catch (Exception var26) {
               System.err.println("Couldn't load " + model.toString());
               var26.printStackTrace();
            }
         }

         Collections.sort(sortedCloth);

         for (Cloth cloth : sortedCloth) {
            PhysicsMod.cloth.put(cloth.name, cloth);
         }
      } catch (IOException var27) {
         var27.printStackTrace();
      }
   }

   private static boolean isValidCloth(Path file) {
      try {
         if (file.toFile().getName().endsWith("dae")
            && new File(file.toFile().getAbsolutePath().substring(0, file.toFile().getAbsolutePath().length() - 3).concat("png")).exists()) {
            return true;
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      return false;
   }

   public static void copyResource(String res, Path dest) throws IOException {
      String[] split = res.split("/");

      try (InputStream src = PhysicsMod.class.getClassLoader().getResourceAsStream(res)) {
         Files.copy(src, dest.resolve(split[split.length - 1]), StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception var8) {
      }
   }

   public static void copyClothResource(String res, Path dest) throws IOException {
      copyResource(res + ".dae", dest);
      copyResource(res + ".png", dest);
      copyResource(res + ".rules", dest);
   }

   public static void resetClothSimulations() {
      ObjectIterator var0 = instances.values().iterator();

      while (var0.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var0.next();

         for (VerletSimulation simulation : mod.getPhysicsWorld().getVerletSimulations()) {
            simulation.destroyed = true;
         }
      }
   }

   public static double getPlaybackSpeed(double deltaInSeconds) {
      if (Minecraft.getInstance().isPaused()) {
         return 0.0;
      } else if (StarterClient.flashback) {
         return (double)ConfigClient.playbackSpeed * Flashback.getPlaybackSpeed(deltaInSeconds);
      } else {
         return StarterClient.replay
            ? deltaInSeconds * (double)ConfigClient.playbackSpeed * Replay.getPlaybackSpeed()
            : deltaInSeconds * (double)ConfigClient.playbackSpeed;
      }
   }

   public static void calculatePlaybackSpeed() {
      if (StarterClient.flashback) {
         Flashback.calculatePlaybackSpeed();
      }
   }

   public static PhysicsMod getInstance(ClientLevel level) {
      synchronized (instances) {
         PhysicsMod mod = (PhysicsMod)instances.get(level);
         if (mod == null) {
            mod = new PhysicsMod(level);
            instances.put(level, mod);
         }

         return mod;
      }
   }

   public static PhysicsMod getInstanceNullable(ClientLevel level) {
      return (PhysicsMod)instances.get(level);
   }

   public static PhysicsMod getCurrentInstance() {
      return currentInstance;
   }

   public static void setCurrentInstance(PhysicsMod currentInstance) {
      PhysicsMod.currentInstance = currentInstance;
   }

   public static Object2ObjectMap<ClientLevel, PhysicsMod> getInstances() {
      return instances;
   }

   public PhysicsMod(ClientLevel level) {
      this.physicsWorld = new PhysicsWorld(level);
      ((DynamicLoader)level.getChunkSource()).setPhysicsMod(this);
   }

   public PhysicsWorld getPhysicsWorld() {
      return this.physicsWorld;
   }

   public static void addSnowball(ClientLevel level, Snowball snowball) {
   }

   public static void addEnderpearl(ClientLevel level, ThrownEnderpearl enderpearl) {
   }

   public static void addEgg(ClientLevel level, ThrownEgg egg) {
   }

   public static void addThrowableProjectile(
      ClientLevel level, ThrowableItemProjectile projectile, Mesh mesh, GpuTextureView textureID, boolean shade, List<Mesh> fractures, boolean fastBoxes
   ) {
      PhysicsMod mod = getInstance(level);
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.ITEM, null);
      Vector3d snowballPos = new Vector3d(projectile.getX(), projectile.getY(), projectile.getZ());
      float snowballRadius = 0.14F;
      MutableBlockPos originPos = new MutableBlockPos();
      MutableBlockPos tmpPos = new MutableBlockPos();
      Vector3d rayDir = new Vector3d(projectile.getX() - projectile.xOld, projectile.getY() - projectile.yOld, projectile.getZ() - projectile.zOld)
         .normalize()
         .negate();
      Vector3d aabbMin = new Vector3d();
      Vector3d aabbMax = new Vector3d();
      Set<BlockPos> hitPositions = new ObjectOpenHashSet();

      for (int count = 0; count < 5; count++) {
         float threshold = snowballRadius;
         boolean hasHit = false;
         originPos.set(snowballPos.x, snowballPos.y, snowballPos.z);

         for (int x = -1; x <= 1 && !hasHit; x++) {
            for (int y = -1; y <= 1 && !hasHit; y++) {
               for (int z = -1; z <= 1 && !hasHit; z++) {
                  tmpPos.set(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                  BlockState state = level.getBlockState(tmpPos);
                  if (!state.isAir() && !hitPositions.contains(tmpPos)) {
                     VoxelShape voxelShape = state.getCollisionShape(level, projectile.blockPosition());
                     if (!voxelShape.isEmpty()) {
                        for (AABB aabb : voxelShape.toAabbs()) {
                           if (AABB3D.isInside(
                              aabb.minX - (double)threshold,
                              aabb.minY - (double)threshold,
                              aabb.minZ - (double)threshold,
                              aabb.maxX + (double)threshold,
                              aabb.maxY + (double)threshold,
                              aabb.maxZ + (double)threshold,
                              snowballPos.x,
                              snowballPos.y,
                              snowballPos.z
                           )) {
                              aabbMin.set(aabb.minX - (double)threshold, aabb.minY - (double)threshold, aabb.minZ - (double)threshold);
                              aabbMax.set(aabb.maxX + (double)threshold, aabb.maxY + (double)threshold, aabb.maxZ + (double)threshold);
                              RayIntersection.IntersectionResult result = RayIntersection.intersectAABB(snowballPos, rayDir, aabbMin, aabbMax);
                              if (result.hit) {
                                 hasHit = true;
                                 snowballPos.x = snowballPos.x + rayDir.x * result.fraction;
                                 snowballPos.y = snowballPos.y + rayDir.y * result.fraction;
                                 snowballPos.z = snowballPos.z + rayDir.z * result.fraction;
                                 hitPositions.add(new BlockPos(tmpPos));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (hasHit) {
            break;
         }
      }

      entity.getTransformation().translation(snowballPos);
      Random random = new Random((long)projectile.getId());
      float progress = (float)projectile.tickCount;
      entity.getTransformation().rotateX(random.nextDouble() * Math.PI);
      entity.getTransformation().rotateY(random.nextDouble() * Math.PI);
      entity.getTransformation().rotateZ(random.nextDouble() * Math.PI + (double)progress * 0.5);
      Model model = entity.models.get(0);
      model.textureID = textureID;
      model.backfaceCulling = true;
      model.shade = shade;
      model.mesh = mesh;
      List<IRigidBody> bodies = new ObjectArrayList();
      if (fractures != null) {
         mod.physicsWorld.addBlockParticle(fractures, entity, null, bodies, true);
      } else {
         IRigidBody body = mod.physicsWorld.addPhysicsSphere(entity, snowballRadius);
         ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity((float)Math.toRadians(360.0));
         ((PxRigidDynamic)body.getRigidBody()).setRigidBodyFlag(PxRigidBodyFlagEnum.eENABLE_SPECULATIVE_CCD, true);
         ((PxRigidDynamic)body.getRigidBody()).setLinearDamping(0.9F);
         ((PxRigidDynamic)body.getRigidBody()).setAngularDamping(0.9F);
         bodies.add(body);
      }

      double speedX = projectile.getX() - projectile.xOld;
      double speedY = projectile.getY() - projectile.yOld;
      double speedZ = projectile.getZ() - projectile.zOld;
      float speedMultiplier = 10.0F;

      for (IRigidBody body : bodies) {
         MemoryStack mem = MemoryStack.stackPush();

         try {
            ((PxRigidDynamic)body.getRigidBody())
               .setLinearVelocity(
                  PxVec3.createAt(mem, MemoryStack::nmalloc, (float)speedX * speedMultiplier, (float)speedY * speedMultiplier, (float)speedZ * speedMultiplier)
               );
         } catch (Throwable var34) {
            if (mem != null) {
               try {
                  mem.close();
               } catch (Throwable var33) {
                  var34.addSuppressed(var33);
               }
            }

            throw var34;
         }

         if (mem != null) {
            mem.close();
         }
      }
   }

   public static void blockifyEntity(ClientLevel level, LivingEntity entity) {
      PhysicsMod mod = getInstance(level);
      MobPhysicsType physicsType = ConfigMobs.getMobSetting(entity).getType();
      if (physicsType != MobPhysicsType.OFF) {
         if (!mod.alreadyBlockified.contains(entity.getId()) || entity instanceof Player) {
            if (!entity.isInvisible()) {
               mod.alreadyBlockified.add(entity.getId());
               boolean disableEMFModel = physicsType == MobPhysicsType.RAGDOLL
                  || physicsType == MobPhysicsType.RAGDOLL_BREAK
                  || physicsType == MobPhysicsType.RAGDOLL_BREAK_BLOOD;
               if (disableEMFModel) {
                  disabledEMFModel = true;
               }

               EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
               EntityRenderer entityRenderer = dispatcher.getRenderer(entity);
               mod.blockify = true;
               mod.localPivotMatrix = new PoseStack();
               mod.cubifyEntityRenderer = entityRenderer;
               mod.cubifyEntity = entity;
               mod.cubifyTranslucent = false;
               setCurrentInstance(mod);
               TextureManager textureManager = Minecraft.getInstance().getTextureManager();
               float tick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
               mod.blockifyEntity = entity;
               mod.blockifyFeature = null;
               EntityRenderState renderState = null;

               try {
                  renderState = dispatcher.extractEntity(entity, tick);
                  renderState.outlineColor = 0;
                  renderState.displayFireAnimation = false;
               } catch (Exception var31) {
                  System.err.println("error creating rendering state " + entity.getClass());
                  var31.printStackTrace();
               }

               SubmitNodeStorage submits = new SubmitNodeStorage();
               FeatureRendererMap renderers = new FeatureRendererMap();
               renderers.put(ModelFeatureRenderer.TYPE, new ModelFeatureRenderer());
               renderers.put(ItemFeatureRenderer.TYPE, new ItemFeatureRenderer());
               PhysicsFeatureRenderDispatcher renderDispatcher = new PhysicsFeatureRenderDispatcher(renderers) {
                  @Override
                  public Group createGroup() {
                     return new PhysicsFeatureRenderDispatcher.DummyGroup();
                  }
               };
               EntityModel model = null;

               try {
                  if (renderState != null) {
                     if (StarterClient.emf && !disableEMFModel) {
                        EMF.prepare(entity, renderState);
                     }

                     dispatcher.submit(renderState, new CameraRenderState(), 0.0, 0.0, 0.0, new PoseStack(), submits);
                     if (entityRenderer instanceof LivingEntityRenderer) {
                        model = ((LivingEntityRenderer)entityRenderer).getModel();
                     } else if (entityRenderer instanceof EnderDragonRenderer enderDragonRenderer) {
                        model = enderDragonRenderer.model;
                     }

                     renderDispatcher.render(submits);
                  }
               } catch (Exception var29) {
                  System.err.println("error rendering " + entity.getClass());
                  var29.printStackTrace();
               } finally {
                  try {
                     renderDispatcher.close();
                  } catch (Exception var27) {
                     var27.printStackTrace();
                  }
               }

               setCurrentInstance(null);
               mod.blockify = false;
               if (model != null) {
                  try {
                     boolean skipFilter = StarterClient.emf && disableEMFModel && EMF.isEMFModel();
                     if (!skipFilter) {
                        RagdollMapper.filterCuboidsFromEntities(entity, model, mod.blockifiedEntity);
                     }
                  } catch (Exception var32) {
                     System.err.println("error filtering " + entity.getClass());
                     var32.printStackTrace();
                  }
               }

               for (PhysicsEntity physicsEntity : mod.blockifiedEntity) {
                  physicsEntity.backfaceCulling(false);
               }

               MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
               if (type != MobPhysicsType.RAGDOLL && type != MobPhysicsType.RAGDOLL_BREAK && type != MobPhysicsType.RAGDOLL_BREAK_BLOOD) {
                  mod.entityBlocks.addAll(mod.blockifiedEntity);
                  if (mod.entityBlocks.size() > 0) {
                     entity.remove(RemovalReason.DISCARDED);
                     entity.deathTime = 20;
                     entity.hurtTime = 0;
                  }
               } else {
                  Ragdoll ragdoll = null;

                  try {
                     if (renderState != null && model != null) {
                        ragdoll = RagdollMapper.map(type, entity, model, mod.blockifiedEntity, renderState);
                     }
                  } catch (Exception var28) {
                     System.err.println("error creating ragdoll for " + entity.getClass());
                     var28.printStackTrace();
                  }

                  if (ragdoll == null) {
                     mod.entityBlocks.addAll(mod.blockifiedEntity);
                     if (mod.entityBlocks.size() > 0) {
                        entity.remove(RemovalReason.DISCARDED);
                        entity.deathTime = 20;
                        entity.hurtTime = 0;
                     }
                  } else {
                     mod.ragdolls.add(ragdoll);
                     if (level instanceof ClientLevel) {
                        Player closest = level.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 8.0, false);
                        if (closest != null) {
                           ragdoll.velocity.set(entity.getX() - closest.getX(), 2.0, entity.getZ() - closest.getZ()).normalize().mul(5.0);
                        }
                     }

                     ragdoll.velocity.add(entity.getDeltaMovement().x * 10.0, entity.getDeltaMovement().y * 10.0, entity.getDeltaMovement().z * 10.0);
                     entity.remove(RemovalReason.DISCARDED);
                     entity.deathTime = 20;
                     entity.hurtTime = 0;
                  }
               }

               mod.blockifiedEntity.clear();
               if (disableEMFModel) {
                  disabledEMFModel = false;
               }
            }
         }
      }
   }

   public static void createParticlesFromCuboids(
      Pose stack,
      PoseStack local,
      List<Cube> cuboids,
      Entity entity,
      EntityRenderer renderer,
      RenderLayer feature,
      boolean translucent,
      int overlay,
      float red,
      float green,
      float blue
   ) {
      Matrix4f m = stack.pose();
      Matrix4f localM = local.last().pose();
      Matrix3f localNM = local.last().normal();
      Matrix4d transformation = new Matrix4d();
      Matrix4d transformationLocal = new Matrix4d();
      transformation.set(m);
      transformationLocal.set(localM);
      transformation.mul(transformationLocal.invert(new Matrix4d()));
      if (transformation.isFinite()) {
         if (entity.level() instanceof ClientLevel clientLevel) {
            PhysicsMod var70 = getInstance(clientLevel);
            GpuTextureView textureID = TextureHelper.getLoadedTextures();
            float partialTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            double px = Mth.lerp((double)partialTicks, entity.xo, entity.getX());
            double py = Mth.lerp((double)partialTicks, entity.yo, entity.getY());
            double pz = Mth.lerp((double)partialTicks, entity.zo, entity.getZ());
            transformation.setTranslation(px + transformation.m30(), py + transformation.m31(), pz + transformation.m32());
            Vector4f[] minMax = new Vector4f[6];
            Vector3f tmpNormal = new Vector3f();
            Vector4f tmpPos = new Vector4f();

            for (int type = 0; type < minMax.length; type++) {
               minMax[type] = new Vector4f();
            }

            MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
            Iterator var30 = cuboids.iterator();

            while (true) {
               Cube box;
               float minX;
               float minY;
               float minZ;
               float maxX;
               float maxY;
               float maxZ;
               boolean mirrorX;
               boolean mirrorY;
               boolean mirrorZ;
               int[] remap;
               float volume;
               boolean isBlocky;
               boolean noVolume;
               while (true) {
                  if (!var30.hasNext()) {
                     return;
                  }

                  box = (Cube)var30.next();
                  if (box.polygons.length >= 6) {
                     minX = box.polygons[0].vertices()[2].x();
                     minY = box.polygons[0].vertices()[2].y();
                     minZ = box.polygons[0].vertices()[2].z();
                     maxX = box.polygons[1].vertices()[3].x();
                     maxY = box.polygons[1].vertices()[3].y();
                     maxZ = box.polygons[1].vertices()[3].z();
                     mirrorX = false;
                     mirrorY = false;
                     mirrorZ = false;
                     if (minX > maxX) {
                        mirrorX = true;
                     }

                     if (minY > maxY) {
                        mirrorY = true;
                     }

                     if (minZ > maxZ) {
                        mirrorZ = true;
                     }

                     remap = new int[]{5, 4, 3, 2, 1, 0};
                     if (mirrorX) {
                        remap[0] = 3;
                        remap[2] = 5;
                     }

                     volume = Math.abs(maxX - minX) / 16.0F * (Math.abs(maxY - minY) / 16.0F) * (Math.abs(maxZ - minZ) / 16.0F);
                     isBlocky = type == MobPhysicsType.BLOCKY
                        || type == MobPhysicsType.RAGDOLL
                        || type == MobPhysicsType.RAGDOLL_BREAK
                        || type == MobPhysicsType.RAGDOLL_BREAK_BLOOD;
                     noVolume = false;
                     if (!((double)volume <= 1.0E-4)) {
                        break;
                     }

                     if (isBlocky) {
                        noVolume = true;
                        break;
                     }
                  }
               }

               if (!((double)volume <= 0.04) && !isBlocky) {
                  List<Mesh> meshes = brokenBlocksLittle.get((int)(net.diebuddies.math.Math.random() * (float)brokenBlocksLittle.size()));

                  for (int i = 0; i < box.polygons.length; i++) {
                     float minU = 1.0F;
                     float maxU = 0.0F;
                     float minV = 1.0F;
                     float maxV = 0.0F;
                     Vertex[] vertices = box.polygons[i].vertices();
                     if (StarterClient.emf && EMF.isEMFModel()) {
                        minU = vertices[1].u();
                        maxU = vertices[3].u();
                        minV = vertices[1].v();
                        maxV = vertices[3].v();
                        minMax[i].set(minU, maxU, minV, maxV);
                     } else {
                        for (Vertex vertex : vertices) {
                           if (vertex.u() < minU) {
                              minU = vertex.u();
                           }

                           if (vertex.v() < minV) {
                              minV = vertex.v();
                           }

                           if (vertex.u() > maxU) {
                              maxU = vertex.u();
                           }

                           if (vertex.v() > maxV) {
                              maxV = vertex.v();
                           }
                        }

                        minMax[i].set(minU, maxU, minV, maxV);
                     }
                  }

                  PhysicsEntity parent = null;

                  for (Mesh mesh : meshes) {
                     PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.MOB, entity.getType());
                     particle.feature = feature;
                     particle.noVolume = noVolume;
                     Mesh clone = new Mesh();
                     Model model = particle.models.get(0);
                     model.textureID = textureID;
                     model.mesh = clone;
                     model.translucent = translucent;
                     particle.getTransformation().set(transformation);
                     int count = 0;
                     Vector3f offset = new Vector3f();

                     for (int ix = 0; ix < mesh.indices.size(); ix++) {
                        int index = mesh.indices.getInt(ix);
                        byte sideIndex = mesh.sides.getByte(index);
                        Vector3f position = mesh.positions.get(index);
                        Vector2f uv = mesh.uvs.get(index);
                        Vector3f normal = mesh.normals.get(index);
                        float r = red;
                        float g = green;
                        float b = blue;
                        if (sideIndex == -1) {
                           if (type == MobPhysicsType.FRACTURED_BLOOD) {
                              r = 0.6F;
                              g = 0.0F;
                              b = 0.0F;
                           }

                           sideIndex = 0;
                        }

                        tmpNormal.set(mirrorX ? -normal.x : normal.x, mirrorY ? -normal.y : normal.y, mirrorZ ? -normal.z : normal.z);
                        localNM.transform(tmpNormal);
                        tmpNormal.normalize();
                        Vector4f minMaxUVs = minMax[remap[sideIndex]];
                        tmpPos.set(
                           (float)net.diebuddies.math.Math.remap((double)(position.x + mesh.offset.x), -0.5, 0.5, (double)minX, (double)maxX) / 16.0F,
                           (float)net.diebuddies.math.Math.remap((double)(position.y + mesh.offset.y), -0.5, 0.5, (double)minY, (double)maxY) / 16.0F,
                           (float)net.diebuddies.math.Math.remap((double)(position.z + mesh.offset.z), -0.5, 0.5, (double)minZ, (double)maxZ) / 16.0F,
                           1.0F
                        );
                        localM.transform(tmpPos);
                        clone.indices.add(count);
                        offset.add(tmpPos.x(), tmpPos.y(), tmpPos.z());
                        count++;
                        Vector3f posR = new Vector3f(tmpPos.x(), tmpPos.y(), tmpPos.z());
                        clone.positions.add(posR);
                        float fromX = 1.0F;
                        float toX = 0.0F;
                        float fromY = 0.0F;
                        float toY = 1.0F;
                        if (!StarterClient.emf || !EMF.isEMFModel()) {
                           if (mirrorY && sideIndex != 4 && sideIndex != 5) {
                              float flip = fromY;
                              fromY = toY;
                              toY = flip;
                           }

                           if (mirrorX && sideIndex != 0 && sideIndex != 2) {
                              float flip = fromX;
                              fromX = toX;
                              toX = flip;
                           }

                           if (mirrorZ && sideIndex != 1 && sideIndex != 3) {
                              float flip = fromX;
                              fromX = toX;
                              toX = flip;
                           }
                        }

                        clone.uvs
                           .add(
                              new Vector2f(
                                 net.diebuddies.math.Math.remap(uv.x, fromX, toX, minMaxUVs.x, minMaxUVs.y),
                                 net.diebuddies.math.Math.remap(uv.y, fromY, toY, minMaxUVs.z, minMaxUVs.w)
                              )
                           );
                        clone.normals.add(new Vector3f(tmpNormal.x(), tmpNormal.y(), tmpNormal.z()));
                        clone.addColor(r, g, b);
                     }

                     if (StarterClient.iris() || StarterClient.optifabric) {
                        clone.calculatePBRData(false);
                     }

                     offset.div((float)clone.positions.size());

                     for (Vector3f positionx : clone.positions) {
                        positionx.sub(offset);
                     }

                     clone.offset = offset;
                     Vector3d ps = transformationLocal.getTranslation(new Vector3d());
                     particle.pivot.set(ps);
                     if (clone.positions.size() > 0) {
                        if (parent == null) {
                           parent = particle;
                           var70.blockifiedEntity.add(particle);
                        } else {
                           parent.children.add(particle);
                        }
                     }
                  }
               } else {
                  PhysicsEntity particle = new PhysicsEntity(PhysicsEntity.Type.MOB, entity.getType());
                  particle.feature = feature;
                  Mesh clone = new Mesh();
                  Model model = particle.models.get(0);
                  model.textureID = textureID;
                  model.mesh = clone;
                  model.translucent = translucent;
                  particle.getTransformation().set(transformation);
                  int count = 0;
                  Vector3f offset = new Vector3f();
                  if (noVolume && box.polygons.length > 0 && box.polygons[0].vertices().length > 0) {
                     Mesh physicsMesh = new Mesh();
                     Vertex vertex = box.polygons[0].vertices()[0];
                     tmpPos.set(vertex.x() / 16.0F, vertex.y() / 16.0F, vertex.z() / 16.0F, 1.0F);
                     physicsMesh.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z).add(0.01F, 0.0F, 0.0F));
                     physicsMesh.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z).add(-0.01F, 0.0F, 0.0F));
                     physicsMesh.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z).add(0.0F, 0.01F, 0.0F));
                     physicsMesh.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z).add(0.0F, -0.01F, 0.0F));
                     physicsMesh.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z).add(0.0F, 0.0F, 0.01F));
                     physicsMesh.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z).add(0.0F, 0.0F, -0.01F));
                     particle.models.get(0).physicsMesh = physicsMesh;
                     noVolume = false;
                  }

                  particle.noVolume = noVolume;

                  for (int ix = 0; ix < box.polygons.length; ix++) {
                     Polygon polygon = box.polygons[ix];
                     Vertex[] vertices = polygon.vertices();
                     Vector3fc normalx = polygon.normal();
                     clone.indices.add(count);
                     clone.indices.add(count + 1);
                     clone.indices.add(count + 2);
                     clone.indices.add(count + 2);
                     clone.indices.add(count + 3);
                     clone.indices.add(count);
                     count += 4;
                     tmpNormal.set(normalx.x(), normalx.y(), normalx.z());
                     localNM.transform(tmpNormal);
                     tmpNormal.normalize();

                     for (int j = 0; j < vertices.length; j++) {
                        Vertex vertex = vertices[j];
                        tmpPos.set(vertex.x() / 16.0F, vertex.y() / 16.0F, vertex.z() / 16.0F, 1.0F);
                        localM.transform(tmpPos);
                        offset.add(tmpPos.x, tmpPos.y, tmpPos.z);
                        clone.positions.add(new Vector3f(tmpPos.x, tmpPos.y, tmpPos.z));
                        clone.normals.add(new Vector3f(tmpNormal));
                        clone.uvs.add(new Vector2f(vertex.u(), vertex.v()));
                        clone.addColor(red, green, blue);
                     }
                  }

                  if (StarterClient.iris() || StarterClient.optifabric) {
                     clone.calculatePBRData(false);
                  }

                  offset.div((float)clone.positions.size());

                  for (Vector3f positionx : clone.positions) {
                     positionx.sub(offset);
                  }

                  clone.offset = offset;
                  Vector3d ps = transformationLocal.getTranslation(new Vector3d());
                  particle.pivot.set(ps);
                  if (clone.positions.size() > 0) {
                     var70.blockifiedEntity.add(particle);
                  }
               }
            }
         }
      }
   }

   public PhysicsEntity renderBlockIntoEntity(PhysicsEntity.Type type, BlockStateModel model, BlockState state, BlockPos pos, boolean bakeAO) {
      this.itemStackEntity = new PhysicsEntity(type, state);
      this.itemStackEntity.models.get(0).mesh = new Mesh();
      setCurrentInstance(this);
      Vec3 blockOffset = state.getOffset(pos);

      label56: {
         Object var8;
         try {
            LegacyRandomSource random = new LegacyRandomSource(0L);
            this.renderFlat(this.itemStackEntity, this.physicsWorld.getWorld(), model, state, pos, random, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
            break label56;
         } catch (Exception var12) {
            var8 = null;
         } finally {
            setCurrentInstance(null);
         }

         return (PhysicsEntity)var8;
      }

      if (this.itemStackEntity.models.get(0).mesh.indices.size() < 9) {
         setCurrentInstance(null);
         return null;
      } else {
         this.itemStackEntity.models.get(0).mesh.calculateOffset();
         if (StarterClient.iris() || StarterClient.optifabric) {
            this.itemStackEntity.models.get(0).mesh.calculatePBRData(false);
         }

         this.itemStackEntity.models.get(0).textureID = Minecraft.getInstance()
            .getTextureManager()
            .getTexture(model.particleMaterial().sprite().atlasLocation())
            .getTextureView();
         this.itemStackEntity
            .getTransformation()
            .set(new Matrix4d().translate((double)pos.getX() + blockOffset.x, (double)pos.getY() + blockOffset.y, (double)pos.getZ() + blockOffset.z));
         if (type == PhysicsEntity.Type.BLOCK) {
            BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
            this.itemStackEntity.getTransformation().scale((double)((float)blockSetting.getScale()));
         }

         this.itemStackEntity.models.get(0).animationSprite = model.particleMaterial().sprite();
         return this.itemStackEntity;
      }
   }

   public PhysicsEntity renderBlockIntoEntity(
      PhysicsEntity.Type type,
      BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer,
      BlockEntity blockEntity,
      BlockState state,
      BlockPos pos,
      boolean destruction
   ) {
      PhysicsEntity physicsBlockEntity = new PhysicsEntity(type, state);
      physicsBlockEntity.backfaceCulling(false);
      physicsBlockEntity.models.clear();
      setCurrentInstance(this);
      sodiumCatch = true;
      float tickDelta = 0.0F;
      PhysicsSubmitNodeStorage submits = new PhysicsSubmitNodeStorage(destruction);
      FeatureRendererMap renderers = new FeatureRendererMap();
      renderers.put(MovingBlockFeatureRenderer.TYPE, new MovingBlockFeatureRenderer());
      renderers.put(BlockModelFeatureRenderer.TYPE, new BlockModelFeatureRenderer());
      renderers.put(ModelFeatureRenderer.TYPE, new ModelFeatureRenderer());
      renderers.put(TextFeatureRenderer.TYPE, new TextFeatureRenderer());
      PhysicsFeatureRenderDispatcher renderDispatcher = new PhysicsFeatureRenderDispatcher(renderers) {
         {
            Objects.requireNonNull(PhysicsMod.this);
         }

         @Override
         public Group createGroup() {
            return new PhysicsFeatureRenderDispatcher.BlockEntityGroup();
         }
      };

      label129: {
         Object meshes;
         try {
            BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            BlockEntityRenderer<BlockEntity, BlockEntityRenderState> blockEntityRenderer = dispatcher.getRenderer(blockEntity);
            if (blockEntityRenderer != null && blockEntity.hasLevel() && blockEntity.getType().isValid(blockEntity.getBlockState())) {
               BlockEntityRenderState renderState = blockEntityRenderer.createRenderState();
               blockEntityRenderer.extractRenderState(blockEntity, renderState, tickDelta, dispatcher.cameraPos, null);
               renderer.submit(renderState, new PoseStack(), submits, new CameraRenderState());
               renderDispatcher.render(submits);
            }
            break label129;
         } catch (Exception var26) {
            var26.printStackTrace();
            meshes = null;
         } finally {
            setCurrentInstance(null);
            sodiumCatch = false;

            try {
               renderDispatcher.close();
            } catch (Exception var25) {
               var25.printStackTrace();
            }
         }

         return (PhysicsEntity)meshes;
      }

      Vec3 blockOffset = state.getOffset(pos);
      List<Mesh> meshes = new ObjectArrayList();

      for (Group group : renderDispatcher.getGroups()) {
         PhysicsFeatureRenderDispatcher.BlockEntityGroup blockEntityGroup = (PhysicsFeatureRenderDispatcher.BlockEntityGroup)group;

         for (BlockEntityVertexConsumer consumer : blockEntityGroup.getBakedRenderTypeModels().values()) {
            consumer.validateModel();
            Model model = consumer.getModel();
            if (model.mesh.indices.size() >= 6) {
               meshes.add(model.mesh);
               if (StarterClient.iris() || StarterClient.optifabric) {
                  model.mesh.calculatePBRData(false);
               }

               physicsBlockEntity.models.add(model);
            }
         }
      }

      if (physicsBlockEntity.models.size() == 0) {
         setCurrentInstance(null);
         return null;
      } else {
         Mesh.calculateMeshOffsets(meshes, false);
         physicsBlockEntity.getTransformation()
            .set(new Matrix4d().translate((double)pos.getX() + blockOffset.x, (double)pos.getY() + blockOffset.y, (double)pos.getZ() + blockOffset.z));
         if (type == PhysicsEntity.Type.BLOCK) {
            BlockSetting blockSetting = ConfigBlocks.getBlockSetting(state.getBlock());
            physicsBlockEntity.getTransformation().scale((double)((float)blockSetting.getScale()));
         }

         return physicsBlockEntity;
      }
   }

   public PhysicsEntity renderBlockIntoEntity(
      PhysicsEntity.Type type, BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer, BlockEntity blockEntity, BlockState state, BlockPos pos
   ) {
      return this.renderBlockIntoEntity(type, renderer, blockEntity, state, pos, true);
   }

   public PhysicsEntity renderBlockIntoEntity(Level level, PhysicsEntity.Type type, BlockState state, BlockPos pos, boolean bakeAO) {
      if (state.hasBlockEntity()) {
         BlockEntityRenderDispatcher berd = Minecraft.getInstance().getBlockEntityRenderDispatcher();
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity != null) {
            BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer = berd.getRenderer(blockEntity);
            if (renderer != null) {
               PhysicsEntity entity = this.renderBlockIntoEntity(type, renderer, blockEntity, state, pos, false);
               ModelManager manager = Minecraft.getInstance().getModelManager();
               BlockStateModel model = manager.getBlockStateModelSet().get(state);
               PhysicsEntity normalState = this.renderBlockIntoEntity(type, model, state, pos, bakeAO);
               if (entity == null) {
                  return normalState;
               }

               if (normalState == null) {
                  return entity;
               }

               normalState.models.add(entity.models.get(0));
               entity.models.get(0).onlyVisual = true;
               Vector3f diff = normalState.models.get(0).mesh.offset.sub(entity.models.get(0).mesh.offset, new Vector3f());

               for (Vector3f position : entity.models.get(0).mesh.positions) {
                  position.sub(diff);
               }

               return normalState;
            }
         }
      }

      ModelManager managerx = Minecraft.getInstance().getModelManager();
      BlockStateModel modelx = managerx.getBlockStateModelSet().get(state);
      return this.renderBlockIntoEntity(type, modelx, state, pos, bakeAO);
   }

   private int renderFlat(
      PhysicsEntity entity, BlockAndTintGetter world, BlockStateModel model, BlockState state, BlockPos pos, RandomSource random, long seed, int overlay
   ) {
      int hashCode = 0;
      Mesh mesh = entity.models.get(0).mesh;
      List<BlockStateModelPart> parts = new ObjectArrayList();
      model.collectParts(random, parts);

      for (int j = 0; j < parts.size(); j++) {
         BlockStateModelPart part = parts.get(j);

         for (int i = 0; i < DIRECTIONS.length; i++) {
            Direction direction = DIRECTIONS[i];
            random.setSeed(seed);
            List<BakedQuad> list = part.getQuads(direction);
            if (!list.isEmpty()) {
               hashCode = hashCode * 31 + this.renderQuadsFlat(entity, mesh, world, state, pos, overlay, list);
            }
         }

         random.setSeed(seed);
         List<BakedQuad> quads = part.getQuads((Direction)null);
         if (!quads.isEmpty()) {
            hashCode = hashCode * 31 + this.renderQuadsFlat(entity, mesh, world, state, pos, overlay, quads);
         }
      }

      return hashCode;
   }

   private int renderQuadsFlat(PhysicsEntity entity, Mesh mesh, BlockAndTintGetter world, BlockState state, BlockPos pos, int overlay, List<BakedQuad> quads) {
      int hashCode = 0;

      for (int i = 0; i < quads.size(); i++) {
         hashCode = hashCode * 31 + this.renderQuadFlat(entity, mesh, world, state, pos, quads.get(i), overlay);
      }

      return hashCode;
   }

   private int renderQuadFlat(PhysicsEntity entity, Mesh mesh, BlockAndTintGetter world, BlockState state, BlockPos pos, BakedQuad quad, int overlay) {
      int hashCode = 0;
      float red = 1.0F;
      float green = 1.0F;
      float blue = 1.0F;
      if (quad.materialInfo().isTinted()) {
         BlockTintSource tintSource = Minecraft.getInstance().getBlockColors().getTintSource(state, quad.materialInfo().tintIndex());
         if (tintSource != null) {
            int blockColor = tintSource.colorInWorld(state, world, pos);
            hashCode = hashCode * 31 + blockColor;
            red = (float)(blockColor >> 16 & 0xFF) / 255.0F;
            green = (float)(blockColor >> 8 & 0xFF) / 255.0F;
            blue = (float)(blockColor & 0xFF) / 255.0F;
         }
      }

      entity.shade(quad.materialInfo().shade());
      Vector3fc normal = quad.direction().getUnitVec3f();

      for (int l = 0; l < 4; l++) {
         Vector3fc quadPos = quad.position(l);
         long packedUv = quad.packedUV(l);
         float u = UVPair.unpackU(packedUv);
         float v = UVPair.unpackV(packedUv);
         mesh.positions.add(new Vector3f(quadPos.x(), quadPos.y(), quadPos.z()));
         mesh.addColor(red, green, blue);
         mesh.normals.add(new Vector3f(normal.x(), normal.y(), normal.z()));
         mesh.uvs.add(new Vector2f(u, v));
      }

      int index = mesh.positions.size() - 4;
      mesh.indices.add(index);
      mesh.indices.add(index + 1);
      mesh.indices.add(index + 2);
      mesh.indices.add(index);
      mesh.indices.add(index + 2);
      mesh.indices.add(index + 3);
      return hashCode * 31 + quad.hashCode();
   }

   private void calculateShape(
      BlockAndTintGetter blockAndTintGetter, BlockState blockState, BlockPos blockPos, int[] is, Direction direction, @Nullable float[] fs, BitSet bitSet
   ) {
      float f = 32.0F;
      float g = 32.0F;
      float h = 32.0F;
      float i = -32.0F;
      float j = -32.0F;
      float k = -32.0F;

      for (int l = 0; l < 4; l++) {
         float m = Float.intBitsToFloat(is[l * 8]);
         float n = Float.intBitsToFloat(is[l * 8 + 1]);
         float o = Float.intBitsToFloat(is[l * 8 + 2]);
         f = Math.min(f, m);
         g = Math.min(g, n);
         h = Math.min(h, o);
         i = Math.max(i, m);
         j = Math.max(j, n);
         k = Math.max(k, o);
      }

      if (fs != null) {
         fs[Direction.WEST.get3DDataValue()] = f;
         fs[Direction.EAST.get3DDataValue()] = i;
         fs[Direction.DOWN.get3DDataValue()] = g;
         fs[Direction.UP.get3DDataValue()] = j;
         fs[Direction.NORTH.get3DDataValue()] = h;
         fs[Direction.SOUTH.get3DDataValue()] = k;
         int var19 = DIRECTIONS.length;
         fs[Direction.WEST.get3DDataValue() + var19] = 1.0F - f;
         fs[Direction.EAST.get3DDataValue() + var19] = 1.0F - i;
         fs[Direction.DOWN.get3DDataValue() + var19] = 1.0F - g;
         fs[Direction.UP.get3DDataValue() + var19] = 1.0F - j;
         fs[Direction.NORTH.get3DDataValue() + var19] = 1.0F - h;
         fs[Direction.SOUTH.get3DDataValue() + var19] = 1.0F - k;
      }

      float p = 1.0E-4F;
      float m = 0.9999F;
      switch (direction) {
         case DOWN:
            bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, g == j && (g < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case UP:
            bitSet.set(1, f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, g == j && (j > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case NORTH:
            bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
            bitSet.set(0, h == k && (h < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case SOUTH:
            bitSet.set(1, f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F);
            bitSet.set(0, h == k && (k > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case WEST:
            bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, f == i && (f < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
            break;
         case EAST:
            bitSet.set(1, g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F);
            bitSet.set(0, f == i && (i > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndTintGetter, blockPos)));
      }
   }

   public static void blockifyItemStack(ClientLevel level, ItemStack item, boolean mainHand) {
      try {
         if (StarterClient.iris()) {
            Iris.enableHandRendering();
         }

         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         renderHand(level, item, camera, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true), mainHand);
         if (StarterClient.iris()) {
            Iris.disableHandRendering();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   private static void renderHand(ClientLevel level, ItemStack item, Camera camera, float tickDelta, boolean mainHand) {
      PoseStack matrices = new PoseStack();
      matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
      matrices.mulPose(Axis.YP.rotationDegrees(camera.yRot() + 180.0F));
      matrices.last().pose().invert();
      matrices.pushPose();
      bobViewWhenHurt(matrices, tickDelta);
      if ((Boolean)Minecraft.getInstance().options.bobView().get()) {
         bobView(matrices, tickDelta);
      }

      renderItem(
         level,
         item,
         camera,
         mainHand,
         Minecraft.getInstance().gameRenderer.itemInHandRenderer,
         tickDelta,
         matrices,
         Minecraft.getInstance().player,
         Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(Minecraft.getInstance().player, tickDelta)
      );
      matrices.popPose();
   }

   private static void renderItem(
      ClientLevel level,
      ItemStack item,
      Camera camera,
      boolean mainHand,
      ItemInHandRenderer firstPersonRenderer,
      float tickDelta,
      PoseStack matrices,
      LocalPlayer player,
      int light
   ) {
      float f = player.getAttackAnim(tickDelta);
      InteractionHand hand = (InteractionHand)MoreObjects.firstNonNull(player.swingingArm, InteractionHand.MAIN_HAND);
      float xRot = Mth.lerp(tickDelta, player.xRotO, player.getXRot());
      float h = Mth.lerp(tickDelta, player.xBobO, player.xBob);
      float i = Mth.lerp(tickDelta, player.yBobO, player.yBob);
      matrices.mulPose(Axis.XP.rotationDegrees((player.getViewXRot(tickDelta) - h) * 0.1F));
      matrices.mulPose(Axis.YP.rotationDegrees((player.getViewYRot(tickDelta) - i) * 0.1F));
      float anim = f;
      SubmitNodeStorage submits = new SubmitNodeStorage();
      FeatureRendererMap renderers = new FeatureRendererMap();
      renderers.put(ItemFeatureRenderer.TYPE, new ItemFeatureRenderer());
      PhysicsFeatureRenderDispatcher renderDispatcher = new PhysicsFeatureRenderDispatcher(renderers) {
         @Override
         public Group createGroup() {
            return new PhysicsFeatureRenderDispatcher.DummyGroup();
         }
      };
      float height = 1.0F - Mth.lerp(tickDelta, firstPersonRenderer.oMainHandHeight, firstPersonRenderer.mainHandHeight);

      try {
         itemBreakTransformation = new Matrix4f();
         firstPersonRenderer.submitArmWithItem(
            player, tickDelta, xRot, mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, anim, item, height, matrices, submits, light
         );
         renderDispatcher.render(submits);
         ItemStackRenderState scratchRenderState = new ItemStackRenderState();
         Minecraft.getInstance().getItemModelResolver().updateForTopItem(scratchRenderState, item, ItemDisplayContext.GROUND, level, null, 0);
         TextureAtlasSprite sprite = scratchRenderState.pickParticleMaterial(RandomSource.create()).sprite();
         SpriteContents contents = sprite.contents();
         float size = 1.0F / (float)contents.width() * 0.6666666F;
         float texelSizeX = 1.0F / (float)contents.width();
         float texelSizeY = 1.0F / (float)contents.height();
         float depthScale = 0.041666664F / size;
         itemBreakTransformation.translateLocal((float)camera.position().x, (float)camera.position().y, (float)camera.position().z);
         boolean highRes = contents.width() > 32 || contents.height() > 32;
         if (!highRes) {
            for (int x = 0; x < contents.width(); x++) {
               for (int y = 0; y < contents.height(); y++) {
                  if (!contents.isTransparent(0, x, y)) {
                     float uvx = (float)x / (float)contents.width() + texelSizeX * 0.5F;
                     float uvy = (float)y / (float)contents.height() + texelSizeY * 0.5F;
                     ParticleSpawner.spawnItemPhysicsParticle(
                        sprite, level, (double)uvx, (double)(1.0F - uvy), 0.5, size, depthScale, uvx, uvy, itemBreakTransformation
                     );
                  }
               }
            }
         }
      } finally {
         itemBreakTransformation = null;

         try {
            renderDispatcher.close();
         } catch (Exception var36) {
         }
      }
   }

   private static void bobView(PoseStack poseStack, float f) {
      if (Minecraft.getInstance().getCameraEntity() instanceof AbstractClientPlayer abstractClientPlayer) {
         ClientAvatarState clientAvatarState = abstractClientPlayer.avatarState();
         float g = clientAvatarState.getBackwardsInterpolatedWalkDistance(f);
         float h = clientAvatarState.getInterpolatedBob(f);
         poseStack.translate(Mth.sin((double)(g * (float) Math.PI)) * h * 0.5F, -Math.abs(Mth.cos((double)(g * (float) Math.PI)) * h), 0.0F);
         poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin((double)(g * (float) Math.PI)) * h * 3.0F));
         poseStack.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos((double)(g * (float) Math.PI - 0.2F)) * h) * 5.0F));
      }
   }

   private static void bobViewWhenHurt(PoseStack poseStack, float f) {
      if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity livingEntity) {
         float g = (float)livingEntity.hurtTime - f;
         if (livingEntity.isDeadOrDying()) {
            float h = Math.min((float)livingEntity.deathTime + f, 20.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (h + 200.0F)));
         }

         if (g < 0.0F) {
            return;
         }

         g /= (float)livingEntity.hurtDuration;
         g = Mth.sin((double)(g * g * g * g * (float) Math.PI));
         float h = livingEntity.getHurtDir();
         poseStack.mulPose(Axis.YP.rotationDegrees(-h));
         float i = (float)((double)(-g) * 14.0 * (Double)Minecraft.getInstance().options.damageTiltStrength().get());
         poseStack.mulPose(Axis.ZP.rotationDegrees(i));
         poseStack.mulPose(Axis.YP.rotationDegrees(h));
      }
   }

   public static void storeShaderLightDirections() {
      shaderLights = RenderSystem.getShaderLights();
   }

   public static void restoreShaderLightDirections() {
      RenderSystem.setShaderLights(shaderLights);
   }

   static {
      brokenBlocksLittle.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_1.obj"));
      brokenBlocksLittle.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_2.obj"));
      brokenBlocksLittle.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_little_3.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_2.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_3.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_4.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_5.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_6.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_7.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_8.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_9.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_10.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_11.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_12.obj"));
      brokenBlocksLots.add(readBlock("assets/physicsmod/models/fractures/realistic/physics_shattered_lots_13.obj"));
      brokenBlocksLittleVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_1_voxel.obj"));
      brokenBlocksLittleVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_2_voxel.obj"));
      brokenBlocksLittleVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_little_3_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_2_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_3_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_4_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_5_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_6_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_7_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_8_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_9_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_10_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_11_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_12_voxel.obj"));
      brokenBlocksLotsVoxel.add(readBlock("assets/physicsmod/models/fractures/voxel/physics_shattered_lots_13_voxel.obj"));
      snowballMesh.add(readBlock("assets/physicsmod/models/snowball/snowball_voxel.obj").get(0));
      snowballMesh.add(readBlock("assets/physicsmod/models/snowball/snowball_round.obj").get(0));
      snowballMeshFractured.add(readBlock("assets/physicsmod/models/snowball/snowball_voxel_fractured.obj"));
      snowballMeshFractured.add(readBlock("assets/physicsmod/models/snowball/snowball_round_fractured.obj"));
      enderpearlMesh.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_voxel.obj").get(0));
      enderpearlMesh.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_round.obj").get(0));
      enderpearlMeshFractured.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_voxel_fractured.obj"));
      enderpearlMeshFractured.add(readBlock("assets/physicsmod/models/enderpearl/enderpearl_round_fractured.obj"));
      eggMesh.add(readBlock("assets/physicsmod/models/egg/egg_voxel.obj").get(0));
      eggMesh.add(readBlock("assets/physicsmod/models/egg/egg_round.obj").get(0));
      eggMeshFractured.add(readBlock("assets/physicsmod/models/egg/egg_voxel_fractured.obj"));
      eggMeshFractured.add(readBlock("assets/physicsmod/models/egg/egg_round_fractured.obj"));
   }

   public static enum RenderPass {
      SOLID,
      SHADOW;
   }
}
