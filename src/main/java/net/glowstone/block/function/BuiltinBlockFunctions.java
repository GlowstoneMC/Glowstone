package net.glowstone.block.function;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.function.Predicate;

import static net.glowstone.block.function.BlockFunctions.BlockFunctionPlaceAllow;

public class BuiltinBlockFunctions {
    public static class PlaceAllow {
        /**
         * Default check for allowing placement
         */
        public static final BlockFunctionPlaceAllow DEFAULT = (block, against) -> true;
        /**
         * Check for allowing place for cacti
         */
        public static final BlockFunctionPlaceAllow CACTUS = (block, against) -> {
            Material below = block.getRelative(BlockFace.DOWN).getType();
            boolean hasSurroundingBlocks = false;
            for (BlockFace face : BlockFunctionUtil.NEAR_BLOCKS) {
                if (!BlockFunctionUtil.CACTUS_BUILD.test(block.getRelative(face).getType())) {
                    hasSurroundingBlocks = true;
                    break;
                }
            }
            return ((below == Material.CACTUS) || below == Material.SAND) && !hasSurroundingBlocks;
        };
    }

    public static class BlockFunctionUtil {
        public static final BlockFace[] NEAR_BLOCKS = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
        public static Predicate<Material> CACTUS_BUILD = (type) -> {
            switch (type) {
                case GRASS:
                case DIRT:
                case SAND:
                case GLASS:
                case STONE:
                case FURNACE:
                case BURNING_FURNACE:
                case STAINED_GLASS:
                case THIN_GLASS:
                case FENCE:
                case ACACIA_FENCE:
                case BIRCH_FENCE:
                case DARK_OAK_FENCE:
                case IRON_FENCE:
                case JUNGLE_FENCE:
                case NETHER_FENCE:
                case SPRUCE_FENCE:
                case ACACIA_FENCE_GATE:
                case BIRCH_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case SPRUCE_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case FENCE_GATE:
                case ACACIA_DOOR:
                case BIRCH_DOOR:
                case DARK_OAK_DOOR:
                case IRON_DOOR:
                case JUNGLE_DOOR:
                case SPRUCE_DOOR:
                case WOODEN_DOOR:
                case TRAP_DOOR:
                case IRON_TRAPDOOR:
                case SPONGE:
                case COBBLESTONE:
                case MOSSY_COBBLESTONE:
                    return false;
                default:
                    return true;
            }
        };
    }
}
