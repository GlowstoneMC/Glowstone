package net.glowstone.generator.structures.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.generator.objects.RandomItemsContent;
import net.glowstone.generator.structures.GlowStructurePiece;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class StructureBuilder {

    /**
     * The world this structure is built in.
     */
    private final World world;
    /**
     * The structure piece whose coordinate origin and orientation we're using.
     */
    private final GlowStructurePiece structure;
    /**
     * The bounding box in which to operate; not necessarily the same as the structure piece's
     * bounding box.
     */
    private final StructureBoundingBox boundingBox;
    /**
     * The BlockStateDelegate used to read and write blocks.
     */
    private final BlockStateDelegate delegate;

    public void addRandomMaterial(Map<StructureMaterial, Integer> materials, int weight,
            Material type, int data) {
        materials.put(new StructureMaterial(type, data), weight);
    }

    /**
     * Chooses a random {@link StructureMaterial} from a weighted list.
     *
     * @param random the PRNG to use
     * @param materials a map of materials to integer weights
     * @return a random material
     */
    public StructureMaterial getRandomMaterial(Random random,
            Map<StructureMaterial, Integer> materials) {
        int totalWeight = 0;
        for (int weight : materials.values()) {
            totalWeight += weight;
        }
        int weight = random.nextInt(totalWeight);
        for (Entry<StructureMaterial, Integer> entry : materials.entrySet()) {
            weight -= entry.getValue();
            if (weight < 0) {
                return entry.getKey();
            }
        }
        return new StructureMaterial(Material.AIR);
    }

    public BlockState getBlockState(Vector pos) {
        Vector vec = translate(pos);
        return delegate.getBlockState(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    /**
     * Sets the block at a given point, if it is inside this structure's bounding box.
     *
     * @param pos a point relative to this structure's root point
     * @param type the new block type
     */
    public void setBlock(Vector pos, Material type) {
        setBlock(pos, type, 0);
    }

    /**
     * Sets the block at a given point, if it is inside this builder's bounding box.
     *
     * @param pos a point relative to this structure's root point
     * @param type the new block type
     * @param data the new block data
     */
    public void setBlock(Vector pos, Material type, int data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            delegate
                    .setTypeAndRawData(world, vec.getBlockX(), vec.getBlockY(), vec
                                    .getBlockZ(), type,
                            data);
        }
    }

    /**
     * Sets the block at a given point, if it is inside this structure's bounding box.
     *
     * @param pos a point relative to this structure's root point
     * @param type the new block type
     * @param data the new block data
     */
    public void setBlock(Vector pos, Material type, MaterialData data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            delegate.setTypeAndData(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ(), type,
                    data);
        }
    }

    /**
     * Builds a 1x1 column out of the given block, replacing non-solid blocks starting at a given
     * location and proceeding downward until a solid block is reached.
     *
     * @param pos the highest point to possibly replace, relative to this structure's root
     *         point
     * @param type the block type to fill
     */
    public void setBlockDownward(Vector pos, Material type) {
        setBlockDownward(pos, type, 0);
    }

    /**
     * Builds a 1x1 column out of the given block, replacing non-solid blocks starting at a given
     * location and proceeding downward until a solid block is reached.
     *
     * @param pos the highest point to possibly replace, relative to this structure's root
     *         point
     * @param type the block type to fill
     * @param data the block data
     */
    public void setBlockDownward(Vector pos, Material type, int data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            int y = vec.getBlockY();
            while (!world.getBlockAt(vec.getBlockX(), y, vec.getBlockZ()).getType().isSolid()
                    && y > 1) {
                delegate.setTypeAndRawData(world, vec.getBlockX(), y, vec.getBlockZ(), type, data);
                y--;
            }
        }
    }

    /**
     * Builds a 1x1 column out of the given block, replacing non-solid blocks starting at a given
     * location and proceeding downward until a solid block is reached.
     *
     * @param pos the highest point to possibly replace, relative to this structure's root
     *         point
     * @param type the block type to fill
     * @param data the block data
     */
    public void setBlockDownward(Vector pos, Material type, MaterialData data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            int x = vec.getBlockX();
            int y = vec.getBlockY();
            int z = vec.getBlockZ();
            while (!world.getBlockAt(x, y, z).getType().isSolid() && y > 1) {
                delegate.setTypeAndData(world, x, y, z, type, data);
                y--;
            }
        }
    }

    public void setBlockWithRandomMaterial(Vector pos, Random random,
            Map<StructureMaterial, Integer> materials) {
        StructureMaterial material = getRandomMaterial(random, materials);
        setBlock(pos, material.getType(), material.getData());
    }

    /**
     * Fills a box with the given block.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param type the block type
     */
    public void fill(Vector min, Vector max, Material type) {
        fill(min, max, type, 0);
    }

    /**
     * Fills a box with the given block.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param type the block type
     * @param data the block data
     */
    public void fill(Vector min, Vector max, Material type, int data) {
        fill(min, max, type, data, type, data);
    }

    /**
     * Fills a box with the given block.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param type the block type
     * @param data the block data
     */
    public void fill(Vector min, Vector max, Material type, MaterialData data) {
        fill(min, max, type, data, type, data);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param innerType the block type for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, Material innerType) {
        fill(min, max, outerType, 0, innerType, 0);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param innerType the block type for the interior
     * @param innerData the block data for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, Material innerType,
            int innerData) {
        fill(min, max, outerType, 0, innerType, innerData);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param innerType the block type for the interior
     * @param innerData the block data for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, Material innerType,
            MaterialData innerData) {
        fill(min, max, outerType, 0, innerType, innerData);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param outerData the block data for the faces
     * @param innerType the block type for the interior
     * @param innerData the block data for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, int outerData, Material innerType,
            MaterialData innerData) {
        fill(min, max, outerType, outerData, innerType, innerData.getData());
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param outerData the block data for the faces
     * @param innerType the block type for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, int outerData,
            Material innerType) {
        fill(min, max, outerType, outerData, innerType, 0);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param outerData the block data for the faces
     * @param innerType the block type for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, MaterialData outerData,
            Material innerType) {
        fill(min, max, outerType, outerData, innerType, 0);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param outerData the block data for the faces
     * @param innerType the block type for the interior
     * @param innerData the block data for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, MaterialData outerData,
            Material innerType, int innerData) {
        fill(min, max, outerType, outerData.getData(), innerType, innerData);
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param outerData the block data for the faces
     * @param innerType the block type for the interior
     * @param innerData the block data for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, int outerData, Material innerType,
            int innerData) {
        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();
        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material type;
                    int data;
                    if (x != minX && x != maxX && z != minZ && z != maxZ
                            && y != minY && y != maxY) {
                        type = innerType;
                        data = innerData;
                    } else {
                        type = outerType;
                        data = outerData;
                    }
                    setBlock(new Vector(x, y, z), type, data);
                }
            }
        }
    }

    /**
     * Builds a box from one block, and fills it with another.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param outerType the block type for the faces
     * @param outerData the block data for the faces
     * @param innerType the block type for the interior
     * @param innerData the block data for the interior
     */
    public void fill(Vector min, Vector max, Material outerType, MaterialData outerData,
            Material innerType, MaterialData innerData) {
        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();
        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material type;
                    MaterialData data;
                    if (x != minX && x != maxX && z != minZ && z != maxZ
                            && y != minY && y != maxY) {
                        type = innerType;
                        data = innerData;
                    } else {
                        type = outerType;
                        data = outerData;
                    }
                    setBlock(new Vector(x, y, z), type, data);
                }
            }
        }
    }

    /**
     * Sets a box of blocks to have random types, chosen independently.
     *
     * @param min the minimum coordinates, relative to this structure's root point
     * @param max the maximum coordinates, relative to this structure's root point
     * @param random the PRNG to use
     * @param materials a map of possible blocks to integer weights
     */
    public void fillWithRandomMaterial(Vector min, Vector max, Random random,
            Map<StructureMaterial, Integer> materials) {
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    StructureMaterial material = getRandomMaterial(random, materials);
                    setBlock(new Vector(x, y, z), material.getType(), material.getData());
                }
            }
        }
    }

    /**
     * Sets the given block to a container and fills it with random items.
     *
     * @param pos a point relative to this structure's root point
     * @param random the PRNG to use
     * @param content the distribution to draw items from
     * @param container the container to place
     * @param maxStacks the maximum number of slots to fill
     * @return true if the container was placed and filled; false if {@code pos} is outside the
     *         builder's bounding box or {@link RandomItemsContent#fillContainer(Random, BlockState,
     *         int)} fails
     */
    public boolean createRandomItemsContainer(Vector pos, Random random, RandomItemsContent content,
            DirectionalContainer container, int maxStacks) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            BlockState state = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ())
                    .getState();
            delegate.backupBlockState(state.getBlock());

            state.setType(container.getItemType());
            state.setData(container);
            state.update(true);

            return content.fillContainer(random, state, maxStacks);
        }
        return false;
    }

    /**
     * Sets the given block to a spawner for the given entity type.
     *
     * @param pos a point relative to this structure's root point
     * @param entityType the type of entity the spawner will spawn
     */
    public void createMobSpawner(Vector pos, EntityType entityType) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            BlockState state = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ())
                    .getState();
            delegate.backupBlockState(state.getBlock());

            state.setType(Material.MOB_SPAWNER);
            state.update(true);

            state = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getState();
            if (state instanceof CreatureSpawner) {
                ((CreatureSpawner) state).setSpawnedType(entityType);
            }
        }
    }

    /**
     * Spawns an entity if the given position is within this structure's bounding box.
     *
     * @param pos a point relative to this structure's root point
     * @param entityType the type of entity to spawn
     * @return true if the entity was spawned; false if {@code pos} is outside the builder's
     *         bounding box or {@link World#spawnEntity(Location, EntityType)} fails
     */
    public boolean spawnMob(Vector pos, EntityType entityType) {
        Vector vec = translate(pos);
        return boundingBox.isVectorInside(vec) && world
                .spawnEntity(new Location(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()),
                        entityType) != null;
    }

    private Vector translate(Vector pos) {
        StructureBoundingBox boundingBox = structure.getBoundingBox();
        switch (structure.getOrientation()) {
            case EAST:
                return new Vector(boundingBox.getMax().getBlockX() - pos.getBlockZ(),
                        boundingBox.getMin().getBlockY() + pos.getBlockY(),
                        boundingBox.getMin().getBlockZ() + pos.getBlockX());
            case SOUTH:
                return new Vector(boundingBox.getMin().getBlockX() + pos.getBlockX(),
                        boundingBox.getMin().getBlockY() + pos.getBlockY(),
                        boundingBox.getMax().getBlockZ() - pos.getBlockZ());
            case WEST:
                return new Vector(boundingBox.getMin().getBlockX() + pos.getBlockZ(),
                        boundingBox.getMin().getBlockY() + pos.getBlockY(),
                        boundingBox.getMin().getBlockZ() + pos.getBlockX());
            default: // NORTH
                return new Vector(boundingBox.getMin().getBlockX() + pos.getBlockX(),
                        boundingBox.getMin().getBlockY() + pos.getBlockY(),
                        boundingBox.getMin().getBlockZ() + pos.getBlockZ());
        }
    }

    @Data
    public static final class StructureMaterial {
        private Material type;
        private int data;
    }
}
