package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class QueryBlockNBTMessage implements Message {
    private final int transactionID;
    private final int x;
    private final int y;
    private final int z;
}
