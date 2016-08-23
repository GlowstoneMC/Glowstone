package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PlayerActionPacket implements Message {

    private final int id, action, jumpBoost;

}

