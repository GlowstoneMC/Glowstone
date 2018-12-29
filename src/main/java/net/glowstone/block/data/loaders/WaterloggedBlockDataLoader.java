package net.glowstone.block.data.loaders;

import java.util.Map;
import java.util.Objects;
import net.glowstone.block.data.BlockData;
import net.glowstone.block.data.SingletonBlockData;
import net.glowstone.block.data.Waterlogged;
import org.bukkit.Material;

public class WaterloggedBlockDataLoader extends BlockDataLoader<Waterlogged> {
    @Override
    public SingletonBlockData<Waterlogged> createSingletonBlockData(
            Material material, int stateId, Map<String, String> data) {

        final boolean waterlogged = Boolean.parseBoolean(
                data.getOrDefault("waterlogged", "false"));

        return new SingletonBlockData<Waterlogged>(stateId, material) {
            @Override
            public Waterlogged mutate() {
                return new Waterlogged() {
                    private boolean wl = waterlogged;

                    @Override
                    public boolean isWaterlogged() {
                        return wl;
                    }

                    @Override
                    public void setWaterlogged(boolean waterlogged) {
                        wl = waterlogged;
                    }

                    @Override
                    public Material getMaterial() {
                        return material;
                    }

                    @Override
                    public Class<? extends BlockData> getBaseClass() {
                        return Waterlogged.class;
                    }
                };
            }
        };
    }

    @Override
    public int hashBlockData(Waterlogged blockData) {
        return Objects.hash(blockData.getMaterial().ordinal(),
                blockData.isWaterlogged() ? 1 : 0);
    }
}
