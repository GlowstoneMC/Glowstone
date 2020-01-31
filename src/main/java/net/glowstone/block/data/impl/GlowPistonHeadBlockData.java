package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPistonNeck;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.PistonHead;
import org.jetbrains.annotations.NotNull;

public class GlowPistonHeadBlockData extends AbstractBlockData implements GlowDirectional, GlowPistonNeck, PistonHead {

    public GlowPistonHeadBlockData(Material material) {
        super(material, StateGenerator.SIX_FACING, StateGenerator.PISTON_NECK_TYPE, StateGenerator.SHORT);
    }

    public BooleanStateValue getShortStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("is_short").get();
    }

    @Override
    public boolean isShort() {
        return this.getShortStateValue().getValue();
    }

    @Override
    public void setShort(boolean b) {
        this.getShortStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowPistonHeadBlockData head = new GlowPistonHeadBlockData(this.getMaterial());
        head.setShort(this.isShort());
        head.setType(this.getType());
        head.setFacing(this.getFacing());
        return head;
    }
}
