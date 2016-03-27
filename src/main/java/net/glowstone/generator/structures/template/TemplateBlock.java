package net.glowstone.generator.structures.template;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class TemplateBlock {

    private Vector position;
    private int raw;

    public TemplateBlock(Vector position, int raw) {
        this.position = position;
        this.raw = raw;
    }

    public Vector getPosition() {
        return position;
    }

    public int getRawState() {
        return raw;
    }

    public Material getBlockType() {
        return Material.getMaterial(raw & 4095);
    }

    public MaterialData getData() {
        return new MaterialData(getBlockType(), (byte) (raw >> 12 & 15));
    }

}
