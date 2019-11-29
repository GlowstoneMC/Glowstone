package net.glowstone.block.data.loaders;

import java.util.Map;
import net.glowstone.block.data.BlockData;
import net.glowstone.block.data.SingletonBlockData;
import org.bukkit.Material;

public abstract class BlockDataLoader<T extends BlockData> {

    public abstract SingletonBlockData<T> createSingletonBlockData(
            Material material, int stateId, Map<String, String> data);

    public abstract int hashBlockData(T blockData);
}
