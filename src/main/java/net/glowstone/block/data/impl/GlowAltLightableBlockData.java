package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowLightable;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowAltLightableBlockData extends AbstractBlockData implements GlowLightable {

    public GlowAltLightableBlockData(Material material) {
        super(material, StateGenerator.LIT_INVERTED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowAltLightableBlockData data = new GlowAltLightableBlockData(this.getMaterial());
        data.setLit(this.isLit());
        return data;
    }
}
