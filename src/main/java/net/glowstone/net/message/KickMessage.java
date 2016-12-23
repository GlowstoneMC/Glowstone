package net.glowstone.net.message;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Data
@RequiredArgsConstructor
public final class KickMessage implements Message {

    private final BaseComponent text;

    public KickMessage(String text) {
        this(TextComponent.fromLegacyText(text)[0]);
    }

}
