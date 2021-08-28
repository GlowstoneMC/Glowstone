package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class LoginPluginResponseMessage implements Message {
    private final int transactionId;
    private final boolean successful;
    private final byte[] data;
}
