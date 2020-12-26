package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowBeaconInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.BeaconEffectMessage;

public final class BeaconEffectHandler implements MessageHandler<GlowSession, BeaconEffectMessage> {
    @Override
    public void handle(GlowSession session, BeaconEffectMessage message) {
        GlowPlayer player = session.getPlayer();

        // Verify that the current player is in a beacon
        if (!(player.getOpenInventory().getTopInventory() instanceof GlowBeaconInventory)) {
            return;
        }

        GlowBeaconInventory beacon = (GlowBeaconInventory) player.getOpenInventory().getTopInventory();
        beacon.setActiveEffects(message.getPrimary(), message.getSecondary());
    }
}
