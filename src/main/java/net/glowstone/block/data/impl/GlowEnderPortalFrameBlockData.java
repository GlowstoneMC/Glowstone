package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EndPortalFrame;
import org.jetbrains.annotations.NotNull;

public class GlowEnderPortalFrameBlockData extends AbstractBlockData implements GlowDirectional, EndPortalFrame {

    public GlowEnderPortalFrameBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.EYE);
    }

    public BooleanStateValue getEyeStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("eye").get();
    }

    @Override
    public boolean hasEye() {
        return this.getEyeStateValue().getValue();
    }

    @Override
    public void setEye(boolean b) {
        this.getEyeStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowEnderPortalFrameBlockData enderFrame = new GlowEnderPortalFrameBlockData(this.getMaterial());
        enderFrame.setEye(this.hasEye());
        enderFrame.setFacing(this.getFacing());
        return enderFrame;
    }
}
