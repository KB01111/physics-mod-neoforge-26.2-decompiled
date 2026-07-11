package net.diebuddies.opengl;

import org.joml.Vector3d;

public class Data {
   public static final Data POSITION = new Data(0, 3);
   public static final Data COLOR = new Data(1, 4, true, 5121, 1.0F, 1.0F, 1.0F, 1.0F);
   public static final Data TEX_COORD = new Data(2, 2);
   public static final Data OVERLAY = new Data(3, 2, false, 5123, false, true);
   public static final Data LIGHT = new Data(4, 2, false, 5123, false, true);
   public static final Data NORMAL = new Data(5, 4, true, 5120);
   public static final Data INDEX = new Data(6, 1);
   public static final Data POSITION_SHADER = new Data(0, 3);
   public static final Data COLOR_SHADER = new Data(1, 4, true, 5121, 1.0F, 1.0F, 1.0F, 1.0F);
   public static final Data TEX_COORD_SHADER = new Data(2, 2);
   public static final Data LIGHT_SHADER = new Data(3, 2, false, 5123, false, true);
   public static final Data OCEAN_WAVINESS = new Data(4, 1, true, 5126);
   public static final Data OCEAN_WAVINESS_SHADER = new Data(9, 1, true, 5126);
   public static final Data MID_TEX_COORD_SHADER = new Data(7, 2);
   public static final Data TANGENT_SHADER = new Data(8, 4, true, 5120);
   public static final Data MID_TEX_COORD_TERRAIN_SHADER = new Data(6, 2);
   public static final Data TANGENT_TERRAIN_SHADER = new Data(7, 4, true, 5120);
   public static final Data NORMAL_SHADER = new Data(4, 4, true, 5120);
   public static final Data ENTITY_ID_SHADER = new Data(6, 4, 5123);
   public static final Data MID_TEX_COORD_OPTIFINE = new Data(12, 2);
   public static final Data TANGENT_OPTIFINE = new Data(13, 4, true, 5120);
   public static final Data SMOKE_LIGHT = new Data(6, 4, false, 5121, true, true);
   public static final Data SMOKE_POS = new Data(7, 4, false, 5126, true, false);
   public static final Data SMOKE_POS_NEW = new Data(8, 4, false, 5126, true, false);
   public static final Data PUDDLE_POS = new Data(6, 4, false, 5126, true, false);
   public static final Data PUDDLE_POS_NEW = new Data(7, 4, false, 5126, true, false);
   public static final Data LIQUID_LIGHT = new Data(6, 4, false, 5121, true, true);
   public static final Data LIQUID_POS = new Data(7, 4, false, 5126, true, false);
   public static final Data LIQUID_POS_NEW = new Data(8, 4, false, 5126, true, false);
   public static final Data EMPTY = new Data(10, 1);
   private float[] defaultValue;
   private boolean normalize;
   private int dataType;
   private int dataTypeSize;
   private int attribute;
   private int size;
   private int stride;
   private boolean instanced;
   private boolean pureInteger;

   private Data(int attribute, int size, boolean normalize, int dataType, boolean perInstance, boolean pureInteger, float... defaultValue) {
      this.defaultValue = defaultValue;
      this.attribute = attribute;
      this.size = size;
      this.dataType = dataType;
      this.normalize = normalize;
      this.instanced = perInstance;
      this.pureInteger = pureInteger;
      switch (dataType) {
         case 5120:
            this.dataTypeSize = 1;
            break;
         case 5121:
            this.dataTypeSize = 1;
            break;
         case 5122:
            this.dataTypeSize = 2;
            break;
         case 5123:
            this.dataTypeSize = 2;
            break;
         case 5124:
            this.dataTypeSize = 4;
            break;
         case 5125:
            this.dataTypeSize = 4;
            break;
         case 5126:
            this.dataTypeSize = 4;
            break;
         case 5130:
            this.dataTypeSize = 8;
            break;
         case 5131:
            this.dataTypeSize = 4;
            break;
         case 33640:
            this.dataTypeSize = 1;
            break;
         default:
            this.dataTypeSize = 4;
      }

      this.stride = size * this.dataTypeSize;
   }

   private Data(int attribute, int size, boolean normalize, int dataType, boolean pureInteger, float... defaultValue) {
      this(attribute, size, normalize, dataType, false, pureInteger, defaultValue);
   }

   private Data(int attribute, int size, boolean normalize, int dataType, float... defaultValue) {
      this(attribute, size, normalize, dataType, false, false, defaultValue);
   }

   private Data(int attribute, int size, boolean normalize, float... defaultValue) {
      this(attribute, size, normalize, 5126, false, defaultValue);
   }

   private Data(int attribute, int size, int dataType, float... defaultValue) {
      this(attribute, size, false, dataType, false, defaultValue);
   }

   private Data(int attribute, int size, float... defaultValue) {
      this(attribute, size, 5126, defaultValue);
   }

   public int getSize() {
      return this.size;
   }

   public boolean normalize() {
      return this.normalize;
   }

   public int getAttribute() {
      return this.attribute;
   }

   public void setAttribute(int attribute) {
      this.attribute = attribute;
   }

   public float[] getDefaultValue() {
      return this.defaultValue;
   }

   public int getDataType() {
      return this.dataType;
   }

   public boolean isInstanced() {
      return this.instanced;
   }

   public int getDataTypeSize() {
      return this.dataTypeSize;
   }

   public int getStride() {
      return this.stride;
   }

   public boolean isPureInteger() {
      return this.pureInteger;
   }

   public static int pack_INT_2_10_10_10_REV(float x, float y, float z, float w) {
      x = x * 0.5F + 0.5F;
      y = y * 0.5F + 0.5F;
      z = z * 0.5F + 0.5F;
      int wi = Math.round(w * 3.0F) & 3;
      int xi = Math.round(x * 1023.0F) & 1023;
      int yi = Math.round(y * 1023.0F) & 1023;
      int zi = Math.round(z * 1023.0F) & 1023;
      return wi << 30 | zi << 20 | yi << 10 | xi;
   }

   public static void unpack_INT_2_10_10_10_REV(int value, Vector3d dst) {
      double x = (double)(value & 1023) / 1023.0 * 2.0 - 1.0;
      double y = (double)(value >> 10 & 1023) / 1023.0 * 2.0 - 1.0;
      double z = (double)(value >> 20 & 1023) / 1023.0 * 2.0 - 1.0;
      dst.set(x, y, z);
   }
}
