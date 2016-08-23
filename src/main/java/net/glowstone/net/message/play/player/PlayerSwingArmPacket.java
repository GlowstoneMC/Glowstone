package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class PlayerSwingArmPacket implements Message {

    private final int hand;
}
