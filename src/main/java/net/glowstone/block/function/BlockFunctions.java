package net.glowstone.block.function;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BlockFunctions {
    @FunctionalInterface
    public interface BlockFunctionInteract extends ItemFunction {
        boolean apply(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc);
    }
    
    @FunctionalInterface
    public interface BlockFunctionPulse extends ItemFunction {
        void apply(GlowBlock block);
    }

    @FunctionalInterface
    public interface BlockFunctionPulseRate extends ItemFunction {
        int apply(GlowBlock block);

        @Override
        default boolean isSingle() {
            return true;
        }
    }

    @FunctionalInterface
    public interface BlockFunctionPulseTimes extends ItemFunction {
        boolean apply(GlowBlock block);

        @Override
        default boolean isSingle() {
            return true;
        }
    }
}
