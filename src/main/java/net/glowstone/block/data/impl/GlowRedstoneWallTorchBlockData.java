package net.glowstone.block.data.impl;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWallTorch;
import org.jetbrains.annotations.NotNull;

public class GlowRedstoneWallTorchBlockData extends GlowWallTorchBlockData implements RedstoneWallTorch {

    public GlowRedstoneWallTorchBlockData(Material material) {
        super(material);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRedstoneWallTorchBlockData data = new GlowRedstoneWallTorchBlockData(this.getMaterial());
        data.setFacing(this.getFacing());
        data.setLit(this.isLit());
        return data;
    }
}
