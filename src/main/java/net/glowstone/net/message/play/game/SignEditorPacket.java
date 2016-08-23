package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SignEditorPacket implements Message {

    private final int x, y, z;

}

