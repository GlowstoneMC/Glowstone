package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.decorators.BlockDecorator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DirtType;

import java.util.Random;

public class MushroomDecorator extends BlockDecorator {

    private final Material type;
    private boolean fixedHeightRange;
    private double density;

    /**
     * Creates a mushroom decorator for the overworld.
     *
     * @param type {@link Material#BROWN_MUSHROOM} or {@link Material#RED_MUSHROOM}
     */
    public MushroomDecorator(Material type) {
        if (type != Material.BROWN_MUSHROOM && type != Material.RED_MUSHROOM) {
            throw new IllegalArgumentException(
                "MushroomDecorator material must be BROWN_MUSHROOM or RED_MUSHROOM");
        }
        this.type = type;
        fixedHeightRange = false;
    }

    public MushroomDecorator setUseFixedHeightRange() {
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

                Block block = world.getBlockAt(x, y, z);
                Block blockBelow = world.getBlockAt(x, y - 1, z);
                if (y < 255 && block.getType() == Material.AIR) {
                    boolean canPlaceShroom;
                    switch (blockBelow.getType()) {
                        case MYCEL:
                            canPlaceShroom = true;
                            break;
                        case GRASS:
                            canPlaceShroom = (block.getLightLevel() < 13);
                            break;
                        case DIRT:
                            MaterialData data = blockBelow.getState().getData();
                            if (data instanceof Dirt) {
                                canPlaceShroom = (((Dirt) data).getType() == DirtType.PODZOL
                                        || block.getLightLevel() < 13);
                            } else {
                                canPlaceShroom = false;
                            }
                            break;
                        default:
                            canPlaceShroom = false;
                    }
                    if (canPlaceShroom) {
                        BlockState state = block.getState();
                        state.setType(type);
                        state.setData(new MaterialData(type));
                        state.update(true);
                    }
                }
            }
        }
    }
}
