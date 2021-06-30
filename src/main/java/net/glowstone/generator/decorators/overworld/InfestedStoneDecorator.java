package net.glowstone.generator.decorators.overworld;

import java.util.Random;
import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.OreType;
import net.glowstone.generator.objects.OreVein;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MonsterEggs;

public class InfestedStoneDecorator extends BlockDecorator {

    private final OreType oreType;

    public InfestedStoneDecorator() {
        // TODO: 1.13: per entity spawn eggs
        oreType = new OreType(Material.LEGACY_MONSTER_EGGS, new MonsterEggs(), 0, 64, 8);
    }

    @Override
    public void decorate(World world, Random random, Chunk chunk) {
        int cx = chunk.getX() << 4;
        int cz = chunk.getZ() << 4;

        for (int n = 0; n < 7; n++) {
            int sourceX = cx + random.nextInt(16);
            int sourceZ = cz + random.nextInt(16);
            int minY = oreType.getMinY();
            int maxY = oreType.getMaxY();
            int sourceY = minY == maxY
                    ? random.nextInt(minY) + random.nextInt(minY)
                    : random.nextInt(maxY - minY) + minY;

            new OreVein(oreType).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }
}
