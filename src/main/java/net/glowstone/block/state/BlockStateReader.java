package net.glowstone.block.state;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public abstract class BlockStateReader<T extends MaterialData> {

    public abstract T read(Material material, BlockStateData data) throws InvalidBlockStateException;

    public abstract boolean matches(BlockStateData state, T data) throws InvalidBlockStateException;
}
