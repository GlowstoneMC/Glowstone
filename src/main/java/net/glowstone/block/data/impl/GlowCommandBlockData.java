package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CommandBlock;
import org.jetbrains.annotations.NotNull;

public class GlowCommandBlockData extends AbstractBlockData implements GlowDirectional, CommandBlock {

    public GlowCommandBlockData(Material material) {
        super(material, StateGenerator.SIX_FACING, StateGenerator.CONDITIONAL);
    }

    public BooleanStateValue getConditionalStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("conditional").get();
    }

    @Override
    public boolean isConditional() {
        return getConditionalStateValue().getValue();
    }

    @Override
    public void setConditional(boolean b) {
        getConditionalStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowCommandBlockData command = new GlowCommandBlockData(this.getMaterial());
        command.setConditional(this.isConditional());
        command.setFacing(this.getFacing());
        return command;
    }
}
