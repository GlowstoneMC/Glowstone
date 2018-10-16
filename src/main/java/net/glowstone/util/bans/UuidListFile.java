package net.glowstone.util.bans;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.entity.meta.profile.ProfileCache;
import net.glowstone.util.UuidUtils;
import org.bukkit.OfflinePlayer;

/**
 * Common management for whitelist and ops list files.
 */
public final class UuidListFile extends JsonListFile {

    private Map<UUID, Entry> entriesByUuid = new ConcurrentHashMap<>();

    @Override
    public void load() {
        super.load();
        for (BaseEntry entry : entries) {
            entriesByUuid.put(((Entry) entry).uuid, (Entry) entry);
        }
    }

    public UuidListFile(File file) {
        super(file);
    }

    /**
     * Returns a {@link GlowPlayerProfile} for each player whose UUID is in the list file.
     *
     * @return a list of {@link GlowPlayerProfile} instances
     */
    public List<GlowPlayerProfile> getProfiles() {
        return entries
                .stream()
                .parallel()
                .map(entry -> {
                    try {
                        return ProfileCache.getProfile(((Entry) entry).uuid).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Searches for a UUID.
     *
     * @param uuid the UUID to search for
     * @return true if the UUID is present; false otherwise
     */
    public boolean containsUuid(UUID uuid) {
        return entriesByUuid.containsKey(uuid);
    }

    /**
     * Checks whether the player with a given UUID is in this list.
     *
     * @param profile the player whose UUID will be looked up
     * @return whether the player is on this list
     */
    public boolean containsProfile(GlowPlayerProfile profile) {
        return containsUuid(profile.getId());
    }

    /**
     * If the given player is not already on this list, adds that player and saves the change to
     * disk.
     *
     * @param player the player to add
     */
    public void add(OfflinePlayer player) {
        UUID playerUuid = player.getUniqueId();
        if (!containsUuid(playerUuid)) {
            Entry newEntry = new Entry(playerUuid, player.getName());
            entries.add(newEntry);
            entriesByUuid.put(playerUuid, newEntry);
            save();
        }
    }

    /**
     * If the given player is on this list, removes that player and saves the change to disk.
     *
     * @param profile the player to remove
     */
    public void remove(GlowPlayerProfile profile) {
        UUID playerUuid = profile.getId();
        entriesByUuid.remove(playerUuid);
        // FIXME: Unnecessary linear time
        Iterator<BaseEntry> iter = entries.iterator();
        boolean modified = false;
        while (iter.hasNext()) {
            if (((Entry) iter.next()).uuid.equals(playerUuid)) {
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
        return new Entry(UuidUtils.fromString(map.get("uuid")), map.get("name"));
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
            GlowPlayerProfile profile = ProfileCache.getProfile(uuid).join();
            String name = profile.getName() != null ? profile.getName() : fallbackName;
            Map<String, String> result = new HashMap<>(2);
            result.put("uuid", UuidUtils.toString(uuid));
            result.put("name", name);
            return result;
        }
    }

}
