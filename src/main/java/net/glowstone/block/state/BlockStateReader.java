package net.glowstone.block.state;

import java.util.Set;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public abstract class BlockStateReader<T extends MaterialData> {

    public abstract Set<String> getValidStates();

    public abstract T read(Material material, BlockStateData data)
        throws InvalidBlockStateException;

    public abstract boolean matches(BlockStateData state, T data) throws InvalidBlockStateException;
}
