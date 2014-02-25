package net.glowstone.constants;

import org.bukkit.Achievement;
import org.bukkit.Statistic;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests for the GlowStatistic and GlowAchievement classes.
 */
public class StatisticTest {

    @Test
    public void testAchievements() {
        for (Achievement achievement : Achievement.values()) {
            assertTrue("Name missing for achievement " + achievement, GlowAchievement.getName(achievement) != null);
        }
    }

    @Test
    public void testUntypedStatistics() {
        for (Statistic stat : Statistic.values()) {
            if (stat.getType() != Statistic.Type.UNTYPED) continue;
            assertTrue("Name missing for untyped statistic " + stat, GlowStatistic.getName(stat) != null);
        }
    }

}
