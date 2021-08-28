package net.glowstone.generator.populators.common;

import net.glowstone.generator.objects.OreType;
import net.glowstone.generator.objects.OreVein;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class BaseOrePopulator extends BlockPopulator {
    protected final Map<OreType, Integer> ores = new LinkedHashMap<>();

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int cx = chunk.getX() << 4;
        int cz = chunk.getZ() << 4;

        for (Entry<OreType, Integer> entry : ores.entrySet()) {

            OreType oreType = entry.getKey();
            for (int n = 0; n < entry.getValue(); n++) {

                int sourceX = cx + random.nextInt(16);
                int sourceZ = cz + random.nextInt(16);
                int sourceY = oreType.getRandomHeight(random);

                new OreVein(oreType).generate(world, random, sourceX, sourceY, sourceZ);
            }
        }
    }

}
