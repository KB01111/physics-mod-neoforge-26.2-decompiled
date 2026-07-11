package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.util.GLSLModifier;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class ShaderInjectionSmoke {
   private static String position = "gl_Vertex";
   private static String normalMatrix = "gl_NormalMatrix";
   private static String normal = "gl_Normal";
   private static String multiCoord0 = "gl_MultiTexCoord0";
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
      normal = "vaNormal";
      multiCoord0 = "vec4(vaUV0, 0.0, 1.0)";
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

      return vertexSource == null ? null : transformVertexSmokeShader(vertexSource);
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
            Iris.smokeError = "";
         }

         return transformFragmentSmokeShader(fragmentSource, false);
      }
   }

   @Nullable
   public static String getVertexShadowSource(String vertexSource) {
      if (StarterClient.optifabric) {
         setupOptifineInjection();
      }

      return vertexSource == null ? null : transformVertexSmokeShader(vertexSource);
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
            Iris.smokeError = "";
         }

         return transformFragmentSmokeShader(fragmentSource, true);
      }
   }

   private static String transformVertexSmokeShader(String vertex) {
      vertex = GLSLModifier.removeComments(vertex);
      List<String> smokeInjection = getSmokeInjectionVertex();
      vertex = GLSLModifier.insertBeforeFirstFunction(vertex, GLSLModifier.convertToString(smokeInjection));
      vertex = StringUtils.replace(vertex, position, "physics_finalPosition");
      vertex = StringUtils.replace(vertex, "physics_finalPositionID", "gl_VertexID");
      vertex = StringUtils.replace(vertex, "gl_Normal", "physics_normal");
      vertex = StringUtils.replace(vertex, "physics_normalMatrix", "gl_NormalMatrix");
      vertex = GLSLModifier.insertAtFunctionStart(
         vertex, "main", "physics_transformVertex(" + position + ".xyz, " + normal + ".xyz);\nphysics_setSmokeAttributes(" + multiCoord0 + ".xy);\n"
      );
      return GLSLModifier.replaceFunctionCalls(vertex, "ftransform", "(" + modelViewProjectionMatrix + " * physics_finalPosition)");
   }

   private static String transformFragmentSmokeShader(String fragment, boolean shadow) {
      fragment = GLSLModifier.removeComments(fragment);
      List<String> smokeInjection = getSmokeInjectionFragment();
      fragment = GLSLModifier.insertBeforeFirstFunction(fragment, GLSLModifier.convertToString(smokeInjection));
      fragment = GLSLModifier.insertAtFunctionStart(fragment, "main", "vec3 physics_smokeColor = physics_calculateSmoke();\n");
      if (!shadow) {
         Map<Integer, GLSLModifier.VarInfo> outputs = GLSLModifier.findOutputNames(fragment);
         String outputName = outputColor0;
         if (outputs.containsKey(0)) {
            GLSLModifier.VarInfo info = outputs.get(0);
            outputName = info.varName();
            if (outputName.equals("gbuffer_data_0")) {
               fragment = GLSLModifier.insertAtFunctionEnd(
                  fragment, "main", outputName + ".x = pack_unorm_2x8(clamp(physics_smokeColor.rg, vec2(0.0), vec2(1.0)));\n"
               );
               return GLSLModifier.insertAtFunctionEnd(
                  fragment, "main", outputName + ".y = pack_unorm_2x8(physics_smokeColor.b, clamp(float(new_material_mask) * (1.0 / (255.0)), 0.0, 1.0));\n"
               );
            }

            if (info.glslType().equalsIgnoreCase("uvec4") && outputName.equals("outColor0")) {
               fragment = GLSLModifier.insertAtFunctionEnd(
                  fragment, "main", "data.r = packUnorm4x8(vec4(clamp(physics_smokeColor.rgb, vec3(0.0), vec3(1.0)), 1.0));\n"
               );
               fragment = GLSLModifier.insertAtFunctionEnd(fragment, "main", "data.g = packUnorm4x8(vec4(physics_pass_normal, 1.0));\n");
               return GLSLModifier.insertAtFunctionEnd(fragment, "main", "outColor0 = data;\n");
            }
         }

         if (fragment.contains("encodeVec2(data0")) {
            return GLSLModifier.insertAtFunctionEnd(
               fragment,
               "main",
               outputName
                  + " = vec4(encodeVec2(clamp(physics_smokeColor.x, 0.0, 1.0),data1.x),encodeVec2(clamp(physics_smokeColor.y, 0.0, 1.0),data1.y),encodeVec2(clamp(physics_smokeColor.z, 0.0, 1.0),data1.z),encodeVec2(data1.w,data0.w));\n"
            );
         }

         if (fragment.contains("vec4(normalize(albedo.rgb+0.000")) {
            fragment = GLSLModifier.insertAtFunctionEnd(
               fragment, "main", outputColor0 + " = vec4(pow(physics_smokeColor.rgb,vec3(2.2)) * (finalSunlight*diffuse+ambientNdotL.rgb), 1.0);\n"
            );
            return GLSLModifier.insertAtFunctionEnd(fragment, "main", outputColor1 + " = vec4(normalize(physics_smokeColor+0.00001), 1.0);\n");
         }

         fragment = GLSLModifier.insertAtFunctionEnd(fragment, "main", outputName + " = vec4(physics_smokeColor, 1.0);\n");
      }

      return fragment;
   }

   private static List<String> getSmokeInjectionVertex() {
      return getSmokeInjection("assets/physicsmod/shaders/include/smoke_vertex.glsl");
   }

   private static List<String> getSmokeInjectionFragment() {
      return getSmokeInjection("assets/physicsmod/shaders/include/smoke_fragment.glsl");
   }

   private static List<String> getSmokeInjection(String source) {
      List<String> lines = new ObjectArrayList();
      String all = "";
      int brackets = 0;

      String line;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(PhysicsMod.class.getClassLoader().getResourceAsStream(source)))) {
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
}
