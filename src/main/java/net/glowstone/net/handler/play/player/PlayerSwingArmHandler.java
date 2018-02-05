package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import java.util.Set;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;
import org.bukkit.EntityAnimation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;

public final class PlayerSwingArmHandler implements
        MessageHandler<GlowSession, PlayerSwingArmMessage> {

    @Override
    public void handle(GlowSession session, PlayerSwingArmMessage message) {
        GlowPlayer player = session.getPlayer();

        Block block = player.getTargetBlock((Set<Material>) null, 6);

        if (block == null || block.isEmpty()) {
            if (EventFactory.onPlayerInteract(
                    player, Action.LEFT_CLICK_AIR, message.getHandSlot()).useItemInHand()
                    == Result.DENY) {
                return;
            }
            // todo: item interactions with air
        }

        if (!EventFactory.callEvent(new PlayerAnimationEvent(player)).isCancelled()) {
            // play the animation to others
            player.playAnimation(message.getHand() == 1 ? EntityAnimation.SWING_OFF_HAND
                    : EntityAnimation.SWING_MAIN_HAND);
        }
    }
}
