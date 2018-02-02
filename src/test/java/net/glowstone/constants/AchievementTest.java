package net.glowstone.constants;

import static org.junit.Assert.assertThat;

import java.util.Collection;
import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Achievement;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
        assertThat("Name missing for achievement " + achievement,
            GlowAchievement.getName(achievement), IsNull.notNullValue());
    }

}
