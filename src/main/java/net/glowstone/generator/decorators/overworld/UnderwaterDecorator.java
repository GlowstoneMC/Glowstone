package net.glowstone.generator.decorators.overworld;

import java.util.Random;
import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.BlockPatch;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

public class UnderwaterDecorator extends BlockDecorator {

    private final Material type;
    private int horizRadius;
    private int vertRadius;
    private Material[] overridables;

    public UnderwaterDecorator(Material type) {
        this.type = type;
    }

    /**
     * Updates the size of this decorator.
     *
     * @param horizRadius the maximum radius on the horizontal plane
     * @param vertRadius the depth above and below the center
     * @return this, updated
     */
    public final UnderwaterDecorator setRadii(int horizRadius, int vertRadius) {
        this.horizRadius = horizRadius;
        this.vertRadius = vertRadius;
        return this;
    }

    public final UnderwaterDecorator setOverridableBlocks(Material... overridables) {
        this.overridables = overridables;
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = world.getHighestBlockYAt(sourceX, sourceZ) - 1;
        while (
            world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.STATIONARY_WATER
                ||
                world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.WATER
                    && sourceY > 1) {
            sourceY--;
        }
        Material material = world.getBlockAt(sourceX, sourceY, sourceZ).getType();
        if (material == Material.STATIONARY_WATER || material == Material.WATER) {
            new BlockPatch(type, horizRadius, vertRadius, overridables)
                .generate(world, random, sourceX, sourceY, sourceZ);
        }
    }
}
