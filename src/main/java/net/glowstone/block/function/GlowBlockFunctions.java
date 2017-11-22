package net.glowstone.block.function;

import net.glowstone.block.function.BlockFunctions.BlockFunctionInteract;
import net.glowstone.inventory.GlowAnvilInventory;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import java.util.function.Predicate;

import static net.glowstone.block.function.BlockFunctions.BlockFunctionPlaceAllow;

public class GlowBlockFunctions {
    public static class PlaceAllow {
        /**
         * Check for allowing place for cactus
         */
        public static final BlockFunctionPlaceAllow CACTUS = (block, against) -> {
            Material below = block.getRelative(BlockFace.DOWN).getType();
            boolean hasSurroundingBlocks = false;
            for (BlockFace face : Util.ADJACENT_FACES) {
                if (!Util.CACTUS_BUILD.test(block.getRelative(face).getType())) {
                    hasSurroundingBlocks = true;
                    break;
                }
            }
            return ((below == Material.CACTUS) || below == Material.SAND) && !hasSurroundingBlocks;
        };
    }

    public static class Interact {
        /**
         * Opens an anvil inventory
         */
        public static final BlockFunctionInteract ANVIL = (player, block, face, clickedLoc) -> player.openInventory(new GlowAnvilInventory(player)) != null;

        /**
         * Opens a chest
         */
        public static final BlockFunctionInteract CHEST = (player, block, face, clickedLoc) -> {
            Chest chest = (Chest) block.getState();
            player.openInventory(chest.getInventory());
            player.incrementStatistic(Statistic.CHEST_OPENED);
            return true;
        };
    }

    public static class Util {
        /**
         * Directly adjacent block faces
         */
        public static final BlockFace[] ADJACENT_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

        /**
         * Checks if cactus can be placed next to this block
         */
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
