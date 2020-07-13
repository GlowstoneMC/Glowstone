package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SelectTradeMessage implements Message {
    private final int selectedSlot;
}
