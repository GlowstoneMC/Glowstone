package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import javax.annotation.Nullable;
import lombok.Data;
import org.bukkit.SoundCategory;

@Data
public final class StopSoundMessage implements Message {

    @Nullable
    private final SoundCategory source;

    @Nullable
    private final String sound;
}
