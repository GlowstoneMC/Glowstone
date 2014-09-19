package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Definitions of potion effect types.
 */
public final class GlowPotionEffect extends PotionEffectType {

    private final Impl impl;

    private GlowPotionEffect(Impl impl) {
        super(impl.id);
        this.impl = impl;
    }

    @Override
    public String getName() {
        return impl.name();
    }

    @Override
    public boolean isInstant() {
        return impl.instant;
    }

    @Override
    public double getDurationModifier() {
        return impl.modifier;
    }

    /**
     * Pulse this potion effect on a specified entity. If the potion effect
     * is not applicable, nothing happens. For instant effects, will only
     * have an effect if 'ticks' is 0.
     * @param entity The entity to pulse on.
     * @param effect Information on the effect's state.
     */
    public void pulse(LivingEntity entity, PotionEffect effect) {
        // todo: implement pulse() for effects which need it
        Validate.notNull(entity, "entity must not be null");
        if (!impl.instant || effect.getDuration() != 0) {
            impl.pulse(entity, effect.getAmplifier(), effect.getDuration());
        }
    }

    /**
     * Register all potion effect types with PotionEffectType.
     */
    public static void register() {
        for (Impl impl : Impl.values()) {
            registerPotionEffectType(new GlowPotionEffect(impl));
        }
        stopAcceptingRegistrations();
    }

    private static enum Impl {
        SPEED(1, false, 1.0),
        SLOW(2, false, 0.5),
        FAST_DIGGING(3, false, 1.5),
        SLOW_DIGGING(4, false, 0.5),
        INCREASE_DAMAGE(5, false, 1.0),
        HEAL(6, true, 1.0),
        HARM(7, true, 0.5),
        JUMP(8, false, 1.0),
        CONFUSION(9, false, 0.25),
        REGENERATION(10, false, 0.25),
        DAMAGE_RESISTANCE(11, false, 1.0),
        FIRE_RESISTANCE(12, false, 1.0),
        WATER_BREATHING(13, false, 1.0),
        INVISIBILITY(14, false, 1.0),
        BLINDNESS(15, false, 0.25),
        NIGHT_VISION(16, false, 1.0),
        HUNGER(17, false, 0.5),
        WEAKNESS(18, false, 0.5),
        POISON(19, false, 0.25),
        WITHER(20, false, 0.25),
        HEALTH_BOOST(21, false, 1.0),
        ABSORPTION(22, false, 1.0),
        SATURATION(23, true, 1.0);

        private final int id;
        private final boolean instant;
        private final double modifier;

        Impl(int id, boolean instant, double modifier) {
            this.id = id;
            this.instant = instant;
            this.modifier = modifier;
        }

        protected void pulse(LivingEntity entity, int amplifier, int ticks) {
        }
    }
}
