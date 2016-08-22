package net.glowstone.entity.passive;

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

import java.util.UUID;

public abstract class GlowTameable extends GlowAnimal implements Tameable {

    protected boolean tamed;
    private AnimalTamer owner;
    private UUID ownerUUId;
    private boolean sitting;
    private MetadataIndex status = MetadataIndex.TAMEABLEAANIMAL_STATUS;
    private MetadataIndex ownerMetadata = MetadataIndex.TAMEABLEANIMAL_OWNER;

    public GlowTameable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    protected GlowTameable(Location location, EntityType type, double maxHealth, AnimalTamer owner) {
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
        metadata.setBit(status, TameableFlags.IS_TAME, isTamed); //TODO 1.9 The flag might need change
        tamed = isTamed;
    }

    @Override
    public AnimalTamer getOwner() {
        return owner instanceof Player ? owner : Bukkit.getPlayer(ownerUUId);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        if (animalTamer == null) {
            owner = null;
            ownerUUId = null;
            return;
        }
        owner = animalTamer;
        ownerUUId = animalTamer.getUniqueId();
        metadata.set(ownerMetadata, owner.getUniqueId());
    }

    public UUID getOwnerUUID() {
        return ownerUUId;
    }

    /**
     * Added needed method for Storage to convert from UUID to owners.
     * The UUID's are validated through offline player checking. If a player
     * with the specified UUID has not played on the server before, the
     * owner is not set.
     *
     * @param ownerUUID The player UUID of the owner.
     */
    public void setOwnerUUID(UUID ownerUUID) {
        if (ownerUUID == null) {
            ownerUUId = null;
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(ownerUUId);
        if (player != null && player.hasPlayedBefore()) {
            ownerUUId = ownerUUID;
        }
    }

    public boolean isSitting() {
        return sitting;
    }

    public void setSitting(boolean isSitting) {
        metadata.setBit(status, TameableFlags.IS_SITTING, isSitting); //TODO 1.9 - This flag might need change
        sitting = isSitting;
    }

}
