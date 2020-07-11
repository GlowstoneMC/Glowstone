package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class QueryEntityNBTMessage implements Message {
    private final int transactionID;
    private final int entityID;
}
