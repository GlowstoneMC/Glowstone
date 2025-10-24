package net.glowstone.block.data;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Minimal BlockData implementation that carries a legacy data byte for blocks
 * that still rely on pre-1.13 data values (e.g., redstone repeater/torch orientation).
 */
public class LegacyBlockData implements BlockData, Cloneable {
    private final Material material;
    private final byte legacyData;

    public LegacyBlockData(Material material, int legacyData) {
        this.material = material;
        this.legacyData = (byte) legacyData;
    }

    public byte getLegacyData() {
        return legacyData;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getAsString() {
        return getAsString(false);
    }

    @Override
    public String getAsString(boolean hideUnspecified) {
        return material.getKey().toString();
    }

    @Override
    public boolean matches(BlockData data) {
        return data != null && data.getMaterial() == material;
    }

    @Override
    public BlockData merge(BlockData data) {
        // No merging semantics; return this for now.
        return this;
    }

    @Override
    public LegacyBlockData clone() {
        return new LegacyBlockData(material, legacyData);
    }
}
