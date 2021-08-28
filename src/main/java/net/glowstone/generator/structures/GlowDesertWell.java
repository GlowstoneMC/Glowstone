package net.glowstone.generator.structures;

import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.generator.structures.util.StructureBuilder;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Random;

public class GlowDesertWell extends GlowStructurePiece {

    public GlowDesertWell(Location location) {
        super(location, new Vector(5, 6, 5));
    }

    @Override
    public boolean generate(World world, Random random, StructureBoundingBox genBoundingBox,
        BlockStateDelegate delegate) {
        if (!super.generate(world, random, boundingBox, delegate)) {
            return false;
        }

        boundingBox.offset(new Vector(-2, -2, -2));

        StructureBuilder builder = new StructureBuilder(world, this, genBoundingBox, delegate);
        while (builder.getBlockState(new Vector(2, 1, 2)).getType() == Material.AIR
            && boundingBox.getMin().getBlockY() > 0) {
            boundingBox.offset(new Vector(0, -1, 0));
        }

        if (builder.getBlockState(new Vector(2, 1, 2)).getType() != Material.SAND) {
            return false;
        }

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if (builder.getBlockState(new Vector(x, 0, z)).getType() == Material.AIR
                    && builder.getBlockState(new Vector(x, -1, z)).getType() == Material.AIR) {
                    return false;
                }
            }
        }

        builder.fill(new Vector(0, 0, 0), new Vector(4, 2, 4), Material.SANDSTONE);
        builder.fill(new Vector(1, 2, 1), new Vector(3, 2, 3), Material.AIR);
        builder.setBlock(new Vector(2, 2, 0), Material.SANDSTONE_SLAB);
        builder.setBlock(new Vector(0, 2, 2), Material.SANDSTONE_SLAB);
        builder.setBlock(new Vector(4, 2, 2), Material.SANDSTONE_SLAB);
        builder.setBlock(new Vector(2, 2, 4), Material.SANDSTONE_SLAB);
        builder.fill(new Vector(2, 1, 1), new Vector(2, 1, 3), Material.WATER);
        builder.fill(new Vector(1, 1, 2), new Vector(3, 1, 2), Material.WATER);

        builder.fill(new Vector(1, 2, 1), new Vector(1, 4, 1), Material.SANDSTONE);
        builder.fill(new Vector(1, 2, 3), new Vector(1, 4, 3), Material.SANDSTONE);
        builder.fill(new Vector(3, 2, 1), new Vector(3, 4, 1), Material.SANDSTONE);
        builder.fill(new Vector(3, 2, 3), new Vector(3, 4, 3), Material.SANDSTONE);

        builder.fill(new Vector(1, 5, 1), new Vector(3, 5, 3), Material.SANDSTONE_SLAB);
        builder.setBlock(new Vector(2, 5, 2), Material.SANDSTONE);

        return true;
    }
}
