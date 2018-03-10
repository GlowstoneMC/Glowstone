package net.glowstone.constants;

import static org.junit.Assert.assertThat;

import org.bukkit.Achievement;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Tests for {@link GlowAchievement}.
 */
public class AchievementTest {
    @EnumSource(Achievement.class)
    @ParameterizedTest
    public void testAchievement(Achievement achievement) {
        assertThat("Name missing for achievement " + achievement,
            GlowAchievement.getName(achievement), IsNull.notNullValue());
    }

}
