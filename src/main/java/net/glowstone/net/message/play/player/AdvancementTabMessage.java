package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class AdvancementTabMessage implements Message {

    public static final int ACTION_OPEN = 0;
    public static final int ACTION_CLOSE = 1;

    private final int action;
    private final String tabId;

    public AdvancementTabMessage() {
        this(ACTION_CLOSE, null);
    }

    public AdvancementTabMessage(int action, String tabId) {
        this.action = action;
        this.tabId = tabId;
    }
}
