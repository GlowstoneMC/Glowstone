package net.glowstone.block;

import net.glowstone.block.physics.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * An enum containing an entry for every block describing that block's physical properties.
 */
public enum BlockProperties {
    
    AIR(Material.AIR, passthru()),
    STONE(Material.STONE, drops(Material.COBBLESTONE)),
    GRASS(Material.GRASS, drops(Material.DIRT)),
    DIRT(Material.DIRT),
    COBBLESTONE(Material.COBBLESTONE),
    WOOD(Material.WOOD),
    SAPLING(Material.SAPLING, passthru()),
    BEDROCK(Material.BEDROCK),
    WATER(Material.WATER, passthru(), /*physics(),*/ opaque(2)),
    STATIONARY_WATER(Material.STATIONARY_WATER, passthru(), /*physics(),*/ opaque(2)),
    LAVA(Material.LAVA, passthru(), /*physics(),*/ emitsLight(15)),
    STATIONARY_LAVA(Material.STATIONARY_LAVA, passthru(), /*physics(),*/ emitsLight(15)),
    SAND(Material.SAND /* , physics(new FallingBlockPhysics(Material.SAND))*/),
    GRAVEL(Material.GRAVEL/*, physics(new FallingBlockPhysics(Material.GRAVEL))*/),
    GOLD_ORE(Material.GOLD_ORE),
    IRON_ORE(Material.IRON_ORE),
    COAL_ORE(Material.COAL_ORE, drops(Material.COAL)),
    LOG(Material.LOG),
    LEAVES(Material.LEAVES/*, physics()*/), // TODO: 'Ticking' block physics
    SPONGE(Material.SPONGE),
    GLASS(Material.GLASS, drops()),
    LAPIS_ORE(Material.LAPIS_ORE, drops(Material.INK_SACK, 11)),    // todo: data drops
    LAPIS_BLOCK(Material.LAPIS_BLOCK),
    DISPENSER(Material.DISPENSER, interact(), place(), redstone()),
    SANDSTONE(Material.SANDSTONE),
    NOTE_BLOCK(Material.NOTE_BLOCK, interact(), redstone(), entity(GlowNoteBlock.class)),
    BED_BLOCK(Material.BED_BLOCK, interact()),                  // todo: height
    POWERED_RAIL(Material.POWERED_RAIL, place(), redstone()),
    DETECTOR_RAIL(Material.DETECTOR_RAIL, place(), redstone()),
    PISTON_STICKY_BASE(Material.PISTON_STICKY_BASE, place(), redstone()),
    WEB(Material.WEB, passthru()),
    LONG_GRASS(Material.LONG_GRASS, passthru(), drops()),
    DEAD_BUSH(Material.DEAD_BUSH, passthru(), drops()),
    PISTON_BASE(Material.PISTON_BASE, place(), redstone()),
    PISTON_EXTENSION(Material.PISTON_EXTENSION, redstone()),
    WOOL(Material.WOOL),
    PISTON_MOVING_PIECE(Material.PISTON_MOVING_PIECE, redstone()),
    YELLOW_FLOWER(Material.YELLOW_FLOWER, place(), passthru()),
    RED_ROSE(Material.RED_ROSE, place(), passthru()),
    BROWN_MUSHROOM(Material.BROWN_MUSHROOM, place(), passthru()),
    RED_MUSHROOM(Material.RED_MUSHROOM, place(), passthru()),
    GOLD_BLOCK(Material.GOLD_BLOCK),
    IRON_BLOCK(Material.IRON_BLOCK),
    DOUBLE_STEP(Material.DOUBLE_STEP, drops(new ItemStack(Material.STEP, 2))),
    STEP(Material.STEP, passthru(), physics(new DoubleStepPhysics())),                       // todo: height
    BRICK(Material.BRICK),
    TNT(Material.TNT, redstone()),
    BOOKSHELF(Material.BOOKSHELF),
    MOSSY_COBBLESTONE(Material.MOSSY_COBBLESTONE),
    OBSIDIAN(Material.OBSIDIAN),
    TORCH(Material.TORCH, place(), passthru(), emitsLight(14)),
    FIRE(Material.FIRE, passthru(), emitsLight(15), drops()),
    MOB_SPAWNER(Material.MOB_SPAWNER, entity(GlowCreatureSpawner.class)),
    WOOD_STAIRS(Material.WOOD_STAIRS, physics(new StairPhysics()), drops(Material.WOOD)),
    CHEST(Material.CHEST, interact()),
    REDSTONE_WIRE(Material.REDSTONE_WIRE, redstone()),
    DIAMOND_ORE(Material.DIAMOND_ORE, drops(Material.DIAMOND)),
    DIAMOND_BLOCK(Material.DIAMOND_BLOCK),
    WORKBENCH(Material.WORKBENCH, interact()),
    CROPS(Material.CROPS, passthru()),
    SOIL(Material.SOIL, drops(Material.DIRT)),
    FURNACE(Material.FURNACE, interact(), place()),
    BURNING_FURNACE(Material.BURNING_FURNACE, interact(), place()),
    SIGN_POST(Material.SIGN_POST, passthru(), entity(GlowSign.class), drops(Material.SIGN)),
    WOODEN_DOOR(Material.WOODEN_DOOR, passthru(), interact(), place(), drops(Material.WOOD_DOOR)),
    LADDER(Material.LADDER, place(), passthru()),
    RAILS(Material.RAILS, place()),
    COBBLESTONE_STAIRS(Material.COBBLESTONE_STAIRS, passthru(), drops(Material.COBBLESTONE), physics(new StairPhysics())),
    WALL_SIGN(Material.WALL_SIGN, passthru(), entity(GlowSign.class), drops(Material.SIGN)),
    LEVER(Material.LEVER, place(), interact(), passthru(), redstone()),
    STONE_PLATE(Material.STONE_PLATE, place(), passthru(), redstone()),
    IRON_DOOR_BLOCK(Material.IRON_DOOR_BLOCK, place(), passthru()),
    WOOD_PLATE(Material.WOOD_PLATE, place(), passthru(), redstone()),
    REDSTONE_ORE(Material.REDSTONE_ORE, interact()),
    GLOWING_REDSTONE_ORE(Material.GLOWING_REDSTONE_ORE, interact()/*, physics()*/),
    REDSTONE_TORCH_OFF(Material.REDSTONE_TORCH_OFF, passthru(), redstone()),
    REDSTONE_TORCH_ON(Material.REDSTONE_TORCH_ON, passthru(), redstone()),
    STONE_BUTTON(Material.STONE_BUTTON, passthru(), interact(), redstone()),
    SNOW(Material.SNOW, passthru()),
    ICE(Material.ICE, opaque(2)),
    SNOW_BLOCK(Material.SNOW_BLOCK),
    CACTUS(Material.CACTUS, place(), physics(new SpecialPlaceBelowPhysics(Material.CACTUS.getId(), Material.SAND.getId()))),
    CLAY(Material.CLAY, drops(new ItemStack(Material.CLAY_BALL, 4))),
    SUGAR_CANE_BLOCK(Material.SUGAR_CANE_BLOCK, place(), drops(Material.SUGAR_CANE)),
    JUKEBOX(Material.JUKEBOX, interact()),
    FENCE(Material.FENCE, place(), opaque(0)),
    PUMPKIN(Material.PUMPKIN, place()),
    NETHERRACK(Material.NETHERRACK),
    SOUL_SAND(Material.SOUL_SAND),
    GLOWSTONE(Material.GLOWSTONE, drops(new ItemStack(Material.GLOWSTONE_DUST, 4))),
    PORTAL(Material.PORTAL, place()/*, physics()*/),
    JACK_O_LANTERN(Material.JACK_O_LANTERN, place()),
    CAKE_BLOCK(Material.CAKE_BLOCK, passthru()),
    DIODE_BLOCK_OFF(Material.DIODE_BLOCK_OFF, passthru(), redstone(), interact()),
    DIODE_BLOCK_ON(Material.DIODE_BLOCK_ON, passthru(), redstone(), interact()),
    LOCKED_CHEST(Material.LOCKED_CHEST),
    TRAP_DOOR(Material.TRAP_DOOR, redstone(), interact()),
    SILVERFISH_BLOCK(Material.MONSTER_EGG, interact()),
    SMOOTH_BRICK(Material.SMOOTH_BRICK),
    HUGE_MUSHROOM_BROWN(Material.HUGE_MUSHROOM_1, drops(Material.BROWN_MUSHROOM)),
    HUGE_MUSHROOM_RED(Material.HUGE_MUSHROOM_2, drops(Material.RED_MUSHROOM)),
    IRON_BARS(Material.IRON_FENCE),
    GLASS_PANE(Material.THIN_GLASS),
    MELON_BLOCK(Material.MELON_BLOCK),
    PUMPKIN_STEM(Material.PUMPKIN_STEM, drops(), passthru()),
    MELON_STEM(Material.MELON_STEM, drops(), passthru()),
    VINE(Material.VINE, passthru()),
    FENCE_GATE(Material.FENCE_GATE, interact()),
    BRICK_STAIRS(Material.BRICK_STAIRS, physics(new StairPhysics()), drops(Material.BRICK)),
    SMOOTH_STAIRS(Material.SMOOTH_STAIRS, physics(new StairPhysics()), drops(Material.SMOOTH_BRICK)),
    MYCELIUM(Material.MYCEL, drops(Material.DIRT)),
    LILY_PAD(Material.WATER_LILY, passthru()),
    NETHER_BRICK(Material.NETHER_BRICK),
    NETHER_BRICK_FENCE(Material.NETHER_FENCE, drops(Material.NETHER_BRICK)),
    NETHER_BRICK_STAIRS(Material.NETHER_BRICK_STAIRS, physics(new StairPhysics())),
    NETHER_WART(Material.NETHER_WARTS, passthru(), drops(Material.NETHER_STALK)),
    ENCHANTMENT_TABLE(Material.ENCHANTMENT_TABLE),
    BREWING_STAND(Material.BREWING_STAND, drops(Material.BREWING_STAND)),
    CAULDRON(Material.CAULDRON, drops(Material.CAULDRON)),
    END_PORTAL(Material.ENDER_PORTAL, passthru()),
    END_PORTAL_FRAME(Material.ENDER_PORTAL_FRAME),
    END_STONE(Material.ENDER_STONE),
    DRAGON_EGG(Material.DRAGON_EGG);
    
