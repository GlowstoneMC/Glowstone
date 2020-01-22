package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.generator.EnumStateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlowFacingBlockData extends AbstractBlockData implements Directional {

    public GlowFacingBlockData(Material material, EnumStateGenerator<BlockFace> facingGenerator){
        super(material, facingGenerator);
    }

    public GlowFacingBlockData(Material material, EnumStateValue<BlockFace> facingValue) {
        super(material, facingValue);
    }

    public EnumStateValue<BlockFace> getFacingState(){
        return (EnumStateValue<BlockFace>)this.getStateValue("facing");
    }

    @Override
    public @NotNull BlockFace getFacing() {
        return this.getFacingState().getValue();
    }

    @Override
    public void setFacing(@NotNull BlockFace blockFace) {
        this.getFacingState().setValue(blockFace);
    }

    @Override
    public @NotNull Set<BlockFace> getFaces() {
        return new HashSet<>(Arrays.asList(this.getFacingState().getGenerator().getValues()));
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowFacingBlockData(this.getMaterial(), this.getFacingState().clone());
    }
}
