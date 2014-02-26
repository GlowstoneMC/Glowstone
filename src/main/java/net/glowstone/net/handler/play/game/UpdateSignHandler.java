package net.glowstone.net.handler.play.game;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.UpdateSignMessage;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public final class UpdateSignHandler implements MessageHandler<GlowSession, UpdateSignMessage> {
    public void handle(GlowSession session, UpdateSignMessage message) {
        final GlowPlayer player = session.getPlayer();

        GlowServer.logger.info(session + ": " + message);

        Location location = new Location(player.getWorld(), message.getX(), message.getY(), message.getZ());
        if (player.checkSignLocation(location)) {
            // update the sign if it's actually still there
            BlockState state = location.getBlock().getState();
            if (state instanceof Sign) {
                Sign sign = (Sign) state;
                for (int i = 0; i < message.getMessage().length; ++i) {
                    sign.setLine(i, message.getMessage()[i]);
                }
                sign.update();
            }
        } else {
            // player shouldn't be editing this sign
            GlowServer.logger.warning(session + " tried to edit sign at " + location);
        }
    }
}
