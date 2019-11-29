package net.glowstone.block.data;

import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public Material getMaterial() {
        return material;
    }

    @NotNull
    @Override
    public String getAsString() {
        return asString;
    }

    @Override
    public @NotNull String getAsString(boolean hideUnspecified) {
        // TODO: 1.13: hideUnspecified
        return getAsString();
    }

    @NotNull
    @Override
    public BlockData merge(@NotNull BlockData blockData) {
        throw new UnsupportedOperationException("SimpleBlockData cannot be merged."); // TODO: Or can it?
    }

    @Override
    public boolean matches(BlockData blockData) {
        return Objects.equals(blockData.getMaterial(), material);
    }

    @NotNull
    @Override
    public BlockData clone() {
        return new SimpleBlockData(material);
    }

    public static SimpleBlockData empty() {
        return new SimpleBlockData(Material.AIR);
    }
}
