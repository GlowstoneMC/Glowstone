package net.glowstone.constants;

import io.papermc.paper.potion.PotionMix;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Definitions of potion effect types.
 */
public final class GlowPotionEffect extends PotionEffectType {

    private static final List<String> VANILLA_IDS = new ArrayList<>();
    private static final Map<String, PotionEffectType> BY_VANILLA_ID = new HashMap<>();

    static {
        VANILLA_IDS.addAll(
            Arrays.stream(Impl.values()).map(Impl::getVanillaId).map(NamespacedKey::toString).collect(Collectors.toSet()));
    }

    private final Impl impl;

    private GlowPotionEffect(Impl impl) {
        super(impl.id, impl.getVanillaId());
        this.impl = impl;
    }

    /**
     * Register all potion effect types with PotionEffectType.
     */
    public static void register() {
        Potion.setPotionBrewer(new Brewer());
        for (Impl impl : Impl.values()) {
            GlowPotionEffect effect = new GlowPotionEffect(impl);
            BY_VANILLA_ID.put(impl.getVanillaId().toString(), effect);
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
    public NamespacedKey getVanillaId() {
        return impl.getVanillaId();
    }

    @Override
    public boolean isInstant() {
        return impl.instant;
    }

    @Override
    public Color getColor() {
        return impl.color;
    }

    @Override
    public @NotNull Map<Attribute, AttributeModifier> getEffectAttributes() {
        return null;
    }

    @Override
    public double getAttributeModifierAmount(@NotNull Attribute attribute, int effectAmplifier) {
        return 0;
    }

    @Override
    public @NotNull Category getEffectCategory() {
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

    @Override
    public @NotNull String translationKey() {
        return null;
    }

    @RequiredArgsConstructor
    private enum Impl {
        SPEED(1, false, 1.0, Color.fromBGR(8171462), NamespacedKey.minecraft("speed")),
        SLOW(2, false, 0.5, Color.fromBGR(5926017), NamespacedKey.minecraft("slowness")),
        FAST_DIGGING(3, false, 1.5, Color.fromBGR(14270531), NamespacedKey.minecraft("haste")),
        SLOW_DIGGING(4, false, 0.5, Color.fromBGR(4866583), NamespacedKey.minecraft("mining_fatigue")),
        INCREASE_DAMAGE(5, false, 1.0, Color.fromBGR(9643043), NamespacedKey.minecraft("strength")),
        HEAL(6, true, 1.0, Color.fromBGR(16262179), NamespacedKey.minecraft("instant_heal")),
        HARM(7, true, 0.5, Color.fromBGR(4393481), NamespacedKey.minecraft("instant_damage")),
        JUMP(8, false, 1.0, Color.fromBGR(2293580), NamespacedKey.minecraft("jump_boost")),
        CONFUSION(9, false, 0.25, Color.fromBGR(5578058), NamespacedKey.minecraft("nausea")),
        REGENERATION(10, false, 0.25, Color.fromBGR(13458603), NamespacedKey.minecraft("regeneration")),
        DAMAGE_RESISTANCE(11, false, 1.0, Color.fromBGR(10044730), NamespacedKey.minecraft("resistance")),
        FIRE_RESISTANCE(12, false, 1.0, Color.fromBGR(14981690), NamespacedKey.minecraft("fire_resistance")),
        WATER_BREATHING(13, false, 1.0, Color.fromBGR(3035801), NamespacedKey.minecraft("water_breathing")),
        INVISIBILITY(14, false, 1.0, Color.fromBGR(8356754), NamespacedKey.minecraft("invisibility")),
        BLINDNESS(15, false, 0.25, Color.fromBGR(2039587), NamespacedKey.minecraft("blindness")),
        NIGHT_VISION(16, false, 1.0, Color.fromBGR(2039713), NamespacedKey.minecraft("night_vision")),
        HUNGER(17, false, 0.5, Color.fromBGR(5797459), NamespacedKey.minecraft("hunger")),
        WEAKNESS(18, false, 0.5, Color.fromBGR(4738376), NamespacedKey.minecraft("weakness")),
        POISON(19, false, 0.25, Color.fromBGR(5149489), NamespacedKey.minecraft("poison")),
        WITHER(20, false, 0.25, Color.fromBGR(3484199), NamespacedKey.minecraft("wither")),
        HEALTH_BOOST(21, false, 1.0, Color.fromBGR(16284963), NamespacedKey.minecraft("health_boost")),
        ABSORPTION(22, false, 1.0, Color.fromBGR(2445989), NamespacedKey.minecraft("absorption")),
        SATURATION(23, true, 1.0, Color.fromBGR(16262179), NamespacedKey.minecraft("saturation")),
        GLOWING(24, false, 1.0, Color.fromBGR(9740385), NamespacedKey.minecraft("glowing")),
        LEVITATION(25, false, 1.0, Color.fromBGR(13565951), NamespacedKey.minecraft("levitation")),
        LUCK(26, false, 1.0, Color.fromBGR(3381504), NamespacedKey.minecraft("luck")),
        UNLUCK(27, false, 1.0, Color.fromBGR(12624973), NamespacedKey.minecraft("unluck"));

        private final int id;
        private final boolean instant;
        private final double modifier;
        private final Color color;
        @Getter
        private final NamespacedKey vanillaId;

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

        @Override
        public void addPotionMix(@NotNull PotionMix potionMix) {

        }

        @Override
        public void removePotionMix(@NotNull NamespacedKey key) {

        }

        @Override
        public void resetPotionMixes() {

        }
    }
}
