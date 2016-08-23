package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.util.TextMessage;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public final class PlayerListItemPacket implements Message {

    private final Action action;
    private final List<Entry> entries;

    public PlayerListItemPacket(Action action, List<Entry> entries) {
        this.action = action;
        this.entries = entries;

        for (Entry entry : entries) {
            if (entry.action != action) {
                throw new IllegalArgumentException("Entries must be " + action + ", not " + entry.action);
            }
        }
    }

    public PlayerListItemPacket(Action action, Entry entry) {
        this(action, Arrays.asList(entry));
    }

    // add

    public static Entry add(PlayerProfile profile) {
        return add(profile, 0, 0, null);
    }

    public static Entry add(PlayerProfile profile, int gameMode, int ping, TextMessage displayName) {
        // TODO measure ping
        return new Entry(profile.getUniqueId(), profile, gameMode, ping, displayName, Action.ADD_PLAYER);
    }

    public static PlayerListItemPacket addOne(PlayerProfile profile) {
        return new PlayerListItemPacket(Action.ADD_PLAYER, add(profile));
    }

    // gamemode

    public static Entry gameMode(UUID uuid, int gameMode) {
        return new Entry(uuid, null, gameMode, 0, null, Action.UPDATE_GAMEMODE);
    }

    public static PlayerListItemPacket gameModeOne(UUID uuid, int gameMode) {
        return new PlayerListItemPacket(Action.UPDATE_GAMEMODE, gameMode(uuid, gameMode));
    }

    // latency

    public static Entry latency(UUID uuid, int ping) {
        return new Entry(uuid, null, 0, ping, null, Action.UPDATE_LATENCY);
    }

    public static PlayerListItemPacket latencyOne(UUID uuid, int ping) {
        return new PlayerListItemPacket(Action.UPDATE_LATENCY, latency(uuid, ping));
    }

    // display name

    public static Entry displayName(UUID uuid, TextMessage displayName) {
        return new Entry(uuid, null, 0, 0, displayName, Action.UPDATE_DISPLAY_NAME);
    }

    public static PlayerListItemPacket displayNameOne(UUID uuid, TextMessage displayName) {
        return new PlayerListItemPacket(Action.UPDATE_DISPLAY_NAME, displayName(uuid, displayName));
    }

    // remove

    public static Entry remove(UUID uuid) {
        return new Entry(uuid, null, 0, 0, null, Action.REMOVE_PLAYER);
    }

    public static PlayerListItemPacket removeOne(UUID uuid) {
        return new PlayerListItemPacket(Action.REMOVE_PLAYER, remove(uuid));
    }

    // inner classes

    public enum Action {
        ADD_PLAYER,
        UPDATE_GAMEMODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER
    }

    @Data
    public static final class Entry {
        public final UUID uuid;
        public final PlayerProfile profile;
        public final int gameMode;
        public final int ping;
        public final TextMessage displayName;
        private final Action action;
    }
}
