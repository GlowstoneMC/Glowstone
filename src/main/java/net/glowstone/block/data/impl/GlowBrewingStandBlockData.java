package net.glowstone.block.data.impl;

import com.google.common.collect.ImmutableSet;
import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BrewingStand;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GlowBrewingStandBlockData extends AbstractBlockData implements BrewingStand {

    public GlowBrewingStandBlockData(Material material) {
        super(material, StateGenerator.HAS_BOTTLE_0, StateGenerator.HAS_BOTTLE_1, StateGenerator.HAS_BOTTLE_2);
    }

    public BooleanStateValue getBottleStateValue(int value){
        return (BooleanStateValue)this.getStateValue("has_bottle_" + value);
    }

    @Override
    public boolean hasBottle(int i) {
        return this.getBottleStateValue(i).getValue();
    }

    @Override
    public void setBottle(int i, boolean b) {
        this.getBottleStateValue(i).setValue(b);
    }

    @Override
    public @NotNull Set<Integer> getBottles() {
        ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
        for(int i = 0; i < 3; i++){
            if(this.getBottleStateValue(i).getValue()){
                builder.add(i);
            }
        }
        return builder.build();
    }

    @Override
    public int getMaximumBottles() {
        return 3;
    }

    @Override
    public @NotNull BlockData clone() {
        GlowBrewingStandBlockData data = new GlowBrewingStandBlockData(this.getMaterial());
        for(int i = 0; i < 3; i++){
            data.getBottleStateValue(i).setValue(this.getBottleStateValue(i).getValue());
        }
        return data;
    }
}
