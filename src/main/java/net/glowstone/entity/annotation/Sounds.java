package net.glowstone.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.Sound;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Sounds {
    /**
     * Get the death sound of this entity, or null for silence.
     *
     * @return the death sound if available
     */
    Sound death();

    /**
     * Get the hurt sound of this entity, or null for silence.
     *
     * @return the hurt sound if available
     */
    Sound hurt();

    /**
     * Get the ambient sound this entity makes randomly, or null for silence.
     *
     * @return the ambient sound if available
     */
    Sound ambient();
}
