package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Data
@RequiredArgsConstructor
public final class ChatMessage implements Message {

    private final BaseComponent[] text;
    private final int mode;

    public ChatMessage(BaseComponent... message) {
        this(message, 0);
    }

    public ChatMessage(String text) {
        this(TextComponent.fromLegacyText(text), 0);
    }

    public ChatMessage(String text, int mode) {
        this(TextComponent.fromLegacyText(text), mode);
    }
}
