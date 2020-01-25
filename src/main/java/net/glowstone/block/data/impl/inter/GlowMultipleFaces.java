package net.glowstone.block.data.impl.inter;

import com.google.common.collect.ImmutableSet;
import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface GlowMultipleFaces extends IBlockData, MultipleFacing {

    default BooleanStateValue getNorthStateValue(){
        return (BooleanStateValue) this.getStateValue("north");
    }

    default BooleanStateValue getWestStateValue(){
        return (BooleanStateValue) this.getStateValue("west");
    }

    default BooleanStateValue getSouthStateValue(){
        return (BooleanStateValue) this.getStateValue("south");
    }

    default BooleanStateValue getEastStateValue(){
        return (BooleanStateValue) this.getStateValue("east");
    }

    default BooleanStateValue getUpStateValue(){
        StateValue<?> value = this.getStateValue("up");
        if(value == null){
            return null;
        }
        return (BooleanStateValue)value;
    }

    default BooleanStateValue getDownStateValue(){
        StateValue<?> value = this.getStateValue("down");
        if(value == null){
            return null;
        }
        return (BooleanStateValue)value;
    }

    @Override
    default boolean hasFace(@NotNull BlockFace blockFace) {
        switch (blockFace){
            case NORTH: return getNorthStateValue().getValue();
            case EAST: return getEastStateValue().getValue();
            case SOUTH: return getSouthStateValue().getValue();
            case WEST: return getWestStateValue().getValue();
            case UP:
                BooleanStateValue up = getUpStateValue();
                if(up == null){
                    return false;
                }
                return up.getValue();
            case DOWN:
                BooleanStateValue down = getDownStateValue();
                if(down == null){
                    return false;
                }
                return down.getValue();
            default:
                return false;
        }
    }

    @Override
    default void setFace(@Nullable BlockFace blockFace, boolean b) {
        switch (blockFace){
            case NORTH:
                getNorthStateValue().setValue(b);
                return;
            case EAST:
                getEastStateValue().setValue(b);
                return;
            case SOUTH:
                getSouthStateValue().setValue(b);
                return;
            case WEST:
                getWestStateValue().setValue(b);
                return;
            case UP:
                BooleanStateValue up = getUpStateValue();
                if(up == null){
                    return;
                }
                up.setValue(b);
                return;
            case DOWN:
                BooleanStateValue down = getDownStateValue();
                if(down == null){
                    return;
                }
                down.setValue(b);
                return;
            default:
                return;
        }
    }

    @Override
    default @NotNull Set<BlockFace> getFaces() {
        ImmutableSet.Builder<BlockFace> builder = ImmutableSet.builder();
        for(BlockFace face : BlockFace.values()){
            if(this.hasFace(face)){
                builder.add(face);
            }
        }
        return builder.build();
    }

    @Override
    default @NotNull Set<BlockFace> getAllowedFaces() {
        ImmutableSet.Builder<BlockFace> builder = ImmutableSet.builder();
        if(this.getWestStateValue() != null){
            builder.add(BlockFace.WEST);
        }
        if(this.getNorthStateValue() != null){
            builder.add(BlockFace.NORTH);
        }
        if(this.getEastStateValue() != null){
            builder.add(BlockFace.EAST);
        }
        if(this.getSouthStateValue() != null){
            builder.add(BlockFace.SOUTH);
        }
        return builder.build();
    }
}
