package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAgeable;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Fire;
import org.jetbrains.annotations.NotNull;

public class GlowFireBlockData extends AbstractBlockData implements GlowMultipleFaces, GlowAgeable, Fire {

    public GlowFireBlockData(Material material) {
        super(material, StateGenerator.SIXTEEN_AGE, StateGenerator.BOOLEAN_SOUTH, StateGenerator.BOOLEAN_NORTH, StateGenerator.BOOLEAN_EAST, StateGenerator.BOOLEAN_DOWN, StateGenerator.BOOLEAN_UP, StateGenerator.BOOLEAN_WEST);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowFireBlockData fire = new GlowFireBlockData(this.getMaterial());
        for(BlockFace face : this.getFaces()){
            fire.setFace(face, true);
        }
        fire.setAge(this.getAge());
        return fire;
    }
}
