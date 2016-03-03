package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class LoginSuccessMessage implements Message {

    private final String uuid;
    private final String username;

}
