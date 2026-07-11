package net.diebuddies.jbox2d.callbacks;

import net.diebuddies.jbox2d.common.Color3f;
import net.diebuddies.jbox2d.common.IViewportTransform;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.particle.ParticleColor;

public abstract class DebugDraw {
   public static final int e_shapeBit = 2;
   public static final int e_jointBit = 4;
   public static final int e_aabbBit = 8;
   public static final int e_pairBit = 16;
   public static final int e_centerOfMassBit = 32;
   public static final int e_dynamicTreeBit = 64;
   public static final int e_wireframeDrawingBit = 128;
   protected int m_drawFlags = 0;
   protected IViewportTransform viewportTransform;

   public DebugDraw() {
      this(null);
   }

   public DebugDraw(IViewportTransform viewport) {
      this.viewportTransform = viewport;
   }

   public void setViewportTransform(IViewportTransform viewportTransform) {
      this.viewportTransform = viewportTransform;
   }

   public void setFlags(int flags) {
      this.m_drawFlags = flags;
   }

   public int getFlags() {
      return this.m_drawFlags;
   }

   public void appendFlags(int flags) {
      this.m_drawFlags |= flags;
   }

   public void clearFlags(int flags) {
      this.m_drawFlags &= ~flags;
   }

   public void drawPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
      if (vertexCount == 1) {
         this.drawSegment(vertices[0], vertices[0], color);
      } else {
         for (int i = 0; i < vertexCount - 1; i++) {
            this.drawSegment(vertices[i], vertices[i + 1], color);
         }

         if (vertexCount > 2) {
            this.drawSegment(vertices[vertexCount - 1], vertices[0], color);
         }
      }
   }

   public abstract void drawPoint(Vec2 var1, float var2, Color3f var3);

   public abstract void drawSolidPolygon(Vec2[] var1, int var2, Color3f var3);

   public abstract void drawCircle(Vec2 var1, float var2, Color3f var3);

   public void drawCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
      this.drawCircle(center, radius, color);
   }

   public abstract void drawSolidCircle(Vec2 var1, float var2, Vec2 var3, Color3f var4);

   public abstract void drawSegment(Vec2 var1, Vec2 var2, Color3f var3);

   public abstract void drawTransform(Transform var1);

   public abstract void drawString(float var1, float var2, String var3, Color3f var4);

   public abstract void drawParticles(Vec2[] var1, float var2, ParticleColor[] var3, int var4);

   public abstract void drawParticlesWireframe(Vec2[] var1, float var2, ParticleColor[] var3, int var4);

   public void flush() {
   }

   public void drawString(Vec2 pos, String s, Color3f color) {
      this.drawString(pos.x, pos.y, s, color);
   }

   public IViewportTransform getViewportTranform() {
      return this.viewportTransform;
   }

   public void setCamera(float x, float y, float scale) {
      this.viewportTransform.setCamera(x, y, scale);
   }

   public void getScreenToWorldToOut(Vec2 argScreen, Vec2 argWorld) {
      this.viewportTransform.getScreenToWorld(argScreen, argWorld);
   }

   public void getWorldToScreenToOut(Vec2 argWorld, Vec2 argScreen) {
      this.viewportTransform.getWorldToScreen(argWorld, argScreen);
   }

   public void getWorldToScreenToOut(float worldX, float worldY, Vec2 argScreen) {
      argScreen.set(worldX, worldY);
      this.viewportTransform.getWorldToScreen(argScreen, argScreen);
   }

   public Vec2 getWorldToScreen(Vec2 argWorld) {
      Vec2 screen = new Vec2();
      this.viewportTransform.getWorldToScreen(argWorld, screen);
      return screen;
   }

   public Vec2 getWorldToScreen(float worldX, float worldY) {
      Vec2 argScreen = new Vec2(worldX, worldY);
      this.viewportTransform.getWorldToScreen(argScreen, argScreen);
      return argScreen;
   }

   public void getScreenToWorldToOut(float screenX, float screenY, Vec2 argWorld) {
      argWorld.set(screenX, screenY);
      this.viewportTransform.getScreenToWorld(argWorld, argWorld);
   }

   public Vec2 getScreenToWorld(Vec2 argScreen) {
      Vec2 world = new Vec2();
      this.viewportTransform.getScreenToWorld(argScreen, world);
      return world;
   }

   public Vec2 getScreenToWorld(float screenX, float screenY) {
      Vec2 screen = new Vec2(screenX, screenY);
      this.viewportTransform.getScreenToWorld(screen, screen);
      return screen;
   }
}
