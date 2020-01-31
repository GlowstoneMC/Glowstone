package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAttachable;
import net.glowstone.block.data.impl.inter.GlowMultipleFaces;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Tripwire;
import org.jetbrains.annotations.NotNull;

public class GlowTripwireHookBlockData extends AbstractBlockData implements GlowPowered, GlowMultipleFaces, GlowAttachable, Tripwire {
    public GlowTripwireHookBlockData(Material material) {
        super(material, StateGenerator.POWERED, StateGenerator.ATTACHED, StateGenerator.DISARMED, StateGenerator.BOOLEAN_EAST, StateGenerator.BOOLEAN_NORTH, StateGenerator.BOOLEAN_SOUTH, StateGenerator.BOOLEAN_WEST);
    }

    public BooleanStateValue getDisarmedStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("disarmed").get();
    }

    @Override
    public boolean isDisarmed() {
        return this.getDisarmedStateValue().getValue();
    }

    @Override
    public void setDisarmed(boolean b) {
        this.getDisarmedStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowTripwireHookBlockData tripwire = new GlowTripwireHookBlockData(this.getMaterial());
        tripwire.setDisarmed(this.isDisarmed());
        tripwire.setAttached(this.isAttached());
        tripwire.setPowered(this.isPowered());
        for(BlockFace face : this.getAllowedFaces()){
            tripwire.setFace(face, true);
        }
        return tripwire;
    }
}
