package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.GlowVehicle;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GlowBoat extends GlowVehicle implements Boat {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.04;

    @Getter
    private TreeSpecies woodType;
    private boolean workOnLand;

    /**
     * Creates a boat.
     *
     * @param location the boat's location
     */
    public GlowBoat(Location location) {
        super(location);
        setSize(1.375f, 0.5625f);
        setAirDrag(0.95);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
        setWoodType(TreeSpecies.GENERIC);
    }

    @Override
    public EntityType getType() {
        return EntityType.BOAT;
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Arrays.asList(
            new SpawnObjectMessage(entityId, getUniqueId(),
                    EntityNetworkUtil.getObjectId(EntityType.BOAT), location),
            new EntityMetadataMessage(entityId, metadata.getEntryList())
        );
    }

    @Override
    public void reset() {
        super.reset();
        setDamage(getDamage() - 1);
        setHitTime(getHitTime() - 1);
    }

    @Override
    public boolean isEmpty() {
        return getPassengers().size() <= 2;
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() == Action.ATTACK.ordinal()) {
            damage(player);
            return true;
        }
        if (message.getAction() != Action.INTERACT.ordinal()) {
            return false;
        }
        if (player.isSneaking()) {
            return false;
        }
        if (isEmpty()) {
            addPassenger(player);
            return true;
        }
        return false;
    }

    private void damage(GlowPlayer player) {
        //TODO: Do proper damage calculations, based upon the tool used
        VehicleDamageEvent damageEvent = new VehicleDamageEvent(this, player, 10);
        if (EventFactory.getInstance().callEvent(damageEvent).isCancelled()) {
            return;
        }

        player.playSound(location, Sound.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
        setDamage(getDamage() + (float) damageEvent.getDamage());
        setHitTime(9);

        boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
        if (getDamage() > 40.0 || isCreative) {
            if (EventFactory.getInstance()
                    .callEvent(new VehicleDestroyEvent(this, player)).isCancelled()) {
                return;
            }
            remove();
            if (!isCreative) {
                world.dropItem(location, getItem());
            }
        }
    }

    private ItemStack getItem() {
        Material type = Material.BOAT;
        switch (woodType) {
            case REDWOOD:
                type = Material.BOAT_SPRUCE;
                break;
            case BIRCH:
                type = Material.BOAT_BIRCH;
                break;
            case JUNGLE:
                type = Material.BOAT_JUNGLE;
                break;
            case ACACIA:
                type = Material.BOAT_ACACIA;
                break;
            case DARK_OAK:
                type = Material.BOAT_DARK_OAK;
                break;
            default:
                type = Material.BOAT;
        }
        return new ItemStack(type);
    }

    private float getDamage() {
        return metadata.getFloat(MetadataIndex.BOAT_DAMAGE_TAKEN);
    }

    private void setDamage(float damage) {
        metadata.set(MetadataIndex.BOAT_DAMAGE_TAKEN, Math.max(damage, 0));
    }

    private int getHitTime() {
        return metadata.getInt(MetadataIndex.BOAT_HIT_TIME);
    }

    private void setHitTime(int time) {
        metadata.set(MetadataIndex.BOAT_HIT_TIME, Math.max(time, 0));
    }

    @Override
    public void setWoodType(TreeSpecies treeSpecies) {
        this.woodType = treeSpecies;
        metadata.set(MetadataIndex.BOAT_TYPE, this.woodType.ordinal());
    }

    @Override
    public double getMaxSpeed() {
        return 0.4;
    }

    @Override
    public void setMaxSpeed(double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getOccupiedDeceleration() {
        return 0.2;
    }

    @Override
    public void setOccupiedDeceleration(double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getUnoccupiedDeceleration() {
        return -1;
    }

    @Override
    public void setUnoccupiedDeceleration(double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getWorkOnLand() {
        return this.workOnLand;
    }

    @Override
    public void setWorkOnLand(boolean workOnLand) {
        this.workOnLand = workOnLand;
    }

    public boolean getRightPaddleTurning() {
        return metadata.getBoolean(MetadataIndex.BOAT_RIGHT_PADDLE_TURNING);
    }

    public void setRightPaddleTurning(boolean rightPaddleTurning) {
        metadata.set(MetadataIndex.BOAT_RIGHT_PADDLE_TURNING, rightPaddleTurning);
    }

    public boolean getLeftPaddleTurning() {
        return metadata.getBoolean(MetadataIndex.BOAT_LEFT_PADDLE_TURNING);
    }

    public void setLeftPaddleTurning(boolean leftPaddleTurning) {
        metadata.set(MetadataIndex.BOAT_LEFT_PADDLE_TURNING, leftPaddleTurning);
    }
}
