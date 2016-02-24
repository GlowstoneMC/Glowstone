package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataIndex.TameableFlags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;

import java.util.UUID;

public abstract class GlowTameable extends GlowAnimal implements Tameable {

    private AnimalTamer owner;
    private UUID ownerUUId;
    protected boolean tamed;
    private boolean sitting;
    private MetadataIndex flagIndex;

    public GlowTameable(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
        switch (type) {
            case WOLF:
                flagIndex = MetadataIndex.WOLF_FLAGS;
                break;
            case HORSE:
                flagIndex = MetadataIndex.HORSE_FLAGS;
                break;
            case OCELOT:
                flagIndex = MetadataIndex.OCELOT_TYPE;
                break;
        }
    }

    protected GlowTameable(Location location, EntityType type, double maxHealth, AnimalTamer owner) {
        super(location, type, maxHealth);
        this.owner = owner;
    }

    @Override
    public boolean isTamed() {
        return tamed;
    }

    @Override
    public void setTamed(boolean isTamed) {
        metadata.setBit(flagIndex, TameableFlags.IS_TAME, isTamed);
        this.tamed = isTamed;
    }

    @Override
    public AnimalTamer getOwner() {
        return owner instanceof Player ? owner : Bukkit.getPlayer(this.ownerUUId);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        if (animalTamer == null) {
            this.owner = null;
            this.ownerUUId = null;
            return;
        }
        this.owner = animalTamer;
        this.ownerUUId = animalTamer.getUniqueId();
    }

    public UUID getOwnerUUID() {
        return this.ownerUUId;
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
            this.ownerUUId = null;
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(ownerUUId);
        if (player != null && player.hasPlayedBefore()) {
            this.ownerUUId = ownerUUID;
        }
    }

    public boolean isSitting() {
        return sitting;
    }

    public void setSitting(boolean isSitting) {
        metadata.setBit(flagIndex, TameableFlags.IS_SITTING, isSitting);
        this.sitting = isSitting;
    }

}
