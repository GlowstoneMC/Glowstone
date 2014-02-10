package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class TabCompleteMessage implements Message {

    private final String text;

    public TabCompleteMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "TabCompleteMessage{" +
                "text='" + text + '\'' +
                '}';
    }
}

