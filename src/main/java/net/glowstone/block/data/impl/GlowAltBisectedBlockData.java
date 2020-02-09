package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowBisected;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowAltBisectedBlockData extends AbstractBlockData implements GlowBisected {

    public GlowAltBisectedBlockData(Material material) {
        super(material, StateGenerator.HALF_ALT_NAME);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowAltBisectedBlockData data = new GlowAltBisectedBlockData(this.getMaterial());
        data.setHalf(this.getHalf());
        return data;
    }
}
