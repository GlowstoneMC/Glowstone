package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.structures.GlowDesertWell;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Random;

public class DesertWellDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk source) {
        if (random.nextInt(1000) == 0) {
            int x = (source.getX() << 4) + random.nextInt(16);
            int z = (source.getZ() << 4) + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);

            GlowDesertWell well = new GlowDesertWell(new Location(world, x, y, z));
            BlockStateDelegate delegate = new BlockStateDelegate();
            if (well.generate(world, random, new StructureBoundingBox(new Vector(x - 15, 1, z - 15),
                new Vector(x + 15, 511, z + 15)), delegate)) {
                delegate.updateBlockStates();
            }
        }
    }
}
