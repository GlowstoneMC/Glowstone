package net.glowstone.entity.monster.complex;

public class GlowEnderDragonPartTest extends net.glowstone.entity.GlowEntityTest<GlowEnderDragonPart> {
    public GlowEnderDragonPartTest() {
        super(location -> new GlowEnderDragonPart(new GlowEnderDragon(location)));
    }
}
