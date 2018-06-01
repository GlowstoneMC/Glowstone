package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CombatEventMessage implements Message {

    private final Event event;
    private final int duration;
    private final int entityId;
    private final int playerId;
    private final TextMessage message;

    // BEGIN_COMBAT
    public CombatEventMessage(Event event) {
        this(event, 0, 0, 0, null);
    }

    // END_COMBAT
    public CombatEventMessage(Event event, int duration, int entityId) {
        this(event, duration, entityId, 0, null);
    }

    // ENTITY_DEAD
    public CombatEventMessage(Event event, int entityId, int playerId, TextMessage message) {
        this(event, 0, entityId, playerId, message);
    }

    public enum Event {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DEAD;

        public static Event getAction(int id) {
            Event[] values = values();
            return id < 0 || id >= values.length ? null : values[id];
        }
    }

}
