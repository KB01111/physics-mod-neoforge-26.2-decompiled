package net.diebuddies.jbox2d.common;

public interface IViewportTransform {
   boolean isYFlip();

   void setYFlip(boolean var1);

   Vec2 getExtents();

   void setExtents(Vec2 var1);

   void setExtents(float var1, float var2);

   Vec2 getCenter();

   void setCenter(Vec2 var1);

   void setCenter(float var1, float var2);

   void setCamera(float var1, float var2, float var3);

   void getWorldVectorToScreen(Vec2 var1, Vec2 var2);

   void getScreenVectorToWorld(Vec2 var1, Vec2 var2);

   Mat22 getMat22Representation();

   void getWorldToScreen(Vec2 var1, Vec2 var2);

   void getScreenToWorld(Vec2 var1, Vec2 var2);

   void mulByTransform(Mat22 var1);
}
