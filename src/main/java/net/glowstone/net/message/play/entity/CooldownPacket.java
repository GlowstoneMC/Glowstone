package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CooldownPacket implements Message {

    private final int itemID, cooldownTicks;
}
