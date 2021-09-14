package cn.apisium.papershelled.annotation;

import java.lang.annotation.*;

/**
 * Defines a plugin mixin collection
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Mixins.class)
public @interface Mixin {
    boolean required() default true;
    Class<?>[] value() default {};
    String minVersion() default "0.8";
    String compatibilityLevel() default "JAVA_8";
    String refMap() default "";
    boolean hasRefMap() default true;
}
