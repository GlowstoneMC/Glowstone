package net.glowstone.generator.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.glowstone.GlowServer;
import net.glowstone.generator.objects.RandomItemsContent;
import net.glowstone.generator.objects.RandomItemsContent.RandomAmountItem;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.generator.structures.util.StructureBuilder;
import net.glowstone.generator.structures.util.StructureBuilder.StructureMaterial;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Chest;
import org.bukkit.util.Vector;

public class GlowDungeon extends GlowStructurePiece {

    private static final int HEIGHT = 6;
    private static final int MIN_RADIUS = 3;
    private final int radiusX;
    private final int radiusZ;
    private final int sizeX;
    private final int sizeZ;
    private final EntityType[] mobTypes = new EntityType[]{
        EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
    private final Location loc;

    /**
     * Creates an instance with a random size.
     *
     * @param random the PRNG that will generate the size
     * @param location the location to generate in
     */
    public GlowDungeon(Random random, Location location) {
        super(location, new Vector(9, HEIGHT, 9));
        // inner dungeon shape is 5x5, 5x7 or 7x7
        radiusX = random.nextInt(2) + MIN_RADIUS;
        radiusZ = random.nextInt(2) + MIN_RADIUS;
        sizeX = (radiusX << 1) + 1;
        sizeZ = (radiusZ << 1) + 1;
        loc = location;
    }

    /**
     * Returns whether a given {@link StructureBuilder} can place this dungeon in its chosen
     * location.
     *
     * @param builder a StructureBuilder for this and for the world and {@link BlockStateDelegate}
     *         that would be used to generate this
     * @return true if this dungeon can be placed; false otherwise
     */
    public boolean canPlace(StructureBuilder builder) {
        if (boundingBox.getMin().getBlockY() < 1) {
            return false;
        }

        int i = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int y = 0; y < HEIGHT; y++) {
                    Material type = builder.getBlockState(new Vector(x, y, z)).getType();
                    // checks we are between 2 solid material layers
                    if ((y == 0 || y == HEIGHT - 1) && !type.isSolid()) {
                        return false;
                    }
                    // checks a few blocks at bottom of walls are opened to air
                    // in order to have a natural door like access
                    if ((x == 0 || x == sizeX - 1 || z == 0 || z == sizeZ - 1)
                            && y == 1 && type == Material.AIR
                            && builder.getBlockState(new Vector(x, y + 1, z)).getType()
                            == Material.AIR) {
                        i++;
                        // TODO change min to 1 when caves will be generated ! this will be required
                        // so that dungeons are minimally exposed to air
                        if (i < 0 || i > 5) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean generate(World world, Random random, StructureBoundingBox genBoundingBox,
            BlockStateDelegate delegate) {
        if (!super.generate(world, random, boundingBox, delegate)) {
            return false;
        }

        boundingBox.offset(new Vector(-radiusX, -1, -radiusZ));

        StructureBuilder builder = new StructureBuilder(world, this, genBoundingBox, delegate);

        if (!canPlace(builder)) {
            return false;
        }

        Map<StructureMaterial, Integer> stones = new HashMap<>();
        builder.addRandomMaterial(stones, 1, Material.COBBLESTONE, 0);
        builder.addRandomMaterial(stones, 3, Material.MOSSY_COBBLESTONE, 0);

        for (int x = 0; x < sizeX; x++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int y = HEIGHT - 1; y >= 0; y--) {
                    BlockState state = builder.getBlockState(new Vector(x, y, z));
                    if (y > 0 && x > 0 && z > 0 && x < sizeX - 1 && y < HEIGHT - 1
                            && z < sizeZ - 1) {
                        // empty space inside
                        builder.setBlock(new Vector(x, y, z), Material.AIR);
                    } else if (!builder.getBlockState(new Vector(x, y - 1, z)).getType()
                            .isSolid()) {
                        // cleaning walls from non solid materials (because of air gaps below)
                        builder.setBlock(new Vector(x, y, z), Material.AIR);
                    } else if (state.getType().isSolid()) {
                        // for walls we only replace solid material in order to
                        // preserve the air gaps
                        if (y == 0) {
                            builder.setBlockWithRandomMaterial(new Vector(x, y, z), random, stones);
                        } else {
                            builder.setBlock(new Vector(x, y, z), Material.COBBLESTONE);
                        }
                    }
                }
            }
        }

        RandomItemsContent chestContent = new RandomItemsContent();
        chestContent.addItem(new RandomAmountItem(Material.SADDLE, 1, 1), 10);
        chestContent.addItem(new RandomAmountItem(Material.IRON_INGOT, 1, 4), 10);
        chestContent.addItem(new RandomAmountItem(Material.BREAD, 1, 1), 10);
        chestContent.addItem(new RandomAmountItem(Material.WHEAT, 1, 4), 10);
        chestContent.addItem(new RandomAmountItem(Material.GUNPOWDER, 1, 4), 10);
        chestContent.addItem(new RandomAmountItem(Material.STRING, 1, 4), 10);
        chestContent.addItem(new RandomAmountItem(Material.BUCKET, 1, 1), 10);
        chestContent.addItem(new RandomAmountItem(Material.GOLDEN_APPLE, 1, 1), 1);
        chestContent.addItem(new RandomAmountItem(Material.REDSTONE, 1, 4), 10);
        chestContent.addItem(new RandomAmountItem(Material.MUSIC_DISC_13, 1, 1), 4);
        chestContent.addItem(new RandomAmountItem(Material.MUSIC_DISC_CAT, 1, 1), 4);
        chestContent.addItem(new RandomAmountItem(Material.NAME_TAG, 1, 1), 10);
        chestContent.addItem(new RandomAmountItem(Material.GOLDEN_HORSE_ARMOR, 1, 1), 2);
        chestContent.addItem(new RandomAmountItem(Material.IRON_HORSE_ARMOR, 1, 1), 5);
        chestContent.addItem(new RandomAmountItem(Material.DIAMOND_HORSE_ARMOR, 1, 1), 1);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                int x = random.nextInt((radiusX << 1) - 1) + 1;
                int z = random.nextInt((radiusZ << 1) - 1) + 1;
                if (builder.getBlockState(new Vector(x, 1, z)).getType() == Material.AIR) {
                    BlockFace face = null;
                    int solidBlocksCount = 0;
                    if (builder.getBlockState(new Vector(x - 1, 1, z)).getType()
                            == Material.COBBLESTONE) {
                        solidBlocksCount++;
                        face = BlockFace.EAST;
                    }
                    if (builder.getBlockState(new Vector(x + 1, 1, z)).getType()
                            == Material.COBBLESTONE) {
                        solidBlocksCount++;
                        face = BlockFace.WEST;
                    }
                    if (builder.getBlockState(new Vector(x, 1, z - 1)).getType()
                            == Material.COBBLESTONE) {
                        solidBlocksCount++;
                        face = BlockFace.SOUTH;
                    }
                    if (builder.getBlockState(new Vector(x, 1, z + 1)).getType()
                            == Material.COBBLESTONE) {
                        solidBlocksCount++;
                        face = BlockFace.NORTH;
                    }
                    if (solidBlocksCount == 1) {
                        builder
                                .createRandomItemsContainer(new Vector(x, 1, z), random,
                                        chestContent,
                                        new Chest(face), 8);
                        break;
                    }
                }
            }
        }

        builder.createMobSpawner(new Vector(radiusX, 1, radiusZ),
                mobTypes[random.nextInt(mobTypes.length)]);

        GlowServer.logger.finer(
                "dungeon generated: " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc
                        .getBlockZ());

        return true;
    }
}
