package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowLevelled;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowLevelledBlockData extends AbstractBlockData implements GlowLevelled {

    public GlowLevelledBlockData(Material material, IntegerStateGenerator.Ranged level) {
        super(material, level);
    }

    private GlowLevelledBlockData(Material material, IntegerStateValue.Ranged level){
        super(material, level);
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowLevelledBlockData(this.getMaterial(), this.getLevelStateValue());
    }
}
