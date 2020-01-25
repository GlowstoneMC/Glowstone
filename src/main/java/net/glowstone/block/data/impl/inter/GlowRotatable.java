package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.StateUtil;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rotatable;
import org.jetbrains.annotations.NotNull;

public interface GlowRotatable extends IBlockData, Rotatable {

    default IntegerStateValue.Ranged getRangedStateValue(){
        return this.getRangedStateValue();
    }

    @Override
    default  @NotNull BlockFace getRotation() {
        return StateUtil.getBlockFace(this.getRangedStateValue().getValue(), StateUtil.SIXTEEN_BLOCK_FACES);
    }

    @Override
    default void setRotation(@NotNull BlockFace blockFace) {
        this.getRangedStateValue().setValue(StateUtil.getBlockFaceId(blockFace, StateUtil.SIXTEEN_BLOCK_FACES));
    }
}
