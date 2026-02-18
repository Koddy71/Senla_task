package ru.ilya.autodi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import ru.ilya.autodi.ValueType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigProperty {
    String configFilename() default "";

    String propertyName() default "";

    ValueType type() default ValueType.AUTO;
}
