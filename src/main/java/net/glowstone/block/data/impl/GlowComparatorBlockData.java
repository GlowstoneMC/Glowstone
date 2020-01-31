package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Comparator;
import org.jetbrains.annotations.NotNull;

public class GlowComparatorBlockData extends AbstractBlockData implements GlowDirectional, GlowPowered, Comparator {

    public GlowComparatorBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.POWERED, StateGenerator.COMPARATOR_MODE);
    }

    public EnumStateValue<Comparator.Mode> getModeStateValue(){
        return (EnumStateValue<Mode>) this.<Mode>getStateValue("mode").get();
    }

    @Override
    public @NotNull Mode getMode() {
        return this.getModeStateValue().getValue();
    }

    @Override
    public void setMode(@NotNull Mode mode) {
        this.getModeStateValue().setValue(mode);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowComparatorBlockData comparator = new GlowComparatorBlockData(this.getMaterial());
        comparator.setMode(this.getMode());
        comparator.setFacing(this.getFacing());
        comparator.setPowered(this.isPowered());
        return comparator;
    }
}
