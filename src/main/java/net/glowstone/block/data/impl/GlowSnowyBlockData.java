package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowSnowy;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.jetbrains.annotations.NotNull;

public class GlowSnowyBlockData extends AbstractBlockData implements GlowSnowy {

    public GlowSnowyBlockData(Material material) {
        super(material, StateGenerator.SNOWY);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSnowyBlockData data = new GlowSnowyBlockData(this.getMaterial());
        data.setSnowy(this.isSnowy());
        return data;
    }
}
