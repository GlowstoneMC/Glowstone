package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class LoginPluginResponseMessage implements Message {
    private final int transactionId;
    private final boolean successful;
    private final ByteBuf data;
}
