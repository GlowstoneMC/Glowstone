package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.EnumStateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowFacingBlockData extends AbstractBlockData implements GlowDirectional {

    public GlowFacingBlockData(Material material, EnumStateGenerator<BlockFace> facingGenerator){
        super(material, facingGenerator);
    }

    private GlowFacingBlockData(Material material, EnumStateValue<BlockFace> facingValue) {
        super(material, facingValue);
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowFacingBlockData(this.getMaterial(), this.getFacingState().clone());
    }
}
