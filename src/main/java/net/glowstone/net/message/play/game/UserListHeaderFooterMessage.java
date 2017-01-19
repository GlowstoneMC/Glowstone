package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;

@Data
public final class UserListHeaderFooterMessage implements Message {
    private final BaseComponent[] header, footer;
}
