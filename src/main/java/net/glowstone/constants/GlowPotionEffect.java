package net.glowstone.constants;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.potion.PotionType;

/**
 * Definitions of potion effect types.
 */
public final class GlowPotionEffect extends PotionEffectType {

    private final Impl impl;

    private GlowPotionEffect(Impl impl) {
        super(impl.id);
        this.impl = impl;
    }

    /**
     * Register all potion effect types with PotionEffectType.
     */
    public static void register() {
        Potion.setPotionBrewer(new Brewer());
        for (Impl impl : Impl.values()) {
            registerPotionEffectType(new GlowPotionEffect(impl));
        }
        stopAcceptingRegistrations();
    }

    /**
     * Get a GlowPotionEffect from a PotionEffectType if possible.
     *
     * @param type The PotionEffectType.
     * @return The associated GlowPotionEffect, or null.
     */
    public static GlowPotionEffect getEffect(PotionEffectType type) {
        if (type instanceof GlowPotionEffect) {
            return (GlowPotionEffect) type;
        } else if (type instanceof PotionEffectTypeWrapper) {
            return getEffect(getById(type.getId()));
        } else {
            return null;
        }
    }
    
    public static List<String> getEffectNames() {
        return Arrays.stream(Impl.values()).map(impl -> impl.toString().toLowerCase()).collect(
            Collectors.toList());
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
    public Color getColor() {
        return null;
    }

    @Override
    public double getDurationModifier() {
        return impl.modifier;
    }

    /**
     * Pulse this potion effect on a specified entity. If the potion effect
     * is not applicable, nothing happens. For instant effects, will only
     * have an effect if 'ticks' is 0.
     *
     * @param entity The entity to pulse on.
     * @param effect Information on the effect's state.
     */
    public void pulse(LivingEntity entity, PotionEffect effect) {
        // todo: implement pulse() for effects which need it
        checkNotNull(entity, "entity must not be null");
        if (!impl.instant || effect.getDuration() != 0) {
            impl.pulse(entity, effect.getAmplifier(), effect.getDuration());
        }
    }

    private enum Impl {
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
        SATURATION(23, true, 1.0),
        GLOWING(24, false, 1.0),
        LEVITATION(25, false, 1.0),
        LUCK(26, false, 1.0),
        UNLUCK(27, false, 1.0);

        private final int id;
        private final boolean instant;
        private final double modifier;

        Impl(int id, boolean instant, double modifier) {
            this.id = id;
            this.instant = instant;
            this.modifier = modifier;
        }

        protected void pulse(LivingEntity entity, int amplifier, int ticks) {
            // TODO implement potion pulse
        }
    }

    private static class Brewer implements PotionBrewer {
        @Override
        public PotionEffect createEffect(PotionEffectType potion, int duration, int amplifier) {
            // todo: apply duration modifiers, etc.
            return new PotionEffect(potion, duration, amplifier);
        }

        @Override
        public Collection<PotionEffect> getEffectsFromDamage(int damage) {
            // todo: convert damage value to potion effects
            return Collections.emptySet();
        }

        @Override
        public Collection<PotionEffect> getEffects(PotionType potionType, boolean b, boolean b1) {
            return null;
        }
    }
}
