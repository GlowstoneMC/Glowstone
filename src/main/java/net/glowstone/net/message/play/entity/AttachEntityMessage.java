package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class AttachEntityMessage implements Message {

    private final int attached;
    private final int holding;

}
