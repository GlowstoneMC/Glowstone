package net.glowstone.block.data.impl.inter;

import com.google.common.collect.ImmutableSet;
import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.StateValue;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface GlowMultipleFaces extends IBlockData, MultipleFacing {

    default BooleanStateValue getNorthStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("north").get();
    }

    default BooleanStateValue getWestStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("west").get();
    }

    default BooleanStateValue getSouthStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("south").get();
    }

    default BooleanStateValue getEastStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("east").get();
    }

    default Optional<BooleanStateValue> getUpStateValue(){
        Optional<StateValue<Boolean>> opValue = this.getStateValue("up");
        return opValue.map(stateValue -> (BooleanStateValue) stateValue);
    }

    default Optional<BooleanStateValue> getDownStateValue(){
        Optional<StateValue<Boolean>> opValue = this.getStateValue("down");
        return opValue.map(stateValue -> (BooleanStateValue) stateValue);
    }

    @Override
    default boolean hasFace(@NotNull BlockFace blockFace) {
        switch (blockFace){
            case NORTH: return getNorthStateValue().getValue();
            case EAST: return getEastStateValue().getValue();
            case SOUTH: return getSouthStateValue().getValue();
            case WEST: return getWestStateValue().getValue();
            case UP:
                Optional<BooleanStateValue> opUp = getUpStateValue();
                if(opUp.isPresent()){
                    return opUp.get().getValue();
                }
                return false;
            case DOWN:
                Optional<BooleanStateValue> opDown = getDownStateValue();
                if(opDown.isPresent()){
                    return opDown.get().getValue();
                }
                return false;
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
                getUpStateValue().ifPresent(s -> s.setValue(b));
                return;
            case DOWN:
                getDownStateValue().ifPresent(s -> s.setValue(b));
                return;
            default:
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
