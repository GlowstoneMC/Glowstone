package net.glowstone.util.bans;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.*;

/**
 * Common management for whitelist and ops list files.
 */
public final class UuidListFile extends JsonListFile {

    public UuidListFile(File file) {
        super(file);
    }

    public List<UUID> getUUIDs() {
        List<UUID> result = new ArrayList<>(entries.size());
        for (BaseEntry baseEntry : entries) {
            result.add(((Entry) baseEntry).uuid);
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

    public void add(OfflinePlayer player) {
        if (!containsUUID(player.getUniqueId())) {
            entries.add(new Entry(player.getUniqueId(), player.getName()));
            save();
        }
    }

    public void remove(UUID uuid) {
        Iterator<BaseEntry> iter = entries.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            if (((Entry) iter.next()).uuid.equals(uuid)) {
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

    private class Entry implements BaseEntry {
        private final UUID uuid;
        private final String fallbackName;

        private Entry(UUID uuid, String fallbackName) {
            this.uuid = uuid;
            this.fallbackName = fallbackName;
        }

        @Override
        public Map<String, String> write() {
            String name = Bukkit.getOfflinePlayer(uuid).getName();

            Map<String, String> result = new HashMap<>(2);
            result.put("uuid", uuid.toString());
            result.put("name", name != null ? name : fallbackName);
            return result;
        }
    }

}
