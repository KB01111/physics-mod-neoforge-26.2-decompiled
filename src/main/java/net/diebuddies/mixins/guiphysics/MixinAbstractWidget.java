package net.diebuddies.mixins.guiphysics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.BodyType;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.math.Math;
import net.diebuddies.physics.Box2DUtil;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.diebuddies.physics.settings.gui.ScreenExtension;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.Animator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractWidget.class})
public class MixinAbstractWidget implements Animatable {
   @Shadow
   private int width;
   @Shadow
   private int height;
   @Shadow
   private int x;
   @Shadow
   private int y;
   @Unique
   private double positionX;
   @Unique
   private double positionY;
   @Unique
   private float rotation;
   @Unique
   private Body buttonBody;
   @Unique
   private float totalDelta;
   @Unique
   private boolean isSimActive;
   @Unique
   private float animX;
   @Unique
   private float animY;
   @Unique
   private float animWidth;
   @Unique
   private float animHeight;
   @Unique
   private List<Animator> animations;
   @Unique
   private float animRed = 1.0F;
   @Unique
   private float animGreen = 1.0F;
   @Unique
   private float animBlue = 1.0F;
   @Unique
   private float animAlpha = 1.0F;
   @Unique
   private float animDepth = 0.0F;
   @Unique
   private float renderPercent = 0.0F;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   private void physicsmod$constructor(int x, int y, int width, int height, Component component, CallbackInfo info) {
      this.animX = (float)x;
      this.animY = (float)y;
      this.animWidth = (float)width;
      this.animHeight = (float)height;
      this.animations = new ObjectArrayList();
   }

   @Unique
   private void physicsmod$physicsSetup() {
      Screen screen = Minecraft.getInstance().gui.screen();
      if (screen != null && !(screen instanceof PhysicsSettingsScreen)) {
         World world = ((ScreenExtension)screen).getPhysicsWorld();
         AbstractWidget widget = (AbstractWidget)(Object)this;
         this.buttonBody = Box2DUtil.createBox(
            world, (float)widget.getX(), (float)widget.getY(), (float)widget.getWidth(), (float)widget.getHeight(), BodyType.DYNAMIC
         );
         this.buttonBody.setAngularVelocity(Math.random() * 10.0F - 5.0F);
         this.buttonBody.setLinearVelocity(new Vec2(Math.random() * 100.0F - 50.0F, Math.random() * 100.0F - 50.0F));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"extractRenderState"},
      cancellable = true
   )
   private void physicsmod$renderHead(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      this.renderPercent += delta;

      while (this.renderPercent >= 1.0F) {
         this.renderPercent--;

         for (Animator animator : this.animations) {
            animator.tick(this);
         }
      }

      Screen screen = Minecraft.getInstance().gui.screen();
      if (screen != null) {
         World world = ((ScreenExtension)screen).getPhysicsWorld();
         if (world != null && (this.buttonBody == null || world != this.buttonBody.getWorld())) {
            this.physicsmod$physicsSetup();
         }

         if (this.buttonBody != null) {
            Vec2 pos = this.buttonBody.getPosition();
            this.rotation = this.buttonBody.getAngle();
            this.positionX = (double)pos.x;
            this.positionY = (double)pos.y;
            ((MixinAbstractWidgetAccessor)this).setIsHovered(this.isInside((double)mouseX, (double)mouseY));
            this.isSimActive = true;
         }

         if (world == null && this.isSimActive) {
            this.buttonBody = null;
            this.totalDelta += 0.05F * delta;
            AbstractWidget widget = (AbstractWidget)(Object)this;
            float posX = (float)((double)this.getAnimX() + (double)this.getAnimWidth() / 2.0);

            for (float posY = (float)((double)this.getAnimY() + (double)this.getAnimHeight() / 2.0);
               this.totalDelta > 0.004166667F;
               this.totalDelta -= 0.004166667F
            ) {
               this.rotation = org.joml.Math.lerp(this.rotation, 0.0F, 0.05F);
               this.positionX = org.joml.Math.lerp(this.positionX, (double)posX, 0.05F);
               this.positionY = org.joml.Math.lerp(this.positionY, (double)posY, 0.05F);
            }

            if (java.lang.Math.abs(this.positionX - (double)posX) < 0.1F
               && java.lang.Math.abs(this.positionX - (double)posX) < 0.1F
               && java.lang.Math.abs(this.rotation) < 0.005F) {
               this.isSimActive = false;
            }
         } else {
            this.totalDelta = 0.0F;
         }

         Matrix3x2fStack poseStack = guiGraphics.pose();
         if (this.physicsmod$isAnimationActive()) {
            poseStack.pushMatrix();
            this.physicsmod$applyTransformation(poseStack);
         }

         boolean cancelled = false;
         float tickAdjustedDelta = delta / 20.0F;

         for (Animator animator : this.animations) {
            cancelled |= animator.extraxtRenderState(this, guiGraphics, mouseX, mouseY, this.renderPercent, tickAdjustedDelta);
         }

         if (cancelled) {
            if (this.physicsmod$isAnimationActive()) {
               poseStack.popMatrix();
               ((MixinAbstractWidgetAccessor)this).setIsHovered(this.isInside((double)mouseX, (double)mouseY));
            }

            info.cancel();
         }
      }
   }

