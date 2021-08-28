package net.glowstone.block;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BuiltinMaterialValueManagerTest {

    @Test
    public void correctMaterialValues() {
        // throws exception if wrong file format
        MaterialValueManager bvm = new BuiltinMaterialValueManager();

        // test some 'features'
        assertThat("Bedrock hardness not max value", bvm.getValues(Material.BEDROCK).getHardness(),
            is(Float.MAX_VALUE)); //in yml -1

        // test some fixed values
        assertThat(bvm.getValues(Material.OBSIDIAN).getBlastResistance(), is(6000f));
        assertThat(bvm.getValues(Material.OBSIDIAN).getHardness(), is(50f));
        assertThat(bvm.getValues(Material.OBSIDIAN).getLightOpacity(), is(255));
        assertThat(bvm.getValues(Material.WATER).getLightOpacity(), is(3));
        assertThat(bvm.getValues(Material.AIR).getLightOpacity(), is(0));
        assertThat(bvm.getValues(Material.STONE).getFlameResistance(), is(-1));
        assertThat(bvm.getValues(Material.COBBLESTONE).getFireResistance(), is(-1));
        assertThat(bvm.getValues(Material.LEGACY_WOOD).getFlameResistance(), is(5));
        assertThat(bvm.getValues(Material.TNT).getFireResistance(), is(100));

        // test defaults
        assertThat("Nonexistent value defined", bvm.getValues(Material.CAULDRON).getHardness(),
            is(1f)); // item doesn't exist at all
    }
}
