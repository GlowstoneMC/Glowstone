package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAnalogPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowRedstonePowerBlockData extends AbstractBlockData implements GlowAnalogPowered {

    public GlowRedstonePowerBlockData(Material material){
        super(material, StateGenerator.REDSTONE_POWER);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRedstonePowerBlockData power = new GlowRedstonePowerBlockData(this.getMaterial());
        power.setPower(this.getPower());
        return power;
    }
}
