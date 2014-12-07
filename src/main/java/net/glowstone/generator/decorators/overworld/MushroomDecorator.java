package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import net.glowstone.generator.decorators.BlockDecorator;

import org.bukkit.Chunk;
import org.bukkit.DirtType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.MaterialData;

public class MushroomDecorator extends BlockDecorator {

    private final Material type;
    private boolean fixedHeightRange;
    private double density;

    public MushroomDecorator(Material type) {
        if (type != Material.BROWN_MUSHROOM && type != Material.RED_MUSHROOM) {
            throw new IllegalArgumentException("MushroomDecorator material must be BROWN_MUSHROOM or RED_MUSHROOM");
        }
        this.type = type;
        fixedHeightRange = false;
    }

    public MushroomDecorator setFixedHeightRange() {
        fixedHeightRange = true;
        return this;
    }

    public MushroomDecorator setDensity(double density) {
        this.density = density;
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        if (random.nextFloat() < density) {
            int sourceX = (source.getX() << 4) + random.nextInt(16);
            int sourceZ = (source.getZ() << 4) + random.nextInt(16);
            int sourceY = world.getHighestBlockYAt(sourceX, sourceZ);
            sourceY = fixedHeightRange ? sourceY : random.nextInt(sourceY << 1);

            for (int i = 0; i < 64; i++) {
                int x = sourceX + random.nextInt(8) - random.nextInt(8);
                int z = sourceZ + random.nextInt(8) - random.nextInt(8);
                int y = sourceY + random.nextInt(4) - random.nextInt(4);

                final Block block = world.getBlockAt(x, y, z);
                final Block blockBelow = world.getBlockAt(x, y - 1, z);
                if (y < 255 && block.getType() == Material.AIR &&
                        (((blockBelow.getType() == Material.GRASS || (blockBelow.getState().getData() instanceof Dirt &&
                        ((Dirt) blockBelow.getState().getData()).getType() != DirtType.PODZOL)) && block.getLightLevel() < 13) ||
                        blockBelow.getType() == Material.MYCEL || (blockBelow.getState().getData() instanceof Dirt &&
                        ((Dirt) blockBelow.getState().getData()).getType() == DirtType.PODZOL))) {
                    final BlockState state = block.getState();
                    state.setType(type);
                    state.setData(new MaterialData(type));
                    state.update(true);
                }
            }
        }
    }
}
