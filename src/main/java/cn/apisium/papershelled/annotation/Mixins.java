package cn.apisium.papershelled.annotation;

import java.lang.annotation.*;

/**
 * Part of the plugin annotations framework.
 * <p>
 * Represents a list of this plugin's registered mixins.
 * <br>
 * This specific annotation should not be used by people who do not know
 * how repeating annotations work.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mixins {
    boolean required() default true;
    Class[] value() default {};
    String compatibilityLevel() default "JAVA_8";
}
