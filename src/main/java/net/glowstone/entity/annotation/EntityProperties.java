package net.glowstone.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents certain constant properties for an entity.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityProperties {
    /**
     * Gets whether the entity is undead.
     *
     * @return whether the entity is undead.
     */
    boolean undead() default false;

    /**
     * Gets whether the entity is an arthropod.
     *
     * @return whether the entity is an arthropod.
     */
    boolean arthropod() default false;

    /**
     * Gets whether the entity should be saved as part of the world.
     *
     * @return True if the entity should be saved.
     */
    boolean shouldSave() default true;
}
