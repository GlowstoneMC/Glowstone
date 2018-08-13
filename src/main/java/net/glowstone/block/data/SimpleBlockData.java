package net.glowstone.block.data;

import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

/**
 * Represents a simple BlockData implementation.
 */
public class SimpleBlockData implements BlockData {

    private final Material material;
    private final String asString;

    public SimpleBlockData(Material material) {
        this.material = material;
        this.asString = "minecraft:" + material.name().toLowerCase();
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public String getAsString() {
        return asString;
    }

    @Override
    public BlockData merge(BlockData blockData) {
        throw new UnsupportedOperationException("SimpleBlockData cannot be merged."); // TODO: Or can it?
    }

    @Override
    public boolean matches(BlockData blockData) {
        return Objects.equals(blockData.getMaterial(), material);
    }

    @Override
    public BlockData clone() {
        return new SimpleBlockData(material);
    }

    public static SimpleBlockData empty() {
        return new SimpleBlockData(Material.AIR);
    }
}
