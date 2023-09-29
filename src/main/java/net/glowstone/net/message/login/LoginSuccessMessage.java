package net.glowstone.net.message.login;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data
public final class LoginSuccessMessage implements Message {

    private final UUID uuid;
    private final String username;
    private final ArrayList<LoginSuccessProperties> properties;

    @Data
    public class LoginSuccessProperties {
        private final String name;
        private final String value;
        private final boolean isSigned;
        private final String signature;
    }

}
