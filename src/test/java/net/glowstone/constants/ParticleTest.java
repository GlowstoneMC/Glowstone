package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.material.MaterialData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlowParticle}.
 */
@RunWith(Parameterized.class)
public class ParticleTest {

    private static final MaterialData STONE = new MaterialData(Material.STONE, (byte) 1);

    private final Particle particle;

    public ParticleTest(Particle particle) {
        this.particle = particle;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ParameterUtils.enumCases(Particle.values());
    }

    @Test
    public void testHasId() {
        assertTrue("Id missing for particle " + particle, GlowParticle.getId(particle) >= 0);
    }

    @Test
    public void testGetData() {
        switch (particle) {
            case ITEM_BREAK:
                assertEquals("Wrong usesMaterial for " + particle, true, particle.usesMaterial());
                assertArrayEquals("Wrong extData for " + particle, new int[]{Material.STONE.getId(), 1}, GlowParticle.getData(particle, STONE));
                break;
            case BLOCK_BREAK:
            case BLOCK_DUST:
                assertEquals("Wrong usesMaterial for " + particle, true, particle.usesMaterial());
                assertArrayEquals("Wrong extData for " + particle, new int[]{Material.STONE.getId()}, GlowParticle.getData(particle, STONE));
                break;
            default:
                assertEquals("Wrong usesMaterial for " + particle, false, particle.usesMaterial());
                assertArrayEquals("Wrong extData for " + particle, new int[0], GlowParticle.getData(particle, null));
        }
    }

}
