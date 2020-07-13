package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.SetBeaconEffectMessage;

public final class SetBeaconEffectHandler implements MessageHandler<GlowSession, SetBeaconEffectMessage> {
    @Override
    public void handle(GlowSession session, SetBeaconEffectMessage message) {
        //TODO: handle packet
    }
}
