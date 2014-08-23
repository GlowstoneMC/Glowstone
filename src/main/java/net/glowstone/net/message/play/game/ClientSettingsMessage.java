package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class ClientSettingsMessage implements Message {

    private final String locale;
    private final int viewDistance, chatFlags;
    private final boolean chatColors;
    private final int skinFlags;

    public ClientSettingsMessage(String locale, int viewDistance, int chatFlags, boolean chatColors, int skinFlags) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColors = chatColors;
        this.skinFlags = skinFlags;
    }

    @Override
    public String toString() {
        return "ClientSettingsMessage{" +
                "locale='" + locale + '\'' +
                ", viewDistance=" + viewDistance +
                ", chatFlags=" + chatFlags +
                ", chatColors=" + chatColors +
                ", skinFlags=" + skinFlags +
                '}';
    }

    public String getLocale() {
        return locale;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    public int getChatFlags() {
        return chatFlags;
    }

    public boolean getChatColors() {
        return chatColors;
    }

    public int getSkinFlags() {
        return skinFlags;
    }

}
