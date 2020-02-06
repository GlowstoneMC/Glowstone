package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.state.generator.BooleanStateGenerator;
import net.glowstone.block.data.state.generator.StateGenerator;
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
        BooleanStateGenerator[] generators = new BooleanStateGenerator[this.getAllowedFaces().size()];
        generators[0] = this.getNorthStateValue().getGenerator();
        generators[1] = this.getSouthStateValue().getGenerator();
        generators[2] = this.getEastStateValue().getGenerator();
        generators[3] = this.getWestStateValue().getGenerator();
        this.getUpStateValue().ifPresent(s -> generators[4] = s.getGenerator());
        this.getDownStateValue().ifPresent(s -> generators[5] = s.getGenerator());
        GlowMultipleFacesBlockData faceData = new GlowMultipleFacesBlockData(this.getMaterial(), generators);
        for(BlockFace face : faceData.getFaces()){
            faceData.setFace(face, true);
        }
        return faceData;
    }
}
