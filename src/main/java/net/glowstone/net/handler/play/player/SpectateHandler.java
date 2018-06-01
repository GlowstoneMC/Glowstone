package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import java.util.Objects;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.SpectateMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;

public final class SpectateHandler implements MessageHandler<GlowSession, SpectateMessage> {

    @Override
    public void handle(GlowSession session, SpectateMessage message) {

        GlowPlayer player = session.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR && !Objects
            .equals(player.getProfile().getId(), message.getTarget())) {
            Entity entity = Bukkit.getEntity(message.getTarget());

            if (entity != null) {
                player.setSpectatorTarget(entity);
            }
        }
    }
}
