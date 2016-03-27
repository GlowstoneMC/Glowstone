package net.glowstone.generator.structures;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Random;

public class GlowIgloo extends GlowTemplePiece {

    public GlowIgloo() {
    }

    public GlowIgloo(Random random, Location location, Vector size) {
        super(random, location, size);
    }
}
