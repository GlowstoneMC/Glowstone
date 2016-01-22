package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Statistic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlowStatistic}.
 */
@RunWith(Parameterized.class)
public class StatisticTest {

    private final Statistic statistic;

    public StatisticTest(Statistic statistic) {
        this.statistic = statistic;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ParameterUtils.enumCases(Statistic.values());
    }

    @Test
    public void testNameAvailable() {
        if (statistic.getType() != Statistic.Type.UNTYPED) {
            // typed statistics not yet tested
            return;
        }
        assertTrue("Name missing for untyped statistic " + statistic, GlowStatistic.getName(statistic) != null);
    }

}
