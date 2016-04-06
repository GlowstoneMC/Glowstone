package net.glowstone.generator.structures;

import net.glowstone.GlowServer;
import net.glowstone.generator.structures.template.Template;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Random;

public class GlowIgloo extends GlowTemplePiece {

    public GlowIgloo() {}

    public GlowIgloo(Random random, Location location) {
        super(random, location, new Vector(7, 5, 8));
    }

    @Override
    public boolean generate(World world, Random random, StructureBoundingBox boundingBox, BlockStateDelegate delegate) {
        if (!super.generate(world, random, boundingBox, delegate)) {
            return false;
        }

        Location origin = new Location(world, boundingBox.getMin().getX(), world.getSeaLevel(), boundingBox.getMin().getZ());
        Template templateTop = ((GlowServer) Bukkit.getServer()).getTemplateManager().getTemplate("igloo/igloo_top");
        ((GlowServer) Bukkit.getServer()).getTemplateManager().place(world, origin, templateTop);

        if (random.nextDouble() < 0.5) {
            // Generate middle & bottom
            Template templateMiddle = ((GlowServer) Bukkit.getServer()).getTemplateManager().getTemplate("igloo/igloo_middle");
            Template templateBottom = ((GlowServer) Bukkit.getServer()).getTemplateManager().getTemplate("igloo/igloo_bottom");

            int middleLength = random.nextInt(8) + 4;
            for (int level = 0; level < middleLength; level++) {
                Vector middleVector = new Vector(2, -3 - level * 3, 4);
                Location originMiddle = origin.clone().add(middleVector);
                ((GlowServer) Bukkit.getServer()).getTemplateManager().place(world, originMiddle, templateMiddle);
            }
            Vector bottomVector = new Vector(0, -3 - middleLength * 3, -2);
            Location originBottom = origin.clone().add(bottomVector);
            ((GlowServer) Bukkit.getServer()).getTemplateManager().place(world, originBottom, templateBottom);
        } else {
            // Remove the trapdoor if there is no bottom part
            Vector trapdoor = new Vector(3, 1, 5);
            Location trapdoorL = origin.clone().add(trapdoor);
            world.getBlockAt(trapdoorL).setType(Material.SNOW);
        }
        return true;
    }
}
