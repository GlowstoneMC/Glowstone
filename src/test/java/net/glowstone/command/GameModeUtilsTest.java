package net.glowstone.command;

import org.bukkit.GameMode;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class GameModeUtilsTest {

    @Test
    public void testGameModeBuild() {
        Locale locale = Locale.getDefault();
        assertThat(GameModeUtils.build("s", locale), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("S", locale), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("survival", locale), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("SURVIVAL", locale), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("0", locale), is(GameMode.SURVIVAL));

        assertThat(GameModeUtils.build("c", locale), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("C", locale), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("creative", locale), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("CREATIVE", locale), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("1", locale), is(GameMode.CREATIVE));

        assertThat(GameModeUtils.build("a", locale), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("A", locale), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("adventure", locale), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("ADVENTURE", locale), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("2", locale), is(GameMode.ADVENTURE));

        assertThat(GameModeUtils.build("sp", locale), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("SP", locale), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("spectator", locale), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("SPECTATOR", locale), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("3", locale), is(GameMode.SPECTATOR));

        assertThat(GameModeUtils.build("", locale), nullValue());
        assertThat(GameModeUtils.build(null, locale), nullValue());
        assertThat(GameModeUtils.build("unknown mode", locale), nullValue());
    }

    @Test
    public void testPrettyPrint() {
        Locale locale = Locale.getDefault();
        assertThat(GameModeUtils.prettyPrint(GameMode.CREATIVE, locale), is("Creative"));
        assertThat(GameModeUtils.prettyPrint(GameMode.ADVENTURE, locale), is("Adventure"));
        assertThat(GameModeUtils.prettyPrint(GameMode.SURVIVAL, locale), is("Survival"));
        assertThat(GameModeUtils.prettyPrint(GameMode.SPECTATOR, locale), is("Spectator"));
    }
}
