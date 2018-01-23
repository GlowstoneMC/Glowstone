package net.glowstone.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockSkull;
import net.glowstone.block.entity.SkullEntity;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.util.Position;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;

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
        return Bukkit.getOfflinePlayer(owner.getUniqueId());
    }

    @Override
    public void setOwningPlayer(OfflinePlayer offlinePlayer) {
        this.owner = new GlowPlayerProfile(offlinePlayer.getName(), offlinePlayer.getUniqueId());
    }

    @Override
    public void setSkullType(SkullType type) {
        if (type != SkullType.PLAYER) {
            owner = null;
        }
        this.skullType = type;
    }
}
