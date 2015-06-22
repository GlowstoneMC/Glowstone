package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.OreType;
import net.glowstone.generator.objects.OreVein;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MonsterEggs;

import java.util.Random;

public class InfestedStoneDecorator extends BlockDecorator {

    private final OreType oreType;

    public InfestedStoneDecorator() {
        oreType = new OreType(Material.MONSTER_EGGS, new MonsterEggs(), 0, 64, 8);
    }

    @Override
    public void decorate(World world, Random random, Chunk chunk) {
        final int cx = (chunk.getX() << 4);
        final int cz = (chunk.getZ() << 4);

        for (int n = 0; n < 7; n++) {
            int sourceX = cx + random.nextInt(16);
            int sourceZ = cz + random.nextInt(16);
            int sourceY = oreType.getMinY() == oreType.getMaxY() ?
                    random.nextInt(oreType.getMinY()) + random.nextInt(oreType.getMinY()) :
                        random.nextInt(oreType.getMaxY() - oreType.getMinY()) + oreType.getMinY();

            new OreVein(oreType).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }
}
