package net.glowstone.block;

import org.bukkit.Material;
import org.bukkit.block.PistonMoveReaction;

/**
 * MaterialValueManager provides easy access to {@link Material} related values (e.g. block hardness).
 */
public class MaterialValueManager {
    public enum GlowMaterial {
        DEFAULT(),
        AIR(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setLightReduction(0)),
        STONE(new GlowMaterialBuilder()
                .setHardness(1.5f)
                .setBlastResistance(30)),
        GRASS(new GlowMaterialBuilder()
                .setHardness(0.6f)
                .setBlastResistance(3)
                .setRandomTicks(true)),
        DIRT(new GlowMaterialBuilder()
                .setHardness(0.5f)
                .setBlastResistance(2.5f)),
        COBBLESTONE(new GlowMaterialBuilder()
                .setHardness(2)
                .setBlastResistance(30)),
        WOOD(new GlowMaterialBuilder()
                .setHardness(2)
                .setFlammability(5)
                .setFireResistance(20)),
        SAPLING(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setLightReduction(0)
                .setRandomTicks(true)),
        BEDROCK(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000000)),
        WATER(new GlowMaterialBuilder()
                .setHardness(100)
                .setBlastResistance(500)
                .setLightReduction(3)),
        STATIONARY_WATER(new GlowMaterialBuilder()
                .setHardness(100)
                .setLightReduction(3)),
        LAVA(new GlowMaterialBuilder()
                .setHardness(100)
                .setRandomTicks(true)),
        STATIONARY_LAVA(new GlowMaterialBuilder()
                .setHardness(100)
                .setBlastResistance(500)
                .setRandomTicks(true)),
        SAND(new GlowMaterialBuilder()
                .setHardness(0.5f)),
        GRAVEL(new GlowMaterialBuilder()
                .setHardness(0.6f)),
        GOLD_ORE(new GlowMaterialBuilder()
                .setHardness(3)),
        IRON_ORE(new GlowMaterialBuilder()
                .setHardness(3)),
        COAL_ORE(new GlowMaterialBuilder()
                .setHardness(3)),
        LOG(new GlowMaterialBuilder()
                .setHardness(2)
                .setFireResistance(5)
                .setFlammability(5)),
        LEAVES(new GlowMaterialBuilder()
                .setHardness(0.2f)
                .setLightReduction(0)
                .setFireResistance(30)
                .setFlammability(60)),
        SPONGE(new GlowMaterialBuilder()
                .setHardness(0.6f)),
        GLASS(new GlowMaterialBuilder()
                .setHardness(0.3f)
                .setLightReduction(0)),
        LAPIS_ORE(new GlowMaterialBuilder()
                .setHardness(3)),
        LAPIS_BLOCK(new GlowMaterialBuilder()
                .setHardness(3)),
        DISPENSER(new GlowMaterialBuilder()
                .setHardness(3.5f)),
        SANDSTONE(new GlowMaterialBuilder()
                .setHardness(0.8f)),
        NOTE_BLOCK(new GlowMaterialBuilder()
                .setHardness(0.8f)),
        BED_BLOCK(new GlowMaterialBuilder()
                .setHardness(0.2f)),
        POWERED_RAIL(new GlowMaterialBuilder()
                .setHardness(0.7f)),
        DETECTOR_RAIL(new GlowMaterialBuilder()
                .setHardness(0.7f)),
        PISTON_STICKY_BASE(new GlowMaterialBuilder()
                .setHardness(0.5f)),
        WEB(new GlowMaterialBuilder()
                .setHardness(4)
                .setLightReduction(0)),
        LONG_GRASS(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setFireResistance(60)
                .setFlammability(100)),
        DEAD_BUSH(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        PISTON_BASE(),
        PISTON_EXTENSION(),
        WOOL(new GlowMaterialBuilder()
                .setFireResistance(30)
                .setFlammability(60)),
        PISTON_MOVING_PIECE(),
        YELLOW_FLOWER(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setFireResistance(60)
                .setFlammability(100)),
        RED_ROSE(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setFireResistance(60)
                .setFlammability(100)),
        BROWN_MUSHROOM(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setRandomTicks(true)),
        RED_MUSHROOM(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setRandomTicks(true)),
        GOLD_BLOCK(),
        IRON_BLOCK(),
        DOUBLE_STEP(),
        STEP(),
        BRICK(),
        TNT(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setFireResistance(15)
                .setFlammability(100)),
        BOOKSHELF(new GlowMaterialBuilder()
                .setFireResistance(30)
                .setFlammability(20)),
        MOSSY_COBBLESTONE(),
        OBSIDIAN(new GlowMaterialBuilder()
                .setHardness(50)
                .setBlastResistance(6000)),
        TORCH(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        FIRE(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setRandomTicks(true)),
        MOB_SPAWNER(),
        WOOD_STAIRS(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        CHEST(),
        REDSTONE_WIRE(new GlowMaterialBuilder()
                .setHardness(0)),
        DIAMOND_ORE(),
        DIAMOND_BLOCK(),
        WORKBENCH(),
        CROPS(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setRandomTicks(true)),
        SOIL(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        FURNACE(),
        BURNING_FURNACE(),
        SIGN_POST(),
        WOODEN_DOOR(),
        LADDER(new GlowMaterialBuilder()
                .setLightReduction(0)),
        RAILS(),
        COBBLESTONE_STAIRS(),
        WALL_SIGN(),
        LEVER(),
        STONE_PLATE(),
        IRON_DOOR_BLOCK(),
        WOOD_PLATE(),
        REDSTONE_ORE(),
        GLOWING_REDSTONE_ORE(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        REDSTONE_TORCH_OFF(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        REDSTONE_TORCH_ON(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        STONE_BUTTON(),
        SNOW(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        ICE(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        SNOW_BLOCK(),
        CACTUS(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        CLAY(),
        SUGAR_CANE_BLOCK(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setRandomTicks(true)),
        JUKEBOX(),
        FENCE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        PUMPKIN(),
        NETHERRACK(new GlowMaterialBuilder()
                .setFireResistance(-1)),
        SOUL_SAND(),
        GLOWSTONE(),
        PORTAL(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(0)
                .setLightReduction(0)
                .setRandomTicks(true)),
        JACK_O_LANTERN(),
        CAKE_BLOCK(),
        DIODE_BLOCK_OFF(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        DIODE_BLOCK_ON(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        STAINED_GLASS(),
        TRAP_DOOR(),
        MONSTER_EGGS(),
        SMOOTH_BRICK(),
        HUGE_MUSHROOM_1(),
        HUGE_MUSHROOM_2(),
        IRON_FENCE(),
        THIN_GLASS(),
        MELON_BLOCK(),
        PUMPKIN_STEM(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        MELON_STEM(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        VINE(new GlowMaterialBuilder()
                .setFireResistance(15)
                .setFlammability(100)),
        FENCE_GATE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        BRICK_STAIRS(),
        SMOOTH_STAIRS(),
        MYCEL(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        WATER_LILY(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        NETHER_BRICK(),
        NETHER_FENCE(),
        NETHER_BRICK_STAIRS(),
        NETHER_WARTS(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        ENCHANTMENT_TABLE(new GlowMaterialBuilder()
                .setBlastResistance(6000)),
        BREWING_STAND(),
        CAULDRON(),
        ENDER_PORTAL(new GlowMaterialBuilder()
                .setHardness(-1)
                .setLightReduction(0)
                .setBlastResistance(18000000)),
        ENDER_PORTAL_FRAME(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000000)),
        ENDER_STONE(),
        DRAGON_EGG(),
        REDSTONE_LAMP_OFF(),
        REDSTONE_LAMP_ON(),
        WOOD_DOUBLE_STEP(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        WOOD_STEP(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        COCOA(),
        SANDSTONE_STAIRS(),
        EMERALD_ORE(),
        ENDER_CHEST(new GlowMaterialBuilder()
                .setHardness(22.5f)
                .setBlastResistance(3000)),
        TRIPWIRE_HOOK(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        TRIPWIRE(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        EMERALD_BLOCK(),
        SPRUCE_WOOD_STAIRS(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        BIRCH_WOOD_STAIRS(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        JUNGLE_WOOD_STAIRS(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        COMMAND(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000000)),
        BEACON(),
        COBBLE_WALL(),
        FLOWER_POT(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        CARROT(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        POTATO(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        WOOD_BUTTON(),
        SKULL(),
        ANVIL(new GlowMaterialBuilder()
                .setBlastResistance(6000)),
        TRAPPED_CHEST(),
        GOLD_PLATE(),
        IRON_PLATE(),
        REDSTONE_COMPARATOR_OFF(new GlowMaterialBuilder()
                .setHardness(0)),
        REDSTONE_COMPARATOR_ON(new GlowMaterialBuilder()
                .setHardness(0)),
        DAYLIGHT_DETECTOR(),
        REDSTONE_BLOCK(),
        QUARTZ_ORE(),
        HOPPER(),
        QUARTZ_BLOCK(),
        QUARTZ_STAIRS(),
        ACTIVATOR_RAIL(),
        DROPPER(),
        STAINED_CLAY(),
        STAINED_GLASS_PANE(),
        LEAVES_2(new GlowMaterialBuilder()
                .setHardness(0.2f)
                .setLightReduction(0)
                .setFireResistance(30)
                .setFlammability(60)),
        LOG_2(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(5)),
        ACACIA_STAIRS(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        DARK_OAK_STAIRS(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        SLIME_BLOCK(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        BARRIER(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000003)),
        IRON_TRAPDOOR(),
        PRISMARINE(),
        SEA_LANTERN(),
        HAY_BLOCK(new GlowMaterialBuilder()
                .setFireResistance(60)
                .setFlammability(20)),
        CARPET(new GlowMaterialBuilder()
                .setLightReduction(0)
                .setFireResistance(60)
                .setFlammability(20)),
        HARD_CLAY(),
        COAL_BLOCK(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(5)),
        PACKED_ICE(),
        DOUBLE_PLANT(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)
                .setFireResistance(60)
                .setFlammability(100)),
        STANDING_BANNER(),
        WALL_BANNER(),
        DAYLIGHT_DETECTOR_INVERTED(),
        RED_SANDSTONE(),
        RED_SANDSTONE_STAIRS(),
        DOUBLE_STONE_SLAB2(),
        STONE_SLAB2(),
        SPRUCE_FENCE_GATE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        BIRCH_FENCE_GATE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        JUNGLE_FENCE_GATE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        DARK_OAK_FENCE_GATE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        ACACIA_FENCE_GATE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        SPRUCE_FENCE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        BIRCH_FENCE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        JUNGLE_FENCE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        DARK_OAK_FENCE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        ACACIA_FENCE(new GlowMaterialBuilder()
                .setFireResistance(5)
                .setFlammability(20)),
        SPRUCE_DOOR(),
        BIRCH_DOOR(),
        JUNGLE_DOOR(),
        ACACIA_DOOR(),
        DARK_OAK_DOOR(),
        END_ROD(new GlowMaterialBuilder()
                .setBlastResistance(0)),
        CHORUS_PLANT(),
        CHORUS_FLOWER(new GlowMaterialBuilder()
                .setRandomTicks(true)),
        PURPUR_BLOCK(),
        PURPUR_PILLAR(),
        PURPUR_STAIRS(),
        PURPUR_DOUBLE_SLAB(),
        PURPUR_SLAB(),
        END_BRICKS(),
        BEETROOT_BLOCK(),
        GRASS_PATH(),
        END_GATEWAY(new GlowMaterialBuilder()
                .setBlastResistance(18000000)),
        COMMAND_REPEATING(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000000)),
        COMMAND_CHAIN(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000000)),
        FROSTED_ICE(),
        MAGMA(),
        NETHER_WART_BLOCK(),
        RED_NETHER_BRICK(),
        BONE_BLOCK(),
        STRUCTURE_VOID(new GlowMaterialBuilder()
                .setHardness(0)
                .setBlastResistance(0)),
        STRUCTURE_BLOCK(new GlowMaterialBuilder()
                .setHardness(-1)
                .setBlastResistance(18000000)),
        ;

        /**
         * The hardness of a block, which affects the break time.
         * -1 for infinite hardness.
         */
        private final float hardness;
        /**
         * How much explosion power this block absorbs.
         */
        private final float blastResistance;
        /**
         * How much the block reduces the light level by.
         * The maximum light level is 15.
         */
        private final int lightReduction;
        /**
         * How long the block will burn for.
         */
        private final int fireResistance;
        /**
         * How quickly the block has fire spread to it.
         */
        private final int flammability;
        /**
         * Should this block be considered for random ticks?
         */
        private final boolean randomTicks;
        /**
         * How this block moves when pushed by a piston.
         */
        private final PistonMoveReaction pistonMoveReaction;

        private static class GlowMaterialBuilder {
            private float hardness = 1;
            private float blastResistance = 1;
            private int lightReduction = 15;
            private int flammability = -1;
            private int fireResistance = -1;
            private boolean randomTicks;
            private PistonMoveReaction pistonMoveReaction = PistonMoveReaction.MOVE;

            GlowMaterialBuilder() {
            }

            float getHardness() {
                return hardness;
            }

            GlowMaterialBuilder setHardness(float hardness) {
                this.hardness = hardness;
                return this;
            }

            float getBlastResistance() {
                return blastResistance;
            }

            GlowMaterialBuilder setBlastResistance(float blastResistance) {
                this.blastResistance = blastResistance;
                return this;
            }

            int getLightReduction() {
                return lightReduction;
            }

            GlowMaterialBuilder setLightReduction(int lightReduction) {
                this.lightReduction = lightReduction;
                return this;
            }

            int getFlammability() {
                return flammability;
            }

            GlowMaterialBuilder setFlammability(int flammability) {
                this.flammability = flammability;
                return this;
            }

            int getFireResistance() {
                return fireResistance;
            }

            GlowMaterialBuilder setFireResistance(int fireResistance) {
                this.fireResistance = fireResistance;
                return this;
            }

            boolean doRandomTicks() {
                return randomTicks;
            }

            public GlowMaterialBuilder setRandomTicks(boolean randomTicks) {
                this.randomTicks = randomTicks;
                return this;
            }

            public PistonMoveReaction getPistonMoveReaction() {
                return pistonMoveReaction;
            }

            public GlowMaterialBuilder setPistonMoveReaction(PistonMoveReaction pistonMoveReaction) {
                this.pistonMoveReaction = pistonMoveReaction;
                return this;
            }
        }

        GlowMaterial() {
            this(new GlowMaterialBuilder());
        }

        GlowMaterial(GlowMaterialBuilder builder) {
            this.hardness = builder.getHardness();
            this.blastResistance = builder.getBlastResistance();
            this.lightReduction = builder.getLightReduction();
            this.fireResistance = builder.getFireResistance();
            this.flammability = builder.getFlammability();
            this.randomTicks = builder.doRandomTicks();
            this.pistonMoveReaction = builder.getPistonMoveReaction();
        }

        public float getHardness() {
            return hardness;
        }

        public float getBlastResistance() {
            return blastResistance;
        }

        public int getLightReduction() {
            return lightReduction;
        }

        public int getFlammability() {
            return flammability;
        }

        public int getFireResistance() {
            return fireResistance;
        }

        public boolean doRandomTicks() {
            return randomTicks;
        }

        public PistonMoveReaction getPistonMoveReaction() {
            return pistonMoveReaction;
        }
    }

    public static GlowMaterial getValues(Material material) {
        try {
            return GlowMaterial.valueOf(material.name());
        } catch (IllegalArgumentException ignore) {
            return GlowMaterial.DEFAULT;
        }
    }
}
