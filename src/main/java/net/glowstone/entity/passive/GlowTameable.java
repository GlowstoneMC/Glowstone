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

    protected boolean tamed;
    private AnimalTamer owner;
    @Getter
    private UUID ownerUuid;
    private boolean sitting;
    private MetadataIndex status = MetadataIndex.TAMEABLEAANIMAL_STATUS;
    private MetadataIndex ownerMetadata = MetadataIndex.TAMEABLEANIMAL_OWNER;

    public GlowTameable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    protected GlowTameable(Location location, EntityType type, double maxHealth,
        AnimalTamer owner) {
        super(location, type, maxHealth);
        if (owner != null) {
            this.owner = owner;
            metadata.set(ownerMetadata, owner.getUniqueId());
        }
    }

    @Override
    public boolean isTamed() {
        return tamed;
    }

    @Override
    public void setTamed(boolean isTamed) {
        metadata
            .setBit(status, TameableFlags.IS_TAME, isTamed); //TODO 1.9 The flag might need change
        tamed = isTamed;
    }

    @Override
    public AnimalTamer getOwner() {
        return owner instanceof Player ? owner : Bukkit.getPlayer(ownerUuid);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        if (animalTamer == null) {
            owner = null;
            ownerUuid = null;
            return;
        }
        owner = animalTamer;
        ownerUuid = animalTamer.getUniqueId();
        metadata.set(ownerMetadata, owner.getUniqueId());
    }

    /**
     * Added needed method for Storage to convert from UUID to owners.
     *
     * <p>The UUID's are validated through offline player checking.
     *
     * <p>If a player with the specified UUID has not played on the server before, the owner is not
     * set.
     *
     * @param ownerUuid The player UUID of the owner.
     */
    public void setOwnerUuid(UUID ownerUuid) {
        if (ownerUuid == null) {
            this.ownerUuid = null;
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(ownerUuid);
        if (player != null && player.hasPlayedBefore()) {
            this.ownerUuid = ownerUuid;
        }
    }

    /**
     * Checks if this animal is sitting.
     *
     * @return true if sitting
     */
    public boolean isSitting() {
        return sitting;
    }

    /**
     * Sets if this animal is sitting. Will remove any path that the animal
     * was following beforehand.
     *
     * @param isSitting true if sitting
     */
    public void setSitting(boolean isSitting) {
        metadata.setBit(status, TameableFlags.IS_SITTING,
            isSitting); //TODO 1.9 - This flag might need change
        sitting = isSitting;
    }

}
