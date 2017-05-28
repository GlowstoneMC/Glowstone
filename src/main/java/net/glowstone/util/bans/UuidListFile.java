package net.glowstone.util.bans;

import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.meta.profile.ProfileCache;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Common management for whitelist and ops list files.
 */
public final class UuidListFile extends JsonListFile {

    public UuidListFile(File file) {
        super(file);
    }

    public List<UUID> getUUIDs() {
        List<UUID> result = new ArrayList<>(entries.size());
        result.addAll(entries.stream().map(baseEntry -> ((Entry) baseEntry).uuid).collect(Collectors.toList()));
        return result;
    }

    public List<PlayerProfile> getProfiles() {
        List<PlayerProfile> result = new ArrayList<>(entries.size());
        for (BaseEntry baseEntry : entries) {
            Entry entry = (Entry) baseEntry;
            PlayerProfile profile = ProfileCache.getProfile(entry.uuid);
            if (profile == null) {
                profile = new PlayerProfile(entry.fallbackName, entry.uuid);
            }
            result.add(profile);
        }
        return result;
    }

    public boolean containsUUID(UUID uuid) {
        for (BaseEntry baseEntry : entries) {
            if (uuid.equals(((Entry) baseEntry).uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsProfile(PlayerProfile profile) {
        for (BaseEntry baseEntry : entries) {
            if (profile.getUniqueId().equals(((Entry) baseEntry).uuid)) {
                return true;
            }
        }
        return false;
    }

    public void add(OfflinePlayer player) {
        if (!containsUUID(player.getUniqueId())) {
            entries.add(new Entry(player.getUniqueId(), player.getName()));
            save();
        }
    }

    public void remove(PlayerProfile profile) {
        Iterator<BaseEntry> iter = entries.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            if (((Entry) iter.next()).uuid.equals(profile.getUniqueId())) {
                iter.remove();
                modified = true;
            }
        }
        if (modified) {
            save();
        }
    }

    @Override
    protected BaseEntry readEntry(Map<String, String> map) {
        return new Entry(UUID.fromString(map.get("uuid")), map.get("name"));
    }

    private static class Entry implements BaseEntry {
        private final UUID uuid;
        private final String fallbackName;

        private Entry(UUID uuid, String fallbackName) {
            this.uuid = uuid;
            this.fallbackName = fallbackName;
        }

        @Override
        public Map<String, String> write() {
            PlayerProfile profile = ProfileCache.getProfile(uuid);
            String name = profile != null ? profile.getName() != null ? profile.getName() : fallbackName : fallbackName;
            Map<String, String> result = new HashMap<>(2);
            result.put("uuid", uuid.toString());
            result.put("name", name);
            return result;
        }
    }

}
