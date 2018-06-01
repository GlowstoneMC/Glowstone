package net.glowstone.entity.meta;

import lombok.Data;
import net.glowstone.net.message.play.game.ClientSettingsMessage;

/**
 * Container for settings which the client communicates to the server.
 */
@Data
public class ClientSettings {

    public static final ClientSettings DEFAULT = new ClientSettings("en_US", 8, 0, true, 127, 0);

    public static final int CHAT_ENABLED = 0;
    public static final int CHAT_COMMANDS_ONLY = 1;
    public static final int CHAT_HIDDEN = 2;

    public static final int SKIN_CAPE = 1;
    public static final int SKIN_JACKET = 1 << 1;
    public static final int SKIN_LEFT_SLEEVE = 1 << 2;
    public static final int SKIN_RIGHT_SLEEVE = 1 << 3;
    public static final int SKIN_LEFT_PANTS = 1 << 4;
    public static final int SKIN_RIGHT_PANTS = 1 << 5;
    public static final int SKIN_HAT = 1 << 6;

    private final String locale;
    private final int chatFlags;
    private final boolean chatColors;
    private final int skinFlags;
    private final int mainHand;
    private int viewDistance;

    /**
     * Construct the ClientSettings from a ClientSettingsMessage.
     *
     * @param msg The message sent by the client.
     */
    public ClientSettings(ClientSettingsMessage msg) {
        this(msg.getLocale(), msg.getViewDistance(), msg.getChatFlags(), msg.isChatColors(),
            msg.getSkinFlags(), msg.getHand());
    }

    /**
     * Construct a ClientSettings.
     *
     * @param locale The locale, in a form like "en_US".
     * @param viewDistance The view distance, in chunks.
     * @param chatFlags The client's chat flags.
     * @param chatColors Whether the client has chat colors enabled.
     * @param skinFlags The client's skin flags.
     * @param mainHand The main hand of the player.
     */
    public ClientSettings(String locale, int viewDistance, int chatFlags, boolean chatColors,
        int skinFlags, int mainHand) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColors = chatColors;
        this.skinFlags = skinFlags;
        this.mainHand = mainHand;
    }

    /**
     * Get whether player chat should be shown based on chat flags.
     *
     * @return Whether player chat is shown.
     */
    public boolean showChat() {
        return chatFlags == CHAT_ENABLED;
    }

    /**
     * Get whether command output should be shown based on chat flags.
     *
     * @return Whether command output is shown.
     */
    public boolean showCommands() {
        return chatFlags != CHAT_HIDDEN;
    }

    /**
     * Get if the client has chat colors enabled.
     *
     * @return Whether chat colors are enabled.
     */
    public boolean showChatColors() {
        return chatColors;
    }

}
