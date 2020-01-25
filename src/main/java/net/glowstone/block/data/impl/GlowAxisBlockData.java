package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowOrientable;
import net.glowstone.block.data.state.generator.EnumStateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowAxisBlockData extends AbstractBlockData implements GlowOrientable {

    public GlowAxisBlockData(Material material, EnumStateGenerator<Axis> state){
        super(material, state);
    }

    public GlowAxisBlockData(Material material, EnumStateValue<Axis> state) {
        super(material, state);
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowAxisBlockData(this.getMaterial(), this.getAxisStateValue().clone());
    }
}
