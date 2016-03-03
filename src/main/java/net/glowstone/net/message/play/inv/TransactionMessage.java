package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class TransactionMessage implements Message {

    private final int id, transaction;
    private final boolean accepted;

}
