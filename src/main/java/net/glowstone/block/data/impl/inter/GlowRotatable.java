package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.StateUtil;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.NotNull;

public interface GlowRotatable extends IBlockData, Rotatable {

    default IntegerStateValue.Ranged getRotationStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("face").get();
    }

    @Override
    default  @NotNull BlockFace getRotation() {
        return StateUtil.getBlockFace(this.getRotationStateValue().getValue(), StateUtil.SIXTEEN_BLOCK_FACES);
    }

    @Override
    default void setRotation(@NotNull BlockFace blockFace) {
        this.getRotationStateValue().setValue(StateUtil.getBlockFaceId(blockFace, StateUtil.SIXTEEN_BLOCK_FACES));
    }
}
