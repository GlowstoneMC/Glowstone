package net.glowstone.block.data;

import org.bukkit.Material;

public abstract class SingletonBlockData<T extends BlockData> {
    private final int globalPaletteId;
    private final Material material;

    protected SingletonBlockData(int globalPaletteId, Material material) {
        this.globalPaletteId = globalPaletteId;
        this.material = material;
    }

    public final int getGlobalPaletteId() {
        return globalPaletteId;
    }

    public final Material getMaterial() {
        return material;
    }

    /**
     * Creates an API-compatible mutable BlockData instance.
     *
     * @return an API-compatible BlockData instance
     */
    public abstract T mutate();

    @Override
    @SuppressWarnings("unchecked")
    public final int hashCode() {
        // TODO: Use material.getBlockDataClass
        Class<? extends BlockData> dataClass = Waterlogged.class;
        return BlockDataStore.findLoader(dataClass).hashBlockData(mutate());
    }
}
