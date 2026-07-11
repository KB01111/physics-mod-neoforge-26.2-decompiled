package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.util.GLSLModifier;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class ShaderInjectionOcean {
   private static String position = "gl_Vertex";
   private static String normalMatrix = "gl_NormalMatrix";
   private static String modelViewProjectionMatrix = "gl_ModelViewProjectionMatrix";
   private static String outputColor0 = "gl_FragData[0]";
   private static String outputColor1 = "gl_FragData[1]";
   private static String outputColor2 = "gl_FragData[2]";
   private static String outputColor3 = "gl_FragData[3]";
   private static String outputColor4 = "gl_FragData[4]";
   private static String lmCoord = "(mat4(vec4(0.00390625, 0.0, 0.0, 0.0), vec4(0.0, 0.00390625, 0.0, 0.0), vec4(0.0, 0.0, 0.00390625, 0.0), vec4(0.03125, 0.03125, 0.03125, 1.0)) * gl_MultiTexCoord1).xy";

   private static void setupOptifineInjection() {
      position = "vec4(vaPosition + modelOffset, 1.0)";
      normalMatrix = "normalMatrix";
      modelViewProjectionMatrix = "(projectionMatrix * modelViewMatrix)";
      outputColor0 = "outColor0";
      outputColor1 = "outColor1";
      outputColor2 = "outColor2";
      outputColor3 = "outColor3";
      outputColor4 = "outColor4";
      lmCoord = "(mat4(vec4(0.00390625, 0.0, 0.0, 0.0), vec4(0.0, 0.00390625, 0.0, 0.0), vec4(0.0, 0.0, 0.00390625, 0.0), vec4(0.03125, 0.03125, 0.03125, 1.0)) * vec4(vaUV2, 0.0, 1.0)).xy";
   }

   @Nullable
   public static String getVertexSource(String vertexSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      if (vertexSource == null) {
         return null;
      } else {
         vertexSource = replaceLegacyConsts(vertexSource);
         return transformVertexOceanShader(evaluatePack(vertexSource), vertexSource);
      }
   }

   private static ShaderInjectionOcean.ShaderPack evaluatePack(String source) {
      if (source.contains("// Complementary Shaders by EminGT //")) {
         return ShaderInjectionOcean.ShaderPack.COMPLEMENTARY_REIMAGINED;
      } else if (source.contains("Complementary Shaders by EminGT")) {
         return ShaderInjectionOcean.ShaderPack.COMPLEMENTARY;
      } else {
         return source.contains("Complementary Reimagined by EminGT")
            ? ShaderInjectionOcean.ShaderPack.COMPLEMENTARY_REIMAGINED
            : ShaderInjectionOcean.ShaderPack.OTHER;
      }
   }

   @Nullable
   public static String getFragmentSource(String fragmentSource, boolean shadowShader) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      if (fragmentSource == null) {
         return null;
      } else {
         fragmentSource = replaceLegacyConsts(fragmentSource);
         if (!shadowShader) {
            if (StarterClient.iris()) {
               Iris.oceanError = "";
            }

            return transformFragmentOceanShader(evaluatePack(fragmentSource), fragmentSource);
         } else {
            fragmentSource = GLSLModifier.removeComments(fragmentSource);
            return GLSLModifier.insertBeforeFirstFunction(
               fragmentSource, "in vec3 physics_localPosition;\nin vec3 physics_foamColor;\nin float physics_localWaviness;\n"
            );
         }
      }
   }

   private static String transformVertexOceanShader(ShaderInjectionOcean.ShaderPack pack, String vertex) {
      vertex = GLSLModifier.removeComments(vertex);
      vertex = StringUtils.replace(vertex, position, "physics_finalPosition");
      vertex = StringUtils.replace(vertex, "physics_finalPositionID", "gl_VertexID");
      vertex = StringUtils.replace(vertex, "mc_chunkFade", "1.0");
      boolean addOptifineUV2 = StarterClient.optifabric && !vertex.contains("vaUV2");
      List<String> oceanInjection = getOceanWaveInjection();
      oceanInjection.add("in float physics_waviness;");
      oceanInjection.add("out vec3 physics_localPosition;");
      oceanInjection.add("out vec3 physics_foamColor;");
      oceanInjection.add("out float physics_localWaviness;");
      oceanInjection.add("vec4 physics_finalPosition;");
      if (addOptifineUV2) {
         oceanInjection.add("in ivec2 vaUV2;");
      }

      vertex = GLSLModifier.insertBeforeFirstFunction(vertex, GLSLModifier.convertToString(oceanInjection));
      vertex = GLSLModifier.replaceFunctionCalls(vertex, "ftransform", "(" + modelViewProjectionMatrix + " * physics_finalPosition)");
      return GLSLModifier.insertAtFunctionStart(
         vertex,
         "main",
         "physics_foamColor = textureLod(physics_lightmap, "
            + lmCoord
            + ", 0).rgb;\nphysics_localWaviness = physics_waviness;\nfloat physics_rippleHeight = physics_rippleVertexHeight("
            + position
            + ".xz);physics_finalPosition = vec4("
            + position
            + ".x, physics_rippleHeight + "
            + position
            + ".y + physics_waveHeight("
            + position
            + ".xz, PHYSICS_ITERATIONS_OFFSET, physics_localWaviness, physics_gameTime), "
            + position
            + ".z, "
            + position
            + ".w);\nphysics_localPosition = physics_finalPosition.xyz;\n"
      );
   }

   public static String replaceLegacyConsts(String glsl) {
      glsl = StringUtils.replace(glsl, "const int PHYSICS_ITERATIONS_OFFSET = 13;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_DRAG_MULT = 0.048;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_XZ_SCALE = 0.035;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_TIME_MULTIPLICATOR = 0.45;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_W_DETAIL = 0.75;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_FREQUENCY = 6.0;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_SPEED = 2.0;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_WEIGHT = 0.8;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_FREQUENCY_MULT = 1.18;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_SPEED_MULT = 1.07;", "");
      glsl = StringUtils.replace(glsl, "const float PHYSICS_ITER_INC = 12.0;", "");
      return StringUtils.replace(glsl, "const float PHYSICS_NORMAL_STRENGTH = 0.6;", "");
   }

   private static String transformFragmentOceanShader(ShaderInjectionOcean.ShaderPack pack, String fragment) {
      fragment = GLSLModifier.removeComments(fragment);
      boolean injectNormalMatrix = StarterClient.optifabric && !fragment.contains(normalMatrix);
      List<String> oceanInjection = getOceanWaveInjection();
      oceanInjection.add("in vec3 physics_localPosition;");
      oceanInjection.add("in vec3 physics_foamColor;");
      oceanInjection.add("in float physics_localWaviness;");
      oceanInjection.add("WavePixelData physics_waveData;");
      if (injectNormalMatrix) {
         oceanInjection.add("uniform mat3 normalMatrix;");
      }

      fragment = GLSLModifier.insertBeforeFirstFunction(fragment, GLSLModifier.convertToString(oceanInjection));
      if (pack == ShaderInjectionOcean.ShaderPack.COMPLEMENTARY) {
         fragment = complementary(fragment);
      } else if (pack == ShaderInjectionOcean.ShaderPack.COMPLEMENTARY_REIMAGINED) {
         fragment = complementaryReimagined(fragment);
      } else if (pack == ShaderInjectionOcean.ShaderPack.OTHER) {
         fragment = other(fragment);
      }

      return StringUtils.replace(fragment, "physics_waveDataPlaceholder", "(gl_FrontFacing?physics_waveData.normal:-physics_waveData.normal)");
   }

   private static void printError() {
      if (StarterClient.iris()) {
         Iris.oceanError = "This shader (or shaderpack settings) is not supported by ocean physics!";
      }
   }

   private static String complementary(String source) {
      boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
      if (!hasWaveNormalFunction) {
         printError();
         return source;
      } else {
         source = GLSLModifier.replaceFunctionContent(source, "GetWaterNormal", "return normalize(" + normalMatrix + " * physics_waveDataPlaceholder);\n");
         source = GLSLModifier.insertAtFunctionStart(
            source,
            "main",
            "physics_waveData = physics_wavePixel(physics_localPosition.xz, physics_localWaviness, physics_iterationsNormal, physics_gameTime);\n"
         );
         source = GLSLModifier.insertAtFunctionEnd(
            source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
         );
         source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         source = GLSLModifier.replaceVariableReferences(source, "VdotN", "dot(nViewPos, normalize(newNormal))");
         return StringUtils.replace(source, "PHYSICS_NORMAL_STRENGTH = 0.6", "PHYSICS_NORMAL_STRENGTH = 1.4");
      }
   }

   private static String complementaryReimagined(String source) {
      source = GLSLModifier.replaceFunctionContent(source, "GetCustomMaterials", "");
      source = GLSLModifier.insertAtFunctionStart(
         source,
         "main",
         "physics_waveData = physics_wavePixel(physics_localPosition.xz, physics_localWaviness, physics_iterationsNormal, physics_gameTime);\nvec3 physics_normal = normalize("
            + normalMatrix
            + " * physics_waveDataPlaceholder);\n"
      );
      source = GLSLModifier.insertAtFunctionEnd(
         source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
      );
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "clamp(normalize(normalMap", "clamp(normalize(physics_normal");
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "dot(normalM", "dot(physics_normal");
      source = GLSLModifier.replaceWithinFunctionContent(
         source, "main", "vec3 normalM = VdotN > 0.0 ? -normal : normal;", "vec3 normalM = VdotN > 0.0 ? -physics_normal : physics_normal;"
      );
      source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
      source = GLSLModifier.replaceVariableReferences(source, "yPosDif", "-2.0");
      return StringUtils.replace(source, "PHYSICS_NORMAL_STRENGTH = 0.6", "PHYSICS_NORMAL_STRENGTH = 1.4");
   }

   private static String other(String source) {
      source = StringUtils.replace(source, "PHYSICS_NORMAL_STRENGTH = 0.6", "PHYSICS_NORMAL_STRENGTH = 1.4");
      source = GLSLModifier.insertAtFunctionStart(
         source, "main", "physics_waveData = physics_wavePixel(physics_localPosition.xz, physics_localWaviness, physics_iterationsNormal, physics_gameTime);\n"
      );
      boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWavesNormal");
      String normalFunctionName = null;
      boolean ignoreNormalMatrix = false;
      if (hasWaveNormalFunction) {
         normalFunctionName = "GetWavesNormal";
         ignoreNormalMatrix = !source.contains("tbnMatrix");
         source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
         if (ignoreNormalMatrix) {
            source = GLSLModifier.insertAtFunctionEnd(
               source,
               "main",
               "if (physics_waveData.foam > 0.2) {\n"
                  + outputColor1
                  + " = vec4(physics_foamColor, 1.0);\n"
                  + outputColor0
                  + " = vec4(EncodeNormal(normal), EncodeVec2(lmcoord.x, lmcoord.y), EncodeVec2(1.0, f0), 1.0);\n}\n"
            );
         } else {
            source = GLSLModifier.insertAtFunctionEnd(
               source,
               "main",
               "if (physics_waveData.foam > 0.2) { " + outputColor4 + " = vec4(physics_foamColor, 1.0); " + outputColor2 + ".b = 7.0 / 255.0; }\n"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "calcBump");
         if (hasWaveNormalFunction) {
            normalFunctionName = "calcBump";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionEnd(
               source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "GetWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionEnd(
               source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
            );
            source = StringUtils.replace(
               source,
               "float f0 = 0.0, porosity = 0.5, ao = 1.0, skyOcclusion = 0.0;",
               "normalMap = normalize("
                  + normalMatrix
                  + " * physics_waveDataPlaceholder);\nnewNormal = normalMap;\nfloat f0 = 0.0, porosity = 0.5, ao = 1.0, skyOcclusion = 0.0;"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            if (!source.contains("tbnMatrixWorld")) {
               source = GLSLModifier.replaceFunctionContent(
                  source, normalFunctionName, "newNormal = normalize(" + normalMatrix + " * physics_waveDataPlaceholder);\n"
               );
               return GLSLModifier.insertAtFunctionEnd(
                  source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
               );
            }

            ignoreNormalMatrix = true;
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveHeight");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaveHeight";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionEnd(
               source,
               "main",
               outputColor0
                  + " = mix("
                  + outputColor0
                  + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
                  + outputColor1
                  + " = mix("
                  + outputColor1
                  + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaveNormal";
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "viewToWorld(normal)", "normalize(physics_waveDataPlaceholder)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "bumpmult = 10.0 * 1.0;", "bumpmult = 1.0;");
            source = GLSLModifier.replaceWithinFunctionContent(
               source, "main", "applyBump(tbnMatrix, NormalTex.xyz, 1.0)", "normalize(" + normalMatrix + " * physics_waveDataPlaceholder)"
            );
            source = GLSLModifier.insertAtFunctionEnd(
               source,
               "main",
               outputColor0
                  + " = mix("
                  + outputColor0
                  + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
                  + outputColor1
                  + " = mix("
                  + outputColor1
                  + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_normals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "get_normals";
            source = GLSLModifier.insertAtFunctionEnd(
               source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "noiseNormals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "noiseNormals";
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "waterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "waterNormal";
            if (source.contains("sceneDataB")) {
               source = GLSLModifier.insertAtFunctionEnd(
                  source,
                  "main",
                  "translucentColor = clamp(mix(sceneColor, vec4(5.0 * physics_foamColor, 5.0), physics_waveData.foam), 0.0, 65535.0);\nreflectionAux = packReflectionAux(mix(directLight * directCol, vec3(physics_foamColor), physics_waveData.foam), mix(albedo, vec3(1.0), physics_waveData.foam));\n"
               );
            } else if (source.contains("sceneData.xy    = encodeNormal(sceneNormal);")) {
               source = GLSLModifier.insertAtFunctionEnd(source, "main", "sceneColor = mix(sceneColor, vec4(physics_foamColor, 1.0), physics_waveData.foam);\n");
            } else {
               source = GLSLModifier.insertAtFunctionEnd(
                  source,
                  "main",
                  "sceneColor = mix(sceneColor, vec4(5.0 * physics_foamColor, 5.0), physics_waveData.foam);\nlightingData = packReflectionAux(mix(directLight, vec3(physics_foamColor), physics_waveData.foam), mix(sceneTint.rgb, vec3(1.0), physics_waveData.foam));\n"
               );
            }

            return GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_waveDataPlaceholder);\n");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getNormals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getNormals";
            ignoreNormalMatrix = true;
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_waveDataPlaceholder);\n");
            return GLSLModifier.insertAtFunctionEnd(
               source, "main", outputColor0 + " = mix(" + outputColor0 + ", vec4(physics_foamColor, 1.0), physics_waveData.foam);\n"
            );
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_water_normal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "get_water_normal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_waveDataPlaceholder.xzy);\n");
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(vec3(1.0, 0.0, 0.0),vec3(0.0, 0.0, 1.0),vec3(0.0, 1.0, 0.0))");
            source = GLSLModifier.replaceFunctionCalls(
               source, "purkinje_shift", "purkinje_shift(mix(fragment_color.rgb, physics_foamColor, physics_waveData.foam), adjusted_light_levels)"
            );
            if (source.contains("vec3 color_to_store = is_water ? shadows : base_color.rgb;")) {
               source = GLSLModifier.insertAtFunctionEnd(
                  source, "main", "gbuffer_data_0.z  = pack_unorm_2x8(encode_unit_vector(physics_waveDataPlaceholder.xyz));"
               );
            }

            return source;
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "H2NWater");
         if (hasWaveNormalFunction) {
            normalFunctionName = "H2NWater";
            return GLSLModifier.insertAtFunctionEnd(source, "main", "normalDataOut = physics_waveDataPlaceholder;\n");
         }
      }

      if (!hasWaveNormalFunction) {
         if (source.contains(outputColor1)) {
            source = GLSLModifier.insertAtFunctionEnd(
               source, "main", outputColor1 + " = vec4(normalize(" + normalMatrix + " * physics_waveDataPlaceholder).xyz, 1.0);\n"
            );
         } else {
            printError();
         }

         return source;
      } else {
         if (ignoreNormalMatrix) {
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_waveDataPlaceholder);\n");
         } else {
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_waveDataPlaceholder);\n");
         }

         return source;
      }
   }

   private static List<String> getOceanWaveInjection() {
      String asset = "assets/physicsmod/shaders/include/ocean.glsl";
      List<String> lines = new ObjectArrayList();
      String all = "";
      int brackets = 0;

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(asset)))) {
         while ((line = reader.readLine()) != null) {
            if (!line.isBlank()) {
               if (line.contains("{")) {
                  brackets++;
               }

               if (line.contains("}")) {
                  brackets--;
               }

               all = all + line;
               if (brackets == 0) {
                  lines.add(all);
                  all = "";
               }
            }
         }
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      return lines;
   }

   public static enum ShaderPack {
      COMPLEMENTARY,
      COMPLEMENTARY_REIMAGINED,
      OTHER;
   }
}
