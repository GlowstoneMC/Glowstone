package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.jetbrains.annotations.NotNull;

public class GlowCakeBlockData extends AbstractBlockData implements Cake {

    public GlowCakeBlockData(Material material) {
        super(material, StateGenerator.SEVEN_BITES);
    }

    public IntegerStateValue.Ranged getBitesStateValue(){
        return (IntegerStateValue.Ranged) this.getStateValue("bites");
    }

    @Override
    public int getBites() {
        return this.getBitesStateValue().getValue();
    }

    @Override
    public void setBites(int i) {
        this.getBitesStateValue().setValue(i);
    }

    @Override
    public int getMaximumBites() {
        return this.getBitesStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowCakeBlockData cake = new GlowCakeBlockData(this.getMaterial());
        cake.setBites(this.getBites());
        return cake;
    }
}
