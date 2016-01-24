package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public class TeleportConfirmMessage implements Message {

    private final int teleportID;
}
