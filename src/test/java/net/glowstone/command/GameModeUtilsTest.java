package net.glowstone.command;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.bukkit.GameMode;
import org.junit.jupiter.api.Test;

public class GameModeUtilsTest {

    @Test
    public void testGameModeBuild() {
        assertThat(GameModeUtils.build("s"), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("S"), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("survival"), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("SURVIVAL"), is(GameMode.SURVIVAL));
        assertThat(GameModeUtils.build("0"), is(GameMode.SURVIVAL));

        assertThat(GameModeUtils.build("c"), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("C"), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("creative"), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("CREATIVE"), is(GameMode.CREATIVE));
        assertThat(GameModeUtils.build("1"), is(GameMode.CREATIVE));

        assertThat(GameModeUtils.build("a"), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("A"), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("adventure"), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("ADVENTURE"), is(GameMode.ADVENTURE));
        assertThat(GameModeUtils.build("2"), is(GameMode.ADVENTURE));

        assertThat(GameModeUtils.build("sp"), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("SP"), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("spectator"), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("SPECTATOR"), is(GameMode.SPECTATOR));
        assertThat(GameModeUtils.build("3"), is(GameMode.SPECTATOR));

        assertThat(GameModeUtils.build(""), nullValue());
        assertThat(GameModeUtils.build(null), nullValue());
        assertThat(GameModeUtils.build("unknown mode"), nullValue());
    }

    @Test
    public void testPrettyPrint() {
        assertThat(GameModeUtils.prettyPrint(GameMode.CREATIVE), is("Creative"));
        assertThat(GameModeUtils.prettyPrint(GameMode.ADVENTURE), is("Adventure"));
        assertThat(GameModeUtils.prettyPrint(GameMode.SURVIVAL), is("Survival"));
        assertThat(GameModeUtils.prettyPrint(GameMode.SPECTATOR), is("Spectator"));
        assertThat(GameModeUtils.prettyPrint(null), nullValue());
    }
}
