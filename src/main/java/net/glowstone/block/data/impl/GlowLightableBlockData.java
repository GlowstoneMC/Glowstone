package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowLightable;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowLightableBlockData extends AbstractBlockData implements GlowLightable {

    public GlowLightableBlockData(Material material) {
        super(material, StateGenerator.LIT);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowLightableBlockData data = new GlowLightableBlockData(this.getMaterial());
        data.setLit(this.isLit());
        return data;
    }
}
