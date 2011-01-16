package net.lightstone.model;

import net.lightstone.msg.EntityRotationMessage;
import net.lightstone.msg.EntityTeleportMessage;
import net.lightstone.msg.Message;
import net.lightstone.msg.RelativeEntityPositionMessage;
import net.lightstone.msg.RelativeEntityPositionRotationMessage;
import net.lightstone.world.World;

public abstract class Mob extends Entity {

	public Mob(World world) {
		super(world);
	}

	@Override
	public Message createUpdateMessage() {
		boolean moved = !position.equals(previousPosition);
		boolean rotated = !rotation.equals(previousRotation);

		int x = position.getAbsX();
		int y = position.getAbsY();
		int z = position.getAbsZ();

		int dx = x - previousPosition.getAbsX();
		int dy = y - previousPosition.getAbsY();
		int dz = z - previousPosition.getAbsZ();

		boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

		int yaw = rotation.getAbsYaw();
		int pitch = rotation.getAbsPitch();

		if (moved && teleport) {
			return new EntityTeleportMessage(id, x, y, z, yaw, pitch);
		} else if (moved && rotated) {
			return new RelativeEntityPositionRotationMessage(id, dx, dy, dz, yaw, pitch);
		} else if (moved) {
			return new RelativeEntityPositionMessage(id, dx, dy, dz);
		} else if (rotated) {
			return new EntityRotationMessage(id, yaw, pitch);
		}

		return null;
	}

}
