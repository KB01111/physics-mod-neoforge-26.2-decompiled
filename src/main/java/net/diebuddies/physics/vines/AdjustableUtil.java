package net.diebuddies.physics.vines;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.gui.AnimationOption;
import net.diebuddies.physics.settings.gui.ButtonOption;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.ParticleOption;
import net.diebuddies.physics.settings.gui.SoundOption;
import net.diebuddies.physics.settings.gui.TextOption;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.vines.BlockOption;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;

public class AdjustableUtil {
   private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
      if (type.getSuperclass() != null) {
         getAllFields(fields, type.getSuperclass());
      }

      fields.addAll(Arrays.asList(type.getDeclaredFields()));
      return fields;
   }

   public static Object readObject(Class<?> clazz, JsonObject json) {
      List<Field> fields = new ObjectArrayList();
      getAllFields(fields, clazz);
      Object object = null;

      try {
         object = clazz.getDeclaredConstructor().newInstance();
         readFields(object, json, fields);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var5) {
         var5.printStackTrace();
      }

      return object;
   }

   public static JsonObject writeObject(JsonObject json, Object object) {
      Class<?> type = object.getClass();
      List<Field> fields = new ObjectArrayList();
      getAllFields(fields, type);
      writeFields(object, json, fields);
      return json;
   }

   public static DynamicSetting readDynamicSetting(JsonObject json) {
      int settingID = json.get("settingID").getAsInt();
      Class<?> type = null;

      for (DynamicSettingEnum ds : DynamicSettingEnum.values()) {
         if (ds.getID() == settingID) {
            type = ds.getType();
         }
      }

      if (type == null) {
         return null;
      } else {
         List<Field> fields = new ObjectArrayList();
         getAllFields(fields, type);
         DynamicSetting object = null;

         try {
            object = (DynamicSetting)type.getDeclaredConstructor().newInstance();
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var7) {
            var7.printStackTrace();
         }

         readFields(object, json, fields);
         return object;
      }
   }

   public static void writeDynamicSetting(JsonObject json, DynamicSetting object) {
      Class<?> type = object.getClass();
      List<Field> fields = new ObjectArrayList();
      getAllFields(fields, type);
      int settingID = 0;

      for (DynamicSettingEnum ds : DynamicSettingEnum.values()) {
         if (ds.getType() == object.getClass()) {
            settingID = ds.getID();
         }
      }

      json.add("settingID", new JsonPrimitive(settingID));
      writeFields(object, json, fields);
   }

   private static void readFields(Object object, JsonObject json, List<Field> fields) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ArrayExprent.getInferredExprType(ArrayExprent.java:45)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:966)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1153)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:902)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.CatchStatement.toJava(CatchStatement.java:166)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.DoStatement.toJava(DoStatement.java:148)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
      //
      // Bytecode:
      // 000: aload 2
      // 001: invokeinterface java/util/List.iterator ()Ljava/util/Iterator; 1
      // 006: astore 3
      // 007: aload 3
      // 008: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 00d: ifeq 395
      // 010: aload 3
      // 011: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 016: checkcast java/lang/reflect/Field
      // 019: astore 4
      // 01b: aload 4
      // 01d: ldc net/diebuddies/physics/vines/Adjustable
      // 01f: invokevirtual java/lang/reflect/Field.isAnnotationPresent (Ljava/lang/Class;)Z
      // 022: ifeq 388
      // 025: aload 4
      // 027: ldc net/diebuddies/physics/vines/Adjustable
      // 029: invokevirtual java/lang/reflect/Field.getAnnotation (Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
      // 02c: checkcast net/diebuddies/physics/vines/Adjustable
      // 02f: astore 5
      // 031: aload 4
      // 033: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 036: getstatic java/lang/Byte.TYPE Ljava/lang/Class;
      // 039: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 03c: ifeq 056
      // 03f: aload 4
      // 041: aload 0
      // 042: aload 1
      // 043: aload 5
      // 045: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 04a: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 04d: invokevirtual com/google/gson/JsonElement.getAsByte ()B
      // 050: invokevirtual java/lang/reflect/Field.setByte (Ljava/lang/Object;B)V
      // 053: goto 388
      // 056: aload 4
      // 058: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 05b: getstatic java/lang/Integer.TYPE Ljava/lang/Class;
      // 05e: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 061: ifeq 07b
      // 064: aload 4
      // 066: aload 0
      // 067: aload 1
      // 068: aload 5
      // 06a: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 06f: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 072: invokevirtual com/google/gson/JsonElement.getAsInt ()I
      // 075: invokevirtual java/lang/reflect/Field.setInt (Ljava/lang/Object;I)V
      // 078: goto 388
      // 07b: aload 4
      // 07d: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 080: getstatic java/lang/Long.TYPE Ljava/lang/Class;
      // 083: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 086: ifeq 0a0
      // 089: aload 4
      // 08b: aload 0
      // 08c: aload 1
      // 08d: aload 5
      // 08f: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 094: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 097: invokevirtual com/google/gson/JsonElement.getAsLong ()J
      // 09a: invokevirtual java/lang/reflect/Field.setLong (Ljava/lang/Object;J)V
      // 09d: goto 388
      // 0a0: aload 4
      // 0a2: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 0a5: getstatic java/lang/Float.TYPE Ljava/lang/Class;
      // 0a8: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 0ab: ifeq 0c5
      // 0ae: aload 4
      // 0b0: aload 0
      // 0b1: aload 1
      // 0b2: aload 5
      // 0b4: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 0b9: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 0bc: invokevirtual com/google/gson/JsonElement.getAsFloat ()F
      // 0bf: invokevirtual java/lang/reflect/Field.setFloat (Ljava/lang/Object;F)V
      // 0c2: goto 388
      // 0c5: aload 4
      // 0c7: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 0ca: getstatic java/lang/Double.TYPE Ljava/lang/Class;
      // 0cd: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 0d0: ifeq 0ea
      // 0d3: aload 4
      // 0d5: aload 0
      // 0d6: aload 1
      // 0d7: aload 5
      // 0d9: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 0de: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 0e1: invokevirtual com/google/gson/JsonElement.getAsDouble ()D
      // 0e4: invokevirtual java/lang/reflect/Field.setDouble (Ljava/lang/Object;D)V
      // 0e7: goto 388
      // 0ea: aload 4
      // 0ec: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 0ef: getstatic java/lang/Short.TYPE Ljava/lang/Class;
      // 0f2: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 0f5: ifeq 10f
      // 0f8: aload 4
      // 0fa: aload 0
      // 0fb: aload 1
      // 0fc: aload 5
      // 0fe: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 103: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 106: invokevirtual com/google/gson/JsonElement.getAsShort ()S
      // 109: invokevirtual java/lang/reflect/Field.setShort (Ljava/lang/Object;S)V
      // 10c: goto 388
      // 10f: aload 4
      // 111: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 114: getstatic java/lang/Boolean.TYPE Ljava/lang/Class;
      // 117: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 11a: ifeq 134
      // 11d: aload 4
      // 11f: aload 0
      // 120: aload 1
      // 121: aload 5
      // 123: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 128: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 12b: invokevirtual com/google/gson/JsonElement.getAsBoolean ()Z
      // 12e: invokevirtual java/lang/reflect/Field.setBoolean (Ljava/lang/Object;Z)V
      // 131: goto 388
      // 134: aload 4
      // 136: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 139: ldc java/lang/String
      // 13b: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 13e: ifeq 158
      // 141: aload 4
      // 143: aload 0
      // 144: aload 1
      // 145: aload 5
      // 147: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 14c: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 14f: invokevirtual com/google/gson/JsonElement.getAsString ()Ljava/lang/String;
      // 152: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 155: goto 388
      // 158: aload 4
      // 15a: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 15d: invokevirtual java/lang/Class.isEnum ()Z
      // 160: ifeq 183
      // 163: aload 4
      // 165: aload 0
      // 166: aload 4
      // 168: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 16b: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
      // 16e: aload 1
      // 16f: aload 5
      // 171: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 176: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 179: invokevirtual com/google/gson/JsonElement.getAsInt ()I
      // 17c: aaload
      // 17d: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 180: goto 388
      // 183: aload 4
      // 185: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 188: ldc org/joml/Vector3f
      // 18a: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 18d: ifeq 207
      // 190: ldc org/joml/Vector3f
      // 192: ldc "x"
      // 194: invokevirtual java/lang/Class.getDeclaredField (Ljava/lang/String;)Ljava/lang/reflect/Field;
      // 197: astore 6
      // 199: ldc org/joml/Vector3f
      // 19b: ldc "y"
      // 19d: invokevirtual java/lang/Class.getDeclaredField (Ljava/lang/String;)Ljava/lang/reflect/Field;
      // 1a0: astore 7
      // 1a2: ldc org/joml/Vector3f
      // 1a4: ldc "z"
      // 1a6: invokevirtual java/lang/Class.getDeclaredField (Ljava/lang/String;)Ljava/lang/reflect/Field;
      // 1a9: astore 8
      // 1ab: aload 4
      // 1ad: aload 0
      // 1ae: invokevirtual java/lang/reflect/Field.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 1b1: checkcast org/joml/Vector3f
      // 1b4: astore 9
      // 1b6: aload 6
      // 1b8: aload 9
      // 1ba: aload 1
      // 1bb: aload 5
      // 1bd: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 1c2: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "\u0001 x" ]
      // 1c7: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 1ca: invokevirtual com/google/gson/JsonElement.getAsFloat ()F
      // 1cd: invokevirtual java/lang/reflect/Field.setFloat (Ljava/lang/Object;F)V
      // 1d0: aload 7
      // 1d2: aload 9
      // 1d4: aload 1
      // 1d5: aload 5
      // 1d7: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 1dc: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "\u0001 y" ]
      // 1e1: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 1e4: invokevirtual com/google/gson/JsonElement.getAsFloat ()F
      // 1e7: invokevirtual java/lang/reflect/Field.setFloat (Ljava/lang/Object;F)V
      // 1ea: aload 8
      // 1ec: aload 9
      // 1ee: aload 1
      // 1ef: aload 5
      // 1f1: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 1f6: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "\u0001 z" ]
      // 1fb: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 1fe: invokevirtual com/google/gson/JsonElement.getAsFloat ()F
      // 201: invokevirtual java/lang/reflect/Field.setFloat (Ljava/lang/Object;F)V
      // 204: goto 388
      // 207: aload 4
      // 209: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 20c: ldc_w net/minecraft/world/level/block/Block
      // 20f: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 212: ifeq 24f
      // 215: aload 1
      // 216: aload 5
      // 218: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 21d: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 220: astore 6
      // 222: aconst_null
      // 223: astore 7
      // 225: aload 6
      // 227: ifnull 244
      // 22a: aload 6
      // 22c: invokevirtual com/google/gson/JsonElement.isJsonNull ()Z
      // 22f: ifne 244
      // 232: getstatic net/diebuddies/physics/PhysicsMod.invRegisteredBlocks Ljava/util/Map;
      // 235: aload 6
      // 237: invokevirtual com/google/gson/JsonElement.getAsString ()Ljava/lang/String;
      // 23a: invokeinterface java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 23f: checkcast net/minecraft/world/level/block/Block
      // 242: astore 7
      // 244: aload 4
      // 246: aload 0
      // 247: aload 7
      // 249: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 24c: goto 388
      // 24f: aload 4
      // 251: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 254: ldc_w net/minecraft/core/particles/ParticleOptions
      // 257: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 25a: ifeq 27c
      // 25d: aload 4
      // 25f: aload 0
      // 260: getstatic net/diebuddies/physics/PhysicsMod.registeredParticles Ljava/util/Map;
      // 263: aload 1
      // 264: aload 5
      // 266: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 26b: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 26e: invokevirtual com/google/gson/JsonElement.getAsString ()Ljava/lang/String;
      // 271: invokeinterface java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 276: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 279: goto 388
      // 27c: aload 4
      // 27e: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 281: ldc_w net/minecraft/sounds/SoundEvent
      // 284: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 287: ifeq 2cd
      // 28a: aload 1
      // 28b: aload 5
      // 28d: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 292: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 295: astore 6
      // 297: aload 6
      // 299: ifnull 2a4
      // 29c: aload 6
      // 29e: invokevirtual com/google/gson/JsonElement.isJsonNull ()Z
      // 2a1: ifeq 2ae
      // 2a4: aload 4
      // 2a6: aload 0
      // 2a7: aconst_null
      // 2a8: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 2ab: goto 2ca
      // 2ae: aload 4
      // 2b0: aload 0
      // 2b1: getstatic net/diebuddies/physics/PhysicsMod.registeredSounds Ljava/util/Map;
      // 2b4: aload 1
      // 2b5: aload 5
      // 2b7: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 2bc: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 2bf: invokevirtual com/google/gson/JsonElement.getAsString ()Ljava/lang/String;
      // 2c2: invokeinterface java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 2c7: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 2ca: goto 388
      // 2cd: aload 4
      // 2cf: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 2d2: ldc_w net/diebuddies/physics/animation/Animation
      // 2d5: invokevirtual java/lang/Object.equals (Ljava/lang/Object;)Z
      // 2d8: ifeq 2fa
      // 2db: aload 4
      // 2dd: aload 0
      // 2de: getstatic net/diebuddies/config/ConfigAnimations.animations Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;
      // 2e1: aload 1
      // 2e2: aload 5
      // 2e4: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 2e9: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 2ec: invokevirtual com/google/gson/JsonElement.getAsLong ()J
      // 2ef: invokeinterface it/unimi/dsi/fastutil/longs/Long2ObjectMap.get (J)Ljava/lang/Object; 3
      // 2f4: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 2f7: goto 388
      // 2fa: ldc_w java/util/Collection
      // 2fd: aload 4
      // 2ff: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 302: invokevirtual java/lang/Class.isAssignableFrom (Ljava/lang/Class;)Z
      // 305: ifeq 368
      // 308: aload 4
      // 30a: aload 0
      // 30b: invokevirtual java/lang/reflect/Field.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 30e: checkcast java/util/Collection
      // 311: astore 6
      // 313: aload 1
      // 314: aload 5
      // 316: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 31b: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 31e: invokevirtual com/google/gson/JsonElement.getAsJsonArray ()Lcom/google/gson/JsonArray;
      // 321: astore 7
      // 323: bipush 0
      // 324: istore 8
      // 326: iload 8
      // 328: aload 7
      // 32a: invokevirtual com/google/gson/JsonArray.size ()I
      // 32d: if_icmpge 365
      // 330: aload 7
      // 332: iload 8
      // 334: invokevirtual com/google/gson/JsonArray.get (I)Lcom/google/gson/JsonElement;
      // 337: invokevirtual com/google/gson/JsonElement.getAsJsonObject ()Lcom/google/gson/JsonObject;
      // 33a: astore 9
      // 33c: aload 4
      // 33e: invokevirtual java/lang/reflect/Field.getGenericType ()Ljava/lang/reflect/Type;
      // 341: checkcast java/lang/reflect/ParameterizedType
      // 344: invokeinterface java/lang/reflect/ParameterizedType.getActualTypeArguments ()[Ljava/lang/reflect/Type; 1
      // 349: bipush 0
      // 34a: aaload
      // 34b: checkcast java/lang/Class
      // 34e: aload 9
      // 350: invokestatic net/diebuddies/physics/vines/AdjustableUtil.readObject (Ljava/lang/Class;Lcom/google/gson/JsonObject;)Ljava/lang/Object;
      // 353: astore 10
      // 355: aload 6
      // 357: aload 10
      // 359: invokeinterface java/util/Collection.add (Ljava/lang/Object;)Z 2
      // 35e: pop
      // 35f: iinc 8 1
      // 362: goto 326
      // 365: goto 388
      // 368: aload 4
      // 36a: invokevirtual java/lang/reflect/Field.getType ()Ljava/lang/Class;
      // 36d: aload 1
      // 36e: aload 5
      // 370: invokeinterface net/diebuddies/physics/vines/Adjustable.id ()Ljava/lang/String; 1
      // 375: invokevirtual com/google/gson/JsonObject.get (Ljava/lang/String;)Lcom/google/gson/JsonElement;
      // 378: invokevirtual com/google/gson/JsonElement.getAsJsonObject ()Lcom/google/gson/JsonObject;
      // 37b: invokestatic net/diebuddies/physics/vines/AdjustableUtil.readObject (Ljava/lang/Class;Lcom/google/gson/JsonObject;)Ljava/lang/Object;
      // 37e: astore 6
      // 380: aload 4
      // 382: aload 0
      // 383: aload 6
      // 385: invokevirtual java/lang/reflect/Field.set (Ljava/lang/Object;Ljava/lang/Object;)V
      // 388: goto 392
      // 38b: astore 5
      // 38d: aload 5
      // 38f: invokevirtual java/lang/Exception.printStackTrace ()V
      // 392: goto 007
      // 395: return
   }

   private static void writeFields(Object object, JsonObject json, List<Field> fields) {
      for (Field f : fields) {
         try {
            if (f.isAnnotationPresent(Adjustable.class)) {
               Adjustable adjustable = f.getAnnotation(Adjustable.class);
               if (f.getType().equals(byte.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getByte(object)));
               } else if (f.getType().equals(int.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getInt(object)));
               } else if (f.getType().equals(long.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getLong(object)));
               } else if (f.getType().equals(float.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getFloat(object)));
               } else if (f.getType().equals(double.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getDouble(object)));
               } else if (f.getType().equals(short.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getShort(object)));
               } else if (f.getType().equals(boolean.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(f.getBoolean(object)));
               } else if (f.getType().equals(String.class)) {
                  json.add(adjustable.id(), new JsonPrimitive((String)f.get(object)));
               } else if (f.getType().isEnum()) {
                  json.add(adjustable.id(), new JsonPrimitive(((Enum)f.get(object)).ordinal()));
               } else if (f.getType().equals(Vector3f.class)) {
                  Field xField = Vector3f.class.getDeclaredField("x");
                  Field yField = Vector3f.class.getDeclaredField("y");
                  Field zField = Vector3f.class.getDeclaredField("z");
                  Vector3f value = (Vector3f)f.get(object);
                  json.add(adjustable.id() + " x", new JsonPrimitive(xField.getFloat(value)));
                  json.add(adjustable.id() + " y", new JsonPrimitive(yField.getFloat(value)));
                  json.add(adjustable.id() + " z", new JsonPrimitive(zField.getFloat(value)));
               } else if (f.getType().equals(Block.class)) {
                  Block block = (Block)f.get(object);
                  if (block == null) {
                     json.add(adjustable.id(), JsonNull.INSTANCE);
                  } else {
                     json.add(adjustable.id(), new JsonPrimitive(PhysicsMod.registeredBlocks.get(block)));
                  }
               } else if (f.getType().equals(ParticleOptions.class)) {
                  json.add(adjustable.id(), new JsonPrimitive(PhysicsMod.invRegisteredParticles.getOrDefault(f.get(object), "minecraft:lava")));
               } else if (f.getType().equals(SoundEvent.class)) {
                  String sound = PhysicsMod.invRegisteredSounds.get(f.get(object));
                  if (sound == null) {
                     json.add(adjustable.id(), JsonNull.INSTANCE);
                  } else {
                     json.add(adjustable.id(), new JsonPrimitive(PhysicsMod.invRegisteredSounds.get(f.get(object))));
                  }
               } else if (f.getType().equals(Animation.class)) {
                  Object animation = f.get(object);
                  long id = -1L;
                  ObjectIterator var20 = ConfigAnimations.animations.long2ObjectEntrySet().iterator();

                  while (true) {
                     if (var20.hasNext()) {
                        Entry<Animation> entry = (Entry<Animation>)var20.next();
                        if (!((Animation)entry.getValue()).equals(animation)) {
                           continue;
                        }

                        id = entry.getLongKey();
                     }

                     json.add(adjustable.id(), new JsonPrimitive(id));
                     break;
                  }
               } else if (!Collection.class.isAssignableFrom(f.getType())) {
                  JsonObject subObj = new JsonObject();
                  writeObject(subObj, f.get(object));
                  json.add(adjustable.id(), subObj);
               } else {
                  Collection<Object> collection = (Collection<Object>)f.get(object);
                  JsonArray arr = new JsonArray();

                  for (Object it : collection) {
                     JsonObject subObj = new JsonObject();
                     writeObject(subObj, it);
                     arr.add(subObj);
                  }

                  json.add(adjustable.id(), arr);
               }
            }
         } catch (IllegalAccessException | SecurityException | NoSuchFieldException | IllegalArgumentException var11) {
            var11.printStackTrace();
         }
      }
   }

   public static List<LegacyOption> generateOptions(Screen screen, Object object) {
      return generateOptions(screen, object, () -> {
      }, () -> {
      });
   }

   public static List<LegacyOption> generateOptions(Screen screen, Object object, Runnable onRefresh, Runnable onUpdate) {
      Class<?> type = object.getClass();
      List<Field> fields = new ObjectArrayList();
      getAllFields(fields, type);
      List<LegacyOption> options = new ObjectArrayList();

      for (Field f : fields) {
         try {
            if (f.isAnnotationPresent(Adjustable.class)) {
               Adjustable adjustable = f.getAnnotation(Adjustable.class);
               if (f.getType().equals(byte.class)) {
                  options.add(
                     createProgressOptionByte(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        f,
                        object,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(int.class)) {
                  options.add(
                     createProgressOptionInt(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        f,
                        object,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(long.class)) {
                  options.add(
                     createProgressOptionLong(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        f,
                        object,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(float.class)) {
                  options.add(
                     createProgressOptionFloat(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        f,
                        object,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(double.class)) {
                  options.add(
                     createProgressOptionDouble(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        f,
                        object,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(short.class)) {
                  options.add(
                     createProgressOptionShort(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        f,
                        object,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(boolean.class)) {
                  options.add(CycleOption.createOnOff(Language.getInstance().getOrDefault(adjustable.translationId()), gameOptions -> {
                     try {
                        return f.getBoolean(object);
                     } catch (IllegalAccessException | IllegalArgumentException var4x) {
                        var4x.printStackTrace();
                        return false;
                     }
                  }, (gameOptions, option, valuex) -> {
                     try {
                        f.setBoolean(object, valuex);
                        onUpdate.run();
                     } catch (IllegalAccessException | IllegalArgumentException var7) {
                        var7.printStackTrace();
                     }
                  }));
               } else if (f.getType().equals(String.class)) {
                  options.add(createTextOption(Language.getInstance().getOrDefault(adjustable.translationId()), f, object, onUpdate));
               } else if (f.getType().isEnum()) {
                  options.add(createEnumOption(Language.getInstance().getOrDefault(adjustable.translationId()), f, object, onUpdate));
               } else if (f.getType().equals(Vector3f.class)) {
                  Field xField = Vector3f.class.getDeclaredField("x");
                  Field yField = Vector3f.class.getDeclaredField("y");
                  Field zField = Vector3f.class.getDeclaredField("z");
                  Vector3f value = (Vector3f)f.get(object);
                  options.add(
                     createProgressOptionFloat(
                        Language.getInstance().getOrDefault(adjustable.translationId()) + " X",
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        xField,
                        value,
                        onUpdate
                     )
                  );
                  options.add(
                     createProgressOptionFloat(
                        Language.getInstance().getOrDefault(adjustable.translationId()) + " Y",
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        yField,
                        value,
                        onUpdate
                     )
                  );
                  options.add(
                     createProgressOptionFloat(
                        Language.getInstance().getOrDefault(adjustable.translationId()) + " Z",
                        adjustable.min(),
                        adjustable.max(),
                        adjustable.step(),
                        Language.getInstance().getOrDefault(adjustable.maxTranslationId()),
                        zField,
                        value,
                        onUpdate
                     )
                  );
               } else if (f.getType().equals(Block.class)) {
                  Block block = (Block)f.get(object);
                  options.add(
                     new BlockOption(
                        Language.getInstance().getOrDefault(adjustable.translationId()), PhysicsMod.registeredBlocks.get(block), true, screen, blockChange -> {
                           try {
                              if (blockChange == null) {
                                 f.set(object, null);
                              } else {
                                 f.set(object, PhysicsMod.invRegisteredBlocks.get(blockChange));
                                 onUpdate.run();
                              }
                           } catch (IllegalAccessException | IllegalArgumentException var5x) {
                              var5x.printStackTrace();
                           }
                        }
                     )
                  );
               } else if (f.getType().equals(ParticleOptions.class)) {
                  ParticleOptions particle = (ParticleOptions)f.get(object);
                  options.add(
                     new ParticleOption(
                        Language.getInstance().getOrDefault(adjustable.translationId()),
                        PhysicsMod.invRegisteredParticles.get(particle),
                        screen,
                        particleChange -> {
                           try {
                              if (particleChange == null) {
                                 f.set(object, null);
                              } else {
                                 f.set(object, PhysicsMod.registeredParticles.get(particleChange));
                                 onUpdate.run();
                              }
                           } catch (IllegalAccessException | IllegalArgumentException var5x) {
                              var5x.printStackTrace();
                           }
                        }
                     )
                  );
               } else if (f.getType().equals(SoundEvent.class)) {
                  SoundEvent sound = (SoundEvent)f.get(object);
                  options.add(
                     new SoundOption(
                        Language.getInstance().getOrDefault(adjustable.translationId()), PhysicsMod.invRegisteredSounds.get(sound), screen, soundChange -> {
                           try {
                              if (soundChange == null) {
                                 f.set(object, null);
                              } else {
                                 f.set(object, PhysicsMod.registeredSounds.get(soundChange));
                                 onUpdate.run();
                              }
                           } catch (IllegalAccessException | IllegalArgumentException var5x) {
                              var5x.printStackTrace();
                           }
                        }
                     )
                  );
               } else if (!f.getType().equals(Animation.class)) {
                  if (Collection.class.isAssignableFrom(f.getType())) {
                     Collection<Object> collection = (Collection<Object>)f.get(object);
                     String fieldName = Language.getInstance().getOrDefault(adjustable.translationId());
                     options.add(new LabelOption(""));
                     options.add(
                        new ButtonOption(
                           String.format(Language.getInstance().getOrDefault("physicsmod.prop.add"), fieldName),
                           button -> {
                              try {
                                 collection.add(
                                    ((Class)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0]).getDeclaredConstructor().newInstance()
                                 );
                                 onRefresh.run();
                                 onUpdate.run();
                              } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var6x) {
                                 var6x.printStackTrace();
                              }
                           }
                        )
                     );
                     int fieldCount = 0;

                     for (Object subObject : collection) {
                        options.add(new LabelOption(fieldName + " " + ++fieldCount));
                        options.addAll(generateOptions(screen, subObject, onRefresh, onUpdate));
                        options.add(new ButtonOption(String.format(Language.getInstance().getOrDefault("physicsmod.prop.remove"), fieldName), button -> {
                           collection.remove(subObject);
                           onRefresh.run();
                           onUpdate.run();
                        }));
                     }
                  }
               } else {
                  Animation animation = (Animation)f.get(object);
                  long id = -1L;
                  ObjectIterator value = ConfigAnimations.animations.long2ObjectEntrySet().iterator();

                  while (true) {
                     if (value.hasNext()) {
                        Entry<Animation> entry = (Entry<Animation>)value.next();
                        if (!((Animation)entry.getValue()).equals(animation)) {
                           continue;
                        }

                        id = entry.getLongKey();
                     }

                     options.add(new AnimationOption(Language.getInstance().getOrDefault(adjustable.translationId()), id, screen, particleChange -> {
                        try {
                           if (particleChange == null) {
                              f.set(object, null);
                           } else {
                              f.set(object, ConfigAnimations.animations.get(Long.parseLong((String)particleChange)));
                              onUpdate.run();
                           }
                        } catch (IllegalAccessException | IllegalArgumentException var5x) {
                           var5x.printStackTrace();
                        }
                     }, Language.getInstance().getOrDefault("physicsmod.prop.mainrule"), true));
                     break;
                  }
               }
            }
         } catch (IllegalAccessException | SecurityException | NoSuchFieldException | IllegalArgumentException var15) {
            var15.printStackTrace();
         }
      }

      return options;
   }

   private static LegacyOption createTextOption(String name, Field f, Object object, Runnable onUpdate) {
      try {
         return new TextOption(name, (String)f.get(object), textChanged -> {
            try {
               f.set(object, textChanged);
               onUpdate.run();
            } catch (IllegalAccessException | IllegalArgumentException var5x) {
               var5x.printStackTrace();
            }
         });
      } catch (IllegalAccessException | IllegalArgumentException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   private static LegacyOption createEnumOption(String name, Field f, Object object, Runnable onUpdate) {
      try {
         Object[] enumConstants = f.getType().getEnumConstants();
         return CycleOption.create(
            name,
            enumConstants,
            value -> Component.translatable(value.toString()),
            gameOptions -> {
               try {
                  return f.get(object);
               } catch (IllegalAccessException var4x) {
                  var4x.printStackTrace();
                  return enumConstants[0];
               }
            },
            (gameOptions, option, value) -> {
               try {
                  f.set(object, value);
                  onUpdate.run();
               } catch (IllegalAccessException | IllegalArgumentException var7) {
                  var7.printStackTrace();
               }
            }
         );
      } catch (IllegalArgumentException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   private static LegacyOption createProgressOptionDouble(
      String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate
   ) {
      return new ProgressOption(name, min, max, (float)step, options -> {
         try {
            return f.getDouble(object);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
            return 0.0;
         }
      }, (options, value) -> {
         try {
            f.setDouble(object, value);
            onUpdate.run();
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }, (options, option) -> {
         double val = option.get(options);
         return val >= max && !maxText.isEmpty() ? Component.literal(name + ": " + maxText) : Component.literal(name + ": " + String.format("%.2f", val));
      });
   }

   private static LegacyOption createProgressOptionFloat(
      String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate
   ) {
      return new ProgressOption(name, min, max, (float)step, options -> {
         try {
            return (double)f.getFloat(object);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
            return 0.0;
         }
      }, (options, value) -> {
         try {
            f.setFloat(object, value.floatValue());
            onUpdate.run();
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }, (options, option) -> {
         double val = option.get(options);
         return val >= max && !maxText.isEmpty() ? Component.literal(name + ": " + maxText) : Component.literal(name + ": " + String.format("%.2f", val));
      });
   }

   private static LegacyOption createProgressOptionInt(
      String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate
   ) {
      return new ProgressOption(name, min, max, (float)step, options -> {
         try {
            return (double)f.getInt(object);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
            return 0.0;
         }
      }, (options, value) -> {
         try {
            f.setInt(object, value.intValue());
            onUpdate.run();
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }, (options, option) -> {
         double val = option.get(options);
         return val >= max && !maxText.isEmpty() ? Component.literal(name + ": " + maxText) : Component.literal(name + ": " + String.format("%.2f", val));
      });
   }

   private static LegacyOption createProgressOptionLong(
      String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate
   ) {
      return new ProgressOption(name, min, max, (float)step, options -> {
         try {
            return (double)f.getLong(object);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
            return 0.0;
         }
      }, (options, value) -> {
         try {
            f.setLong(object, value.longValue());
            onUpdate.run();
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }, (options, option) -> {
         double val = option.get(options);
         return val >= max && !maxText.isEmpty() ? Component.literal(name + ": " + maxText) : Component.literal(name + ": " + String.format("%.2f", val));
      });
   }

   private static LegacyOption createProgressOptionShort(
      String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate
   ) {
      return new ProgressOption(name, min, max, (float)step, options -> {
         try {
            return (double)f.getShort(object);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
            return 0.0;
         }
      }, (options, value) -> {
         try {
            f.setShort(object, value.shortValue());
            onUpdate.run();
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }, (options, option) -> {
         double val = option.get(options);
         return val >= max && !maxText.isEmpty() ? Component.literal(name + ": " + maxText) : Component.literal(name + ": " + String.format("%.2f", val));
      });
   }

   private static LegacyOption createProgressOptionByte(
      String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate
   ) {
      return new ProgressOption(name, min, max, (float)step, options -> {
         try {
            return (double)f.getByte(object);
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
            return 0.0;
         }
      }, (options, value) -> {
         try {
            f.setByte(object, value.byteValue());
            onUpdate.run();
         } catch (IllegalAccessException | IllegalArgumentException var6) {
            var6.printStackTrace();
         }
      }, (options, option) -> {
         double val = option.get(options);
         return val >= max && !maxText.isEmpty() ? Component.literal(name + ": " + maxText) : Component.literal(name + ": " + String.format("%.2f", val));
      });
   }
}