    // -----------------

    private static BlockProperties[] byId = new BlockProperties[256];
    
    static {
        for (BlockProperties prop : values()) {
            if (byId.length > prop.id) {
                byId[prop.id] = prop;
            } else {
                byId = Arrays.copyOf(byId, prop.id + 2);
                byId[prop.id] = prop;
            }
        }
    }
    
    public static BlockProperties get(Material material) {
        return get(material.getId());
    }
    
    public static BlockProperties get(int id) {
        if (byId.length > id) {
            return byId[id];
        } else {
            return null;
        }
    }
    
    // -----------------

    private ItemStack[] drops;
    private Class<? extends GlowBlockState> entity;
    private BlockPhysicsHandler physics = new DefaultBlockPhysics();
    private boolean redstone = false;
    private boolean interact = false;
    private boolean place = false;
    private boolean solid = true;
    private int emitsLight = 0;
    private int blocksLight = 15;
    private final int id;
    
    private BlockProperties(Material mat, Property... props) {
        id = mat.getId();
        drops = new ItemStack[] { new ItemStack(mat, 1) };
        
        for (Property p : props) {
            p.apply(this);
        }
    }
    
    public ItemStack[] getDrops() {
        return drops;
    }

    public ItemStack[] getDrops(short damage) {
        ItemStack[] drops = this.drops.clone();
        for (ItemStack stack : drops) {
            if (stack.getDurability() == -1) {
                stack.setDurability(damage);
            }
        }
        return drops;
    }
    
