package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BubbleColumn;
import org.jetbrains.annotations.NotNull;

public class GlowBubbleColumn extends AbstractBlockData implements BubbleColumn {

    public GlowBubbleColumn(Material material) {
        super(material, StateGenerator.DRAG);
    }

    public BooleanStateValue getDragStateValue(){
        return (BooleanStateValue) this.getStateValue("drag");
    }

    @Override
    public boolean isDrag() {
        return this.getDragStateValue().getValue();
    }

    @Override
    public void setDrag(boolean b) {
        this.getDragStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowBubbleColumn column = new GlowBubbleColumn(this.getMaterial());
        column.setDrag(this.isDrag());
        return column;
    }
}
