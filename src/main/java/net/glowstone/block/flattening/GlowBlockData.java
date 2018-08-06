package net.glowstone.block.flattening;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public abstract class GlowBlockData implements BlockData {
    private final Material material;

    protected GlowBlockData(Material material) {
        this.material = material;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public String getAsString() {
        // TODO: What is the syntax for this? (1.13)
        return null;
    }

    @Override
    public abstract BlockData clone();

    public abstract int serialize();
}
