package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public final class UserListItemMessage implements Message {

    private final Action action;
    private final List<Entry> entries;

    public UserListItemMessage(Action action, List<Entry> entries) {
        this.action = action;
        this.entries = entries;

        for (Entry entry : entries) {
            if (entry.action != action) {
                throw new IllegalArgumentException("Entries must be " + action + ", not " + entry.action);
            }
        }
    }

    public UserListItemMessage(Action action, Entry entry) {
        this(action, Arrays.asList(entry));
    }

    // add

    public static Entry add(PlayerProfile profile) {
        return add(profile, 0, 0, null);
    }

    public static UserListItemMessage addOne(PlayerProfile profile) {
        return new UserListItemMessage(Action.ADD_PLAYER, add(profile));
    }

    public static Entry add(PlayerProfile profile, int gameMode, int ping, BaseComponent... displayName) {
        // TODO measure ping
        return new Entry(profile.getUniqueId(), profile, gameMode, ping, displayName, Action.ADD_PLAYER);
    }

    // gamemode

    public static Entry gameMode(UUID uuid, int gameMode) {
        return new Entry(uuid, null, gameMode, 0, null, Action.UPDATE_GAMEMODE);
    }

    public static UserListItemMessage gameModeOne(UUID uuid, int gameMode) {
        return new UserListItemMessage(Action.UPDATE_GAMEMODE, gameMode(uuid, gameMode));
    }

    // latency

    public static Entry latency(UUID uuid, int ping) {
        return new Entry(uuid, null, 0, ping, null, Action.UPDATE_LATENCY);
    }

    public static UserListItemMessage latencyOne(UUID uuid, int ping) {
        return new UserListItemMessage(Action.UPDATE_LATENCY, latency(uuid, ping));
    }

    // display name

    public static Entry displayName(UUID uuid, BaseComponent... displayName) {
        return new Entry(uuid, null, 0, 0, displayName, Action.UPDATE_DISPLAY_NAME);
    }

    public static UserListItemMessage displayNameOne(UUID uuid, BaseComponent... displayName) {
        return new UserListItemMessage(Action.UPDATE_DISPLAY_NAME, displayName(uuid, displayName));
    }

    // remove

    public static Entry remove(UUID uuid) {
        return new Entry(uuid, null, 0, 0, null, Action.REMOVE_PLAYER);
    }

    public static UserListItemMessage removeOne(UUID uuid) {
        return new UserListItemMessage(Action.REMOVE_PLAYER, remove(uuid));
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
        public final BaseComponent[] displayName;
        private final Action action;
    }
}
