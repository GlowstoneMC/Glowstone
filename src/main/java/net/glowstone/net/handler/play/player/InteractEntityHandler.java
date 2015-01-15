package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.constants.AttackDamage;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public final class InteractEntityHandler implements MessageHandler<GlowSession, InteractEntityMessage> {

    @Override
    public void handle(GlowSession session, InteractEntityMessage message) {
        GlowPlayer player = session.getPlayer();

        // You can't do anything when you're dead
        if (player.isDead()) {
            GlowServer.logger.info("Player " + player.getName() + " tried to interact with an entity while dead");
            return;
        }

        GlowEntity possibleTarget = player.getWorld().getEntityManager().getEntity(message.getId());
        GlowLivingEntity target = possibleTarget instanceof GlowLivingEntity ? (GlowLivingEntity) possibleTarget : null;

        if (message.getAction() == InteractEntityMessage.Action.ATTACK.ordinal()) {
            if (target == null) {
                GlowServer.logger.info("Player " + player.getName() + " tried to attack an entity that does not exist");
            } else if (!target.isDead() && target.canTakeDamage(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                // Calculate damage amount
                ItemStack hand = player.getItemInHand();
                Material type = hand == null ? Material.AIR : hand.getType();

                // todo: Actual critical hit check
                float damage = AttackDamage.getMeleeDamage(type, false);

                // Apply damage. Calls the EntityDamageByEntityEvent
                target.damage(damage, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK);

                // Apply durability loss (if applicable)
                short durabilityLoss = AttackDamage.getMeleeDurabilityLoss(type);
                if (durabilityLoss > 0 && hand != null && player.getGameMode() != GameMode.CREATIVE) {
                    // Yes, this actually subtracts
                    hand.setDurability((short) (hand.getDurability() + durabilityLoss));
                }
            }
        } else if (message.getAction() == InteractEntityMessage.Action.INTERACT_AT.ordinal()) {
            // todo: Interaction with entity at a specified location (X, Y, and Z are present in the message)
            // used for adjusting specific portions of armor stands
        } else if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            // todo: Interaction with something (X, Y, and Z are NOT present in the message)
        } else {
            GlowServer.logger.info("Player " + player.getName() + " sent unknown interact action: " + message.getAction());
        }
    }
}
