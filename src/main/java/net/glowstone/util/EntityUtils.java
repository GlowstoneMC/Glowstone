package net.glowstone.util;

import net.glowstone.EventFactory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Utility methods for dealing with entities.
 */
public class EntityUtils {
    /**
     * Heals an entity by a specific amount.
     *
     * @param target the entity to heal
     * @param amount the amount of health to regain
     * @param reason the reason supplied to the {@link EntityRegainHealthEvent}
     */
    public static void heal(LivingEntity target, double amount,
            EntityRegainHealthEvent.RegainReason reason) {
        if (target.isDead()) {
            return; // too late!
        }
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(target, amount, reason);
        EventFactory.getInstance().callEvent(event);
        if (!event.isCancelled()) {
            target.setHealth(Math.min(
                    target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(),
                    target.getHealth() + amount));
        }
    }

    /**
     * Applies a potion effect with an intensity ranging from 0.0 for no effect to 1.0 for full
     * effect.
     *
     * @param effect the effect
     * @param target the target to apply the effect to
     * @param instantIntensity the intensity multiplier if the effect is instantaneous
     * @param durationIntensity the duration multiplier if the effect has a duration
     */
    public static void applyPotionEffectWithIntensity(
            PotionEffect effect, LivingEntity target, double instantIntensity,
            double durationIntensity) {
        PotionEffectType type = effect.getType();
        final int baseAmplifier = effect.getAmplifier();
        if (type.equals(PotionEffectType.HEAL)) {
            heal(target, (2 << baseAmplifier) * instantIntensity,
                    EntityRegainHealthEvent.RegainReason.MAGIC);
        } else if (type.equals(PotionEffectType.HARM)) {
            target.damage(3 << baseAmplifier, EntityDamageEvent.DamageCause.MAGIC);
        } else if (type.isInstant()) {
            // Custom instant potion effect: can't partially apply, so scale amplifier down instead
            // (but never reduce it to zero)
            target.addPotionEffect((instantIntensity >= 1.0 || baseAmplifier <= 1)
                    ? effect
                    : new PotionEffect(
                            type,
                            0,
                            Math.max(1, (int) (baseAmplifier * instantIntensity + 0.5)),
                            effect.isAmbient(),
                            effect.hasParticles(),
                            effect.getColor()));
        } else {
            target.addPotionEffect(durationIntensity >= 1.0 ? effect : new PotionEffect(
                    type,
                    (int) (effect.getDuration() * durationIntensity),
                    baseAmplifier,
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.getColor()));
        }
    }
}
