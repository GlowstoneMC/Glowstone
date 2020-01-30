package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowBisected;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowOpenable;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.jetbrains.annotations.NotNull;

public class GlowDoorBlockData extends AbstractBlockData implements Door, GlowBisected, GlowDirectional, GlowOpenable, GlowPowered {

    public GlowDoorBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.OPEN, StateGenerator.POWERED, StateGenerator.HINGE);
    }

    public EnumStateValue<Door.Hinge> getHingeStateValue(){
        return (EnumStateValue<Hinge>) this.getStateValue("hinge");
    }

    @Override
    public @NotNull Door.Hinge getHinge() {
        return this.getHingeStateValue().getValue();
    }

    @Override
    public void setHinge(@NotNull Door.Hinge hinge) {
        this.getHingeStateValue().setValue(hinge);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowDoorBlockData door = new GlowDoorBlockData(this.getMaterial());
        door.setHinge(this.getHinge());
        door.setFacing(this.getFacing());
        door.setHalf(this.getHalf());
        door.setOpen(this.isOpen());
        door.setPowered(this.isPowered());
        return door;
    }
}
