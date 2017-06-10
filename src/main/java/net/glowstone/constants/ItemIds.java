package net.glowstone.constants;

import net.glowstone.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Temporary mappings for Minecraft's string-based item ids.
 */
public final class ItemIds {

    private static final Map<Integer, String> names = new HashMap<>();
    private static final Map<String, Integer> items = new HashMap<>();
    private static final Map<String, Integer> blocks = new HashMap<>();
    private static final List<String> ids = new ArrayList<>();

    static {
        // blocks
        both(0, "air");
        both(1, "stone");
        both(2, "grass");
        both(3, "dirt");
        both(4, "cobblestone");
        both(5, "planks");
        both(6, "sapling");
        both(7, "bedrock");
        block(8, "flowing_water");
        block(9, "water");
        block(10, "flowing_lava");
        block(11, "lava");
        both(12, "sand");
        both(13, "gravel");
        both(14, "gold_ore");
        both(15, "iron_ore");
        both(16, "coal_ore");
        both(17, "log");
        both(18, "leaves");
        both(19, "sponge");
        both(20, "glass");
        both(21, "lapis_ore");
        both(22, "lapis_block");
        both(23, "dispenser");
        both(24, "sandstone");
        both(25, "noteblock");
        block(26, "bed"); // bed is also 355
        both(27, "golden_rail");
        both(28, "detector_rail");
        both(29, "sticky_piston");
        both(30, "web");
        both(31, "tallgrass");
        both(32, "deadbush");
        both(33, "piston");
        block(34, "piston_head");
        both(35, "wool");
        block(36, "piston_extension");
        both(37, "yellow_flower");
        both(38, "red_flower");
        both(39, "brown_mushroom");
        both(40, "red_mushroom");
        both(41, "gold_block");
        both(42, "iron_block");
        block(43, "double_stone_slab");
        both(44, "stone_slab");
        both(45, "brick_block");
        both(46, "tnt");
        both(47, "bookshelf");
        both(48, "mossy_cobblestone");
        both(49, "obsidian");
        both(50, "torch");
        block(51, "fire");
        both(52, "mob_spawner");
        both(53, "oak_stairs");
        both(54, "chest");
        block(55, "redstone_wire");
        both(56, "diamond_ore");
        both(57, "diamond_block");
        both(58, "crafting_table");
        block(59, "wheat"); // wheat is also 296
        both(60, "farmland");
        both(61, "furnace");
        both(62, "lit_furnace");
        block(63, "standing_sign");
        both(64, "wooden_door"); // wooden_door is also 324
        both(65, "ladder");
        both(66, "rail");
        both(67, "stone_stairs");
        block(68, "wall_sign");
        both(69, "lever");
        both(70, "stone_pressure_plate");
        block(71, "iron_door"); // iron_door is also 330
        both(72, "wooden_pressure_plate");
        both(73, "redstone_ore");
        block(74, "lit_redstone_ore");
        block(75, "unlit_redstone_torch");
        both(76, "redstone_torch");
        alternate(76, "redstone_torch_(active)");
        both(77, "stone_button");
        both(78, "snow_layer");
        both(79, "ice");
        both(80, "snow");
        both(81, "cactus");
        both(82, "clay");
        block(83, "reeds"); // reeds is also 338
        both(84, "jukebox");
        both(85, "fence");
        both(86, "pumpkin");
        both(87, "netherrack");
        both(88, "soul_sand");
        both(89, "glowstone");
        block(90, "portal");
        both(91, "lit_pumpkin");
        block(92, "cake"); // cake is also 354
        block(93, "unpowered_repeater");
        block(94, "powered_repeater");
        both(95, "stained_glass");
        both(96, "trapdoor");
        both(97, "monster_egg");
        both(98, "stonebrick");
        both(99, "brown_mushroom_block");
        both(100, "red_mushroom_block");
        both(101, "iron_bars");
        both(102, "glass_pane");
        both(103, "melon_block");
        block(104, "pumpkin_stem");
        block(105, "melon_stem");
        both(106, "vine");
        both(107, "fence_gate");
        both(108, "brick_stairs");
        both(109, "stone_brick_stairs");
        both(110, "mycelium");
        both(111, "waterlily");
        both(112, "nether_brick");
        both(113, "nether_brick_fence");
        both(114, "nether_brick_stairs");
        block(115, "nether_wart"); // nether_wart is also 372
        both(116, "enchanting_table");
        block(117, "brewing_stand"); // brewing_stand is also 379
        block(118, "cauldron"); // cauldron is also 380
        block(119, "end_portal");
        both(120, "end_portal_frame");
        both(121, "end_stone");
        both(122, "dragon_egg");
        both(123, "redstone_lamp");
        alternate(123, "redstone_lamp_(inactive)");
        block(124, "lit_redstone_lamp");
        block(125, "double_wooden_slab");
        both(126, "wooden_slab");
        block(127, "cocoa");
        both(128, "sandstone_stairs");
        both(129, "emerald_ore");
        both(130, "ender_chest");
        both(131, "tripwire_hook");
        block(132, "tripwire");
        both(133, "emerald_block");
        both(134, "spruce_stairs");
        both(135, "birch_stairs");
        both(136, "jungle_stairs");
        both(137, "command_block");
        both(138, "beacon");
        both(139, "cobblestone_wall");
        block(140, "flower_pot"); // flower_pot is also 390
        block(141, "carrots");
        block(142, "potatoes");
        both(143, "wooden_button");
        block(144, "skull"); // skull is also 397
        both(145, "anvil");
        both(146, "trapped_chest");
        both(147, "light_weighted_pressure_plate");
        both(148, "heavy_weighted_pressure_plate");
        block(149, "unpowered_comparator");
        block(150, "powered_comparator");
        both(151, "daylight_detector");
        both(152, "redstone_block");
        both(153, "quartz_ore");
        both(154, "hopper");
        both(155, "quartz_block");
        both(156, "quartz_stairs");
        both(157, "activator_rail");
        both(158, "dropper");
        both(159, "stained_hardened_clay");
        both(160, "stained_glass_pane");
        both(161, "leaves2");
        both(162, "log2");
        both(163, "acacia_stairs");
        both(164, "dark_oak_stairs");
        both(165, "slime");
        both(166, "barrier");
        both(167, "iron_trapdoor");
        both(168, "prismarine");
        both(169, "sea_lantern");
        both(170, "hay_block");
        both(171, "carpet");
        both(172, "hardened_clay");
        both(173, "coal_block");
        both(174, "packed_ice");
        both(175, "double_plant");
        alternate(175, "large_flowers");
        block(176, "standing_banner");
        block(177, "wall_banner");
        block(178, "daylight_detector_inverted");
        both(179, "red_sandstone");
        both(180, "red_sandstone_stairs");
        block(181, "double_stone_slab2");
        both(182, "stone_slab2");
        both(183, "spruce_fence_gate");
        both(184, "birch_fence_gate");
        both(185, "jungle_fence_gate");
        both(186, "dark_oak_fence_gate");
        both(187, "acacia_fence_gate");
        both(188, "spruce_fence");
        both(189, "birch_fence");
        both(190, "jungle_fence");
        both(191, "dark_oak_fence");
        both(192, "acacia_fence");
        block(193, "spruce_door"); // spruce_door is also 427
        block(194, "birch_door"); // birch_door is also 428
        block(195, "jungle_door"); // jungle_door is also 429
        block(196, "acacia_door"); // acacia_door is also 430
        block(197, "dark_oak_door"); // dark_oak_door is also 431
        both(198, "end_rod");
        both(199, "chorus_plant");
        both(200, "chorus_flower");
        both(201, "purpur_block");
        both(202, "purpur_pillar");
        both(203, "purpur_stairs");
        block(204, "purpur_double_slab");
        both(205, "purpur_slab");
        both(206, "end_bricks");
        block(207, "beetroots");
        block(208, "grass_path");
        block(209, "end_gateway");
        block(210, "repeating_command_block");
        block(211, "chain_command_block");
        block(212, "frosted_ice");
        both(213, "magma");
        both(214, "nether_wart_block");
        both(215, "red_nether_brick");
        both(216, "bone_block");
        both(217, "structure_void");
        both(218, "observer");
        both(219, "white_shulker_box");
        both(220, "orange_shulker_box");
        both(221, "magenta_shulker_box");
        both(222, "light_blue_shulker_box");
        both(223, "yellow_shulker_box");
        both(224, "lime_shulker_box");
        both(225, "pink_shulker_box");
        both(226, "gray_shulker_box");
        both(227, "silver_shulker_box");
        both(228, "cyan_shulker_box");
        both(229, "purple_shulker_box");
        both(230, "blue_shulker_box");
        both(231, "brown_shulker_box");
        both(232, "green_shulker_box");
        both(233, "red_shulker_box");
        both(234, "black_shulker_box");
        both(235, "white_glazed_terracotta");
        both(236, "orange_glazed_terracotta");
        both(237, "magenta_glazed_terracotta");
        both(238, "light_blue_glazed_terracotta");
        both(239, "yellow_glazed_terracotta");
        both(240, "lime_glazed_terracotta");
        both(241, "pink_glazed_terracotta");
        both(242, "gray_glazed_terracotta");
        both(243, "silver_glazed_terracotta");
        both(244, "cyan_glazed_terracotta");
        both(245, "purple_glazed_terracotta");
        both(246, "blue_glazed_terracotta");
        both(247, "brown_glazed_terracotta");
        both(248, "green_glazed_terracotta");
        both(249, "red_glazed_terracotta");
        both(250, "black_glazed_terracotta");
        both(251, "concrete");
        both(252, "concrete_powder");
        block(255, "structure_block");
        // items
        item(256, "iron_shovel");
        item(257, "iron_pickaxe");
        item(258, "iron_axe");
        item(259, "flint_and_steel");
        item(260, "apple");
        item(261, "bow");
        item(262, "arrow");
        item(263, "coal");
        item(264, "diamond");
        item(265, "iron_ingot");
        item(266, "gold_ingot");
        item(267, "iron_sword");
        item(268, "wooden_sword");
        item(269, "wooden_shovel");
        item(270, "wooden_pickaxe");
        item(271, "wooden_axe");
        item(272, "stone_sword");
        item(273, "stone_shovel");
        item(274, "stone_pickaxe");
        item(275, "stone_axe");
        item(276, "diamond_sword");
        item(277, "diamond_shovel");
        item(278, "diamond_pickaxe");
        item(279, "diamond_axe");
        item(280, "stick");
        item(281, "bowl");
        item(282, "mushroom_stew");
        item(283, "golden_sword");
        item(284, "golden_shovel");
        item(285, "golden_pickaxe");
        item(286, "golden_axe");
        item(287, "string");
        item(288, "feather");
        item(289, "gunpowder");
        item(290, "wooden_hoe");
        item(291, "stone_hoe");
        item(292, "iron_hoe");
        item(293, "diamond_hoe");
        item(294, "golden_hoe");
        item(295, "wheat_seeds");
        item(296, "wheat");
        item(297, "bread");
        item(298, "leather_helmet");
        item(299, "leather_chestplate");
        item(300, "leather_leggings");
        item(301, "leather_boots");
        item(302, "chainmail_helmet");
        item(303, "chainmail_chestplate");
        item(304, "chainmail_leggings");
        item(305, "chainmail_boots");
        item(306, "iron_helmet");
        item(307, "iron_chestplate");
        item(308, "iron_leggings");
        item(309, "iron_boots");
        item(310, "diamond_helmet");
        item(311, "diamond_chestplate");
        item(312, "diamond_leggings");
        item(313, "diamond_boots");
        item(314, "golden_helmet");
        item(315, "golden_chestplate");
        item(316, "golden_leggings");
        item(317, "golden_boots");
        item(318, "flint");
        item(319, "porkchop");
        item(320, "cooked_porkchop");
        item(321, "painting");
        item(322, "golden_apple");
        item(323, "sign");
        item(324, "wooden_door");
        item(325, "bucket");
        item(326, "water_bucket");
        item(327, "lava_bucket");
        item(328, "minecart");
        item(329, "saddle");
        item(330, "iron_door");
        item(331, "redstone");
        item(332, "snowball");
        item(333, "boat");
        item(334, "leather");
        item(335, "milk_bucket");
        item(336, "brick");
        item(337, "clay_ball");
        item(338, "reeds");
        item(339, "paper");
        item(340, "book");
        item(341, "slime_ball");
        item(342, "chest_minecart");
        item(343, "furnace_minecart");
        item(344, "egg");
        item(345, "compass");
        item(346, "fishing_rod");
        item(347, "clock");
        item(348, "glowstone_dust");
        item(349, "fish");
        item(350, "cooked_fish");
        item(351, "dye");
        item(352, "bone");
        item(353, "sugar");
        item(354, "cake");
        item(355, "bed");
        item(356, "repeater");
        item(357, "cookie");
        item(358, "filled_map");
        item(359, "shears");
        item(360, "melon");
        item(361, "pumpkin_seeds");
        item(362, "melon_seeds");
        item(363, "beef");
        item(364, "cooked_beef");
        item(365, "chicken");
        item(366, "cooked_chicken");
        item(367, "rotten_flesh");
        item(368, "ender_pearl");
        item(369, "blaze_rod");
        item(370, "ghast_tear");
        item(371, "gold_nugget");
        item(372, "nether_wart");
        item(373, "potion");
        item(374, "glass_bottle");
        item(375, "spider_eye");
        item(376, "fermented_spider_eye");
        item(377, "blaze_powder");
        item(378, "magma_cream");
        item(379, "brewing_stand");
        item(380, "cauldron");
        item(381, "ender_eye");
        item(382, "speckled_melon");
        item(383, "spawn_egg");
        item(384, "experience_bottle");
        item(385, "fire_charge");
        item(386, "writable_book");
        item(387, "written_book");
        item(388, "emerald");
        item(389, "item_frame");
        item(390, "flower_pot");
        item(391, "carrot");
        item(392, "potato");
        item(393, "baked_potato");
        item(394, "poisonous_potato");
        item(395, "map");
        item(396, "golden_carrot");
        item(397, "skull");
        item(398, "carrot_on_a_stick");
        item(399, "nether_star");
        item(400, "pumpkin_pie");
        item(401, "fireworks");
        item(402, "firework_charge");
        item(403, "enchanted_book");
        item(404, "comparator");
        item(405, "netherbrick");
        item(406, "quartz");
        item(407, "tnt_minecart");
        item(408, "hopper_minecart");
        item(409, "prismarine_shard");
        item(410, "prismarine_crystals");
        item(411, "rabbit");
        item(412, "cooked_rabbit");
        item(413, "rabbit_stew");
        item(414, "rabbit_foot");
        item(415, "rabbit_hide");
        item(416, "armor_stand");
        item(417, "iron_horse_armor");
        item(418, "golden_horse_armor");
        item(419, "diamond_horse_armor");
        item(420, "lead");
        item(421, "name_tag");
        item(422, "command_block_minecart");
        item(423, "mutton");
        item(424, "cooked_mutton");
        item(425, "banner");
        item(426, "end_crystal");
        item(427, "spruce_door");
        item(428, "birch_door");
        item(429, "jungle_door");
        item(430, "acacia_door");
        item(431, "dark_oak_door");
        item(432, "chorus_fruit");
        item(433, "popped_chorus_fruit");
        item(434, "beetroot");
        item(435, "beetroot_seeds");
        item(436, "beetroot_soup");
        item(437, "dragon_breath");
        item(438, "splash_potion");
        item(439, "spectral_arrow");
        item(440, "tipped_arrow");
        item(441, "lingering_potion");
        item(442, "shield");
        item(443, "elytra");
        item(444, "spruce_boat");
        item(445, "birch_boat");
        item(446, "jungle_boat");
        item(447, "acacia_boat");
        item(448, "dark_oak_boat");
        item(449, "totem");
        item(450, "shulker_shell");
        item(452, "iron_nugget");
        item(453, "knowledge_book");
        item(2256, "record_13");
        item(2257, "record_cat");
        item(2258, "record_blocks");
        item(2259, "record_chirp");
        item(2260, "record_far");
        item(2261, "record_mall");
        item(2262, "record_mellohi");
        item(2263, "record_stal");
        item(2264, "record_strad");
        item(2265, "record_ward");
        item(2266, "record_11");
        item(2267, "record_wait");

        ids.addAll(items.keySet());
    }

