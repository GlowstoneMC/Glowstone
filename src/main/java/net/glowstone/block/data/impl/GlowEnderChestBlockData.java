package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.EnderChest;

public class GlowEnderChestBlockData extends AbstractBlockData implements GlowDirectional, GlowWaterlogged, EnderChest {

    public GlowEnderChestBlockData(Material material) {
        super(material, StateGenerator.WATER_LOGGED, StateGenerator.FOUR_FACING);
    }

    @Override
    public BlockData clone(){
        GlowEnderChestBlockData data = new GlowEnderChestBlockData(this.getMaterial());
        data.setFacing(this.getFacing());
        data.setWaterlogged(this.isWaterlogged());
        return data;
    }
}
