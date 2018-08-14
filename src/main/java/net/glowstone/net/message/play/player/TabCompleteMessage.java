package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class TabCompleteMessage implements Message {

    private final int transactionId;
    private final String text;

}

