package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class TransactionMessage implements Message {

    private final int id;
    private final int transaction;
    private final boolean accepted;

}
