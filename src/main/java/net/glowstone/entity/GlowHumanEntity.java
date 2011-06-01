package net.glowstone.entity;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.glowstone.GlowWorld;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnPlayerMessage;
import net.glowstone.util.Position;
import net.glowstone.inventory.GlowPlayerInventory;

/**
 *
 * @author Tad
 */
public abstract class GlowHumanEntity extends GlowLivingEntity implements HumanEntity {

    /**
     * The name of this human.
     */
	private final String name;
    
    /**
     * The inventory of this human.
     */
    private final GlowPlayerInventory inventory = new GlowPlayerInventory();
    
    /**
     * Whether this human is sleeping or not.
     */
    protected boolean sleeping = false;
    
    /**
     * How long this human has been sleeping.
     */
    private int sleepingTicks = 0;
    
    /**
     * Creates a human within the specified world and with the specified name.
     * @param world The world.
     * @param name The human's name.
     */
	public GlowHumanEntity(GlowWorld world, String name) {
		super(world);
        this.name = name;
	}

    @Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		int yaw = Position.getIntYaw(location);
		int pitch = Position.getIntPitch(location);
		return new SpawnPlayerMessage(id, name, x, y, z, yaw, pitch, 0);
	}

    public String getName() {
        return name;
    }

    public GlowPlayerInventory getInventory() {
        return inventory;
    }

    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    public void setItemInHand(ItemStack item) {
        getInventory().setItemInHand(item);
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public int getSleepTicks() {
        return sleepingTicks;
    }
    
	@Override
	public void pulse() {
        super.pulse();
        if (sleeping) {
            ++sleepingTicks;
        } else {
            sleepingTicks = 0;
        }
    }
}
