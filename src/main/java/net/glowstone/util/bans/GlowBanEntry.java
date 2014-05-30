package net.glowstone.util.bans;

import org.bukkit.BanEntry;

import java.util.Date;

/**
 * Implementation of BanEntry.
 */
final class GlowBanEntry implements BanEntry, Cloneable {

    private final GlowBanList list;
    private final String target;
    private Date created, expires;
    private String source, reason;

    GlowBanEntry(GlowBanList list, String target, String reason, Date created, Date expires, String source) {
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

    public String getTarget() {
        return target;
    }

    public Date getCreated() {
        return copy(created);
    }

    public void setCreated(Date created) {
        this.created = copy(created);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getExpiration() {
        return copy(expires);
    }

    public void setExpiration(Date expiration) {
        expires = copy(expiration);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void save() {
        list.putEntry(this);
    }

    private Date copy(Date d) {
        return d == null ? null : (Date) d.clone();
    }

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
}
