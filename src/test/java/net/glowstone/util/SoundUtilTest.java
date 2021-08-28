package net.glowstone.util;

import org.bukkit.SoundCategory;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class SoundUtilTest {

    @Test
    public void testBuildSoundCategory() {
        assertThat(SoundUtil.buildSoundCategory("master"), is(SoundCategory.MASTER));
        assertThat(SoundUtil.buildSoundCategory("MaStEr"), is(SoundCategory.MASTER));
        assertThat(SoundUtil.buildSoundCategory("music"), is(SoundCategory.MUSIC));
        assertThat(SoundUtil.buildSoundCategory("records"), is(SoundCategory.RECORDS));
        assertThat(SoundUtil.buildSoundCategory("weather"), is(SoundCategory.WEATHER));
        assertThat(SoundUtil.buildSoundCategory("blocks"), is(SoundCategory.BLOCKS));
        assertThat(SoundUtil.buildSoundCategory("hostile"), is(SoundCategory.HOSTILE));
        assertThat(SoundUtil.buildSoundCategory("neutral"), is(SoundCategory.NEUTRAL));
        assertThat(SoundUtil.buildSoundCategory("players"), is(SoundCategory.PLAYERS));
        assertThat(SoundUtil.buildSoundCategory("ambient"), is(SoundCategory.AMBIENT));
        assertThat(SoundUtil.buildSoundCategory("voice"), is(SoundCategory.VOICE));

        assertThat(SoundUtil.buildSoundCategory("noise"), nullValue());
        assertThat(SoundUtil.buildSoundCategory(""), nullValue());
        assertThat(SoundUtil.buildSoundCategory(null), nullValue());
    }

}
