package net.diebuddies.physics.settings.gui.legacy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ProgressOption extends LegacyOption {
   public boolean active = true;
   protected final float steps;
   protected final double minValue;
   protected double maxValue;
   private final Function<Options, Double> getter;
   private final BiConsumer<Options, Double> setter;
   private final BiFunction<Options, ProgressOption, Component> toString;
   private final Function<Minecraft, Component> tooltipSupplier;
   public SliderButton widget;

   public ProgressOption(
      String string,
      double d,
      double e,
      float f,
      Function<Options, Double> function,
      BiConsumer<Options, Double> biConsumer,
      BiFunction<Options, ProgressOption, Component> biFunction,
      Function<Minecraft, Component> function2
   ) {
      super(string);
      this.minValue = d;
      this.maxValue = e;
      this.steps = f;
      this.getter = function;
      this.setter = biConsumer;
      this.toString = biFunction;
      this.tooltipSupplier = function2;
   }

   public ProgressOption(
      String string,
      double d,
      double e,
      float f,
      Function<Options, Double> function,
      BiConsumer<Options, Double> biConsumer,
      BiFunction<Options, ProgressOption, Component> biFunction
   ) {
      this(string, d, e, f, function, biConsumer, biFunction, minecraft -> null);
   }

   @Override
   public AbstractWidget createButton(Options options, int i, int j, int k) {
      Component list = this.tooltipSupplier.apply(Minecraft.getInstance());
      this.widget = new SliderButton(options, i, j, k, 20, this, list);
      this.widget.active = this.active;
      return this.widget;
   }

   public void setActive(boolean active) {
      this.active = active;
      if (this.widget != null) {
         this.widget.active = active;
      }
   }

   public double toPct(double d) {
      return Mth.clamp((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
   }

   public double toValue(double d) {
      return this.clamp(Mth.lerp(Mth.clamp(d, 0.0, 1.0), this.minValue, this.maxValue));
   }

   public double toValue() {
      return this.clamp(Mth.lerp(Mth.clamp(this.widget.getValue(), 0.0, 1.0), this.minValue, this.maxValue));
   }

   private double clamp(double d) {
      if (this.steps > 0.0F) {
         d = (double)(this.steps * (float)Math.round(d / (double)this.steps));
      }

      return Mth.clamp(d, this.minValue, this.maxValue);
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(float f) {
      this.maxValue = (double)f;
   }

   public void set(Options options, double d) {
      this.setter.accept(options, d);
   }

   public double get(Options options) {
      return this.getter.apply(options);
   }

   public Component getMessage(Options options) {
      return this.toString.apply(options, this);
   }
}
