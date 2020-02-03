package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowBisected;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.StateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowBisectedBlockData extends AbstractBlockData implements GlowBisected {

    public GlowBisectedBlockData(Material material) {
        super(material, StateGenerator.HALF);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowBisectedBlockData data = new GlowBisectedBlockData(this.getMaterial());
        data.setHalf(this.getHalf());
        return data;
    }
}
