package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.EntityEffect;

@Data
@RequiredArgsConstructor
public final class EntityStatusMessage implements Message {

    // statuses not included in Bukkit's EntityEffect
    public static final int MYSTERY_LIVING = 0;
    public static final int MYSTERY_PLAYER = 1;
    public static final int GOLEM_FLING_ARMS = 4;
    public static final int EATING_ACCEPTED = 9;
    public static final int ANIMAL_HEARTS = 18;
    public static final int ENABLE_REDUCED_DEBUG_INFO = 22;
    public static final int DISABLE_REDUCED_DEBUG_INFO = 23;

    private final int id, status;

    public EntityStatusMessage(int id, EntityEffect effect) {
        this(id, effect.getData());
    }

}
