package net.glowstone.model;

import net.glowstone.msg.EntityRotationMessage;
import net.glowstone.msg.EntityTeleportMessage;
import net.glowstone.msg.Message;
import net.glowstone.msg.RelativeEntityPositionMessage;
import net.glowstone.msg.RelativeEntityPositionRotationMessage;
import net.glowstone.world.World;

/**
 * A Mob is a {@link Player} or {@link Monster}.
 * @author Graham Edgecombe.
 */
public abstract class Mob extends Entity {

    /**
     * Creates a mob within the specified world.
     * @param world The world.
     */
	public Mob(World world) {
		super(world);
	}

	@Override
	public Message createUpdateMessage() {
		boolean moved = !position.equals(previousPosition);
		boolean rotated = !rotation.equals(previousRotation);

		int x = position.getIntX();
		int y = position.getIntY();
		int z = position.getIntZ();

		int dx = x - previousPosition.getIntX();
		int dy = y - previousPosition.getIntY();
		int dz = z - previousPosition.getIntZ();

		boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

		int yaw = rotation.getIntYaw();
		int pitch = rotation.getIntPitch();

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
