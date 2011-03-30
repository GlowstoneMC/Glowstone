package net.lightstone.model;

import java.util.ArrayList;
import java.util.List;

import net.lightstone.msg.Message;
import net.lightstone.msg.SpawnMobMessage;
import net.lightstone.util.Parameter;
import net.lightstone.world.World;

/**
 * Represents a monster such as a creeper.
 * @author Graham Edgecombe
 */
public final class Monster extends Mob {

    /**
     * The type of monster.
     */
	private final int type;

    /**
     * The monster's metadata.
     */
	private final List<Parameter<?>> metadata = new ArrayList<Parameter<?>>();

    /**
     * Creates a new monster.
     * @param world The world this monster is in.
     * @param type The type of monster.
     */
	public Monster(World world, int type) {
		super(world);
		this.type = type;
	}

    /**
     * Gets the type of monster.
     * @return The type of monster.
     */
	public int getType() {
		return type;
	}

	@Override
	public Message createSpawnMessage() {
		int x = position.getIntX();
		int y = position.getIntY();
		int z = position.getIntZ();
		int yaw = rotation.getIntYaw();
		int pitch = rotation.getIntPitch();
		return new SpawnMobMessage(id, type, x, y, z, yaw, pitch, metadata);
	}

}
