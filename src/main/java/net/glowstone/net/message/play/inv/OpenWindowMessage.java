package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Data
@RequiredArgsConstructor
public final class OpenWindowMessage implements Message {

    private final int id;
    private final String type;
    private final BaseComponent[] title;
    private final int slots, entityId;

    public OpenWindowMessage(int id, String type, String title, int slots) {
        this(id, type, TextComponent.fromLegacyText(title), slots, 0);
    }

}
