package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.*;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrapDoor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GlowTrapDoorBlockData extends AbstractBlockData implements GlowBisected, GlowOpenable, GlowPowered, GlowWaterlogged, GlowDirectional, TrapDoor {

    public GlowTrapDoorBlockData(Material material) {
        super(material, StateGenerator.OPEN, StateGenerator.HALF, StateGenerator.POWERED, StateGenerator.FOUR_FACING, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowTrapDoorBlockData door = new GlowTrapDoorBlockData(this.getMaterial());
        door.setFacing(this.getFacing());
        door.setHalf(this.getHalf());
        door.setOpen(this.isOpen());
        door.setPowered(this.isPowered());
        door.setWaterlogged(this.isWaterlogged());
        return door;
    }
}
