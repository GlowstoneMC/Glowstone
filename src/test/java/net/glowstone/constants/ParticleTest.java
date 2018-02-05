package net.glowstone.constants;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GlowParticle}.
 */
public class ParticleTest {

    private static final MaterialData STONE = new MaterialData(Material.STONE, (byte) 1);

    public static Collection<Effect> getCases() {
        System.err.println("getCases() called");
        try {
            return Stream.of(Effect.values())
                    .parallel()
                    .filter(effect -> effect.getType() == Effect.Type.PARTICLE)
                    .collect(Collectors.toList());
        } finally {
            System.err.println("getCases() returning");
        }
    }

    @MethodSource("getCases")
    @ParameterizedTest
    public void testGetData(Effect particle) {
        switch (particle) {
            case ITEM_BREAK:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle,
                    GlowParticle.getExtData(particle, STONE),
                    is(new int[]{Material.STONE.getId(), 1}));
                break;
            case TILE_BREAK:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle,
                    GlowParticle.getExtData(particle, STONE), is(new int[]{4097}));
                break;
            case TILE_DUST:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle,
                    GlowParticle.getExtData(particle, STONE),
                    is(new int[]{Material.STONE.getId()}));
                break;
            default:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(false));
                assertThat("Wrong extra data for " + particle,
                    GlowParticle.getExtData(particle, null), is(new int[0]));
        }
    }

}
