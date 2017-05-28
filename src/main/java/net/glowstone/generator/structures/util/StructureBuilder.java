package net.glowstone.generator.structures.util;

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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class StructureBuilder {

    private final World world;
    private final BlockStateDelegate delegate;
    private final StructureBoundingBox boundingBox;
    private final GlowStructurePiece structure;

    public StructureBuilder(World world, GlowStructurePiece structure, StructureBoundingBox boundingBox, BlockStateDelegate delegate) {
        this.world = world;
        this.delegate = delegate;
        this.boundingBox = boundingBox;
        this.structure = structure;
    }

    public void addRandomMaterial(Map<StructureMaterial, Integer> materials, int weight, Material type, int data) {
        materials.put(new StructureMaterial(type, data), weight);
    }

    public StructureMaterial getRandomMaterial(Random random, Map<StructureMaterial, Integer> materials) {
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

    public void setBlock(Vector pos, Material type) {
        setBlock(pos, type, 0);
    }

    public void setBlock(Vector pos, Material type, int data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            delegate.setTypeAndRawData(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ(), type, data);
        }
    }

    public void setBlock(Vector pos, Material type, MaterialData data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            delegate.setTypeAndData(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ(), type, data);
        }
    }

    public void setBlockDownward(Vector pos, Material type) {
        setBlockDownward(pos, type, 0);
    }

    public void setBlockDownward(Vector pos, Material type, int data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            int y = vec.getBlockY();
            while (!world.getBlockAt(vec.getBlockX(), y, vec.getBlockZ()).getType().isSolid() && y > 1) {
                delegate.setTypeAndRawData(world, vec.getBlockX(), y, vec.getBlockZ(), type, data);
                y--;
            }
        }
    }

    public void setBlockDownward(Vector pos, Material type, MaterialData data) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            int y = vec.getBlockY();
            while (!world.getBlockAt(vec.getBlockX(), y, vec.getBlockZ()).getType().isSolid() && y > 1) {
                delegate.setTypeAndData(world, vec.getBlockX(), y, vec.getBlockZ(), type, data);
                y--;
            }
        }
    }

    public void setBlockWithRandomMaterial(Vector pos, Random random, Map<StructureMaterial, Integer> materials) {
        StructureMaterial material = getRandomMaterial(random, materials);
        setBlock(pos, material.getType(), material.getData());
    }

    public void fill(Vector min, Vector max, Material type) {
        fill(min, max, type, 0);
    }

    public void fill(Vector min, Vector max, Material type, int data) {
        fill(min, max, type, data, type, data);
    }

    public void fill(Vector min, Vector max, Material type, MaterialData data) {
        fill(min, max, type, data, type, data);
    }

    public void fill(Vector min, Vector max, Material outerType, Material innerType) {
        fill(min, max, outerType, 0, innerType, 0);
    }

    public void fill(Vector min, Vector max, Material outerType, Material innerType, int innerData) {
        fill(min, max, outerType, 0, innerType, innerData);
    }

    public void fill(Vector min, Vector max, Material outerType, Material innerType, MaterialData innerData) {
        fill(min, max, outerType, 0, innerType, innerData);
    }

    public void fill(Vector min, Vector max, Material outerType, int outerData, Material innerType, MaterialData innerData) {
        fill(min, max, outerType, outerData, innerType, innerData.getData());
    }

    public void fill(Vector min, Vector max, Material outerType, int outerData, Material innerType) {
        fill(min, max, outerType, outerData, innerType, 0);
    }

    public void fill(Vector min, Vector max, Material outerType, MaterialData outerData, Material innerType) {
        fill(min, max, outerType, outerData, innerType, 0);
    }

    public void fill(Vector min, Vector max, Material outerType, MaterialData outerData, Material innerType, int innerData) {
        fill(min, max, outerType, outerData.getData(), innerType, innerData);
    }

    public void fill(Vector min, Vector max, Material outerType, int outerData, Material innerType, int innerData) {
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Material type;
                    int data;
                    if (x != min.getBlockX() && x != max.getBlockX() &&
                            z != min.getBlockZ() && z != max.getBlockZ() &&
                            y != min.getBlockY() && y != max.getBlockY()) {
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

    public void fill(Vector min, Vector max, Material outerType, MaterialData outerData, Material innerType, MaterialData innerData) {
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Material type;
                    MaterialData data;
                    if (x != min.getBlockX() && x != max.getBlockX() &&
                            z != min.getBlockZ() && z != max.getBlockZ() &&
                            y != min.getBlockY() && y != max.getBlockY()) {
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

    public void fillWithRandomMaterial(Vector min, Vector max, Random random, Map<StructureMaterial, Integer> materials) {
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    StructureMaterial material = getRandomMaterial(random, materials);
                    setBlock(new Vector(x, y, z), material.getType(), material.getData());
                }
            }
        }
    }

    public boolean createRandomItemsContainer(Vector pos, Random random, RandomItemsContent content, DirectionalContainer container, int maxStacks) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            BlockState state = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getState();
            delegate.backupBlockState(state.getBlock());

            state.setType(container.getItemType());
            state.setData(container);
            state.update(true);

            return content.fillContainer(random, container, state, maxStacks);
        }
        return false;
    }

    public void createMobSpawner(Vector pos, EntityType entityType) {
        Vector vec = translate(pos);
        if (boundingBox.isVectorInside(vec)) {
            BlockState state = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getState();
            delegate.backupBlockState(state.getBlock());

            state.setType(Material.MOB_SPAWNER);
            state.update(true);

            state = world.getBlockAt(vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()).getState();
            if (state instanceof CreatureSpawner) {
                ((CreatureSpawner) state).setSpawnedType(entityType);
            }
        }
    }

    public boolean spawnMob(Vector pos, EntityType entityType) {
        Vector vec = translate(pos);
        return boundingBox.isVectorInside(vec) && world.spawnEntity(new Location(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ()), entityType) != null;
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

    public static class StructureMaterial {
        private Material type;
        private int data;

        public StructureMaterial(Material type) {
            this(type, 0);
        }

        public StructureMaterial(Material type, int data) {
            this.type = type;
            this.data = data;
        }

        public Material getType() {
            return type;
        }

        public int getData() {
            return data;
        }
    }
}
