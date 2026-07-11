package net.diebuddies.physics.ocean;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;

public class OceanRenderState {
   public float yOffset;
   public float ox = 0.0F;
   public float oy = 0.375F;
   public float oz = 0.0F;
   public float diffRot = 0.0F;
   public double forwardZ;
   public double forwardX;
   public double roll;
   public double pitch;
   public boolean isInBoat;
   public boolean valkyrienPatch;

   public void apply(PoseStack poseStack) {
      if (this.valkyrienPatch) {
         poseStack.translate(this.ox, this.oy, this.oz);
      } else {
         double leftX = this.forwardZ;
         double leftZ = -this.forwardX;
         poseStack.translate(0.0F, this.yOffset, 0.0F);
         if (this.isInBoat) {
            poseStack.translate(this.ox, this.oy, this.oz);
            poseStack.mulPose(Axis.YP.rotationDegrees(-this.diffRot));
            poseStack.mulPose(Axis.of(new Vector3f((float)this.forwardX, 0.0F, (float)this.forwardZ)).rotationDegrees((float)(-Math.toDegrees(this.roll))));
            poseStack.mulPose(Axis.of(new Vector3f((float)leftX, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(this.pitch)));
            poseStack.mulPose(Axis.YP.rotationDegrees(this.diffRot));
            poseStack.translate(-this.ox, -this.oy, -this.oz);
         }
      }
   }
}
