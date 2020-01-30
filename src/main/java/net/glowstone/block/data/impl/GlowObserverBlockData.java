package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Observer;
import org.jetbrains.annotations.NotNull;

public class GlowObserverBlockData extends AbstractBlockData implements GlowDirectional, GlowPowered, Observer {

    public GlowObserverBlockData(Material material) {
        super(material, StateGenerator.POWERED, StateGenerator.SIX_FACING);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowObserverBlockData observer = new GlowObserverBlockData(this.getMaterial());
        observer.setFacing(this.getFacing());
        observer.setPowered(this.isPowered());
        return observer;
    }
}
