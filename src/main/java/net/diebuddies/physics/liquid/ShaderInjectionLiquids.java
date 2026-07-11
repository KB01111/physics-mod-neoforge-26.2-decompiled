package net.diebuddies.physics.liquid;

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

public class ShaderInjectionLiquids {
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

      return vertexSource == null ? null : transformVertexLiquidShader(evaluatePack(vertexSource), vertexSource);
   }

   private static ShaderInjectionLiquids.ShaderPack evaluatePack(String source) {
      if (source.contains("// Complementary Shaders by EminGT //")) {
         return ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY_REIMAGINED;
      } else if (source.contains("Complementary Shaders by EminGT")) {
         return ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY;
      } else {
         return source.contains("Complementary Reimagined by EminGT")
            ? ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY_REIMAGINED
            : ShaderInjectionLiquids.ShaderPack.OTHER;
      }
   }

   @Nullable
   public static String getFragmentSource(String fragmentSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      if (fragmentSource == null) {
         return null;
      } else {
         if (StarterClient.iris()) {
            Iris.liquidsError = "";
         }

         return transformFragmentLiquidShader(evaluatePack(fragmentSource), fragmentSource);
      }
   }

   @Nullable
   public static String getVertexShadowSource(String vertexSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      return vertexSource == null ? null : transformVertexLiquidShadowShader(evaluatePack(vertexSource), vertexSource);
   }

   @Nullable
   public static String getFragmentShadowSource(String fragmentSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      if (fragmentSource == null) {
         return null;
      } else {
         if (StarterClient.iris()) {
            Iris.liquidsError = "";
         }

         return fragmentSource;
      }
   }

   private static String transformVertexLiquidShadowShader(ShaderInjectionLiquids.ShaderPack pack, String vertex) {
      vertex = GLSLModifier.removeComments(vertex);
      vertex = StringUtils.replace(vertex, position, "physics_finalPosition");
      vertex = StringUtils.replace(vertex, "physics_finalPositionID", "gl_VertexID");
      vertex = StringUtils.replace(vertex, "mc_chunkFade", "1.0");
      vertex = GLSLModifier.insertBeforeFirstFunction(
         vertex,
         "layout(std140) uniform PhysicsLiquid {\n\tmat4 physics_modelViewMat;\n\tmat4 physics_projMat;\n\tmat4 physics_invProjectionMatrix;\n\tmat4 physics_invViewMatrix;\n\tmat4 physics_viewMatrix;\n\tvec4 physics_liquidCameraPosRenderPercent;\n\tvec4 physics_waterBounds;\n\tvec4 physics_cameraOffset;\n};\nuniform samplerBuffer PhysicsLiquidInstances;\nvec4 physics_finalPosition;\n"
      );
      vertex = GLSLModifier.insertAtFunctionStart(
         vertex,
         "main",
         "int instanceBase = gl_InstanceID * 2;\nvec4 physics_offset = texelFetch(PhysicsLiquidInstances, instanceBase + 0);\nvec4 physics_offsetNew = texelFetch(PhysicsLiquidInstances, instanceBase + 1);\nvec3 physics_liquidCameraPos = physics_liquidCameraPosRenderPercent.xyz;\nfloat physics_renderPercent = physics_liquidCameraPosRenderPercent.w;\nfloat physics_scale = physics_offsetNew.w;\nphysics_finalPosition = vec4("
            + position
            + ".xyz * physics_scale + mix(physics_offset.xyz - physics_liquidCameraPos, physics_offsetNew.xyz - physics_liquidCameraPos, physics_renderPercent), 1.0);\n"
      );
      return GLSLModifier.replaceFunctionCalls(vertex, "ftransform", "(" + modelViewProjectionMatrix + " * physics_finalPosition)");
   }

   private static String transformVertexLiquidShader(ShaderInjectionLiquids.ShaderPack pack, String vertex) {
      vertex = GLSLModifier.removeComments(vertex);
      return GLSLModifier.insertAtFunctionEnd(
         vertex,
         "main",
         "float physics_x = -1.0 + float((gl_VertexID & 1) << 2);\nfloat physics_y = -1.0 + float((gl_VertexID & 2) << 1);\ngl_Position = vec4(physics_x, physics_y, 0, 1);\n"
      );
   }

   private static String transformFragmentLiquidShader(ShaderInjectionLiquids.ShaderPack pack, String fragment) {
      fragment = GLSLModifier.removeComments(fragment);
      boolean injectNormalMatrix = StarterClient.optifabric && !fragment.contains(normalMatrix);
      List<String> liquidsInjection = getLiquidsInjection();
      if (injectNormalMatrix) {
         liquidsInjection.add("uniform mat3 normalMatrix;");
      }

      fragment = GLSLModifier.insertBeforeFirstFunction(fragment, GLSLModifier.convertToString(liquidsInjection));
      if (pack == ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY) {
         fragment = complementary(fragment);
      } else if (pack == ShaderInjectionLiquids.ShaderPack.COMPLEMENTARY_REIMAGINED) {
         fragment = complementaryReimagined(fragment);
      } else if (pack == ShaderInjectionLiquids.ShaderPack.OTHER) {
         fragment = other(fragment);
      }

      return fragment;
   }

   private static void printError() {
      if (StarterClient.iris()) {
         Iris.liquidsError = "This shader (or shaderpack settings) is not supported by liquid physics!";
      }
   }

   private static String complementary(String source) {
      boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
      if (!hasWaveNormalFunction) {
         printError();
         return source;
      } else {
         source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
         source = GLSLModifier.replaceFunctionContent(source, "GetWaterNormal", "return normalize(" + normalMatrix + " * physics_normal);\n");
         source = GLSLModifier.insertAtFunctionStart(
            source,
            "main",
            "float physics_fragZ = physics_sampleDepth(gl_FragCoord.xy / textureSize(physics_liquidData, 0));\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = physics_getNormalFromDepth();\nif (physics_fragZ == 0.0) discard;\ngl_FragDepth = physics_fragZ;\n"
         );
         source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         return GLSLModifier.replaceVariableReferences(source, "VdotN", "dot(nViewPos, normalize(newNormal))");
      }
   }

   private static String complementaryReimagined(String source) {
      source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
      source = GLSLModifier.insertAtFunctionStart(
         source,
         "main",
         "float physics_fragZ = physics_sampleDepth(gl_FragCoord.xy / textureSize(physics_liquidData, 0));\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nvec3 physics_normalWorld = physics_getNormalFromDepth();\nphysics_normal = normalize("
            + normalMatrix
            + " * physics_normalWorld);\nif (physics_fragZ == 0.0) discard;\ngl_FragDepth = physics_fragZ;\nvec3 physics_eyePos = physics_decodeDepth(gl_FragCoord.xy / textureSize(physics_liquidData, 0), physics_invProjectionMatrix);\nvec2 physics_uv0 = physics_waterCoords(physics_eyePos, physics_normalWorld);\nint physics_lightUV = physics_sampleLightCoords(gl_FragCoord.xy / textureSize(physics_liquidData, 0));\nvec2 physics_lmCoord = clamp((vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0 - 0.03125) * 1.06667, 0.0, 1.0);\n"
      );
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "(tex, texCoord)", "(tex, physics_uv0)");
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "clamp(normalize(normalMap", "clamp(normalize(physics_normal");
      source = GLSLModifier.replaceWithinFunctionContent(source, "main", "dot(normalM", "dot(physics_normal");
      source = GLSLModifier.replaceWithinFunctionContent(
         source, "main", "vec3 normalM = VdotN > 0.0 ? -normal : normal;", "vec3 normalM = VdotN > 0.0 ? -physics_normal : physics_normal;"
      );
      source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmCoord", "physics_lmCoord");
      source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
      return GLSLModifier.replaceVariableReferences(source, "yPosDif", "-2.0");
   }

   private static String other(String source) {
      source = customizedFragment(source);
      return GLSLModifier.insertAtFunctionStart(
         source,
         "main",
         "float physics_fragZ = physics_sampleDepth(gl_FragCoord.xy / textureSize(physics_liquidData, 0));\nphysics_fragdepth = vec4(gl_FragCoord.xy, physics_fragZ, gl_FragCoord.w);\nphysics_normal = physics_getNormalFromDepth();\nif (physics_fragZ == 0.0) discard;\ngl_FragDepth = physics_fragZ;\nvec3 physics_eyePos = physics_decodeDepth(gl_FragCoord.xy / textureSize(physics_liquidData, 0), physics_invProjectionMatrix);\nvec2 physics_uv0 = physics_waterCoords(physics_eyePos, physics_normal);\nint physics_lightUV = physics_sampleLightCoords(gl_FragCoord.xy / textureSize(physics_liquidData, 0));\n"
      );
   }

   private static String customizedFragment(String source) {
      source = GLSLModifier.replaceVariableReferences(source, "gl_FragCoord", "physics_fragdepth");
      boolean hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWavesNormal");
      String normalFunctionName = null;
      boolean ignoreNormalMatrix = false;
      if (hasWaveNormalFunction) {
         normalFunctionName = "GetWavesNormal";
         ignoreNormalMatrix = !source.contains("tbnMatrix");
         source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
         source = GLSLModifier.insertAtFunctionStart(
            source, "main", "vec2 physics_lmCoord = vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0;\n"
         );
         source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmcoord", "physics_lmCoord");
         source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "calcBump");
         if (hasWaveNormalFunction) {
            normalFunctionName = "calcBump";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "(texture, texcoord.xy)", "(texture, physics_uv0)");
            source = GLSLModifier.insertAtFunctionStart(
               source, "main", "vec2 physics_lmCoord = vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0;\n"
            );
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmcoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "GetWaterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "GetWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionStart(
               source,
               "main",
               "vec2 physics_lmCoord = clamp((vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0 - 0.03125) * 1.06667, vec2(0.0), vec2(0.9333, 1.0));\n"
            );
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmCoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaterNormal";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            if (!source.contains("tbnMatrixWorld")) {
               source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "newNormal = normalize(" + normalMatrix + " * physics_normal);\n");
               source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "normal", "normalize(" + normalMatrix + " * physics_normal)");
               source = GLSLModifier.replaceWithinFunctionContent(source, "main", "(texture, texcoord.xy)", "(texture, physics_uv0)");
               source = GLSLModifier.insertAtFunctionStart(
                  source,
                  "main",
                  "vec2 physics_lmCoord = clamp((vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0 - 0.03125) * 1.06667, vec2(0.0), vec2(0.9333, 1.0));\n"
               );
               return GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmCoord", "physics_lmCoord");
            }

            ignoreNormalMatrix = true;
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveHeight");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaveHeight";
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionStart(
               source, "main", "vec4 physics_lmCoord = vec4(physics_uv0, vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0);\n"
            );
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmtexcoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getWaveNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getWaveNormal";
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "flatnormal", "normalize(" + normalMatrix + " * physics_normal)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "bumpmult = 10.0 * 1.0;", "bumpmult = 1.0;");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "viewToWorld(normal)", "normalize(physics_normal)");
            source = GLSLModifier.replaceWithinFunctionContent(
               source, "main", "applyBump(tbnMatrix, NormalTex.xyz, 1.0)", "normalize(" + normalMatrix + " * physics_normal)"
            );
            source = GLSLModifier.insertAtFunctionStart(
               source,
               "main",
               "vec4 physics_lmCoord = vec4(physics_uv0, vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0 * (240.0 / 256.0));\n"
            );
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmtexcoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_normals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "get_normals";
            source = GLSLModifier.insertAtFunctionStart(
               source, "main", "vec2 physics_lmCoord = vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0 * 1.0323886;\n"
            );
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmcoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "noiseNormals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "noiseNormals";
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionStart(
               source, "main", "vec2 physics_lmCoord = vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0;\n"
            );
            source = GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmcoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "waterNormal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "waterNormal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return physics_normal;\n");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "(gcolor, uv[0])", "(gcolor, physics_uv0)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "encodeNormal(normal)", "encodeNormal(physics_normal)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "normalize(viewPos)", "normalize(physics_eyePos)");
            source = GLSLModifier.replaceWithinFunctionContent(source, "main", "diffuse * shadow.color * shadow.shadow", "diffuse * shadow.color");
            source = GLSLModifier.insertAtFunctionStart(
               source,
               "main",
               "vec2 physics_lmCoord = vec2(clamp(((float(physics_lightUV & 0xF) / 16.0)-1.0/24.0)/(1.0-1.0/24.0), 0.0, 1.0), clamp(((float((physics_lightUV >> 4) & 0xF) / 16.0)-1.0/16.0)/(1.0-1.0/16.0), 0.0, 1.0));\n"
            );
            return GLSLModifier.replaceWithinFunctionContent(source, "main", "uv[1]", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "getNormals");
         if (hasWaveNormalFunction) {
            normalFunctionName = "getNormals";
            ignoreNormalMatrix = true;
            source = GLSLModifier.replaceVariableReferences(source, "tbnMatrix", "mat3(1.0)");
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_normal);\n");
            source = GLSLModifier.insertAtFunctionStart(
               source, "main", "vec2 physics_lmCoord = vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0;\n"
            );
            return GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "lmcoord", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         hasWaveNormalFunction = GLSLModifier.hasFunction(source, "get_water_normal");
         if (hasWaveNormalFunction) {
            normalFunctionName = "get_water_normal";
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_normal.xyz);\n");
            source = GLSLModifier.replaceVariableReferences(source, "tbn", "mat3(1.0)");
            source = GLSLModifier.insertAtFunctionStart(
               source,
               "main",
               "vec2 physics_lmCoord = clamp(vec2(float(physics_lightUV & 0xF), float((physics_lightUV >> 4) & 0xF)) / 16.0 * (1.0 / 240.0), 0.0, 1.0);\n"
            );
            return GLSLModifier.replaceVariableReferencesWithinFunction(source, "main", "light_levels", "physics_lmCoord");
         }
      }

      if (!hasWaveNormalFunction) {
         printError();
         return source;
      } else {
         if (ignoreNormalMatrix) {
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(physics_normal);\n");
         } else {
            source = GLSLModifier.replaceFunctionContent(source, normalFunctionName, "return normalize(" + normalMatrix + " * physics_normal);\n");
         }

         return source;
      }
   }

   private static List<String> getLiquidsInjection() {
      String asset = "assets/physicsmod/shaders/include/liquids.glsl";
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
