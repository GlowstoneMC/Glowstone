package net.glowstone.util.bans;

import net.glowstone.GlowServer;
import org.bukkit.BanEntry;
import org.bukkit.BanList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Ban list implementation.
 */
public class GlowBanList implements BanList {

    // 2014-02-12 02:27:08 -0600
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private static final String FOREVER = "Forever";

    private final Type type;
    private final File file;
    private final Map<String, GlowBanEntry> entries = new HashMap<>();

    /**
     * Creates a new BanList of the given type.
     * @param server The server this BanList belongs to.
     * @param type The type of BanList.
     */
    public GlowBanList(GlowServer server, Type type) {
        this.type = type;

        String filename;
        if (type == Type.NAME) {
            filename = "banned-names.txt";
        } else if (type == Type.IP) {
            filename = "banned-ips.txt";
        } else {
            throw new IllegalArgumentException("Unknown BanList type " + type);
        }

        file = new File(server.getConfigDir(), filename);
    }

    /**
     * Load the bans from file.
     */
    public void load() {
        entries.clear();

        if (!file.exists()) return;

        int lineNumber = -1;
        try (Scanner scan = new Scanner(file)) {
            lineNumber = 0;
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                ++lineNumber;
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                //# victim name | ban date | banned by | banned until | reason
                String[] split = line.split("\\|");
                String target = split[0];
                String dateString = split[1];
                String source = split[2];
                String expiresString = split[3];
                // if the reason is empty it is not included in split()
                String reason = split.length > 4 ? split[4] : null;

                Date created = DATE_FORMAT.parse(dateString);
                Date expires = null;
                if (!expiresString.equalsIgnoreCase(FOREVER)) {
                    expires = DATE_FORMAT.parse(expiresString);
                }

                GlowBanEntry entry = new GlowBanEntry(this, target, reason, created, expires, source);
                if (!entry.isExpired()) {
                    entries.put(target, entry);
                }
            }
        } catch (Exception e) {
            String lineNumberText = lineNumber < 0 ? "" : " (line " + lineNumber + ")";
            GlowServer.logger.log(Level.SEVERE, "Failed to read bans from " + file + lineNumberText, e);
        }

        save();
    }

    /**
     * Saves the bans to file.
     */
    private void save() {
        //# victim name | ban date | banned by | banned until | reason

        File parent = file.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            GlowServer.logger.log(Level.SEVERE, "Failed to create directory to save bans: " + parent);
            return;
        }

        try (FileWriter out = new FileWriter(file)) {
            out.write("# Updated " + DATE_FORMAT.format(new Date()) + " by Glowstone " + GlowServer.GAME_VERSION + "\n");
            out.write("# victim name | ban date | banned by | banned until | reason\n");
            out.write("\n");

            for (GlowBanEntry entry : entries.values()) {
                if (entry.isExpired()) continue;

                out.write(entry.getTarget());
                out.write('|');
                out.write(DATE_FORMAT.format(entry.getCreated()));
                out.write('|');
                out.write(entry.getSource());
                out.write('|');
                if (entry.getExpiration() == null) {
                    out.write(FOREVER);
                } else {
                    out.write(DATE_FORMAT.format(entry.getExpiration()));
                }
                out.write('|');
                out.write(entry.getReason());
                out.write('\n');
            }
        } catch (IOException e) {
            GlowServer.logger.log(Level.SEVERE, "Failed to save bans to " + file, e);
        }
    }

    /**
     * Remove expired bans.
     */
    private void expungeBans() {
        boolean changed = false;
        Iterator<GlowBanEntry> iter = entries.values().iterator();
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
        entries.put(entry.getTarget(), entry.clone());
        save();
    }

    public BanEntry getBanEntry(String target) {
        expungeBans();
        return entries.get(target).clone();
    }

    public BanEntry addBan(String target, String reason, Date expires, String source) {
        GlowBanEntry entry = new GlowBanEntry(this, target, reason, new Date(), expires, source);
        entries.put(target, entry);
        save();
        return entry.clone();
    }

    public Set<BanEntry> getBanEntries() {
        expungeBans();
        Set<BanEntry> result = new HashSet<>(entries.size());
        for (GlowBanEntry entry : entries.values()) {
            result.add(entry.clone());
        }
        return result;
    }

    public boolean isBanned(String target) {
        expungeBans();
        return entries.containsKey(target);
    }

    public void pardon(String target) {
        entries.remove(target);
        save();
    }
}
