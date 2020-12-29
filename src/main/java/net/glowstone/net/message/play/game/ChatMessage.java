package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;
import net.md_5.bungee.api.ChatMessageType;
import org.json.simple.JSONObject;

@Data
@RequiredArgsConstructor
public final class ChatMessage implements Message {

    private final TextMessage text;
    private final int mode;

    public ChatMessage(JSONObject json) {
        this(new TextMessage(json), 0);
    }

    public ChatMessage(String text) {
        this(new TextMessage(text), 0);
    }

    public ChatMessage(TextMessage text, ChatMessageType type) {
        this(text, type.ordinal());
    }

    public ChatMessage(JSONObject json, ChatMessageType type) {
        this(new TextMessage(json), type.ordinal());
    }

    public ChatMessage(String text, ChatMessageType type) {
        this(new TextMessage(text), type.ordinal());
    }

}
