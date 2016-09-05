package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
    public void testHasId() {
        //TODO assertTrue("Id missing for particle " + particle, GlowParticle.getId(particle) >= 0);
    }

    @Test
    public void testGetData() {
        if (particle.getType() != Effect.Type.PARTICLE) return; // this only tests particle effects

        switch (particle) {
            case ITEM_BREAK:
                assertEquals("Wrong data for " + particle, true, particle.getData() != null);
                assertArrayEquals("Wrong extra data for " + particle, new int[]{Material.STONE.getId(), 1}, GlowParticle.getExtData(particle, STONE));
                break;
            case TILE_BREAK:
                assertEquals("Wrong data for " + particle, true, particle.getData() != null);
                assertArrayEquals("Wrong extra data for " + particle, new int[]{4097}, GlowParticle.getExtData(particle, STONE));
                break;
            case TILE_DUST:
                assertEquals("Wrong data for " + particle, true, particle.getData() != null);
                assertArrayEquals("Wrong extra data for " + particle, new int[]{Material.STONE.getId()}, GlowParticle.getExtData(particle, STONE));
                break;
            default:
                assertEquals("Wrong data for " + particle, false, particle.getData() != null);
                assertArrayEquals("Wrong extra data for " + particle, new int[0], GlowParticle.getExtData(particle, null));
        }
    }

}
