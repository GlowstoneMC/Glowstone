package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;

import java.util.Set;

public final class PlayerSwingArmHandler implements MessageHandler<GlowSession, PlayerSwingArmMessage> {
    @Override
    public void handle(GlowSession session, PlayerSwingArmMessage message) {
        final GlowPlayer player = session.getPlayer();

        Block block;
        try {
            block = player.getTargetBlock((Set<Material>) null, 6);
        } catch (IllegalStateException ex) {
            // getTargetBlock failed to find any block at all
            block = null;
        }

        if (block == null || block.isEmpty()) {
            if (EventFactory.onPlayerInteract(player, Action.LEFT_CLICK_AIR).useItemInHand() == Event.Result.DENY)
                return;
            // todo: item interactions with air
        }

        if (!EventFactory.callEvent(new PlayerAnimationEvent(player)).isCancelled()) {
            // play the animation to others
            AnimateEntityMessage toSend = new AnimateEntityMessage(player.getEntityId(), AnimateEntityMessage.OUT_SWING_ARM);
            player.getWorld().getRawPlayers().stream().filter(observer -> observer != player && observer.canSeeEntity(player)).forEach(observer -> observer.getSession().send(toSend));
        }
    }
}
