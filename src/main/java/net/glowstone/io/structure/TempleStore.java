package net.glowstone.io.structure;

import net.glowstone.GlowWorld;
import net.glowstone.generator.structures.GlowTemple;

import java.util.Random;

public class TempleStore extends StructureStore<GlowTemple> {

    public TempleStore() {
        super(GlowTemple.class, "Temple");
    }

    @Override
    public GlowTemple createStructure(GlowWorld world, int chunkX, int chunkZ) {
        return new GlowTemple(world, chunkX, chunkZ);
    }

    @Override
    public GlowTemple createNewStructure(GlowWorld world, Random random, int chunkX, int chunkZ) {
        return new GlowTemple(world, random, chunkX, chunkZ);
    }
}
