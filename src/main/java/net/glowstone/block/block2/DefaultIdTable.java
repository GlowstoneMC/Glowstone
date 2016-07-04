package net.glowstone.block.block2;

import org.bukkit.TreeSpecies;
import org.spongepowered.api.block.BlockState;

import static net.glowstone.block.block2.Blocks.*;

/**
 * Default {@link IdTable} for pre-block revamp ids.
 */
public final class DefaultIdTable extends IdTable {

    public static final IdTable INSTANCE = new DefaultIdTable();

    private DefaultIdTable() {
        bind(0, AIR);
        bind(1, STONE);
        bind(2, GRASS);
        bind(3, DIRT);
        bind(4, COBBLESTONE);
        bind(5, WOOD);
        bind(6, SAPLING, new SaplingIdResolver());
        bind(7, BEDROCK);
        // ...
        bind(12, SAND);
        bind(13, GRAVEL);
        bind(14, GOLD_ORE);
        bind(15, IRON_ORE);
        bind(16, COAL_ORE);
        bind(17, LOG);
        bind(18, LEAVES);
        bind(19, SPONGE);
        bind(20, GLASS);
        bind(21, LAPIS_ORE);
        bind(22, LAPIS_BLOCK);
        bind(23, DISPENSER);
        bind(24, SANDSTONE);
        bind(25, NOTEBLOCK);
        bind(26, BED);
        bind(27, GOLDEN_RAIL);
        bind(28, DETECTOR_RAIL);
        bind(29, STICKY_PISTON);
        bind(30, WEB);
        bind(31, TALLGRASS);
        bind(32, DEADBUSH);
        bind(33, PISTON);
        bind(34, PISTON_HEAD);
        bind(35, WOOL);
        // ...
        bind(37, YELLOW_FLOWER);
        bind(38, RED_FLOWER);
        bind(39, BROWN_MUSHROOM);
        bind(40, RED_MUSHROOM);
        bind(41, GOLD_BLOCK);
        bind(42, IRON_BLOCK);
        // ...
        bind(45, BRICK_BLOCK);
        bind(46, TNT);
        bind(47, BOOKSHELF);
        bind(48, MOSSY_COBBLESTONE);
        bind(49, OBSIDIAN);
        bind(50, TORCH);
        bind(51, FIRE);
        bind(52, MOB_SPAWNER);
        bind(53, OAK_STAIRS);
        bind(54, CHEST);
        bind(55, REDSTONE_WIRE);
        bind(56, DIAMOND_ORE);
        bind(57, DIAMOND_BLOCK);
        bind(58, CRAFTING_TABLE);
        bind(59, WHEAT);
        bind(60, FARMLAND);
        bind(61, FURNACE);
        bind(62, LIT_FURNACE);
        bind(63, STANDING_SIGN);
        bind(64, WOODEN_DOOR);
        bind(65, LADDER);
        bind(66, RAIL);
        bind(67, STONE_STAIRS);
        bind(68, WALL_SIGN);
        bind(69, LEVER);
        bind(70, STONE_PRESSURE_PLATE);
        bind(71, IRON_DOOR);
        bind(72, WOODEN_PRESSURE_PLATE);
        bind(73, REDSTONE_ORE);
        bind(74, LIT_REDSTONE_ORE);
        bind(75, UNLIT_REDSTONE_TORCH);
        bind(76, REDSTONE_TORCH);
        bind(77, STONE_BUTTON);
        bind(78, SNOW_LAYER);
        bind(79, ICE);
        bind(80, SNOW);
        bind(81, CACTUS);
        bind(82, CLAY);
        bind(83, REEDS);
        bind(84, JUKEBOX);
        bind(85, FENCE);
        bind(86, PUMPKIN);
        bind(87, NETHERRACK);
        bind(88, SOUL_SAND);
        bind(89, GLOWSTONE);
    }

    private static class SaplingIdResolver implements IdResolver {
        @Override
        public int getId(BlockState state, int suggested) {
            if (suggested >= TreeSpecies.values().length) {
                suggested += 8 - TreeSpecies.values().length;
            }
            return suggested;
        }
    }
}
