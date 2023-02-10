package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ClientSettingsMessage implements Message {

    private final String locale;
    private final int viewDistance;
    private final int chatFlags;
    private final boolean chatColors;
    private final int skinFlags;
    private final int hand;
    private final boolean textFilteringEnabled;
    private final boolean serverListingEnables;

}
