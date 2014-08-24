package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.PlayerProfile;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class UserListItemMessage implements Message {

    private final Action action;
    private final List<Entry> entries;

    private UserListItemMessage(Action action, List<Entry> entries) {
        this.action = action;
        this.entries = entries;
    }

    // todo: allow for the fancier options (game mode, ping, display name)
    // and for lists

    public static UserListItemMessage add(PlayerProfile profile) {
        return new UserListItemMessage(Action.ADD_PLAYER, Arrays.<Entry>asList(new AddEntry(profile)));
    }

    public static UserListItemMessage add(List<PlayerProfile> profiles) {
        List<Entry> list = new ArrayList<>(profiles.size());
        for (PlayerProfile profile : profiles) {
            list.add(new AddEntry(profile));
        }

        return new UserListItemMessage(Action.ADD_PLAYER, list);
    }

    public static UserListItemMessage remove(UUID uuid) {
        return new UserListItemMessage(Action.REMOVE_PLAYER, Arrays.asList(new Entry(uuid)));
    }

    public Action getAction() {
        return action;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return "UserListItemMessage{" +
                "action=" + action +
                ", entries=" + entries +
                '}';
    }

    public static enum Action {
        ADD_PLAYER,
        UPDATE_GAMEMODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER
    }

    public static class Entry {
        public final UUID uuid;

        Entry(UUID uuid) {
            this.uuid = uuid;
        }
    }

    public static class AddEntry extends Entry {
        public final PlayerProfile profile;
        public final int gameMode;
        public final int ping;
        public final JSONObject displayName;

        public AddEntry(PlayerProfile profile) {
            super(profile.getUniqueId());
            this.profile = profile;
            this.gameMode = 0;
            this.ping = 0;
            this.displayName = null;
        }
    }
}
