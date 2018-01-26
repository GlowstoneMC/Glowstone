package net.glowstone;

import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface PotionEffectHolder {
    /**
     * Checks for the presence of custom potion effects.
     *
     * @return true if custom potion effects are applied
     */
    boolean hasCustomEffects();

    /**
     * Gets an immutable list containing all custom potion effects applied to
     * this.
     *
     * <p>Plugins should check that hasCustomEffects() returns true before calling
     * this method.
     *
     * @return the immutable list of custom potion effects
     */
    List<PotionEffect> getCustomEffects();

    /**
     * Adds a custom potion effect to this object.
     *
     * @param potionEffect the potion effect to add
     * @param overwrite true if any existing effect of the same type should be
     *         overwritten
     * @return true if the effect was added as a result of this call
     */
    boolean addCustomEffect(PotionEffect potionEffect, boolean overwrite);

    /**
     * Removes a custom potion effect from this object.
     *
     * @param potionEffectType the potion effect type to remove
     * @return true if the an effect was removed as a result of this call
     * @throws IllegalArgumentException if this would place an item or entity in an invalid state
     */
    boolean removeCustomEffect(PotionEffectType potionEffectType);

    /**
     * Checks for a specific custom potion effect type on this object.
     *
     * @param potionEffectType the potion effect type to check for
     * @return true if the potion has this effect
     */
    boolean hasCustomEffect(PotionEffectType potionEffectType);
}
