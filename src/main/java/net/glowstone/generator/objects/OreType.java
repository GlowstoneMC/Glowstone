package net.glowstone.generator.objects;

import java.util.Random;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class OreType {

    @Getter
    private final Material type;
    @Getter
    private final MaterialData data;
    @Getter
    private final int minY;
    @Getter
    private final int maxY;
    @Getter
    private final int amount;
    @Getter
    private final Material targetType;

    public OreType(Material type, int minY, int maxY, int amount) {
        this(type, new MaterialData(type), minY, maxY, amount);
    }

    public OreType(Material type, int minY, int maxY, int amount, Material targetType) {
        this(type, new MaterialData(type), minY, maxY, amount, targetType);
    }

    public OreType(Material type, MaterialData data, int minY, int maxY, int amount) {
        this(type, data, minY, maxY, amount, Material.STONE);
    }

    /**
     * Creates an ore type. If {@code minY} and {@code maxY} are equal, then the height range is
     * 0 to {@code minY}*2, with greatest density around {@code minY}. Otherwise, density is uniform
     * over the height range.
     *
     * @param type the block type
     * @param data the block data value
     * @param minY the minimum height
     * @param maxY the maximum height
     * @param amount the size of a vein
     * @param targetType the block this can replace
     */
    public OreType(Material type, MaterialData data, int minY, int maxY, int amount,
        Material targetType) {
        this.type = type;
        this.data = data;
        this.minY = minY;
        this.maxY = maxY;
        this.amount = ++amount;
        this.targetType = targetType;
    }

    /**
     * Generates a random height at which a vein of this ore can spawn.
     *
     * @param random the PRNG to use
     * @return a random height for this ore
     */
    public int getRandomHeight(Random random) {
        return getMinY() == getMaxY()
                ? random.nextInt(getMinY()) + random.nextInt(getMinY())
                : random.nextInt(getMaxY() - getMinY()) + getMinY();
    }
}
