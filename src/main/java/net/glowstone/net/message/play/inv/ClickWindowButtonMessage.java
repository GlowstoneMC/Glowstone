package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ClickWindowButtonMessage implements Message {

    private final int window;
    private final int button;

}
