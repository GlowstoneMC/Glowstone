package net.glowstone.util.bans;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.util.bans.JsonListFile.BaseEntry;
import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Implementation of BanEntry.
 */
final class GlowBanEntry implements BaseEntry, BanEntry, Cloneable {

    private final GlowBanList list;
    @Getter
    private final String target;
    private Date created;
    private Date expires;
    @Getter
    @Setter
    private String source;
    @Getter
    @Setter
    private String reason;

    GlowBanEntry(GlowBanList list, String target, String reason, Date created, Date expires,
        String source) {
        if (reason == null) {
            reason = "Banned";
        }
        if (source == null) {
            source = "(Unknown)";
        }

        this.list = list;
        this.target = target;
        this.reason = reason;
        this.source = source;
        this.created = created;
        this.expires = expires;
    }

    @Override
    public Map<String, String> write() {
        Map<String, String> result = new LinkedHashMap<>();

        // target
        if (list.type == Type.NAME) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(target);
            result.put("uuid", player.getUniqueId().toString());
            result.put("name", player.getName());
        } else if (list.type == Type.IP) {
            result.put("ip", target);
        }

        // other data
        result.put("created", GlowBanList.DATE_FORMAT.format(created));
        result.put("source", source);
        result.put("expires",
            expires == null ? GlowBanList.FOREVER : GlowBanList.DATE_FORMAT.format(expires));
        result.put("reason", reason);
        return result;
    }

    @Override
    public Date getCreated() {
        return copy(created);
    }

    @Override
    public void setCreated(Date created) {
        this.created = copy(created);
    }

    @Override
    public Date getExpiration() {
        return copy(expires);
    }

    @Override
    public void setExpiration(Date expiration) {
        expires = copy(expiration);
    }

    @Override
    public void save() {
        list.putEntry(this);
    }

    private Date copy(Date d) {
        return d == null ? null : (Date) d.clone();
    }

    @Override
    protected GlowBanEntry clone() {
        try {
            GlowBanEntry result = (GlowBanEntry) super.clone();
            result.created = copy(created);
            result.expires = copy(expires);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new Error("Failed to clone GlowBanEntry", e);
        }
    }

    boolean isExpired() {
        return expires != null && expires.before(new Date());
    }

    @Override
    public String toString() {
        return "GlowBanEntry{"
            + "type=" + list.type
            + ", target='" + target + '\''
            + ", created=" + created
            + ", expires=" + expires
            + ", source='" + source + '\''
            + ", reason='" + reason + '\''
            + '}';
    }
}
