package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;
import net.glowstone.TestUtils;
import org.bukkit.Statistic;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link GlowStatistic}.
 */
public class StatisticTest {

    @DataProvider(name = "Statistic")
    public Iterator<Object[]> statistics() {
        return TestUtils.enumAsDataProvider(Statistic.class);
    }

    @Test(dataProvider = "Statistic")
    public void testNameAvailable(Statistic statistic) {
        if (statistic.getType() != Statistic.Type.UNTYPED) {
            // typed statistics not yet tested
            return;
        }
        assertThat("Name missing for untyped statistic " + statistic,
            GlowStatistic.getName(statistic), not(sameInstance(null)));
    }

}
