package net.lightstone.model;

import java.util.ArrayList;
import java.util.List;

import net.lightstone.msg.Message;
import net.lightstone.msg.SpawnMobMessage;
import net.lightstone.util.Parameter;
import net.lightstone.world.World;

public final class Monster extends Mob {

	private final int type;
	private final List<Parameter<?>> metadata = new ArrayList<Parameter<?>>();

	public Monster(World world, int type) {
		super(world);
		this.type = type;
	}

	public int getType() {
		return type;
	}

	@Override
	public Message createSpawnMessage() {
		int x = position.getAbsX();
		int y = position.getAbsY();
		int z = position.getAbsZ();
		int yaw = rotation.getAbsYaw();
		int pitch = rotation.getAbsPitch();
		return new SpawnMobMessage(id, type, x, y, z, yaw, pitch, metadata);
	}

}
