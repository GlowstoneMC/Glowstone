package net.glowstone.constants;

import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import net.glowstone.TestUtils;
import org.bukkit.Achievement;
import org.hamcrest.core.IsNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link GlowAchievement}.
 */
public class AchievementTest {
    @DataProvider(name = "Achievement")
    public static Iterator<Object[]> achievements() {
        return TestUtils.enumAsDataProvider(Achievement.class);
    }

    @Test(dataProvider = "Achievement")
    public void testAchievement(Achievement achievement) {
        assertNotNull(GlowAchievement.getName(achievement),
                "Name missing for achievement " + achievement);
    }

}
