package net.glowstone.entity;

public class GlowTntPrimedTest extends net.glowstone.entity.GlowExplosiveTest<GlowTntPrimed> {
    public GlowTntPrimedTest() {
        super(location -> new GlowTntPrimed(location, null));
    }
}
