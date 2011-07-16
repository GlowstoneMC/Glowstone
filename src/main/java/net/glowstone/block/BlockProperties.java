package net.glowstone.block;

import java.util.EnumMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * An enum containing an entry for every block describing that block's physical properties.
 */
public enum BlockProperties {
    
    AIR(passthru()),
    STONE(),
    GRASS(drops(Material.DIRT)),
    DIRT(),
    COBBLESTONE(),
    WOOD(),
    SAPLING(passthru()),
    BEDROCK(),
    WATER(passthru(), physics(), opaque(2)),
    STATIONARY_WATER(passthru(), physics(), opaque(2)),
    LAVA(passthru(), physics(), emitsLight(15)),
    STATIONARY_LAVA(passthru(), physics(), emitsLight(15)),
    SAND(physics()),
    GRAVEL(physics()),
    GOLD_ORE(),
    IRON_ORE(),
    COAL_ORE(drops(Material.COAL)),
    LOG(),
    LEAVES(physics()),
    SPONGE(),
    GLASS(drops()),
    LAPIS_ORE(drops(Material.INK_SACK)),    // todo: data drops
    LAPIS_BLOCK(),
    DISPENSER(interact(), place(), redstone()),
    SANDSTONE(),
    NOTE_BLOCK(interact(), redstone(), entity(GlowNoteBlock.class)),
    BED_BLOCK(interact()),                  // todo: height
    POWERED_RAIL(place(), redstone()),
    DETECTOR_RAIL(place(), redstone()),
    PISTON_STICKY_BASE(place(), redstone()),
    WEB(passthru()),
    LONG_GRASS(passthru(), drops()),
    DEAD_BUSH(passthru(), drops()),
    PISTON_BASE(place(), redstone()),
    PISTON_EXTENSION(redstone()),
    WOOL(),
    PISTON_MOVING_PIECE(redstone()),
    YELLOW_FLOWER(place(), passthru()),
    RED_ROSE(place(), passthru()),
    BROWN_MUSHROOM(place(), passthru()),
    RED_MUSHROOM(place(), passthru()),
    GOLD_BLOCK(),
    IRON_BLOCK(),
    DOUBLE_STEP(drops(new ItemStack(Material.STEP, 2))),
    STEP(place(), passthru()),                       // todo: height
    BRICK(),
    TNT(redstone()),
    BOOKSHELF(),
    MOSSY_COBBLESTONE(),
    OBSIDIAN(),
    TORCH(place(), passthru(), emitsLight(14)),
    FIRE(passthru(), emitsLight(15)),
    MOB_SPAWNER(),
    WOOD_STAIRS(place(), passthru()),
    CHEST(interact()),
    REDSTONE_WIRE(redstone()),
    DIAMOND_ORE(drops(Material.DIAMOND)),
    DIAMOND_BLOCK(),
    WORKBENCH(interact()),
    CROPS(passthru()),
    SOIL(),
    FURNACE(interact(), place()),
    BURNING_FURNACE(interact(), place()),
    SIGN_POST(passthru()),
    WOODEN_DOOR(passthru(), interact(), place()),
    LADDER(place(), passthru()),
    RAILS(place()),
    COBBLESTONE_STAIRS(place(), passthru()),
    WALL_SIGN(passthru()),
    LEVER(place(), interact(), passthru(), redstone()),
    STONE_PLATE(place(), passthru(), redstone()),
    IRON_DOOR_BLOCK(place(), passthru()),
    WOOD_PLATE(place(), passthru(), redstone()),
    REDSTONE_ORE(interact()),
    GLOWING_REDSTONE_ORE(interact(), physics()),
    REDSTONE_TORCH_OFF(passthru(), redstone()),
    REDSTONE_TORCH_ON(passthru(), redstone()),
    STONE_BUTTON(passthru(), interact(), redstone()),
    SNOW(passthru()),
    ICE(opaque(2)),
    SNOW_BLOCK(),
    CACTUS(place(), physics()),
    CLAY(drops(new ItemStack(Material.CLAY_BALL, 4))),
    SUGAR_CANE_BLOCK(place(), drops(Material.SUGAR_CANE)),
    JUKEBOX(interact()),
    FENCE(place(), opaque(0)),
    PUMPKIN(place()),
    NETHERRACK(),
    SOUL_SAND(),
    GLOWSTONE(drops(new ItemStack(Material.GLOWSTONE_DUST, 4))),
    PORTAL(place(), physics()),
    JACK_O_LANTERN(place()),
    CAKE_BLOCK(passthru()),
    DIODE_BLOCK_OFF(passthru(), redstone(), interact()),
    DIODE_BLOCK_ON(passthru(), redstone(), interact()),
    LOCKED_CHEST(),
    TRAP_DOOR(redstone(), interact());
    
    // -----------------
    
    private static EnumMap<Material, BlockProperties> materialMap = new EnumMap<Material, BlockProperties>(Material.class);
    
    static {
        for (BlockProperties prop : values()) {
            materialMap.put(prop.material, prop);
        }
    }
    
    public static BlockProperties get(Material material) {
        return materialMap.get(material);
    }
    
    public static BlockProperties get(int id) {
        return materialMap.get(Material.getMaterial(id));
    }
    
    // -----------------
    
    private final Material material;
    private ItemStack[] drops;
    private Class<? extends GlowBlockState> entity;
    
    private boolean physics = false;
    private boolean redstone = false;
    private boolean interact = false;
    private boolean place = false;
    private boolean solid = true;
    private int emitsLight = 0;
    private int blocksLight = 15;
    
    private BlockProperties(Property... props) {
        material = Material.getMaterial(toString());
        drops = new ItemStack[] { new ItemStack(material, 1) };
        
        for (Property p : props) {
            p.apply(this);
        }
    }
    
    public ItemStack[] getDrops() {
        return drops;
    }
    
    public Class<? extends GlowBlockState> getEntityClass() {
        return entity;
    }
    
    public boolean hasPhysics() {
        return physics;
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
    
    private static Property physics() {
        return new Property() { public void apply(BlockProperties p) {
            p.physics = true;
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
