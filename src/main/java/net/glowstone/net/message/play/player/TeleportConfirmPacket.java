package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class TeleportConfirmPacket implements Message {

    private final int teleportID;
}
