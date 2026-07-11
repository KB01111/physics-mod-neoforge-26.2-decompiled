package net.diebuddies.physics.settings.ux;

import java.util.List;

public interface Animatable {
   Animatable addAnimator(Animator var1);

   Animatable addAnimator(int var1, Animator var2);

   Animatable addAnimator(Animator... var1);

   Animatable addAnimator(List<Animator> var1);

   Animatable removeAnimator(Animator var1);

   List<Animator> getAnimators();

   <T extends Animator> T getAnimator(Class<T> var1);

   float getAnimX();

   Animatable setAnimX(float var1);

   float getAnimY();

   Animatable setAnimY(float var1);

   float getAnimWidth();

   Animatable setAnimWidth(float var1);

   float getAnimHeight();

   Animatable setAnimHeight(float var1);

   float getAnimRed();

   Animatable setAnimRed(float var1);

   float getAnimGreen();

   Animatable setAnimGreen(float var1);

   float getAnimBlue();

   Animatable setAnimBlue(float var1);

   float getAnimAlpha();

   Animatable setAnimAlpha(float var1);

   Animatable setAnimColor(float var1, float var2, float var3, float var4);

   float getAnimDepth();

   Animatable setAnimDepth(float var1);

   boolean isInside(double var1, double var3);
}
