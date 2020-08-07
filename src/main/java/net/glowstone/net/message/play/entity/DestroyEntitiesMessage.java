package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.List;

@Data
public final class DestroyEntitiesMessage implements Message {

    private final List<Integer> ids;

}
