package net.diebuddies.jbox2d.common;

public class OBBViewportTransform implements IViewportTransform {
   protected final OBBViewportTransform.OBB box = new OBBViewportTransform.OBB();
   private boolean yFlip = false;
   private final Mat22 yFlipMat = new Mat22(1.0F, 0.0F, 0.0F, -1.0F);
   private final Mat22 inv = new Mat22();
   private final Mat22 inv2 = new Mat22();

   public OBBViewportTransform() {
      this.box.R.setIdentity();
   }

   public void set(OBBViewportTransform vpt) {
      this.box.center.set(vpt.box.center);
      this.box.extents.set(vpt.box.extents);
      this.box.R.set(vpt.box.R);
      this.yFlip = vpt.yFlip;
   }

   @Override
   public void setCamera(float x, float y, float scale) {
      this.box.center.set(x, y);
      Mat22.createScaleTransform(scale, this.box.R);
   }

   @Override
   public Vec2 getExtents() {
      return this.box.extents;
   }

   @Override
   public Mat22 getMat22Representation() {
      return this.box.R;
   }

   @Override
   public void setExtents(Vec2 argExtents) {
      this.box.extents.set(argExtents);
   }

   @Override
   public void setExtents(float halfWidth, float halfHeight) {
      this.box.extents.set(halfWidth, halfHeight);
   }

   @Override
   public Vec2 getCenter() {
      return this.box.center;
   }

   @Override
   public void setCenter(Vec2 argPos) {
      this.box.center.set(argPos);
   }

   @Override
   public void setCenter(float x, float y) {
      this.box.center.set(x, y);
   }

   public Mat22 getTransform() {
      return this.box.R;
   }

   public void setTransform(Mat22 transform) {
      this.box.R.set(transform);
   }

   @Override
   public void mulByTransform(Mat22 transform) {
      this.box.R.mulLocal(transform);
   }

   @Override
   public boolean isYFlip() {
      return this.yFlip;
   }

   @Override
   public void setYFlip(boolean yFlip) {
      this.yFlip = yFlip;
   }

   @Override
   public void getScreenVectorToWorld(Vec2 screen, Vec2 world) {
      this.box.R.invertToOut(this.inv);
      this.inv.mulToOut(screen, world);
      if (this.yFlip) {
         this.yFlipMat.mulToOut(world, world);
      }
   }

   @Override
   public void getWorldVectorToScreen(Vec2 world, Vec2 screen) {
      this.box.R.mulToOut(world, screen);
      if (this.yFlip) {
         this.yFlipMat.mulToOut(screen, screen);
      }
   }

   @Override
   public void getWorldToScreen(Vec2 world, Vec2 screen) {
      screen.x = world.x - this.box.center.x;
      screen.y = world.y - this.box.center.y;
      this.box.R.mulToOut(screen, screen);
      if (this.yFlip) {
         this.yFlipMat.mulToOut(screen, screen);
      }

      screen.x = screen.x + this.box.extents.x;
      screen.y = screen.y + this.box.extents.y;
   }

   @Override
   public void getScreenToWorld(Vec2 screen, Vec2 world) {
      world.x = screen.x - this.box.extents.x;
      world.y = screen.y - this.box.extents.y;
      if (this.yFlip) {
         this.yFlipMat.mulToOut(world, world);
      }

      this.box.R.invertToOut(this.inv2);
      this.inv2.mulToOut(world, world);
      world.x = world.x + this.box.center.x;
      world.y = world.y + this.box.center.y;
   }

   public static class OBB {
      public final Mat22 R = new Mat22();
      public final Vec2 center = new Vec2();
      public final Vec2 extents = new Vec2();
   }
}
