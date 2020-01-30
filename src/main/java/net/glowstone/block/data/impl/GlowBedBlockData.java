package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.jetbrains.annotations.NotNull;

public class GlowBedBlockData extends AbstractBlockData implements Bed, GlowDirectional {

    public GlowBedBlockData(Material material) {
        super(material, StateGenerator.BED_PART, StateGenerator.FOUR_FACING);
    }

    public EnumStateValue<Bed.Part> getPartStateValue(){
        return (EnumStateValue<Part>) this.getStateValue("foot");
    }

    public BooleanStateValue getOccupiedStateValue(){
        return (BooleanStateValue) this.getStateValue("occupied");
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
    public @NotNull BlockData clone() {
        GlowBedBlockData bed = new GlowBedBlockData(this.getMaterial());
        bed.setFacing(this.getFacing());
        bed.setPart(this.getPart());
        bed.getOccupiedStateValue().setValue(this.isOccupied());
        return this;
    }
}
