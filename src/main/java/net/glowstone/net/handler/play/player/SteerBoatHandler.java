package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowBoat;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.SteerBoatMessage;

public class SteerBoatHandler implements MessageHandler<GlowSession, SteerBoatMessage> {

    @Override
    public void handle(GlowSession session, SteerBoatMessage message) {
        GlowPlayer player = session.getPlayer();

        if (!player.isInsideVehicle()) {
            return;
        }

        if (!(player.getVehicle() instanceof GlowBoat)) {
            return;
        }

        GlowBoat boat = (GlowBoat) player.getVehicle();
        boat.setRightPaddleTurning(message.isRightPaddleTurning());
        boat.setLeftPaddleTurning(message.isLeftPaddleTurning());
    }
}
