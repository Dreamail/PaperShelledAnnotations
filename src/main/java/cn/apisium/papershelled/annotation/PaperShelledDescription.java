package cn.apisium.papershelled.annotation;

import java.lang.annotation.*;

/**
 *  Represents the required elements needed to register a Bukkit plugin.
 *  This should be placed in the main class of your plugin
 *  (i.e. the class that extends {@link cn.apisium.papershelled.plugin.PaperShelledPlugin}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PaperShelledDescription {
    Mixins[] mixins() default {};
}
