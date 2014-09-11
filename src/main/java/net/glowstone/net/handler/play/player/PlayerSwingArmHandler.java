package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

public final class PlayerSwingArmHandler implements MessageHandler<GlowSession, PlayerSwingArmMessage> {
    @Override
    public void handle(GlowSession session, PlayerSwingArmMessage message) {
        final GlowPlayer player = session.getPlayer();

        Block block = player.getTargetBlock(null, 6);
        if (block == null || block.isEmpty()) {
            if (EventFactory.onPlayerInteract(player, Action.LEFT_CLICK_AIR).isCancelled())
                return;
            // todo: item interactions with air
        }

        if (!EventFactory.onPlayerAnimate(player).isCancelled()) {
            // play the animation to others
            AnimateEntityMessage toSend = new AnimateEntityMessage(player.getEntityId(), AnimateEntityMessage.OUT_SWING_ARM);
            for (GlowPlayer observer : player.getWorld().getRawPlayers()) {
                if (observer != player && observer.canSee((GlowEntity) player)) {
                    observer.getSession().send(toSend);
                }
            }
        }
    }
}