    public Class<? extends GlowBlockState> getEntityClass() {
        return entity;
    }
    
    public boolean hasRedstone() {
        return redstone;
    }
    
    public boolean isInteractable() {
        return interact;
    }

    public boolean specialPlaceable() {
        return place;
    }
    
    public boolean isSolid() {
        return solid;
    }
    
    public int emittedLightLevel() {
        return emitsLight;
    }
    
    public int blockedLightLevel() {
        return blocksLight;
    }

    public int getId() {
        return id;
    }

    public BlockPhysicsHandler getPhysics() {
        return physics;
    }
    
    // -----------------
    
    private interface Property {
        void apply(BlockProperties prop);
    }
    
    private static Property drops(final ItemStack... mats) {
        return new Property() { public void apply(BlockProperties p) {
            p.drops = mats;
        }};
    }
    
    private static Property drops(final Material mat) {
        return new Property() { public void apply(BlockProperties p) {
            p.drops = new ItemStack[] { new ItemStack(mat, 1) };
        }};
    }

    private static Property drops(final Material mat, final int damage) {
        return new Property() { public void apply(BlockProperties p) {
            p.drops = new ItemStack[] { new ItemStack(mat, 1, (short)damage) };
        }};
    }
    
    private static Property passthru() {
        return new Property() { public void apply(BlockProperties p) {
            p.solid = false;
            p.blocksLight = 0;
        }};
    }
    
    private static Property opaque(final int level) {
        return new Property() { public void apply(BlockProperties p) {
            p.blocksLight = level;
        }};
    }
    
    private static Property emitsLight(final int level) {
        return new Property() { public void apply(BlockProperties p) {
            p.emitsLight = level;
        }};
    }
    
    private static Property physics(final BlockPhysicsHandler physics) {
        return new Property() { public void apply(BlockProperties p) {
            p.physics = physics;
        }};
    }
    
    private static Property redstone() {
        return new Property() { public void apply(BlockProperties p) {
            p.redstone = true;
        }};
    }
    
    private static Property interact() {
        return new Property() { public void apply(BlockProperties p) {
            p.interact = true;
        }};
    }

    private static Property place() {
        return new Property() { public void apply(BlockProperties p) {
            p.place = true;
        }};
    }
    
    private static Property entity(final Class<? extends GlowBlockState> clazz) {
        return new Property() { public void apply(BlockProperties p) {
            p.entity = clazz;
        }};
    }
    
}
