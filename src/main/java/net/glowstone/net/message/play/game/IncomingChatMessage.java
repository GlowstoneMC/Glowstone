package net.glowstone.net.message.play.game;

import com.flowpowered.networking.AsyncableMessage;

public final class IncomingChatMessage implements AsyncableMessage {

    private final String text;

    public IncomingChatMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
