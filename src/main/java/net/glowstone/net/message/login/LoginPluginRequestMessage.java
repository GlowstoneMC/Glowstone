package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class LoginPluginRequestMessage implements Message {
    private final int transactionId;
    private final String channel;
    private final byte[] data;
}
