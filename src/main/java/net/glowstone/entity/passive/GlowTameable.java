package net.glowstone.entity.passive;

import java.util.UUID;
import lombok.Getter;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataIndex.TameableFlags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

public abstract class GlowTameable extends GlowAnimal implements Tameable {

    private static final MetadataIndex META_STATUS = MetadataIndex.TAMEABLEAANIMAL_STATUS;
    private static final MetadataIndex META_OWNER = MetadataIndex.TAMEABLEANIMAL_OWNER;

    private AnimalTamer owner;
    @Getter
    private UUID ownerUniqueId;

    public GlowTameable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    protected GlowTameable(Location location, EntityType type, double maxHealth,
                           AnimalTamer owner) {
        super(location, type, maxHealth);
        if (owner != null) {
            this.owner = owner;
            metadata.set(META_OWNER, owner.getUniqueId());
        }
    }

    @Override
    public boolean isTamed() {
        return metadata.getBit(META_STATUS, TameableFlags.IS_TAME);
    }

    @Override
    public void setTamed(boolean isTamed) {
        metadata.setBit(META_STATUS, TameableFlags.IS_TAME, isTamed);
    }

    @Override
    public AnimalTamer getOwner() {
        return owner instanceof Player ? owner : Bukkit.getPlayer(ownerUniqueId);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        if (animalTamer == null) {
            owner = null;
            ownerUniqueId = null;
            return;
        }
        owner = animalTamer;
        ownerUniqueId = animalTamer.getUniqueId();
        metadata.set(META_OWNER, owner.getUniqueId());
    }

    /**
     * Added needed method for Storage to convert from UUID to owners.
     *
     * <p>The UUID's are validated through offline player checking.
     *
     * <p>If a player with the specified UUID has not played on the server before, the owner is not
     * set.
     *
     * @param ownerUniqueId The player UUID of the owner.
     */
    public void setOwnerUniqueId(UUID ownerUniqueId) {
        if (ownerUniqueId == null) {
            this.ownerUniqueId = null;
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(ownerUniqueId);
        if (player != null && player.hasPlayedBefore()) {
            this.ownerUniqueId = ownerUniqueId;
        }
    }

    /**
     * Checks if this animal is sitting.
     *
     * @return true if sitting
     */
    public boolean isSitting() {
        return metadata.getBit(META_STATUS, TameableFlags.IS_SITTING);
    }

    /**
     * Sets if this animal is sitting. Will remove any path that the animal
     * was following beforehand.
     *
     * @param isSitting true if sitting
     */
    public void setSitting(boolean isSitting) {
        metadata.setBit(META_STATUS, TameableFlags.IS_SITTING, isSitting);
    }
}
