package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.NotNull;

public class GlowStandingBanner extends AbstractBlockData implements Rotatable {

    private BlockFace rotation;

    public GlowStandingBanner(Material material){
        this(material, BlockFace.NORTH);
    }

    public GlowStandingBanner(Material material, BlockFace rotation) {
        super(material);
        this.setRotation(rotation);
    }

    @Override
    public @NotNull BlockFace getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(@NotNull BlockFace blockFace) {
        this.rotation = blockFace;
    }

    @Override
    public @NotNull String getAsString() {
        return "minecraft:" + this.getMaterial().name() + "[rotation:" + this.rotation.name().toLowerCase() + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        return null;
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowStandingBanner(this.getMaterial(), this.rotation);
    }
}
