package net.glowstone.constants;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    private static final List<String> VANILLA_IDS = new ArrayList<>();
    private static final Map<String, PotionEffectType> BY_VANILLA_ID = new HashMap<>();

    static {
        VANILLA_IDS.addAll(
            Arrays.stream(Impl.values()).map(Impl::getVanillaId).collect(Collectors.toSet()));
    }

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
            GlowPotionEffect effect = new GlowPotionEffect(impl);
            BY_VANILLA_ID.put(impl.getVanillaId(), effect);
            registerPotionEffectType(effect);
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

    /**
     * Parses a PotionEffect id or name if possible.
     *
     * @param effectName The PotionEffectType name.
     * @return The associated PotionEffectType, or null.
     */
    public static PotionEffectType parsePotionEffectId(String effectName) {
        try {
            int effectId = Integer.parseInt(effectName);
            PotionEffectType type = PotionEffectType.getById(effectId);

            if (type == null) {
                return null;
            } else {
                return type;
            }
        } catch (NumberFormatException exc) {
            if (effectName.startsWith("minecraft:")) {
                PotionEffectType type = GlowPotionEffect.getByVanillaId(effectName);

                if (type == null) {
                    return null;
                } else {
                    return type;
                }
            } else {
                PotionEffectType type = PotionEffectType.getByName(effectName);

                if (type == null) {
                    return null;
                } else {
                    return type;
                }
            }
        }
    }

    public static PotionEffectType getByVanillaId(String vanillaId) {
        return BY_VANILLA_ID.get(vanillaId);
    }

    public static List<String> getVanillaIds() {
        return VANILLA_IDS;
    }

    @Override
    public String getName() {
        return impl.name();
    }

    /**
     * Returns the vanilla id of a PotionEffect.
     *
     * @return The vanilla id.
     */
    public String getVanillaId() {
        return impl.getVanillaId();
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
     * Pulse this potion effect on a specified entity.
     *
     * <p>If the potion effect is not applicable, nothing happens. For instant effects, will only
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

    @RequiredArgsConstructor
    private enum Impl {
        SPEED(1, false, 1.0, "minecraft:speed"),
        SLOW(2, false, 0.5, "minecraft:slowness"),
        FAST_DIGGING(3, false, 1.5, "minecraft:haste"),
        SLOW_DIGGING(4, false, 0.5, "minecraft:mining_fatigue"),
        INCREASE_DAMAGE(5, false, 1.0, "minecraft:strength"),
        HEAL(6, true, 1.0, "minecraft:instant_heal"),
        HARM(7, true, 0.5, "minecraft:instant_damage"),
        JUMP(8, false, 1.0, "minecraft:jump_boost"),
        CONFUSION(9, false, 0.25, "minecraft:nausea"),
        REGENERATION(10, false, 0.25, "minecraft:regeneration"),
        DAMAGE_RESISTANCE(11, false, 1.0, "minecraft:resistance"),
        FIRE_RESISTANCE(12, false, 1.0, "minecraft:fire_resistance"),
        WATER_BREATHING(13, false, 1.0, "minecraft:water_breathing"),
        INVISIBILITY(14, false, 1.0, "minecraft:invisibility"),
        BLINDNESS(15, false, 0.25, "minecraft:blindness"),
        NIGHT_VISION(16, false, 1.0, "minecraft:night_vision"),
        HUNGER(17, false, 0.5, "minecraft:hunger"),
        WEAKNESS(18, false, 0.5, "minecraft:weakness"),
        POISON(19, false, 0.25, "minecraft:poison"),
        WITHER(20, false, 0.25, "minecraft:wither"),
        HEALTH_BOOST(21, false, 1.0, "minecraft:health_boost"),
        ABSORPTION(22, false, 1.0, "minecraft:absorption"),
        SATURATION(23, true, 1.0, "minecraft:saturation"),
        GLOWING(24, false, 1.0, "minecraft:glowing"),
        LEVITATION(25, false, 1.0, "minecraft:levitation"),
        LUCK(26, false, 1.0, "minecraft:luck"),
        UNLUCK(27, false, 1.0, "minecraft:unluck");

        private final int id;
        private final boolean instant;
        private final double modifier;
        @Getter
        private final String vanillaId;

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
