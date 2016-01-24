package net.glowstone.net.handler.play.inv;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;

public class VehicleMoveHandler implements MessageHandler<GlowSession, VehicleMoveMessage> {
    @Override
    public void handle(GlowSession session, VehicleMoveMessage message) {
        //TODO: Sent when a player moves in a vehicle. Fields are the same as in Player Position And Look. Note that all fields use absolute positioning and do not allow for relative positioning.
    }
}
