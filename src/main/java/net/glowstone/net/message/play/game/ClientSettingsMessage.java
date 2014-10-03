package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class ClientSettingsMessage implements Message {

    private final String locale;
    private final int viewDistance, chatFlags;
    private final boolean chatColors;
    private final int skinFlags;

}
