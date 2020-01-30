package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.jetbrains.annotations.NotNull;

public class GlowSwitchBlockData extends AbstractBlockData implements GlowDirectional, GlowPowered, Switch {

    public GlowSwitchBlockData(Material material) {
        super(material, StateGenerator.SIX_FACING, StateGenerator.POWERED, StateGenerator.SWITCH_FACE);
    }

    public EnumStateValue<Switch.Face> getFaceStateValue(){
        return (EnumStateValue<Face>) this.getStateValue("wall");
    }

    @Override
    public @NotNull Face getFace() {
        return this.getFaceStateValue().getValue();
    }

    @Override
    public void setFace(@NotNull Face face) {
        this.getFaceStateValue().setValue(face);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSwitchBlockData switchData = new GlowSwitchBlockData(this.getMaterial());
        switchData.setFacing(this.getFacing());
        switchData.setPowered(this.isPowered());
        switchData.setFace(this.getFace());
        return switchData;
    }
}
