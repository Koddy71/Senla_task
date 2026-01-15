package ru.ilya.autoconfig;

import java.lang.annotation.*;;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {
   String configFilename() default "";
   String propertyName() default "";
   ValueType type() default ValueType.AUTO;
}
