package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.UpdateSignMessage;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;

public final class UpdateSignHandler implements MessageHandler<GlowSession, UpdateSignMessage> {

    @Override
    public void handle(GlowSession session, UpdateSignMessage message) {
        GlowPlayer player = session.getPlayer();
        Location location = new Location(player.getWorld(), message.getX(), message.getY(),
            message.getZ());

        if (!player.checkSignLocation(location)) {
            GlowServer.logger.warning(session + " tried to edit sign at " + location);
            return;
        }
        // filter out json messages that aren't plaintext
        String[] lines = new String[4];
        for (int i = 0; i < lines.length; ++i) {
            lines[i] = message.getMessage()[i].asPlaintext();
        }
        SignChangeEvent event = new SignChangeEvent(location.getBlock(), player, lines);
        session.getServer().getEventFactory().callEvent(event);
        if (event.isCancelled()) {
            GlowServer.logger.warning("Sign was cancelled");
            return;
        }
        // update the sign if it's actually still there
        BlockState state = location.getBlock().getState();
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            for (int i = 0; i < lines.length; ++i) {
                sign.setLine(i, lines[i]);
            }
            sign.update();
        }
    }
}
