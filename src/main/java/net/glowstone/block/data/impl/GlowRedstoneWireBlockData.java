package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAnalogPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWire;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlowRedstoneWireBlockData extends AbstractBlockData implements GlowAnalogPowered, RedstoneWire {

    public GlowRedstoneWireBlockData(Material material) {
        super(material, StateGenerator.REDSTONE_CONNECTION_EAST, StateGenerator.REDSTONE_CONNECTION_NORTH, StateGenerator.REDSTONE_CONNECTION_SOUTH, StateGenerator.REDSTONE_CONNECTION_WEST, StateGenerator.REDSTONE_POWER);
    }

    public EnumStateValue<RedstoneWire.Connection> getConnectionStateValue(BlockFace face){
        return (EnumStateValue<RedstoneWire.Connection>) this.<RedstoneWire.Connection>getStateValue(face.name().toLowerCase()).get();
    }

    @Override
    public @NotNull Connection getFace(@NotNull BlockFace blockFace) {
        return this.getConnectionStateValue(blockFace).getValue();
    }

    @Override
    public void setFace(@NotNull BlockFace blockFace, @NotNull Connection connection) {
        this.getConnectionStateValue(blockFace).setValue(connection);
    }

    @Override
    public @NotNull Set<BlockFace> getAllowedFaces() {
        return new HashSet<>(Arrays.asList(BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST));
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRedstoneWireBlockData wire = new GlowRedstoneWireBlockData(this.getMaterial());
        for(BlockFace face : this.getAllowedFaces()){
            wire.setFace(face, this.getFace(face));
        }
        wire.setPower(this.getPower());
        return wire;
    }
}
