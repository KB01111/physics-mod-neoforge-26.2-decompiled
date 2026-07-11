package net.diebuddies.bridge;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

public class ReflectionsForge {
   public static final Method setupRotations = ObfuscationReflectionHelper.findMethod(
      LivingEntityRenderer.class, "setupRotations", new Class[]{LivingEntityRenderState.class, PoseStack.class, float.class, float.class}
   );
   public static final Method buildGroup = ObfuscationReflectionHelper.findMethod(
      RenderTypeFeatureRenderer.class, "buildGroup", new Class[]{FeatureFrameContext.class, List.class}
   );
}
