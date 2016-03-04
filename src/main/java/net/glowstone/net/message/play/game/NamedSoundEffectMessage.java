package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class NamedSoundEffectMessage implements Message {

    private final String sound;
    private final SoundCategory soundCategory;
    private final double x, y, z;
    private final float volume, pitch;

    public enum SoundCategory {
        MASTER,
        MUSIC,
        RECORDS,
        WEATHER,
        BLOCKS,
        HOSTILE,
        NEUTRAL,
        PLAYERS,
        AMBIENT,
        VOICE;

        private static SoundCategory[] values = null;

        //Since values() is expensive, let's cache it.
        public static SoundCategory fromInt(int i) {
            if (SoundCategory.values == null) {
                SoundCategory.values = SoundCategory.values();
            }
            return SoundCategory.values[i];
        }
    }
}

