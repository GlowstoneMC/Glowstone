package net.glowstone.entity;

public class GlowTntPrimedTest extends GlowExplosiveTest<GlowTntPrimed> {
    public GlowTntPrimedTest() {
        super(location -> new GlowTntPrimed(location, null));
    }
}
