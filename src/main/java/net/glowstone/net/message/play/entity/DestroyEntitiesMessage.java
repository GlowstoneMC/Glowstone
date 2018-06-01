package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Data;

@Data
public final class DestroyEntitiesMessage implements Message {

    private final List<Integer> ids;

}
