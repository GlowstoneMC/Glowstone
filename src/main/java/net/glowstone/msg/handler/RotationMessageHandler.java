package net.glowstone.msg.handler;

import org.bukkit.Location;

import net.glowstone.entity.Player;
import net.glowstone.msg.RotationMessage;
import net.glowstone.net.Session;

public final class RotationMessageHandler extends MessageHandler<RotationMessage> {

	@Override
	public void handle(Session session, Player player, RotationMessage message) {
		if (player == null)
			return;

        Location loc = player.getLocation();
        loc.setYaw(message.getRotation());
        loc.setPitch(message.getPitch());
		player.setLocation(loc);
	}

}
