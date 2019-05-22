package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class LoginPluginRequestMessage implements Message {
    private final int transactionId;
    private final String channel;
    private final ByteBuf data;
}
