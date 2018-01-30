package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.PlayerActionMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class PlayerActionHandler implements MessageHandler<GlowSession, PlayerActionMessage> {

    @Override
    public void handle(GlowSession session, PlayerActionMessage message) {
        GlowPlayer player = session.getPlayer();

        switch (message.getAction()) {
            case 0: // crouch
                player.setSneaking(true);
                break;
            case 1: // uncrouch
                player.setSneaking(false);
                break;
            case 2: // leave bed
                if (player.isSleeping()) {
                    player.leaveBed(true);
                }
                break;
            case 3: // start sprinting
                player.setSprinting(true);
                break;
            case 4: // stop sprinting
                player.setSprinting(false);
                break;
            case 5: // start jump with horse
                break;
            case 6: // stop jump with horse
                break;
            case 7: // open horse inventory
                break;
            case 8: // start gliding
                ItemStack chestplate = player.getInventory().getChestplate();
                boolean hasElytra = chestplate != null && chestplate.getType() == Material.ELYTRA
                    && chestplate.getDurability() < chestplate.getType().getMaxDurability();
                if (!player.isOnGround() && !player.isGliding() && !player.isInWater()
                    && hasElytra) {
                    player.setGliding(true);
                }
                break;
            default:
                // TODO: Should this raise a warning?
                // do nothing
        }
    }
}
