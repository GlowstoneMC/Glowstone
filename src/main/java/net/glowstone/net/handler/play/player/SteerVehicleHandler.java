package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.SteerVehiclePacket;

public final class SteerVehicleHandler implements MessageHandler<GlowSession, SteerVehiclePacket> {
    @Override
    public void handle(GlowSession session, SteerVehiclePacket message) {
        GlowServer.logger.info(session + ": " + message);

        GlowPlayer player = session.getPlayer();

        if (message.isUnmount() && player.isInsideVehicle()) {
            player.leaveVehicle();
        }
        // todo
    }
}
