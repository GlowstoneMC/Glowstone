package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;
import java.util.stream.Stream;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link GlowParticle}.
 */
public class ParticleTest {

    private static final MaterialData STONE = new MaterialData(Material.STONE, (byte) 1);

    @DataProvider(name = "effects")
    public static Iterator<Object[]> getCases() {
        return Stream.of(Effect.values())
                .filter(effect -> effect.getType() == Effect.Type.PARTICLE)
                .map(x -> new Object[]{x})
                .iterator();
    }

    @Test(dataProvider = "effects")
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
