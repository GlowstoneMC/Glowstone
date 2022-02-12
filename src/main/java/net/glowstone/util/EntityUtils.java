package net.glowstone.util;

import com.flowpowered.network.Message;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

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
     * @return whether any health was regained this way
     * @throws IllegalArgumentException if the specified target has no {@link Attribute#GENERIC_MAX_HEALTH}
     */
    public static boolean heal(@NotNull LivingEntity target, double amount,
                               EntityRegainHealthEvent.RegainReason reason) {
        if (target.isDead() || amount <= 0) {
            return false;
        }
        final AttributeInstance attribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) {
            throw new IllegalArgumentException(
                    "The specified LivingEntity has no " + Attribute.GENERIC_MAX_HEALTH + " attribute");
        }
        final double maxHealth = attribute.getValue();

        final double currentHealth = target.getHealth();
        if (currentHealth >= maxHealth) {
            // already max health
            return false;
        }

        EntityRegainHealthEvent event = new EntityRegainHealthEvent(target, amount, reason);
        EventFactory.getInstance().callEvent(event);
        if (event.isCancelled() || event.getAmount() <= 0) {
            return false;
        }
        target.setHealth(Math.min(maxHealth, currentHealth + event.getAmount()));
        return true;
    }

    /**
     * Applies a potion effect with an intensity ranging from 0.0 for no effect to 1.0 for full
     * effect. Intensity above 1.0 has no additional effect, with the exceptions of
     * {@link PotionEffectType#HEAL} and {@link PotionEffectType#HARM}, and negative intensity
     * has no effect.
     *
     * @param effect            the effect
     * @param target            the target to apply the effect to
     * @param instantIntensity  the intensity multiplier if the effect is instantaneous
     * @param durationIntensity the duration multiplier if the effect has a duration
     */
    public static void applyPotionEffectWithIntensity(
            @NotNull PotionEffect effect,
            LivingEntity target,
            double instantIntensity,
            double durationIntensity) {

        PotionEffectType type = effect.getType();
        final int baseAmplifier = effect.getAmplifier();
        if (type.equals(PotionEffectType.HEAL)) {
            if (instantIntensity > 0.0) {
                heal(target, (2 << baseAmplifier) * instantIntensity,
                        EntityRegainHealthEvent.RegainReason.MAGIC);
            }
        } else if (type.equals(PotionEffectType.HARM)) {
            if (instantIntensity > 0.0) {
                target.damage((3 << baseAmplifier) * instantIntensity,
                        EntityDamageEvent.DamageCause.MAGIC);
            }
        } else if (type.isInstant()) {
            if (instantIntensity <= 0.0) {
                return;
            }
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
                    effect.hasIcon()));
        } else {
            if (durationIntensity <= 0.0) {
                return;
            }
            target.addPotionEffect(durationIntensity >= 1.0 ? effect : new PotionEffect(
                    type,
                    (int) (effect.getDuration() * durationIntensity),
                    baseAmplifier,
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()));
        }
    }

    /**
     * Refreshes the entity for nearby clients.
     *
     * <p>This will first destroy, and then spawn the painting again using its current art and
     * facing value.
     *
     * @param entity the entity to refresh.
     */
    public static void refresh(@NotNull GlowEntity entity) {
        final DestroyEntitiesMessage destroyMessage = new DestroyEntitiesMessage(
                Collections.singletonList(entity.getEntityId()));

        final List<Message> spawnMessages = entity.createSpawnMessage();
        final Message[] messages = new Message[]{destroyMessage, spawnMessages.get(0)};

        entity.getWorld().getRawPlayers().stream()
                .filter(p -> p.canSeeEntity(entity))
                .forEach(p -> p.getSession().sendAll(messages));
    }
}
