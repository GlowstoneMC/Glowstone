package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAnalogPowered;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowRedstonePowerBlockData extends AbstractBlockData implements GlowAnalogPowered {

    public GlowRedstonePowerBlockData(Material material){
        super(material);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRedstonePowerBlockData power = new GlowRedstonePowerBlockData(this.getMaterial());
        power.setPower(this.getPower());
        return power;
    }
}
