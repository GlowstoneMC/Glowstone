package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GlowParticle}.
 */
@RunWith(Parameterized.class)
public class ParticleTest {

    private static final MaterialData STONE = new MaterialData(Material.STONE, (byte) 1);

    private final Effect particle;

    public ParticleTest(Effect particle) {
        this.particle = particle;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ParameterUtils.enumCases(Effect.values());
    }

    @Test
    public void testGetData() {
        if (particle.getType() != Effect.Type.PARTICLE) return; // this only tests particle effects

        switch (particle) {
            case ITEM_BREAK:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle, GlowParticle.getExtData(particle, STONE), is(new int[]{Material.STONE.getId(), 1}));
                break;
            case TILE_BREAK:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle, GlowParticle.getExtData(particle, STONE), is(new int[]{4097}));
                break;
            case TILE_DUST:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(true));
                assertThat("Wrong extra data for " + particle, GlowParticle.getExtData(particle, STONE), is(new int[]{Material.STONE.getId()}));
                break;
            default:
                assertThat("Wrong data for " + particle, particle.getData() != null, is(false));
                assertThat("Wrong extra data for " + particle, GlowParticle.getExtData(particle, null), is(new int[0]));
        }
    }

}
