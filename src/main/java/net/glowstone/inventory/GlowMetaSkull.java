package net.glowstone.inventory;

import java.util.Map;
import java.util.UUID;
import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GlowMetaSkull extends GlowMetaItem implements SkullMeta {

    private static final PlayerProfile UNKNOWN_PLAYER = new PlayerProfile("MHF_Steve",
            new UUID(0xc06f89064c8a4911L, 0x9c29ea1dbd1aab82L));
    PlayerProfile owner;

    /**
     * Creates an instance by copying from the given {@link ItemMeta}. If that item is another
     * {@link SkullMeta} with an owner, attempts to copy the owning player.
     * @param meta the {@link ItemMeta} to copy
     */
    public GlowMetaSkull(ItemMeta meta) {
        super(meta);
        if (!(meta instanceof SkullMeta)) {
            return;
        }
        SkullMeta skull = (SkullMeta) meta;
        if (skull.hasOwner()) {
            if (skull instanceof GlowMetaSkull) {
                owner = ((GlowMetaSkull) skull).owner;
            } else {
                if (!setOwningPlayerInternal(skull.getOwningPlayer())) {
                    owner = UNKNOWN_PLAYER; // necessary to preserve the return value of hasOwner()
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internal stuff

    /**
     * Deserializes an instance as specified in {@link
     * org.bukkit.configuration.serialization.ConfigurationSerializable}.
     *
     * @param data a serialized instance
     * @return the instance as a GlowMetaSkull
     */
    public static GlowMetaSkull deserialize(Map<String, Object> data) {
        GlowMetaSkull result = new GlowMetaSkull(null);
        if (data.containsKey("owner")) {
            result.owner = (PlayerProfile) data.get("owner");
        }
        return result;
    }

    @Override
    public SkullMeta clone() {
        return new GlowMetaSkull(this);
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.SKULL_ITEM;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "SKULL");
        if (hasOwner()) {
            result.put("owner", owner);
        }
        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        if (hasOwner()) {
            tag.putCompound("SkullOwner", owner.toNBT());
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.containsKey("SkullOwner")) {
            if (tag.isString("SkullOwner")) {
                owner = PlayerProfile.getProfile(tag.getString("SkullOwner")).join();
            } else if (tag.isCompound("SkullOwner")) {
                owner = PlayerProfile.fromNBT(tag.getCompound("SkullOwner")).join();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    @Override
    public String getOwner() {
        return hasOwner() ? owner.getName() : null;
    }

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

    @Override
    public boolean setOwner(String name) {
        PlayerProfile owner = PlayerProfile.getProfile(name).join();
        if (owner == null) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    @Override
    public OfflinePlayer getOwningPlayer() {
        return ((GlowServer) Bukkit.getServer()).getOfflinePlayer(owner);
    }

    @Override
    public boolean setOwningPlayer(OfflinePlayer owningPlayer) {
        if (hasOwner()) {
            return false;
        }
        return setOwningPlayerInternal(owningPlayer);
    }

    private boolean setOwningPlayerInternal(OfflinePlayer owningPlayer) {
        if (owningPlayer instanceof GlowOfflinePlayer) {
            GlowOfflinePlayer impl = (GlowOfflinePlayer) owningPlayer;
            this.owner = impl.getProfile();
            return true;
        } else {
            PlayerProfile profile = PlayerProfile.getProfile(owningPlayer.getName()).getNow(null);
            if (profile != null) {
                this.owner = profile;
                return true;
            }
            return false;
        }
    }
}
