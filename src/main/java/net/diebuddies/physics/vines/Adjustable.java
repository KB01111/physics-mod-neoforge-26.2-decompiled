package net.diebuddies.physics.vines;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Adjustable {
   String id() default "";

   double min() default 0.0;

   double max() default 1.0;

   double step() default 0.01;

   String maxTranslationId() default "";

   String translationId();
}
