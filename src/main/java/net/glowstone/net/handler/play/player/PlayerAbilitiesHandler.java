package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerAbilitiesPacket;

public final class PlayerAbilitiesHandler implements MessageHandler<GlowSession, PlayerAbilitiesPacket> {
    @Override
    public void handle(GlowSession session, PlayerAbilitiesPacket message) {
        // player sends this when changing whether or not they are currently flying
        // other values should match what we've sent in the past but are ignored here

        GlowPlayer player = session.getPlayer();
        boolean flying = (message.getFlags() & 0x02) != 0;

        player.setFlying(player.getAllowFlight() && flying);
    }
}
