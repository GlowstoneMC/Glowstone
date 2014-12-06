package net.glowstone.generator.objects;

import org.bukkit.Material;

public enum DoublePlantType {
    SUNFLOWER(Material.DOUBLE_PLANT, 0),
    LILAC(Material.DOUBLE_PLANT, 1),
    DOUBLE_TALLGRASS(Material.DOUBLE_PLANT, 2),
    LARGE_FERN(Material.DOUBLE_PLANT, 3),
    ROSE_BUSH(Material.DOUBLE_PLANT, 4),
    PEONIA(Material.DOUBLE_PLANT, 5);

    private final Material type;
    private final int data;

    private DoublePlantType(Material type, int data) {
        this.type = type;
        this.data = data;
    }

    public Material getType() {
        return type;
    }

    public int getData() {
        return data;
    }
}
