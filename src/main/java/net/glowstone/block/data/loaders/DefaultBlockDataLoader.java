package net.glowstone.block.data.loaders;

import java.util.Map;
import java.util.Objects;
import net.glowstone.block.data.BlockData;
import net.glowstone.block.data.SingletonBlockData;
import org.bukkit.Material;

public class DefaultBlockDataLoader extends BlockDataLoader<BlockData> {
    @Override
    public SingletonBlockData<BlockData> createSingletonBlockData(
            Material material, int stateId, Map<String, String> data) {
        return new SingletonBlockData<BlockData>(stateId, material) {
            @Override
            public BlockData mutate() {
                return new BlockData() {
                    @Override
                    public Material getMaterial() {
                        return material;
                    }
                };
            }
        };
    }

    @Override
    public int hashBlockData(BlockData blockData) {
        return Objects.hash(
                blockData.getBaseClass(),
                blockData.getMaterial().ordinal()
        );
    }
}
