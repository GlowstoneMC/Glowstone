package net.glowstone.util.bans;

import net.glowstone.GlowServer;
import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Ban list implementation.
 */
public class GlowBanList extends JsonListFile implements BanList {

    // 2014-02-12 02:27:08 -0600
    static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    static final String FOREVER = "Forever";

    final Type type;
    private final Map<String, GlowBanEntry> entryMap = new HashMap<>();

    /**
     * Creates a new BanList of the given type.
     * @param server The server this BanList belongs to.
     * @param type The type of BanList.
     */
    public GlowBanList(GlowServer server, Type type) {
        super(getFile(server, type));
        this.type = type;
    }

    private static File getFile(GlowServer server, Type type) {
        switch (type) {
            case NAME:
                return new File(server.getConfigDir(), "banned-players.json");
            case IP:
                return new File(server.getConfigDir(), "banned-ips.json");
            default:
                throw new IllegalArgumentException("Unknown BanList type " + type);
        }
    }

    @Override
    public void load() {
        super.load();

        entryMap.clear();
        for (BaseEntry entry : entries) {
            GlowBanEntry banEntry = ((GlowBanEntry) entry);
            entryMap.put(banEntry.getTarget(), banEntry);
        }
    }

    @Override
    protected BaseEntry readEntry(Map<String, String> object) {
        // target
        String target;
        if (type == Type.NAME) {
            target = object.get("name");
        } else if (type == Type.IP) {
            target = object.get("ip");
        } else {
            throw new RuntimeException("Invalid type in readEntry");
        }

        // other data
        try {
            String dateString = object.get("created");
            String source = object.get("source");
            String expiresString = object.get("expires");
            String reason = object.get("reason");

            Date created = DATE_FORMAT.parse(dateString);
            Date expires = null;
            if (!expiresString.equalsIgnoreCase(FOREVER)) {
                expires = DATE_FORMAT.parse(expiresString);
            }

            return new GlowBanEntry(this, target, reason, created, expires, source);
        } catch (ParseException e) {
            throw new RuntimeException("Error reading ban entry", e);
        }
    }

    @Override
    protected void save() {
        entries.clear();
        entries.addAll(entryMap.values());
        super.save();
    }

    /**
     * Remove expired bans.
     */
    private void expungeBans() {
        boolean changed = false;
        Iterator<GlowBanEntry> iter = entryMap.values().iterator();
        while (iter.hasNext()) {
            if (iter.next().isExpired()) {
                iter.remove();
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }

    /**
     * Save a modified GlowBanEntry back to the ban list.
     * @param entry The ban entry
     */
    void putEntry(GlowBanEntry entry) {
        entryMap.put(entry.getTarget(), entry.clone());
        save();
    }

    @Override
    public BanEntry getBanEntry(String target) {
        expungeBans();
        return entryMap.get(target).clone();
    }

    @Override
    public BanEntry addBan(String target, String reason, Date expires, String source) {
        GlowBanEntry entry = new GlowBanEntry(this, target, reason, new Date(), expires, source);
        entryMap.put(target, entry);
        save();
        return entry.clone();
    }

    @Override
    public Set<BanEntry> getBanEntries() {
        expungeBans();
        Set<BanEntry> result = new HashSet<>(entryMap.size());
        for (GlowBanEntry entry : entryMap.values()) {
            result.add(entry.clone());
        }
        return result;
    }

    @Override
    public boolean isBanned(String target) {
        expungeBans();
        return entryMap.containsKey(target);
    }

    @Override
    public void pardon(String target) {
        entryMap.remove(target);
        save();
    }
}
