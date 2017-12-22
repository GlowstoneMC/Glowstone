package net.glowstone.generator.populators.overworld;

import java.util.Random;
import net.glowstone.generator.structures.GlowDungeon;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.SimplexNoiseGenerator;

/**
 * A BlockPopulator that places dungeons around the map.
 */
public class DungeonPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        SimplexNoiseGenerator noise = new SimplexNoiseGenerator(world);
        double density = noise.noise(source.getX(), source.getZ());
        if (density > 0.8) {
            // TODO later switch to this loop to have 8 attempts of dungeon placement per chunk,
            // once we get caves and ravines
            //for (int i = 0; i < 8; i++) {
            int x = (source.getX() << 4) + random.nextInt(16);
            int z = (source.getZ() << 4) + random.nextInt(16);
            int y = random.nextInt(256);

            GlowDungeon dungeon = new GlowDungeon(random, new Location(world, x, y, z));
            BlockStateDelegate delegate = new BlockStateDelegate();
            if (dungeon.generate(world, random,
                new StructureBoundingBox(new Vector(x - 15, 1, z - 15),
                    new Vector(x + 15, 511, z + 15)), delegate)) {
                delegate.updateBlockStates();
            }
            //}
        }
    }
}
