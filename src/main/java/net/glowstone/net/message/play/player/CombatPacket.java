package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CombatPacket implements Message {

    private final Event event;
    private final int duration;
    private final int entityID, playerID;
    private final TextMessage message;

    // BEGIN_COMBAT
    public CombatPacket(Event event) {
        this(event, 0, 0, 0, null);
    }

    // END_COMBAT
    public CombatPacket(Event event, int duration, int entityID) {
        this(event, duration, entityID, 0, null);
    }

    // ENTITY_DEAD
    public CombatPacket(Event event, int entityID, int playerID, TextMessage message) {
        this(event, 0, entityID, playerID, message);
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
