package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlowBedBlockData extends AbstractBlockData implements Bed, Directional {

    public GlowBedBlockData(Material material) {
        super(material, StateGenerator.BED_PART, StateGenerator.FOUR_FACING);
    }

    public EnumStateValue<Bed.Part> getPartStateValue(){
        return (EnumStateValue<Part>) this.getStateValue("foot");
    }

    public BooleanStateValue getOccupiedStateValue(){
        return (BooleanStateValue) this.getStateValue("occupied");
    }

    public EnumStateValue<BlockFace> getFacingStateValue(){
        return (EnumStateValue<BlockFace>) this.getStateValue("facing");
    }

    @Override
    public @NotNull Bed.Part getPart() {
        return this.getPartStateValue().getValue();
    }

    @Override
    public void setPart(@NotNull Bed.Part part) {
        this.getPartStateValue().setValue(part);
    }

    @Override
    public boolean isOccupied() {
        return this.getOccupiedStateValue().getValue();
    }

    @Override
    public @NotNull BlockFace getFacing() {
        return this.getFacingStateValue().getValue();
    }

    @Override
    public void setFacing(@NotNull BlockFace blockFace) {
        this.getFacingStateValue().setValue(blockFace);
    }

    @Override
    public @NotNull Set<BlockFace> getFaces() {
        return new HashSet<>(Arrays.asList(this.getFacingStateValue().getGenerator().getValues()));
    }

    @Override
    public @NotNull BlockData clone() {
        GlowBedBlockData bed = new GlowBedBlockData(this.getMaterial());
        bed.setFacing(this.getFacing());
        bed.setPart(this.getPart());
        bed.getOccupiedStateValue().setValue(this.isOccupied());
        return this;
    }
}
