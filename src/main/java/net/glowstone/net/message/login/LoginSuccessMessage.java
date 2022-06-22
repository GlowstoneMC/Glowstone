package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.UUID;

@Data
public final class LoginSuccessMessage implements Message {

    private final UUID uuid;
    private final String username;

}
