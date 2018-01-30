package net.glowstone.block.function;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockFunctions {
    @FunctionalInterface
    public interface BlockFunctionPlaceAllow extends ItemFunction {
        boolean apply(GlowBlock block, BlockFace against);

        @Override
        default String getFunctionality() {
            return "block.place.allow";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionPlace extends ItemFunction {
        void apply(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState);

        @Override
        default String getFunctionality() {
            return "block.place";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionPlaceAfter extends ItemFunction {
        void apply(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState);

        @Override
        default String getFunctionality() {
            return "block.place.after";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionInteract extends ItemFunction {
        boolean apply(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc);

        @Override
        default String getFunctionality() {
            return "block.interact";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionDestroy extends ItemFunction {
        void apply(GlowPlayer player, GlowBlock block, BlockFace face);

        @Override
        default String getFunctionality() {
            return "block.destroy";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionDestroyAfter extends ItemFunction {
        void apply(GlowPlayer player, GlowBlock block, BlockFace face, GlowBlockState oldState);

        @Override
        default String getFunctionality() {
            return "block.destroy.after";
        }
    }
    
    @FunctionalInterface
    public interface BlockFunctionTick extends ItemFunction {
        void apply(GlowBlock block);

        @Override
        default String getFunctionality() {
            return "block.tick";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionTickRate extends ItemFunction {
        int apply(GlowBlock block);

        @Override
        default String getFunctionality() {
            return "block.tick.rate";
        }

        @Override
        default boolean isSingle() {
            return true;
        }
    }

    @FunctionalInterface
    public interface BlockFunctionTickRepeating extends ItemFunction {
        boolean apply(GlowBlock block);

        @Override
        default String getFunctionality() {
            return "block.tick.repeating";
        }

        @Override
        default boolean isSingle() {
            return true;
        }
    }

    @FunctionalInterface
    public interface BlockFunctionTickRandom extends ItemFunction {
        boolean apply();

        @Override
        default String getFunctionality() {
            return "block.tick.random";
        }

        @Override
        default boolean isSingle() {
            return true;
        }
    }

    @FunctionalInterface
    public interface BlockFunctionAbsorb extends ItemFunction {
        boolean apply(GlowBlock block, BlockFace face, ItemStack holding);

        @Override
        default String getFunctionality() {
            return "block.absorb";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionOverride extends ItemFunction {
        boolean apply(GlowBlock block, BlockFace face, ItemStack holding);

        @Override
        default String getFunctionality() {
            return "block.override";
        }

        @Override
        default boolean isSingle() {
            return true;
        }
    }

    @FunctionalInterface
    public interface BlockFunctionNearChanged extends ItemFunction {
        void apply(GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType,
                   byte oldData, Material newType, byte newData);

        @Override
        default String getFunctionality() {
            return "block.near_changed";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionChanged extends ItemFunction {
        void apply(GlowBlock block, Material oldType, byte oldData, Material newType, byte data);

        @Override
        default String getFunctionality() {
            return "block.changed";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionPhysics extends ItemFunction {
        void apply(GlowBlock block);

        @Override
        default String getFunctionality() {
            return "block.physics";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionUpdate extends ItemFunction {
        void apply(GlowBlock block);

        @Override
        default String getFunctionality() {
            return "block.update";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionClick extends ItemFunction {
        void apply(GlowPlayer player, GlowBlock block, ItemStack holding);

        @Override
        default String getFunctionality() {
            return "block.click";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionRedstone extends ItemFunction {
        void apply(GlowBlock block);

        @Override
        default String getFunctionality() {
            return "block.redstone";
        }
    }

    @FunctionalInterface
    public interface BlockFunctionStep extends ItemFunction {
        void apply(GlowBlock block, LivingEntity entity);

        @Override
        default String getFunctionality() {
            return "block.step";
        }
    }
}
