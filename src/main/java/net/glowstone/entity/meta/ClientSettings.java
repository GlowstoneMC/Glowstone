package net.glowstone.entity.meta;

import net.glowstone.net.message.play.game.ClientSettingsMessage;

/**
 * Container for settings which the client communicates to the server.
 */
public final class ClientSettings {

    public static final ClientSettings DEFAULT = new ClientSettings("en_US", 8, 0, true, 127);

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
    private final int viewDistance, chatFlags;
    private final boolean chatColors;
    private final int skinFlags;

    /**
     * Construct the ClientSettings from a ClientSettingsMessage.
     * @param msg The message sent by the client.
     */
    public ClientSettings(ClientSettingsMessage msg) {
        this(msg.getLocale(), msg.getViewDistance(), msg.getChatFlags(), msg.isChatColors(), msg.getSkinFlags());
    }

    /**
     * Construct a ClientSettings.
     * @param locale The locale, in a form like "en_US".
     * @param viewDistance The view distance, in chunks.
     * @param chatFlags The client's chat flags.
     * @param chatColors Whether the client has chat colors enabled.
     * @param skinFlags The client's skin flags.
     */
    public ClientSettings(String locale, int viewDistance, int chatFlags, boolean chatColors, int skinFlags) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColors = chatColors;
        this.skinFlags = skinFlags;
    }

    /**
     * Get the locale, in a form like "en_US".
     * @return The locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Get the client's view distance, in chunks.
     * @return The view distance.
     */
    public int getViewDistance() {
        return viewDistance;
    }

    /**
     * Get the client's chat flags.
     * @return The chat flags.
     */
    public int getChatFlags() {
        return chatFlags;
    }

    /**
     * Get whether player chat should be shown based on chat flags.
     * @return Whether player chat is shown.
     */
    public boolean showChat() {
        return chatFlags == CHAT_ENABLED;
    }

    /**
     * Get whether command output should be shown based on chat flags.
     * @return Whether command output is shown.
     */
    public boolean showCommands() {
        return chatFlags != CHAT_HIDDEN;
    }

    /**
     * Get if the client has chat colors enabled.
     * @return Whether chat colors are enabled.
     */
    public boolean showChatColors() {
        return chatColors;
    }

    /**
     * Get the client's skin flags.
     * @return The skin flags.
     */
    public int getSkinFlags() {
        return skinFlags;
    }

    @Override
    public String toString() {
        return "ClientSettings{" +
                "locale='" + locale + '\'' +
                ", viewDistance=" + viewDistance +
                ", chatFlags=" + chatFlags +
                ", chatColors=" + chatColors +
                ", skinFlags=" + skinFlags +
                '}';
    }
}
