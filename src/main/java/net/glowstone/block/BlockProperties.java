package net.glowstone.block;

import java.util.Arrays;

import net.glowstone.block.physics.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * An enum containing an entry for every block describing that block's physical properties.
 */
public enum BlockProperties {
    
    AIR(BlockID.AIR, passthru()),
    STONE(BlockID.STONE, drops(BlockID.COBBLESTONE)),
    GRASS(BlockID.GRASS, drops(BlockID.DIRT)),
    DIRT(BlockID.DIRT),
    COBBLESTONE(BlockID.COBBLESTONE),
    WOOD(BlockID.WOOD),
    SAPLING(BlockID.SAPLING, passthru()),
    BEDROCK(BlockID.BEDROCK),
    WATER(BlockID.WATER, passthru(), /*physics(),*/ opaque(2)),
    STATIONARY_WATER(BlockID.STATIONARY_WATER, passthru(), /*physics(),*/ opaque(2)),
    LAVA(BlockID.LAVA, passthru(), /*physics(),*/ emitsLight(15)),
    STATIONARY_LAVA(BlockID.STATIONARY_LAVA, passthru(), /*physics(),*/ emitsLight(15)),
    SAND(BlockID.SAND /* , physics(new FallingBlockPhysics(BlockID.SAND))*/),
    GRAVEL(BlockID.GRAVEL/*, physics(new FallingBlockPhysics(BlockID.GRAVEL))*/),
    GOLD_ORE(BlockID.GOLD_ORE),
    IRON_ORE(BlockID.IRON_ORE),
    COAL_ORE(BlockID.COAL_ORE, drops(ItemID.COAL)),
    LOG(BlockID.LOG),
    LEAVES(BlockID.LEAVES/*, physics()*/), // TODO: 'Ticking' block physics
    SPONGE(BlockID.SPONGE),
    GLASS(BlockID.GLASS, drops()),
    LAPIS_ORE(BlockID.LAPIS_ORE, drops(ItemID.INK_SACK, 11)),    // todo: data drops
    LAPIS_BLOCK(BlockID.LAPIS_BLOCK),
    DISPENSER(BlockID.DISPENSER, interact(), place(), redstone()),
    SANDSTONE(BlockID.SANDSTONE),
    NOTE_BLOCK(BlockID.NOTE_BLOCK, interact(), redstone(), entity(GlowNoteBlock.class)),
    BED_BLOCK(BlockID.BED_BLOCK, interact()),                  // todo: height
    POWERED_RAIL(BlockID.POWERED_RAIL, place(), redstone()),
    DETECTOR_RAIL(BlockID.DETECTOR_RAIL, place(), redstone()),
    PISTON_STICKY_BASE(BlockID.PISTON_STICKY_BASE, place(), redstone()),
    WEB(BlockID.WEB, passthru()),
    LONG_GRASS(BlockID.LONG_GRASS, passthru(), drops()),
    DEAD_BUSH(BlockID.DEAD_BUSH, passthru(), drops()),
    PISTON_BASE(BlockID.PISTON_BASE, place(), redstone()),
    PISTON_EXTENSION(BlockID.PISTON_EXTENSION, redstone()),
    WOOL(BlockID.WOOL),
    PISTON_MOVING_PIECE(BlockID.PISTON_MOVING_PIECE, redstone()),
    YELLOW_FLOWER(BlockID.YELLOW_FLOWER, place(), passthru()),
    RED_ROSE(BlockID.RED_ROSE, place(), passthru()),
    BROWN_MUSHROOM(BlockID.BROWN_MUSHROOM, place(), passthru()),
    RED_MUSHROOM(BlockID.RED_MUSHROOM, place(), passthru()),
    GOLD_BLOCK(BlockID.GOLD_BLOCK),
    IRON_BLOCK(BlockID.IRON_BLOCK),
    DOUBLE_STEP(BlockID.DOUBLE_STEP, drops(new ItemStack(BlockID.STEP, 2))),
    STEP(BlockID.STEP, passthru(), physics(new DoubleStepPhysics())),                       // todo: height
    BRICK(BlockID.BRICK),
    TNT(BlockID.TNT, redstone()),
    BOOKSHELF(BlockID.BOOKSHELF),
    MOSSY_COBBLESTONE(BlockID.MOSSY_COBBLESTONE),
    OBSIDIAN(BlockID.OBSIDIAN),
    TORCH(BlockID.TORCH, place(), passthru(), emitsLight(14)),
    FIRE(BlockID.FIRE, passthru(), emitsLight(15), drops()),
    MOB_SPAWNER(BlockID.MOB_SPAWNER, entity(GlowCreatureSpawner.class)),
    WOOD_STAIRS(BlockID.WOOD_STAIRS, physics(new StairPhysics()), drops(BlockID.WOOD)),
    CHEST(BlockID.CHEST, interact()),
    REDSTONE_WIRE(BlockID.REDSTONE_WIRE, redstone()),
    DIAMOND_ORE(BlockID.DIAMOND_ORE, drops(ItemID.DIAMOND)),
    DIAMOND_BLOCK(BlockID.DIAMOND_BLOCK),
    WORKBENCH(BlockID.WORKBENCH, interact()),
    CROPS(BlockID.CROPS, passthru()),
    SOIL(BlockID.SOIL, drops(BlockID.DIRT)),
    FURNACE(BlockID.FURNACE, interact(), place()),
    BURNING_FURNACE(BlockID.BURNING_FURNACE, interact(), place()),
    SIGN_POST(BlockID.SIGN_POST, passthru(), entity(GlowSign.class), drops(ItemID.SIGN)),
    WOODEN_DOOR(BlockID.WOODEN_DOOR, passthru(), interact(), place(), drops(ItemID.WOOD_DOOR)),
    LADDER(BlockID.LADDER, place(), passthru()),
    RAILS(BlockID.RAILS, place()),
    COBBLESTONE_STAIRS(BlockID.COBBLESTONE_STAIRS, passthru(), drops(BlockID.COBBLESTONE), physics(new StairPhysics())),
    WALL_SIGN(BlockID.WALL_SIGN, passthru(), entity(GlowSign.class), drops(ItemID.SIGN)),
    LEVER(BlockID.LEVER, place(), interact(), passthru(), redstone()),
    STONE_PLATE(BlockID.STONE_PLATE, place(), passthru(), redstone()),
    IRON_DOOR_BLOCK(BlockID.IRON_DOOR_BLOCK, place(), passthru()),
    WOOD_PLATE(BlockID.WOOD_PLATE, place(), passthru(), redstone()),
    REDSTONE_ORE(BlockID.REDSTONE_ORE, interact()),
    GLOWING_REDSTONE_ORE(BlockID.GLOWING_REDSTONE_ORE, interact()/*, physics()*/),
    REDSTONE_TORCH_OFF(BlockID.REDSTONE_TORCH_OFF, passthru(), redstone()),
    REDSTONE_TORCH_ON(BlockID.REDSTONE_TORCH_ON, passthru(), redstone()),
    STONE_BUTTON(BlockID.STONE_BUTTON, passthru(), interact(), redstone()),
    SNOW(BlockID.SNOW, passthru()),
    ICE(BlockID.ICE, opaque(2)),
    SNOW_BLOCK(BlockID.SNOW_BLOCK),
    CACTUS(BlockID.CACTUS, place(), physics(new SpecialPlaceBelowPhysics(BlockID.CACTUS, BlockID.SAND))),
    CLAY(BlockID.CLAY, drops(new ItemStack(ItemID.CLAY_BALL, 4))),
    SUGAR_CANE_BLOCK(BlockID.SUGAR_CANE_BLOCK, place(), drops(ItemID.SUGAR_CANE)),
    JUKEBOX(BlockID.JUKEBOX, interact()),
    FENCE(BlockID.FENCE, place(), opaque(0)),
    PUMPKIN(BlockID.PUMPKIN, place()),
    NETHERRACK(BlockID.NETHERRACK),
    SOUL_SAND(BlockID.SOUL_SAND),
    GLOWSTONE(BlockID.GLOWSTONE, drops(new ItemStack(ItemID.GLOWSTONE_DUST, 4))),
    PORTAL(BlockID.PORTAL, place()/*, physics()*/),
    JACK_O_LANTERN(BlockID.JACK_O_LANTERN, place()),
    CAKE_BLOCK(BlockID.CAKE_BLOCK, passthru()),
    DIODE_BLOCK_OFF(BlockID.DIODE_BLOCK_OFF, passthru(), redstone(), interact()),
    DIODE_BLOCK_ON(BlockID.DIODE_BLOCK_ON, passthru(), redstone(), interact()),
    LOCKED_CHEST(BlockID.LOCKED_CHEST),
    TRAP_DOOR(BlockID.TRAP_DOOR, redstone(), interact()),
    SILVERFISH_BLOCK(BlockID.SILVERFISH_BLOCK, interact()),
    SMOOTH_BRICK(BlockID.SMOOTH_BRICK),
    HUGE_MUSHROOM_BROWN(BlockID.HUGE_MUSHROOM_BROWN, drops(BlockID.BROWN_MUSHROOM)),
    HUGE_MUSHROOM_RED(BlockID.HUGE_MUSHROOM_RED, drops(BlockID.RED_MUSHROOM)),
    IRON_BARS(BlockID.IRON_BARS),
    GLASS_PANE(BlockID.GLASS_PANE),
    MELON_BLOCK(BlockID.MELON_BLOCK),
    PUMPKIN_STEM(BlockID.PUMPKIN_STEM, drops()),
    MELON_STEM(BlockID.MELON_STEM, drops()),
    VINE(BlockID.VINE),
    FENCE_GATE(BlockID.FENCE_GATE, interact()),
    BRICK_STAIRS(BlockID.BRICK_STAIRS, physics(new StairPhysics()), drops(BlockID.BRICK)),
    SMOOTH_STAIRS(BlockID.SMOOTH_STAIRS, physics(new StairPhysics()), drops(BlockID.SMOOTH_BRICK));
    
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
    
    private BlockProperties(int id, Property... props) {
        this.id = id;
        drops = new ItemStack[] { new ItemStack(id, 1) };
        
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
    
    private static Property drops(final int mat) {
        return new Property() { public void apply(BlockProperties p) {
            p.drops = new ItemStack[] { new ItemStack(mat, 1) };
        }};
    }

    private static Property drops(final int mat, final int damage) {
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
