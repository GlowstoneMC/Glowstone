package net.glowstone.generator.decorators;

import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.GlowServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.generator.BlockPopulator;

public class EntityDecorator extends BlockPopulator {

    @Getter
    private EntityType[] entityTypes;
    @Getter
    @Setter
    private float rarity = 0.1f;
    @Getter
    private int minGroup = 4;
    @Getter
    private int maxGroup = 4;

    public EntityDecorator(EntityType... entityTypes) {
        this.entityTypes = entityTypes;
    }

    public void setEntityTypes(EntityType... entityTypes) {
        this.entityTypes = entityTypes;
    }

    public void setGroupSize(int minGroup, int maxGroup) {
        this.minGroup = minGroup;
        this.maxGroup = maxGroup;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        GlowServer server = (GlowServer) Bukkit.getServer();
        boolean allowAnimals = world.getAllowAnimals() && server.getAnimalsSpawnEnabled();
        boolean allowMonsters = world.getAllowMonsters() && server.getMonstersSpawnEnabled();
        if (entityTypes.length == 0) {
            return;
        }
        if (random.nextFloat() >= rarity) {
            return;
        }
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;
        EntityType type = entityTypes[random.nextInt(entityTypes.length)];
        if ((!allowAnimals && Animals.class.isAssignableFrom(type.getEntityClass()))
                || !allowMonsters && Monster.class.isAssignableFrom(type.getEntityClass())) {
            return;
        }
        int centerX = sourceX + random.nextInt(16);
        int centerZ = sourceZ + random.nextInt(16);
        int count = minGroup == maxGroup ? minGroup
            : random.nextInt(maxGroup - minGroup) + minGroup;
        int range = 5;
        int attempts = 5;
        for (int i = 0; i < count; i++) {
            if (attempts == 0) {
                continue;
            }
            double radius = (double) range * random.nextDouble();
            double angle = random.nextDouble() * Math.PI;
            double x = radius * Math.sin(angle) + centerX;
            double z = radius * Math.cos(angle) + centerZ;
            Block block = world.getHighestBlockAt(new Location(world, x, 0, z));
            if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER
                || block.getType() == Material.LAVA
                || block.getType() == Material.STATIONARY_LAVA) {
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
