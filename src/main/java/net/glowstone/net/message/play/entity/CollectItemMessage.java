package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class CollectItemMessage implements Message {

    private final int id;
    private final int collector;
    private final int count;

}
