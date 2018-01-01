package net.glowstone.entity.monster.complex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.monster.GlowBoss;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

public class GlowEnderDragon extends GlowBoss implements EnderDragon {

    private Map<String, GlowEnderDragonPart> parts = new HashMap<>();

    /**
     * Creates an ender dragon.
     *
     * @param loc the location
     * @param type the entity type
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

    @Override
    public Phase getPhase() {
        return Phase.values()[metadata.getInt(MetadataIndex.ENDERDRAGON_PHASE)];
    }

    @Override
    public void setPhase(Phase phase) {
        metadata.set(MetadataIndex.ENDERDRAGON_PHASE, phase.ordinal());
    }

    @Override
    public Set<ComplexEntityPart> getParts() {
        return new HashSet<>(parts.values());
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ENDERDRAGON_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ENDERDRAGON_AMBIENT;
    }
}
