package ru.ilya.autodi;

import java.lang.annotation.*;

import ru.ilya.autodi.ValueType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {
   String configFilename() default "";
   String propertyName() default "";
   ValueType type() default ValueType.AUTO;
}
