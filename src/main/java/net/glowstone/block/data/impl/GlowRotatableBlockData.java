package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateUtil;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GlowRotatableBlockData extends AbstractBlockData implements Rotatable {

    public GlowRotatableBlockData(Material material, IntegerStateGenerator.Ranged rotations) {
        super(material, rotations);
    }

    public GlowRotatableBlockData(Material material, IntegerStateValue.Ranged rotations) {
        super(material, rotations);
    }

    public GlowRotatableBlockData(Material material, Collection<StateValue<?>> collection) {
        super(material, collection);
    }

    public IntegerStateValue.Ranged getRangedStateValue(){
        return this.getRangedStateValue();
    }

    @Override
    public @NotNull BlockFace getRotation() {
        return StateUtil.getBlockFace(this.getRangedStateValue().getValue(), StateUtil.SIXTEEN_BLOCK_FACES);
    }

    @Override
    public void setRotation(@NotNull BlockFace blockFace) {
        this.getRangedStateValue().setValue(StateUtil.getBlockFaceId(blockFace, StateUtil.SIXTEEN_BLOCK_FACES));
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowRotatableBlockData(this.getMaterial(), this.getRangedStateValue().clone());
    }
}
