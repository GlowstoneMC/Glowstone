package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ClientSettingsPacket implements Message {

    private final String locale;
    private final int viewDistance, chatFlags;
    private final boolean chatColors;
    private final int skinFlags, hand;

}
