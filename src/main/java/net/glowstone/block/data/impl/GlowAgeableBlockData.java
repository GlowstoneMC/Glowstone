package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAgeable;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowAgeableBlockData extends AbstractBlockData implements GlowAgeable {

    public GlowAgeableBlockData(Material material, IntegerStateGenerator.Ranged array) {
        super(material, array);
    }

    private GlowAgeableBlockData(Material material, IntegerStateValue.Ranged... array) {
        super(material, array);
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowAgeableBlockData(this.getMaterial(), this.getAgeStateValue().clone());
    }
}
