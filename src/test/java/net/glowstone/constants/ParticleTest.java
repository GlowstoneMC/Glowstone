package net.glowstone.constants;

import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.material.MaterialData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link GlowParticle}.
 */
// TODO: test Particle class instead
@Disabled
public class ParticleTest {

    private static final MaterialData STONE = new MaterialData(Material.STONE, (byte) 1);

    public static Stream<Particle> getCases() {
        return Stream.of(Particle.values());
    }

    @MethodSource("getCases")
    @ParameterizedTest
    public void testGetData(Particle particle) {
        /* TODO: test Particle class instead
        switch (particle) {
            case ITEM_CRACK:
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
            case REDSTONE:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle,
                    GlowParticle.getExtData(particle, STONE),
                    is(new int[]{Material.STONE.getId()}));
                break;
            default:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(false));
                assertThat("Wrong extra data for " + particle,
                    GlowParticle.getExtData(particle, null), is(new Object[]{}));
        }*/
    }

}
