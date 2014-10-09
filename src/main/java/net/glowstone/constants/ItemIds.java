package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * Temporary mappings for Minecraft's string-based item ids.
 */
public final class ItemIds {

    private ItemIds() {
    }

    private static final Map<Integer, String> idToName = new HashMap<>();
    private static final Map<String, Integer> nameToId = new HashMap<>();

    /**
     * Get the string identifier for a specified Material.
     * @param mat the Material.
     * @return the identifier.
     */
    public static String getName(Material mat) {
        Validate.notNull(mat, "Material cannot be null");
        return idToName.get(mat.getId());
    }

    /**
     * Get the Material corresponding to the specified identifier.
     * @param name the identifier.
     * @return the Material, or null.
     */
    public static Material getMaterial(String name) {
        return Material.getMaterial(nameToId.get(name));
    }

    private static void set(int id, String key) {
        key = "minecraft:" + key;
        idToName.put(id, key);
        nameToId.put(key, id);
    }

    static {
        // blocks
        set(0, "air");
        set(1, "stone");
        set(2, "grass");
        set(3, "dirt");
        set(4, "cobblestone");
        set(5, "planks");
        set(6, "sapling");
        set(7, "bedrock");
        set(8, "flowing_water");
        set(9, "water");
        set(10, "flowing_lava");
        set(11, "lava");
        set(12, "sand");
        set(13, "gravel");
        set(14, "gold_ore");
        set(15, "iron_ore");
        set(16, "coal_ore");
        set(17, "log");
        set(18, "leaves");
        set(19, "sponge");
        set(20, "glass");
        set(21, "lapis_ore");
        set(22, "lapis_block");
        set(23, "dispenser");
        set(24, "sandstone");
        set(25, "noteblock");
        set(26, "bed"); // bed is also 355
        set(27, "golden_rail");
        set(28, "detector_rail");
        set(29, "sticky_piston");
        set(30, "web");
        set(31, "tallgrass");
        set(32, "deadbush");
        set(33, "piston");
        set(34, "piston_head");
        set(35, "wool");
        set(36, "piston_extension");
        set(37, "yellow_flower");
        set(38, "red_flower");
        set(39, "brown_mushroom");
        set(40, "red_mushroom");
        set(41, "gold_block");
        set(42, "iron_block");
        set(43, "double_stone_slab");
        set(44, "stone_slab");
        set(45, "brick_block");
        set(46, "tnt");
        set(47, "bookshelf");
        set(48, "mossy_cobblestone");
        set(49, "obsidian");
        set(50, "torch");
        set(51, "fire");
        set(52, "mob_spawner");
        set(53, "oak_stairs");
        set(54, "chest");
        set(55, "redstone_wire");
        set(56, "diamond_ore");
        set(57, "diamond_block");
        set(58, "crafting_table");
        set(59, "wheat"); // wheat is also 296
        set(60, "farmland");
        set(61, "furnace");
        set(62, "lit_furnace");
        set(63, "standing_sign");
        set(64, "wooden_door"); // wooden_door is also 324
        set(65, "ladder");
        set(66, "rail");
        set(67, "stone_stairs");
        set(68, "wall_sign");
        set(69, "lever");
        set(70, "stone_pressure_plate");
        set(71, "iron_door"); // iron_door is also 330
        set(72, "wooden_pressure_plate");
        set(73, "redstone_ore");
        set(74, "lit_redstone_ore");
        set(75, "unlit_redstone_torch");
        set(76, "redstone_torch_(active)");
        set(77, "stone_button");
        set(78, "snow_layer");
        set(79, "ice");
        set(80, "snow");
        set(81, "cactus");
        set(82, "clay");
        set(83, "reeds"); // reeds is also 338
        set(84, "jukebox");
        set(85, "fence");
        set(86, "pumpkin");
        set(87, "netherrack");
        set(88, "soul_sand");
        set(89, "glowstone");
        set(90, "portal");
        set(91, "lit_pumpkin");
        set(92, "cake"); // cake is also 354
        set(93, "unpowered_repeater");
        set(94, "powered_repeater");
        set(95, "stained_glass");
        set(96, "trapdoor");
        set(97, "monster_egg");
        set(98, "stonebrick");
        set(99, "brown_mushroom_block");
        set(100, "red_mushroom_block");
        set(101, "iron_bars");
        set(102, "glass_pane");
        set(103, "melon_block");
        set(104, "pumpkin_stem");
        set(105, "melon_stem");
        set(106, "vine");
        set(107, "fence_gate");
        set(108, "brick_stairs");
        set(109, "stone_brick_stairs");
        set(110, "mycelium");
        set(111, "waterlily");
        set(112, "nether_brick");
        set(113, "nether_brick_fence");
        set(114, "nether_brick_stairs");
        set(115, "nether_wart"); // nether_wart is also 372
        set(116, "enchanting_table");
        set(117, "brewing_stand"); // brewing_stand is also 379
        set(118, "cauldron"); // cauldron is also 380
        set(119, "end_portal");
        set(120, "end_portal_frame");
        set(121, "end_stone");
        set(122, "dragon_egg");
        set(123, "redstone_lamp_(inactive)");
        set(124, "lit_redstone_lamp");
        set(125, "double_wooden_slab");
        set(126, "wooden_slab");
        set(127, "cocoa");
        set(128, "sandstone_stairs");
        set(129, "emerald_ore");
        set(130, "ender_chest");
        set(131, "tripwire_hook");
        set(132, "tripwire");
        set(133, "emerald_block");
        set(134, "spruce_stairs");
        set(135, "birch_stairs");
        set(136, "jungle_stairs");
        set(137, "command_block");
        set(138, "beacon");
        set(139, "cobblestone_wall");
        set(140, "flower_pot"); // flower_pot is also 390
        set(141, "carrots");
        set(142, "potatoes");
        set(143, "wooden_button");
        set(144, "skull"); // skull is also 397
        set(145, "anvil");
        set(146, "trapped_chest");
        set(147, "light_weighted_pressure_plate");
        set(148, "heavy_weighted_pressure_plate");
        set(149, "unpowered_comparator");
        set(150, "powered_comparator");
        set(151, "daylight_detector");
        set(152, "redstone_block");
        set(153, "quartz_ore");
        set(154, "hopper");
        set(155, "quartz_block");
        set(156, "quartz_stairs");
        set(157, "activator_rail");
        set(158, "dropper");
        set(159, "stained_hardened_clay");
        set(160, "stained_glass_pane");
        set(161, "leaves2");
        set(162, "log2");
        set(163, "acacia_stairs");
        set(164, "dark_oak_stairs");
        set(165, "slime");
        set(166, "barrier");
        set(167, "iron_trapdoor");
        set(168, "prismarine");
        set(169, "sea_lantern");
        set(170, "hay_block");
        set(171, "carpet");
        set(172, "hardened_clay");
        set(173, "coal_block");
        set(174, "packed_ice");
        set(175, "large_flowers");
        set(176, "standing_banner");
        set(177, "wall_banner");
        set(178, "daylight_detector_inverted");
        set(179, "red_sandstone");
        set(180, "red_sandstone_stairs");
        set(181, "double_stone_slab2");
        set(182, "stone_slab2");
        set(183, "spruce_fence_gate");
        set(184, "birch_fence_gate");
        set(185, "jungle_fence_gate");
        set(186, "dark_oak_fence_gate");
        set(187, "acacia_fence_gate");
        set(188, "spruce_fence");
        set(189, "birch_fence");
        set(190, "jungle_fence");
        set(191, "dark_oak_fence");
        set(192, "acacia_fence");
        set(193, "spruce_door"); // spruce_door is also 427
        set(194, "birch_door"); // birch_door is also 428
        set(195, "jungle_door"); // jungle_door is also 429
        set(196, "acacia_door"); // acacia_door is also 430
        set(197, "dark_oak_door"); // dark_oak_door is also 431
        // items
        set(256, "iron_shovel");
        set(257, "iron_pickaxe");
        set(258, "iron_axe");
        set(259, "flint_and_steel");
        set(260, "apple");
        set(261, "bow");
        set(262, "arrow");
        set(263, "coal");
        set(264, "diamond");
        set(265, "iron_ingot");
        set(266, "gold_ingot");
        set(267, "iron_sword");
        set(268, "wooden_sword");
        set(269, "wooden_shovel");
        set(270, "wooden_pickaxe");
        set(271, "wooden_axe");
        set(272, "stone_sword");
        set(273, "stone_shovel");
        set(274, "stone_pickaxe");
        set(275, "stone_axe");
        set(276, "diamond_sword");
        set(277, "diamond_shovel");
        set(278, "diamond_pickaxe");
        set(279, "diamond_axe");
        set(280, "stick");
        set(281, "bowl");
        set(282, "mushroom_stew");
        set(283, "golden_sword");
        set(284, "golden_shovel");
        set(285, "golden_pickaxe");
        set(286, "golden_axe");
        set(287, "string");
        set(288, "feather");
        set(289, "gunpowder");
        set(290, "wooden_hoe");
        set(291, "stone_hoe");
        set(292, "iron_hoe");
        set(293, "diamond_hoe");
        set(294, "golden_hoe");
        set(295, "wheat_seeds");
        set(296, "wheat");
        set(297, "bread");
        set(298, "leather_helmet");
        set(299, "leather_chestplate");
        set(300, "leather_leggings");
        set(301, "leather_boots");
        set(302, "chainmail_helmet");
        set(303, "chainmail_chestplate");
        set(304, "chainmail_leggings");
        set(305, "chainmail_boots");
        set(306, "iron_helmet");
        set(307, "iron_chestplate");
        set(308, "iron_leggings");
        set(309, "iron_boots");
        set(310, "diamond_helmet");
        set(311, "diamond_chestplate");
        set(312, "diamond_leggings");
        set(313, "diamond_boots");
        set(314, "golden_helmet");
        set(315, "golden_chestplate");
        set(316, "golden_leggings");
        set(317, "golden_boots");
        set(318, "flint");
        set(319, "porkchop");
        set(320, "cooked_porkchop");
        set(321, "painting");
        set(322, "golden_apple");
        set(323, "sign");
        set(324, "wooden_door");
        set(325, "bucket");
        set(326, "water_bucket");
        set(327, "lava_bucket");
        set(328, "minecart");
        set(329, "saddle");
        set(330, "iron_door");
        set(331, "redstone");
        set(332, "snowball");
        set(333, "boat");
        set(334, "leather");
        set(335, "milk_bucket");
        set(336, "brick");
        set(337, "clay_ball");
        set(338, "reeds");
        set(339, "paper");
        set(340, "book");
        set(341, "slime_ball");
        set(342, "chest_minecart");
        set(343, "furnace_minecart");
        set(344, "egg");
        set(345, "compass");
        set(346, "fishing_rod");
        set(347, "clock");
        set(348, "glowstone_dust");
        set(349, "fish");
        set(350, "cooked_fish");
        set(351, "dye");
        set(352, "bone");
        set(353, "sugar");
        set(354, "cake");
        set(355, "bed");
        set(356, "repeater");
        set(357, "cookie");
        set(358, "filled_map");
        set(359, "shears");
        set(360, "melon");
        set(361, "pumpkin_seeds");
        set(362, "melon_seeds");
        set(363, "beef");
        set(364, "cooked_beef");
        set(365, "chicken");
        set(366, "cooked_chicken");
        set(367, "rotten_flesh");
        set(368, "ender_pearl");
        set(369, "blaze_rod");
        set(370, "ghast_tear");
        set(371, "gold_nugget");
        set(372, "nether_wart");
        set(373, "potion");
        set(374, "glass_bottle");
        set(375, "spider_eye");
        set(376, "fermented_spider_eye");
        set(377, "blaze_powder");
        set(378, "magma_cream");
        set(379, "brewing_stand");
        set(380, "cauldron");
        set(381, "ender_eye");
        set(382, "speckled_melon");
        set(383, "spawn_egg");
        set(384, "experience_bottle");
        set(385, "fire_charge");
        set(386, "writable_book");
        set(387, "written_book");
        set(388, "emerald");
        set(389, "item_frame");
        set(390, "flower_pot");
        set(391, "carrot");
        set(392, "potato");
        set(393, "baked_potato");
        set(394, "poisonous_potato");
        set(395, "map");
        set(396, "golden_carrot");
        set(397, "skull");
        set(398, "carrot_on_a_stick");
        set(399, "nether_star");
        set(400, "pumpkin_pie");
        set(401, "fireworks");
        set(402, "firework_charge");
        set(403, "enchanted_book");
        set(404, "comparator");
        set(405, "netherbrick");
        set(406, "quartz");
        set(407, "tnt_minecart");
        set(408, "hopper_minecart");
        set(409, "prismarine_shard");
        set(410, "prismarine_crystals");
        set(411, "rabbit");
        set(412, "cooked_rabbit");
        set(413, "rabbit_stew");
        set(414, "rabbit_foot");
        set(415, "rabbit_hide");
        set(416, "armor_stand");
        set(417, "iron_horse_armor");
        set(418, "golden_horse_armor");
        set(419, "diamond_horse_armor");
        set(420, "lead");
        set(421, "name_tag");
        set(422, "command_block_minecart");
        set(423, "mutton");
        set(424, "cooked_mutton");
        set(425, "banner");
        set(427, "spruce_door");
        set(428, "birch_door");
        set(429, "jungle_door");
        set(430, "acacia_door");
        set(431, "dark_oak_door");
        set(2256, "record_13");
        set(2257, "record_cat");
        set(2258, "record_blocks");
        set(2259, "record_chirp");
        set(2260, "record_far");
        set(2261, "record_mall");
        set(2262, "record_mellohi");
        set(2263, "record_stal");
        set(2264, "record_strad");
        set(2265, "record_ward");
        set(2266, "record_11");
        set(2267, "record_wait");
    }

}
