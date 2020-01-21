package net.glowstone.block.data;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class AbstractBlockData implements BlockData {

    private Material material;

    public AbstractBlockData(Material material){
        this.material = material;
    }

    @Override
    public @NotNull Material getMaterial() {
        return this.material;
    }

    @Override
    public @NotNull String getAsString(boolean b) {
        return b ? "minecraft:" + this.material.name().toLowerCase() : getAsString();
    }

    @Override
    public boolean matches(@Nullable BlockData blockData) {
        return Objects.equals(blockData, this);
    }
}
