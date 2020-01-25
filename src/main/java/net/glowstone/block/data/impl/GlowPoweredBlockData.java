package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowPoweredBlockData extends AbstractBlockData implements GlowPowered {

    public GlowPoweredBlockData(Material material) {
        super(material, StateGenerator.POWERED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowPoweredBlockData powered = new GlowPoweredBlockData(this.getMaterial());
        powered.setPowered(this.isPowered());
        return powered;
    }
}
