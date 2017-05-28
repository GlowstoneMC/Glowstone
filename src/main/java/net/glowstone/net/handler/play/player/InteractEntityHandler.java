package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.constants.AttackDamage;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

//import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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


        if (message.getAction() == Action.ATTACK.ordinal()) {
            if (target == null) {
                if (possibleTarget != null) {
                    possibleTarget.entityInteract(player, message);
                } else {
                    GlowServer.logger.info("Player " + player.getName() + " tried to attack an entity that does not exist");
                }
            } else if (!target.isDead() && target.canTakeDamage(DamageCause.ENTITY_ATTACK)) {
                // Calculate damage amount
                ItemStack hand = player.getItemInHand();
                Material type = hand == null ? Material.AIR : hand.getType();

                // todo: Actual critical hit check
                float damage = AttackDamage.getMeleeDamage(type, false);

                // Set entity on fire if the item has Fire Aspect
                if (hand.getEnchantments().containsKey(Enchantment.FIRE_ASPECT)) {
                    target.setFireTicks(target.getFireTicks() + hand.getEnchantments().get(Enchantment.FIRE_ASPECT) * 80);
                }

                // Apply damage. Calls the EntityDamageByEntityEvent
                target.damage(damage, player, DamageCause.ENTITY_ATTACK);
                player.incrementStatistic(Statistic.DAMAGE_DEALT, Math.round(damage));
                player.addExhaustion(0.1f);
                if (target.isDead()) {
                    player.incrementStatistic(target.getType() == EntityType.PLAYER ? Statistic.PLAYER_KILLS : Statistic.MOB_KILLS);
                }

                // Apply durability loss (if applicable)
                short durabilityLoss = AttackDamage.getMeleeDurabilityLoss(type);
                if (durabilityLoss > 0 && !InventoryUtil.isEmpty(hand) && player.getGameMode() != GameMode.CREATIVE) {
                    // Yes, this actually subtracts
                    hand.setDurability((short) (hand.getDurability() + durabilityLoss));
                }
            }
        } else if (message.getAction() == Action.INTERACT_AT.ordinal()) {
            //todo: Handle hand variable
            // todo: Interaction with entity at a specified location (X, Y, and Z are present in the message)
            // used for adjusting specific portions of armor stands
        } else if (message.getAction() == Action.INTERACT.ordinal()) {
            //Todo: Handle hand variable
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, possibleTarget);
            EventFactory.callEvent(event);

            if (!event.isCancelled()) {
                possibleTarget.entityInteract(player, message);
            }
        } else {
            GlowServer.logger.info("Player " + player.getName() + " sent unknown interact action: " + message.getAction());
        }
    }
}
