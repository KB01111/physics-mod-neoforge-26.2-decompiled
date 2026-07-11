package net.diebuddies.physics.ocean.test;

public class OceanSimulation {
   public static final int RANGE = 16;
   public static final int OCEAN = 0;
   public static final int SOLID = 1;
   public int[][] waves = new int[70][40];
   public int[][] depth = new int[70][40];
   public int[][] blurDepth = new int[70][40];
   public int[][] map = new int[70][40];

   public OceanSimulation() {
      for (int x = 0; x < this.depth.length; x++) {
         for (int y = 0; y < this.depth[0].length; y++) {
            this.depth[x][y] = 17;
         }
      }
   }

   public void update(double delta) {
      this.updateOceanValues();
   }

   public void updateOceanValues() {
      for (int x = 0; x < this.map.length; x++) {
         for (int y = 0; y < this.map[0].length; y++) {
            this.waves[x][y] = this.map[x][y] == 1 ? 16 : 0;
         }
      }

      this.blur(this.waves);

      for (int x = 0; x < this.depth.length; x++) {
         for (int y = 0; y < this.depth[0].length; y++) {
            this.blurDepth[x][y] = Math.max(0, 16 - (this.depth[x][y] - 1));
         }
      }

      this.blur(this.blurDepth);

      for (int x = 0; x < this.waves.length; x++) {
         for (int y = 0; y < this.waves[0].length; y++) {
            this.waves[x][y] = Math.max(this.waves[x][y], this.blurDepth[x][y]);
         }
      }
   }

   private void blur(int[][] arr) {
      for (int y = 0; y < arr[0].length; y++) {
         for (int x = 0; x < arr.length - 1; x++) {
            arr[x + 1][y] = Math.max(0, Math.max(arr[x + 1][y], arr[x][y] - 1));
         }

         for (int x = arr.length - 1; x >= 1; x--) {
            arr[x - 1][y] = Math.max(0, Math.max(arr[x - 1][y], arr[x][y] - 1));
         }
      }

      for (int x = 0; x < arr.length; x++) {
         for (int y = 0; y < arr[0].length - 1; y++) {
            arr[x][y + 1] = Math.max(0, Math.max(arr[x][y + 1], arr[x][y] - 1));
         }

         for (int y = arr[0].length - 1; y >= 1; y--) {
            arr[x][y - 1] = Math.max(0, Math.max(arr[x][y - 1], arr[x][y] - 1));
         }
      }
   }

   private boolean outsideRange(int x, int y) {
      return x < 0 || y < 0 || x >= this.map.length || y >= this.map[0].length;
   }

   public boolean isSolid(int x, int y) {
      return this.outsideRange(x, y) ? true : this.map[x][y] == 1;
   }

   public void setSolid(int x, int y) {
      if (!this.outsideRange(x, y)) {
         this.map[x][y] = 1;
      }
   }

   public void setOcean(int x, int y) {
      if (!this.outsideRange(x, y)) {
         this.map[x][y] = 0;
      }
   }

   public void setOceanDepth(int x, int y, int depth) {
      if (!this.outsideRange(x, y)) {
         this.depth[x][y] = depth;
      }
   }
}
