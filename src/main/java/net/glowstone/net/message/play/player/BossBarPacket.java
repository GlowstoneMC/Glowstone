package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.glowstone.util.TextMessage;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class BossBarPacket implements Message {

    private final UUID uuid;
    private final Action action;
    private final TextMessage title;
    private final float health;
    private final Color color;
    private final Division division;
    private final byte flags;


    //For action 1, remove
    public BossBarPacket(UUID uuid, Action action) {
        this(uuid, action, null, 0f, null, null, (byte) 0);
    }

    //For action 2, update health
    public BossBarPacket(UUID uuid, Action action, float health) {
        this(uuid, action, null, health, null, null, (byte) 0);
    }

    //For action 3, update title
    public BossBarPacket(UUID uuid, Action action, TextMessage title) {
        this(uuid, action, title, 0f, null, null, (byte) 0);
    }

    //For action 4, update style
    public BossBarPacket(UUID uuid, Action action, Color color, Division division) {
        this(uuid, action, null, 0f, color, division, (byte) 0);
    }

    //For action 5, update flags
    public BossBarPacket(UUID uuid, Action action, byte flags) {
        this(uuid, action, null, 0f, null, null, flags);
    }

    public enum Action {
        ADD,
        REMOVE,
        UPDATE_HEALTH,
        UPDATE_TITLE,
        UPDATE_STYLE,
        UPDATE_FLAGS;

        private static Action[] values;

        //Since values() is expensive, let's cache it.
        public static Action fromInt(int i) {
            if (values == null) {
                values = Action.values();
            }
            return values[i];
        }
    }

    public enum Color {
        PINK,
        BLUE,
        RED,
        GREEN,
        YELLOW,
        PURPLE,
        WHITE;

        private static Color[] values;

        //Since values() is expensive, let's cache it.
        public static Color fromInt(int i) {
            if (values == null) {
                values = Color.values();
            }
            return values[i];
        }
    }

    public enum Division {
        NO_DIVISION,
        SIX_NOTCHES,
        TEN_NOTCHES,
        TWELVE_NOTCHES,
        TWENTY_NOTCHES;

        private static Division[] values;

        //Since values() is expensive, let's cache it.
        public static Division fromInt(int i) {
            if (values == null) {
                values = Division.values();
            }
            return values[i];
        }
    }
}
