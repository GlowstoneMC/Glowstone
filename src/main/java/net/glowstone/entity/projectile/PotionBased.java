package net.glowstone.entity.projectile;

import java.util.List;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * An object that has fields for color, base potion data and custom effects, like a {@link
 * PotionMeta}, but is not necessarily an {@link ItemMeta}. If Glowstone devs controlled Bukkit,
 * then this would be a superinterface of all the following; instead, it's a superinterface of their
 * implementations.
 * <ul>
 *     <li>{@link TippedArrow}</li>
 *     <li>{@link org.bukkit.entity.AreaEffectCloud}</li>
 *     <li>{@link ItemMeta}</li>
 * </ul>
 */
public interface PotionBased {
    /**
     * Checks for the presence of custom potion effects.
     *
     * @return true if custom potion effects are applied
     */
    boolean hasCustomEffects();

    /**
     * Gets an immutable list containing all custom potion effects applied to
     * this.
     * <p>
     * Plugins should check that hasCustomEffects() returns true before calling
     * this method.
     *
     * @return the immutable list of custom potion effects
     */
    List<PotionEffect> getCustomEffects();

    /**
     * Adds a custom potion effect to this arrow.
     *
     * @param effect the potion effect to add
     * @param overwrite true if any existing effect of the same type should be
     * overwritten
     * @return true if the effect was added as a result of this call
     */
    boolean addCustomEffect(PotionEffect potionEffect, boolean overwrite);

    /**
     * Removes a custom potion effect from this arrow.
     *
     * @param type the potion effect type to remove
     * @return true if the an effect was removed as a result of this call
     * @throws IllegalArgumentException if this operation would leave the Arrow
     * in a state with no Custom Effects and PotionType.UNCRAFTABLE
     */
    boolean removeCustomEffect(PotionEffectType potionEffectType);

    /**
     * Checks for a specific custom potion effect type on this arrow.
     *
     * @param type the potion effect type to check for
     * @return true if the potion has this effect
     */
    boolean hasCustomEffect(PotionEffectType potionEffectType);

    /**
     * Gets the color of this arrow.
     *
     * @return arrow color
     */
    org.bukkit.Color getColor();

    /**
     * Sets the color of this arrow. Will be applied as a tint to its particles.
     *
     * @param color arrow color
     */
    void setColor(org.bukkit.Color color);

    /**
     * Returns the potion data about the base potion
     *
     * @return a PotionData object
     */
    org.bukkit.potion.PotionData getBasePotionData();

    /**
     * Sets the underlying potion data
     *
     * @param data PotionData to set the base potion state to
     */
    void setBasePotionData(org.bukkit.potion.PotionData basePotionData);

    /**
     * Removes all custom potion effects. This method is renamed because of incompatible return
     * types between {@link TippedArrow#clearCustomEffects()} and
     * {@link PotionMeta#clearCustomEffects()}.
     *
     * @throws IllegalArgumentException if this would create an impossible item or entity
     */
    void clearCustomEffects0();

    /**
     * Checks for existence of a potion color.
     *
     * @return true if this has a custom potion color
     */
    default boolean hasColor() {
        return getColor() != null;
    }
}
