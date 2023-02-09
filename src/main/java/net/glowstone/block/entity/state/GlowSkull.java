package net.glowstone.block.entity.state;

import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.block.entity.SkullEntity;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.util.Position;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlowSkull extends GlowBlockState implements Skull {

    @Getter
    private SkullType skullType;
    private GlowPlayerProfile owner;
    @Getter
    @Setter
    private BlockFace rotation;

    /**
     * Creates the instance for the given block.
     *
     * @param block a head/skull block
     */
    public GlowSkull(GlowBlock block) {
        super(block);
        skullType = BlockSkull.getType(getBlockEntity().getType());
        rotation = Position.getDirection(getBlockEntity().getRotation());
        owner = getBlockEntity().getOwner();
    }

    public SkullEntity getBlockEntity() {
        return (SkullEntity) getBlock().getBlockEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            SkullEntity skull = getBlockEntity();
            skull.setType(BlockSkull.getType(skullType));
            if (BlockSkull.canRotate((org.bukkit.material.Skull) getBlock().getState().getData())) {
                skull.setRotation(Position.getDirection(rotation));
            }
            if (skullType == SkullType.PLAYER) {
                skull.setOwner(owner);
            }
            getBlockEntity().updateInRange();
        }
        return result;
    }

    @Override
    public boolean hasOwner() {
        return owner != null;
    }

    @Override
    public String getOwner() {
        return hasOwner() ? owner.getName() : null;
    }

    @Override
    public boolean setOwner(String name) {
        GlowPlayerProfile owner = GlowPlayerProfile.getProfile(name).join();
        if (owner == null) {
            return false;
        }
        this.owner = owner;
        setSkullType(SkullType.PLAYER);
        return true;
    }

    @Override
    public OfflinePlayer getOwningPlayer() {
        return Bukkit.getOfflinePlayer(owner.getId());
    }

    @Override
    public void setOwningPlayer(OfflinePlayer offlinePlayer) {
        owner = new GlowPlayerProfile(offlinePlayer.getName(), offlinePlayer.getUniqueId(), true);
    }

    @Override
    public @Nullable PlayerProfile getPlayerProfile() {
        return owner;
    }

    @Override
    public org.bukkit.profile.@Nullable PlayerProfile getOwnerProfile() {
        return owner;
    }

    @Override
    public void setOwnerProfile(org.bukkit.profile.@Nullable PlayerProfile profile) {
        owner = (GlowPlayerProfile) profile;
    }

    @Override
    public @Nullable NamespacedKey getNoteBlockSound() {
        return null;
    }

    @Override
    public void setNoteBlockSound(@Nullable NamespacedKey noteBlockSound) {

    }

    public void setOwner(org.bukkit.profile.@Nullable PlayerProfile profile) {
        this.owner = (GlowPlayerProfile) profile;
    }

    @Override
    public void setPlayerProfile(@NotNull PlayerProfile profile) {
        // TODO: 1.13 player profile API
    }

    @Override
    public void setSkullType(SkullType type) {
        if (type != SkullType.PLAYER) {
            owner = null;
        }
        this.skullType = type;
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw new UnsupportedOperationException();
    }
}
