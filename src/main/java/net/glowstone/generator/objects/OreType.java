package net.glowstone.generator.objects;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class OreType {
    private final Material type;
    private final MaterialData data;
    private final int minY;
    private final int maxY;
    private final int amount;
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

    public OreType(Material type, MaterialData data, int minY, int maxY, int amount, Material targetType) {
        this.type = type;
        this.data = data;
        this.minY = minY;
        this.maxY = maxY;
        this.amount = ++amount;
        this.targetType = targetType;
    }

    public Material getType() {
        return type;
    }

    public MaterialData getData() {
        return data;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getAmount() {
        return amount;
    }

    public Material getTargetType() {
        return targetType;
    }
}
