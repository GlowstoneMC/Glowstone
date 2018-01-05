package net.glowstone.inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import net.glowstone.GlowOfflinePlayer;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
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

    final AtomicReference<PlayerProfile> owner = new AtomicReference<>();

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
                owner.set(((GlowMetaSkull) skull).owner.get());
            } else {
                if (!setOwningPlayerInternal(skull.getOwningPlayer())) {
                    owner.set(UNKNOWN_PLAYER);
                    // necessary to preserve the return value of hasOwner()
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
            result.owner.set((PlayerProfile) data.get("owner"));
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
            tag.putCompound("SkullOwner", owner.get().toNbt());
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.containsKey("SkullOwner")) {
            if (tag.isString("SkullOwner")) {
                owner.set(PlayerProfile.getProfile(tag.getString("SkullOwner")).join());
            } else if (tag.isCompound("SkullOwner")) {
                owner.set(PlayerProfile.fromNbt(tag.getCompound("SkullOwner")).join());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    @Override
    public String getOwner() {
        return hasOwner() ? owner.get().getName() : null;
    }

    @Override
    public boolean hasOwner() {
        return owner.get() != null;
    }

    @Override
    public boolean setOwner(String name) {
        PlayerProfile owner = PlayerProfile.getProfile(name).join();
        if (owner == null) {
            return false;
        }
        this.owner.set(owner);
        return true;
    }

    @Override
    public OfflinePlayer getOwningPlayer() {
        return ((GlowServer) Bukkit.getServer()).getOfflinePlayer(owner.get());
    }

    /**
     * {@inheritDoc}
     *
     * <p>When this returns false, it may still succeed asynchronously.
     */
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
            this.owner.set(impl.getProfile());
            return true;
        } else if (owningPlayer instanceof GlowPlayer) {
            this.owner.set(((GlowPlayer) owningPlayer).getProfile());
            return true;
        } else {
            CompletableFuture<PlayerProfile> profileFuture = PlayerProfile
                    .getProfile(owningPlayer.getName());
            PlayerProfile profile = profileFuture.getNow(null);
            if (profile != null) {
                this.owner.set(profile);
                return true;
            } else {
                profileFuture.thenAcceptAsync(this.owner::set);
                return false;
            }
        }
    }
}
