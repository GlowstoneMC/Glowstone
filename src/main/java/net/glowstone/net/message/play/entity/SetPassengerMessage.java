package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SetPassengerMessage implements Message {

    private final int entityId;
    private final int[] passengers;
}
