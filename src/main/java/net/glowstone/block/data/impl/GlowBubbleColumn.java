package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BubbleColumn;
import org.jetbrains.annotations.NotNull;

public class GlowBubbleColumn extends AbstractBlockData implements BubbleColumn {

    private boolean drag;

    public GlowBubbleColumn(){
        this(Material.BUBBLE_COLUMN);
    }

    public GlowBubbleColumn(boolean drag){
        this(Material.BUBBLE_COLUMN, drag);
    }

    public GlowBubbleColumn(Material material){
        this(material, true);
    }

    public GlowBubbleColumn(Material material, boolean drag) {
        super(material);
        this.drag = drag;
    }

    @Override
    public boolean isDrag() {
        return this.drag;
    }

    @Override
    public void setDrag(boolean b) {
        this.drag = b;
    }

    @Override
    public @NotNull String getAsString() {
        return "minecraft:" + this.getMaterial().name().toLowerCase() + "[drag:" + this.drag + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        return null;
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowBubbleColumn(this.getMaterial(), this.drag);
    }
}
