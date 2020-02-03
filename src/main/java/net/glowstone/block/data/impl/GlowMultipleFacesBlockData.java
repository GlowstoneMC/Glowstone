package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.state.generator.BooleanStateGenerator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowMultipleFacesBlockData extends AbstractBlockData implements GlowMultipleFaces {

    public GlowMultipleFacesBlockData(Material material, BooleanStateGenerator... generators) {
        super(material, generators);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowMultipleFacesBlockData faceData = new GlowMultipleFacesBlockData(this.getMaterial());
        for(BlockFace face : faceData.getFaces()){
            faceData.setFace(face, true);
        }
        return faceData;
    }
}
