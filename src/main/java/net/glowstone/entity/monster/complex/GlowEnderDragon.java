package net.glowstone.entity.monster.complex;

import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.monster.GlowMonster;
import org.bukkit.Location;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlowEnderDragon extends GlowMonster implements EnderDragon {
    private Map<String, GlowEnderDragonPart> parts = new HashMap<>();

    public GlowEnderDragon(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
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
}
