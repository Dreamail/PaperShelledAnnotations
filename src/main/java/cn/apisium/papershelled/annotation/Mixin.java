package cn.apisium.papershelled.annotation;

import org.bukkit.plugin.java.annotation.command.Commands;

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
    Class[] value() default {};
    String compatibilityLevel() default "JAVA_8";
}
