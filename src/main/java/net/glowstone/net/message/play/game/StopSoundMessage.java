package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.SoundCategory;

import javax.annotation.Nullable;

@Data
public final class StopSoundMessage implements Message {

    @Nullable
    private final SoundCategory source;

    @Nullable
    private final String sound;
}
