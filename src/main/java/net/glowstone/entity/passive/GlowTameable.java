package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import java.util.UUID;

public abstract class GlowTameable extends GlowAnimal implements Tameable {

    private AnimalTamer owner;
    private UUID ownerUUId;
    private boolean tamed;

    public GlowTameable(Location location, EntityType type) {
        super(location, type);
    }

    protected GlowTameable(Location location, EntityType type, AnimalTamer owner) {
        super(location, type);
        this.owner = owner;
    }

    @Override
    public boolean isTamed() {
        return tamed;
    }

    @Override
    public void setTamed(boolean isTamed) {
        this.tamed = isTamed;
    }

    @Override
    public AnimalTamer getOwner() {
        return owner instanceof Player ? owner : Bukkit.getPlayer(this.ownerUUId);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
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
     * @param ownerUUID The player's UUID who is the owner of this entity.
     */
    public void setOwnerUUID(UUID ownerUUID) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(ownerUUId);
        if (player.hasPlayedBefore()) {
            this.ownerUUId = ownerUUID;
        }
    }
}
