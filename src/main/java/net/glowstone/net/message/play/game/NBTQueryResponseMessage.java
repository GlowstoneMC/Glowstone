package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class NBTQueryResponseMessage implements Message {
    private final int transactionID;
    private final String nbt;
}
