package net.glowstone.entity.monster.complex;

import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.monster.GlowBoss;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlowEnderDragon extends GlowBoss implements EnderDragon {

    private Map<String, GlowEnderDragonPart> parts = new HashMap<>();

    /**
     * Creates an ender dragon.
     *
     * @param loc       the location
     * @param type      the entity type
     * @param maxHealth the maximum health
     */
    public GlowEnderDragon(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth, "Ender Dragon", BarColor.PURPLE, BarStyle.SOLID);
        if (type == EntityType.ENDER_DRAGON) {
            parts.put("mouth", new GlowEnderDragonPart(this));
            parts.put("head", new GlowEnderDragonPart(this));
            parts.put("body", new GlowEnderDragonPart(this));
            parts.put("left_wing", new GlowEnderDragonPart(this));
            parts.put("right_wing", new GlowEnderDragonPart(this));
            parts.put("tail1", new GlowEnderDragonPart(this));
            parts.put("tail2", new GlowEnderDragonPart(this));
            parts.put("tail3", new GlowEnderDragonPart(this));
        }
    }

    public GlowEnderDragon(Location loc) {
        this(loc, EntityType.ENDER_DRAGON, 200d);
    }

    @NotNull
    @Override
    public Phase getPhase() {
        return Phase.values()[metadata.getInt(MetadataIndex.ENDERDRAGON_PHASE)];
    }

    @Override
    public void setPhase(Phase phase) {
        metadata.set(MetadataIndex.ENDERDRAGON_PHASE, phase.ordinal());
    }

    @Override
    public @Nullable DragonBattle getDragonBattle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDeathAnimationTicks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Location getPodium() {
        return null;
    }

    @Override
    public void setPodium(@Nullable Location location) {

    }

    @NotNull
    @Override
    public Set<ComplexEntityPart> getParts() {
        return new HashSet<>(parts.values());
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_ENDER_DRAGON_DEATH;
    }

    @Override
    public Sound getAmbientSound() {
        return Sound.ENTITY_ENDER_DRAGON_AMBIENT;
    }
}
