package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetCooldownMessage implements Message {

    private final int itemId;
    private final int cooldownTicks;
}
