package net.diebuddies.physics.vines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
      for (Field f : fields) {
         try {
            if (f.isAnnotationPresent(Adjustable.class)) {
               Adjustable adjustable = f.getAnnotation(Adjustable.class);
               if (f.getType().equals(byte.class)) {
                  f.setByte(object, json.get(adjustable.id()).getAsByte());
               } else if (f.getType().equals(int.class)) {
                  f.setInt(object, json.get(adjustable.id()).getAsInt());
               } else if (f.getType().equals(long.class)) {
                  f.setLong(object, json.get(adjustable.id()).getAsLong());
               } else if (f.getType().equals(float.class)) {
                  f.setFloat(object, json.get(adjustable.id()).getAsFloat());
               } else if (f.getType().equals(double.class)) {
                  f.setDouble(object, json.get(adjustable.id()).getAsDouble());
               } else if (f.getType().equals(short.class)) {
                  f.setShort(object, json.get(adjustable.id()).getAsShort());
               } else if (f.getType().equals(boolean.class)) {
                  f.setBoolean(object, json.get(adjustable.id()).getAsBoolean());
               } else if (f.getType().equals(String.class)) {
                  f.set(object, json.get(adjustable.id()).getAsString());
               } else if (f.getType().isEnum()) {
                  f.set(object, f.getType().getEnumConstants()[json.get(adjustable.id()).getAsInt()]);
               } else if (f.getType().equals(Vector3f.class)) {
                  Field xField = Vector3f.class.getDeclaredField("x");
                  Field yField = Vector3f.class.getDeclaredField("y");
                  Field zField = Vector3f.class.getDeclaredField("z");
                  Vector3f value = (Vector3f)f.get(object);
                  xField.setFloat(value, json.get(adjustable.id() + " x").getAsFloat());
                  yField.setFloat(value, json.get(adjustable.id() + " y").getAsFloat());
                  zField.setFloat(value, json.get(adjustable.id() + " z").getAsFloat());
               } else if (f.getType().equals(Block.class)) {
                  JsonElement element = json.get(adjustable.id());
                  Block block = null;
                  if (element != null && !element.isJsonNull()) {
                     block = PhysicsMod.invRegisteredBlocks.get(element.getAsString());
                  }

                  f.set(object, block);
               } else if (f.getType().equals(ParticleOptions.class)) {
                  JsonElement element = json.get(adjustable.id());
                  if (element == null || element.isJsonNull()) {
                     f.set(object, null);
                  } else {
                     f.set(object, PhysicsMod.registeredParticles.get(element.getAsString()));
                  }
               } else if (f.getType().equals(SoundEvent.class)) {
                  JsonElement element = json.get(adjustable.id());
                  if (element == null || element.isJsonNull()) {
                     f.set(object, null);
                  } else {
                     f.set(object, PhysicsMod.registeredSounds.get(json.get(adjustable.id()).getAsString()));
                  }
               } else if (f.getType().equals(Animation.class)) {
                  JsonElement element = json.get(adjustable.id());
                  if (element == null || element.isJsonNull()) {
                     f.set(object, null);
                  } else {
                     f.set(object, ConfigAnimations.animations.get(element.getAsLong()));
                  }
               } else if (Collection.class.isAssignableFrom(f.getType())) {
                  Collection<Object> collection = (Collection<Object>)f.get(object);
                  JsonArray arr = json.get(adjustable.id()).getAsJsonArray();

                  for (int i = 0; i < arr.size(); i++) {
                     JsonObject subObj = arr.get(i).getAsJsonObject();
                     Class<?> elementType = (Class<?>)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
                     collection.add(readObject(elementType, subObj));
                  }
               } else {
                  f.set(object, readObject(f.getType(), json.get(adjustable.id()).getAsJsonObject()));
               }
            }
         } catch (Exception e) {
            Adjustable adjustable = f.getAnnotation(Adjustable.class);
            String key = adjustable != null ? adjustable.id() : "n/a";
            System.err.println("Failed to deserialize field '" + f.getName() + "' (key: " + key + "): " + e.getMessage());
            e.printStackTrace();
         }
      }
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
