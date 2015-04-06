package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.BlockPatch;

public class UnderwaterDecorator extends BlockDecorator {

    private final Material type;
    private int hRadius;
    private int vRadius;
    private Material[] overridables;

    public UnderwaterDecorator(Material type) {
        this.type = type;
    }

    public final UnderwaterDecorator setRadiuses(int hRadius, int vRadius) {
        this.hRadius = hRadius;
        this.vRadius = vRadius;
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
        while (world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.STATIONARY_WATER ||
                world.getBlockAt(sourceX, sourceY - 1, sourceZ).getType() == Material.WATER && sourceY > 1) {
            sourceY--;
        }
        final Material material = world.getBlockAt(sourceX, sourceY, sourceZ).getType();
        if (material == Material.STATIONARY_WATER || material == Material.WATER) {
            new BlockPatch(type, hRadius, vRadius, overridables).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }
}
