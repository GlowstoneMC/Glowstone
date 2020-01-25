package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface GlowDirectional extends IBlockData, Directional {

    default EnumStateValue<BlockFace> getFacingState(){
        return (EnumStateValue<BlockFace>)this.getStateValue("facing");
    }

    @Override
    default @NotNull BlockFace getFacing() {
        return this.getFacingState().getValue();
    }

    @Override
    default void setFacing(@NotNull BlockFace blockFace) {
        this.getFacingState().setValue(blockFace);
    }

    @Override
    default  @NotNull Set<BlockFace> getFaces() {
        return new HashSet<>(Arrays.asList(this.getFacingState().getGenerator().getValues()));
    }
}
