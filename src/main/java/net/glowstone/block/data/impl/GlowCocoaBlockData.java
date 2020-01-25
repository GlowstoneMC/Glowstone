package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAgeable;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cocoa;
import org.jetbrains.annotations.NotNull;

public class GlowCocoaBlockData extends AbstractBlockData implements Cocoa, GlowAgeable, GlowDirectional {

    public GlowCocoaBlockData(Material material) {
        super(material, StateGenerator.THREE_AGE, StateGenerator.FOUR_FACING);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowCocoaBlockData cocoa = new GlowCocoaBlockData(this.getMaterial());
        cocoa.setFacing(this.getFacing());
        cocoa.setAge(this.getAge());
        return cocoa;
    }
}
