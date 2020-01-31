package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAttachable;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TripwireHook;
import org.jetbrains.annotations.NotNull;

public class GlowTripwireHookBlockData extends AbstractBlockData implements GlowAttachable, GlowDirectional, GlowPowered, TripwireHook {

    public GlowTripwireHookBlockData(Material material) {
        super(material);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowTripwireHookBlockData data = new GlowTripwireHookBlockData(this.getMaterial());
        data.setPowered(this.isPowered());
        data.setAttached(this.isAttached());
        data.setFacing(this.getFacing());
        return data;
    }
}
