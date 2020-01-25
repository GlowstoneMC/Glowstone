package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Observer;
import org.jetbrains.annotations.NotNull;

public class GlowObserver extends AbstractBlockData implements GlowDirectional, GlowPowered, Observer {

    public GlowObserver(Material material) {
        super(material, StateGenerator.POWERED, StateGenerator.SIX_FACING);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowObserver observer = new GlowObserver(this.getMaterial());
        observer.setFacing(this.getFacing());
        observer.setPowered(this.isPowered());
        return observer;
    }
}
