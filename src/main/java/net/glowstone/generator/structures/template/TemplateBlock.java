package net.glowstone.generator.structures.template;

import org.bukkit.util.Vector;

public class TemplateBlock {

    private Vector position;
    private int blockState;

    public TemplateBlock(Vector position, int blockState) {
        this.position = position;
        this.blockState = blockState;
    }

    public Vector getPosition() {
        return position;
    }

    public int getBlockState() {
        return blockState;
    }
}
