package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class CombatEventMessage implements Message {

    public enum Event {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DEAD;

        public static Event getAction(int id) {
            Event[] values = values();
            return id < 0 || id >= values.length ? null : values[id];
        }
    }

    private final Event event;
    private final int duration;
    private final int entityID, playerID;
    private final String message;

    // base constructor
    private CombatEventMessage(Event event, int duration, int entityID, int playerID, String message) {
        this.event = event;
        this.duration = duration;
        this.entityID = entityID;
        this.playerID = playerID;
        this.message = message;
    }

    // BEGIN_COMBAT
    public CombatEventMessage(Event event) {
        this(event, 0, 0, 0, null);
    }

    // END_COMBAT
    public CombatEventMessage(Event event, int duration, int entityID) {
        this(event, duration, entityID, 0, null);
    }

    // ENTITY_DEAD
    public CombatEventMessage(Event event, int entityID, int playerID, String message) {
        this(event, 0, entityID, playerID, message);
    }

    public Event getEvent() {
        return event;
    }

    public int getDuration() {
        return duration;
    }

    public int getEntityID() {
        return entityID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CombatEventMessage{" +
                "event=" + event +
                ", duration=" + duration +
                ", entityID=" + entityID +
                ", playerID=" + playerID +
                ", message='" + message + '\'' +
                '}';
    }
}
