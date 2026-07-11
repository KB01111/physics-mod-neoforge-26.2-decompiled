package net.diebuddies.compat;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

public class EMF {
   private static boolean emfModel = false;

   public static void prepare(LivingEntity entity, EntityRenderState renderState) {
      emfModel = false;

      try {
         Object manager = EMFManager.getInstance();
         Field field = manager.getClass().getField("rootPartsPerEntityTypeForVariation");
         if (field.get(manager) instanceof Map<?, ?> map) {
            Object key = ((EMFEntity)entity).emf$getTypeString();
            emfModel = map.get(key) != null;
         }
      } catch (Exception var8) {
         emfModel = false;
      }

      try {
         if (((HoldsETFRenderState)renderState).etf$getState() instanceof EMFEntityRenderState emfState) {
            EMFAnimationEntityContext.setCurrentEntityIteration(emfState);
         }
      } catch (Exception var7) {
      }
   }

   public static boolean isEMFModel() {
      return emfModel;
   }
}
