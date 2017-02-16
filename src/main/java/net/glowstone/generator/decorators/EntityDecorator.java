package net.glowstone.generator.decorators;

import net.glowstone.GlowWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class EntityDecorator extends BlockPopulator {
    private static final Material[] SKIP_BLOCKS = new Material[]{Material.LONG_GRASS, Material.DOUBLE_PLANT, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.LEAVES, Material.LEAVES_2, Material.SNOW};

    private EntityType[] entityTypes;
    private float rarity = 0.1f;
    private int minGroup = 4, maxGroup = 4;

    public EntityDecorator(EntityType... entityTypes) {
        this.entityTypes = entityTypes;
    }

    public float getRarity() {
        return rarity;
    }

    public void setRarity(float rarity) {
        this.rarity = rarity;
    }

    public EntityType[] getEntityTypes() {
        return entityTypes;
    }

    public void setEntityTypes(EntityType... entityTypes) {
        this.entityTypes = entityTypes;
    }

    public int getMinGroup() {
        return minGroup;
    }

    public int getMaxGroup() {
        return maxGroup;
    }

    public void setGroupSize(int minGroup, int maxGroup) {
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (entityTypes.length == 0) {
            return;
        }
        if (random.nextFloat() >= rarity) {
            return;
        }
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;
        EntityType type = entityTypes[random.nextInt(entityTypes.length)];
        int centerX = sourceX + random.nextInt(16), centerZ = sourceZ + random.nextInt(16);
        int count = minGroup == maxGroup ? minGroup : random.nextInt(maxGroup - minGroup) + minGroup, range = 5;
        int attempts = 5;
        for (int i = 0; i < count; i++) {
            if (attempts == 0) {
                continue;
            }
            double radius = (double) range * random.nextDouble();
            double angle = random.nextDouble() * Math.PI;
            double x = radius * Math.sin(angle) + centerX;
            double z = radius * Math.cos(angle) + centerZ;
            Block block = ((GlowWorld) world).getHighestBlockAt(new Location(world, x, 0, z), SKIP_BLOCKS);
            if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
                i--;
                attempts--;
                continue;
            }
            attempts = 5;
            Location location = block.getLocation().clone().add(0, 1, 0);
            location.setYaw(random.nextFloat() * 360 - 180);
            if (location.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                location.subtract(0, 1, 0);
            }
            world.spawnEntity(location, type);
        }
    }
}
