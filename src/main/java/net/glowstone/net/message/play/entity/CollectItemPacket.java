package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class CollectItemPacket implements Message {

    private final int id, collector;

}
