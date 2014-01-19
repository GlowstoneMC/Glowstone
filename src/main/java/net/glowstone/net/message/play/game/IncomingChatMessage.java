package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class IncomingChatMessage implements Message {

    private final String text;

    public IncomingChatMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
