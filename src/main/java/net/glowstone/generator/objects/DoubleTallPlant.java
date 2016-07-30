package net.glowstone.generator.objects;

import org.bukkit.material.types.DoublePlantSpecies;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.DoublePlant;

import java.util.Random;

public class DoubleTallPlant {

    private final DoublePlantSpecies species;

    public DoubleTallPlant(DoublePlantSpecies species) {
        this.species = species;
    }

    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean placed = false;
        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            Block block = world.getBlockAt(x, y, z);
            if (y < 255 && block.isEmpty() && block.getRelative(BlockFace.UP).isEmpty() &&
                    block.getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
                BlockState state = block.getState();
                state.setType(Material.DOUBLE_PLANT);
                state.setData(new DoublePlant(species));
                state.update(true);
                state = block.getRelative(BlockFace.UP).getState();
                state.setType(Material.DOUBLE_PLANT);
                state.setData(new DoublePlant(DoublePlantSpecies.PLANT_APEX));
                state.update(true);
                placed = true;
            }
        }
        return placed;
    }
}
