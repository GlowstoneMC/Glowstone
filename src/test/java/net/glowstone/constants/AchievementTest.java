package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Achievement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlowAchievement}.
 */
@RunWith(Parameterized.class)
public class AchievementTest {

    private final Achievement achievement;

    public AchievementTest(Achievement achievement) {
        this.achievement = achievement;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ParameterUtils.enumCases(Achievement.values());
    }

    @Test
    public void testAchievements() {
        assertTrue("Name missing for achievement " + achievement, GlowAchievement.getName(achievement) != null);
    }

}
