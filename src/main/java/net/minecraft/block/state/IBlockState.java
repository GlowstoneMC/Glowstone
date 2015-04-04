package net.minecraft.block.state;

import java.util.List;

public interface IBlockState {

    List<IBlockState> getValidStates();
}