    private ItemIds() {
    }

    /**
     * Get the string identifier for a specified Material.
     *
     * @param mat the Material.
     * @return the identifier.
     */
    public static String getName(Material mat) {
        checkNotNull(mat, "Material cannot be null");
        return names.get(mat.getId());
    }

    /**
     * Get the Material corresponding to the specified item identifier.
     *
     * @param name the identifier.
     * @return the Material, or null.
     */
    public static Material getItem(String name) {
        if (!items.containsKey(name)) {
            return null;
        }
        return Material.getMaterial(items.get(name));
    }

    /**
     * Get the Material corresponding to the specified block identifier.
     *
     * @param name the identifier.
     * @return the Material, or null.
     */
    public static Material getBlock(String name) {
        if (!blocks.containsKey(name)) {
            return null;
        }
        return Material.getMaterial(blocks.get(name));
    }

    /**
     * Verify that a given material is a valid item. All non-blocks are valid
     * items, but some blocks cannot be represented as items.
     *
     * @param material The material to verify.
     * @return true if the material is a valid item.
     */
    public static boolean isValidItem(Material material) {
        return getItem(getName(material)) == material;
    }

    /**
     * Convert an ItemStack which may have a type that is unrepresentable as
     * an item to one that does, or to an empty stack if this is not possible.
     *
     * @param stack The stack to sanitize.
     * @return The sanitized stack, or null.
     */
    public static ItemStack sanitize(ItemStack stack) {
        if (InventoryUtil.isEmpty(stack) || stack.getType() == null) {
            return InventoryUtil.createEmptyStack();
        }
        Material item = getItem(getName(stack.getType()));
        if (item == null) {
            return null;
        }
        if (item != stack.getType()) {
            stack = stack.clone();
            stack.setType(item);
        }
        return stack;
    }

    public static List<String> getIds() {
        return ids;
    }

    private static void block(int id, String key) {
        key = "minecraft:" + key;
        names.put(id, key);
        blocks.put(key, id);
    }

    private static void item(int id, String key) {
        key = "minecraft:" + key;
        names.put(id, key);
        items.put(key, id);
    }

    private static void both(int id, String key) {
        key = "minecraft:" + key;
        names.put(id, key);
        blocks.put(key, id);
        items.put(key, id);
    }

    private static void alternate(int id, String key) {
        items.put("minecraft:" + key, id);
    }

}
