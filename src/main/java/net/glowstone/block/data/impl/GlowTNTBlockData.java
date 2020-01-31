package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.generator.BooleanStateGenerator;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TNT;
import org.jetbrains.annotations.NotNull;

public class GlowTNTBlockData extends AbstractBlockData implements TNT {

    public GlowTNTBlockData(Material material) {
        super(material, StateGenerator.UNSTABLE);
    }

    public BooleanStateValue getUnstableStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("unstable").get();
    }

    @Override
    public boolean isUnstable() {
        return getUnstableStateValue().getValue();
    }

    @Override
    public void setUnstable(boolean b) {
        this.getUnstableStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowTNTBlockData tnt = new GlowTNTBlockData(this.getMaterial());
        tnt.setUnstable(this.isUnstable());
        return tnt;
    }
}