   @Unique
   private boolean physicsmod$isAnimationActive() {
      Screen screen = Minecraft.getInstance().gui.screen();
      return screen != null && !(screen instanceof PhysicsSettingsScreen) ? this.buttonBody != null || this.isSimActive : false;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"extractRenderState"}
   )
   private void physicsmod$renderTail(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      if (this.physicsmod$isAnimationActive()) {
         guiGraphics.pose().popMatrix();
         ((MixinAbstractWidgetAccessor)this).setIsHovered(this.isInside((double)mouseX, (double)mouseY));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"isMouseOver"},
      cancellable = true
   )
   private void physicsmod$isMouseOver(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> info) {
      if (this.physicsmod$isAnimationActive()) {
         AbstractWidget widget = (AbstractWidget)(Object)this;
         info.setReturnValue(widget.active && widget.visible && this.isInside(mouseX, mouseY));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"areCoordinatesInRectangle"},
      cancellable = true
   )
   private void physicsmod$areCoordinatesInRectangle(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> info) {
      if (this.physicsmod$isAnimationActive()) {
         info.setReturnValue(this.isInside(mouseX, mouseY));
      }
   }

   @Unique
   private void physicsmod$applyTransformation(Matrix3x2fStack poseStack) {
      poseStack.translate(
         (float)(this.positionX - ((double)this.getAnimX() + (double)this.getAnimWidth() / 2.0)),
         (float)(this.positionY - ((double)this.getAnimY() + (double)this.getAnimHeight() / 2.0))
      );
      poseStack.translate(
         (float)((double)this.getAnimX() + (double)this.getAnimWidth() / 2.0), (float)((double)this.getAnimY() + (double)this.getAnimHeight() / 2.0)
      );
      poseStack.rotate(this.rotation);
      poseStack.translate(
         (float)(-((double)this.getAnimX() + (double)this.getAnimWidth() / 2.0)), (float)(-((double)this.getAnimY() + (double)this.getAnimHeight() / 2.0))
      );
   }

   @Override
   public boolean isInside(double mouseX, double mouseY) {
      if (this.physicsmod$isAnimationActive()) {
         Matrix3x2fStack poseStack = new Matrix3x2fStack();
         this.physicsmod$applyTransformation(poseStack);
         Vector3f trMouse = new Vector3f((float)mouseX, (float)mouseY, 1.0F);
         poseStack.invert();
         poseStack.transform(trMouse);
         return (double)trMouse.x() >= (double)this.getAnimX()
            && (double)trMouse.y() >= (double)this.getAnimY()
            && (double)trMouse.x() < (double)(this.getAnimX() + this.getAnimWidth())
            && (double)trMouse.y() < (double)(this.getAnimY() + this.getAnimHeight());
      } else {
         return mouseX >= (double)this.getAnimX()
            && mouseY >= (double)this.getAnimY()
            && mouseX < (double)(this.getAnimX() + this.getAnimWidth())
            && mouseY < (double)(this.getAnimY() + this.getAnimHeight());
      }
   }

   @Override
   public Animatable addAnimator(Animator animator) {
      this.animations.add(animator);
      animator.init(this);
      return this;
   }

   @Override
   public Animatable addAnimator(int offset, Animator animator) {
      this.animations.add(offset, animator);
      animator.init(this);
      return this;
   }

   @Override
   public Animatable addAnimator(List<Animator> animators) {
      for (Animator animator : animators) {
         this.animations.add(animator);
         animator.init(this);
      }

      return this;
   }

   @Override
   public Animatable addAnimator(Animator... animators) {
      for (Animator animator : animators) {
         this.animations.add(animator);
         animator.init(this);
      }

      return this;
   }

   @Override
   public List<Animator> getAnimators() {
      return this.animations;
   }

   @Override
   public <T extends Animator> T getAnimator(Class<T> clzz) {
      for (Animator animation : this.animations) {
         if (clzz == animation.getClass()) {
            return (T)animation;
         }
      }

      return null;
   }

   @Override
   public Animatable removeAnimator(Animator animator) {
      this.animations.remove(animator);
      return this;
   }

   @Override
   public float getAnimX() {
      return this.animX;
   }

   @Override
   public Animatable setAnimX(float x) {
      this.animX = x;
      this.x = (int)x;
      return this;
   }

   @Override
   public float getAnimY() {
      return this.animY;
   }

   @Override
   public Animatable setAnimY(float y) {
      this.animY = y;
      this.y = (int)y;
      return this;
   }

   @Override
   public float getAnimWidth() {
      return this.animWidth;
   }

   @Override
   public Animatable setAnimWidth(float width) {
      this.animWidth = width;
      this.width = (int)width;
      return this;
   }

   @Override
   public float getAnimHeight() {
      return this.animHeight;
   }

   @Override
   public Animatable setAnimHeight(float height) {
      this.animHeight = height;
      this.height = (int)height;
      return this;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setX"}
   )
   public void setX(int x, CallbackInfo info) {
      this.animX = (float)x;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setY"}
   )
   public void setY(int y, CallbackInfo info) {
      this.animY = (float)y;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setWidth"}
   )
   public void setWidth(int width, CallbackInfo info) {
      this.animWidth = (float)width;
   }

   @Override
   public float getAnimRed() {
      return this.animRed;
   }

   @Override
   public Animatable setAnimRed(float red) {
      this.animRed = red;
      return this;
   }

   @Override
   public float getAnimGreen() {
      return this.animGreen;
   }

   @Override
   public Animatable setAnimGreen(float green) {
      this.animGreen = green;
      return this;
   }

   @Override
   public float getAnimBlue() {
      return this.animBlue;
   }

   @Override
   public Animatable setAnimBlue(float blue) {
      this.animBlue = blue;
      return this;
   }

   @Override
   public float getAnimAlpha() {
      return this.animAlpha;
   }

   @Override
   public Animatable setAnimAlpha(float alpha) {
      this.animAlpha = alpha;
      return this;
   }

   @Override
   public Animatable setAnimColor(float red, float green, float blue, float alpha) {
      this.animRed = red;
      this.animGreen = green;
      this.animBlue = blue;
      this.animAlpha = alpha;
      return this;
   }

   @Override
   public float getAnimDepth() {
      return this.animDepth;
   }

   @Override
   public Animatable setAnimDepth(float depth) {
      this.animDepth = depth;
      return this;
   }
}
