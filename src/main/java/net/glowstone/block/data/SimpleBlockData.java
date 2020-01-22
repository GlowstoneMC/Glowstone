package net.glowstone.block.data;

import net.glowstone.block.data.state.StateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a simple BlockData implementation.
 */
public class SimpleBlockData extends AbstractBlockData {

    public SimpleBlockData(Material material) {
        super(material);
    }

    //OVERRIDES to be slightly more efficient
    @Override
    public @NotNull String getAsString() {
        return "minecraft:" + this.getMaterial().name().toLowerCase();
    }

    @NotNull
    @Override
    public BlockData clone() {
        return new SimpleBlockData(this.getMaterial());
    }

    public static SimpleBlockData empty() {
        return new SimpleBlockData(Material.AIR);
    }
}
