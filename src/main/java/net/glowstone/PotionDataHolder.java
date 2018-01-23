package net.glowstone;

import java.util.List;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A mutable object that has fields for color, base potion data and custom effects, like a {@link
 * PotionMeta}, but is not necessarily an {@link ItemMeta}. If Glowstone devs controlled Bukkit,
 * then this would be a superinterface of all the following; instead, it's a superinterface of their
 * implementations.
 * <ul>
 *     <li>{@link TippedArrow}</li>
 *     <li>{@link org.bukkit.entity.AreaEffectCloud}</li>
 *     <li>{@link PotionMeta}</li>
 * </ul>
 */
public interface PotionDataHolder {
    /**
     * Copies potion data from an item to an entity.
     *
     * @param meta the metadata of the item to copy from
     */
    default void copyFrom(PotionMeta meta) {
        setBasePotionData(meta.getBasePotionData());
        setColor(meta.getColor());
        for (PotionEffect effect : meta.getCustomEffects()) {
            addCustomEffect(effect, true);
        }
    }

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

    /**
     * Gets the color of this object.
     *
     * @return color
     */
    org.bukkit.Color getColor();

    /**
     * Sets the color of this object.
     *
     * @param color new color
     */
    void setColor(org.bukkit.Color color);

    /**
     * Returns the potion data about the base potion
     *
     * @return a PotionData object
     */
    org.bukkit.potion.PotionData getBasePotionData();

    /**
     * Sets the underlying potion data.
     *
     * @param basePotionData PotionData to set the base potion state to
     */
    void setBasePotionData(PotionData basePotionData);

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
