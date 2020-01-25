package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowRotatable;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowRotatableBlockData extends AbstractBlockData implements GlowRotatable {

    public GlowRotatableBlockData(Material material, IntegerStateGenerator.Ranged rotations) {
        super(material, rotations);
    }

    private GlowRotatableBlockData(Material material, IntegerStateValue.Ranged rotations) {
        super(material, rotations);
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowRotatableBlockData(this.getMaterial(), this.getRangedStateValue().clone());
    }
}
