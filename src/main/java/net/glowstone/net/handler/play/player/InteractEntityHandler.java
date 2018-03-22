package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.constants.AttackDamage;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;
import net.glowstone.util.InventoryUtil;
import org.bukkit.EntityAnimation;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class InteractEntityHandler implements
    MessageHandler<GlowSession, InteractEntityMessage> {

    @Override
    public void handle(GlowSession session, InteractEntityMessage message) {
        GlowPlayer player = session.getPlayer();
        EventFactory eventFactory = EventFactory.getInstance();

        // You can't do anything when you're dead
        if (player.isDead()) {
            GlowServer.logger.info(
                "Player " + player.getName() + " tried to interact with an entity while dead");
            return;
        }

        GlowEntity possibleTarget = player.getWorld().getEntityManager().getEntity(message.getId());
        GlowLivingEntity target =
            possibleTarget instanceof GlowLivingEntity ? (GlowLivingEntity) possibleTarget : null;

        EquipmentSlot hand = message.getHandSlot();
        if (message.getAction() == Action.ATTACK.ordinal()) {
            if (target == null) {
                if (possibleTarget != null) {
                    possibleTarget.entityInteract(player, message);
                } else {
                    GlowServer.logger.info("Player " + player.getName()
                        + " tried to attack an entity that does not exist");
                }
            } else if (!target.isDead() && target.canTakeDamage(DamageCause.ENTITY_ATTACK)) {
                // Calculate damage amount
                ItemStack itemInHand = InventoryUtil
                    .itemOrEmpty(player.getInventory().getItem(hand));
                Material type = itemInHand.getType();

                boolean critical =
                    player.getFallDistance() > 0.0F && !player.isOnGround() && !player.isInWater()
                        && !player.isInsideVehicle() && !player.isSprinting();
                float damage = AttackDamage.getMeleeDamage(type, critical);
                if (critical) {
                    // Critical-hit effect
                    target.playAnimation(EntityAnimation.CRITICAL_HIT);
                }

                // Set entity on fire if the item has Fire Aspect
                if (itemInHand.containsEnchantment(Enchantment.FIRE_ASPECT)) {
                    target.setFireTicks(target.getFireTicks()
                        + itemInHand.getEnchantmentLevel(Enchantment.FIRE_ASPECT) * 80);
                }
                boolean showMagicCrit = false;
                // Shows the "magic crit" particles (blue) if the weapon was a sword or an axe (and
                // with a damaging enchantment)
                // Apply other enchantments that amplify damage
                if (itemInHand.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                    // Sharpness
                    int level = itemInHand.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                    if (level > 0) {
                        damage += 1.0F + 0.5F * (level - 1);
                    }
                    if (!showMagicCrit) {
                        showMagicCrit = ToolType.SWORD.matches(type) || ToolType.AXE.matches(type);
                    }
                }
                if (itemInHand.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)) {
                    // Bane of Arthropods (applies to Spiders, Cave Spiders, Silverfish and
                    // Endermites)
                    if (target.isArthropod()) {
                        int level = itemInHand.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS);
                        if (level > 0) {
                            damage += level * 2.5F;
                            // TODO: add Slowness potion effect (after damaging and checking for
                            // event-cancellation)
                        }
                    }
                    if (!showMagicCrit) {
                        showMagicCrit = ToolType.SWORD.matches(type) || ToolType.AXE.matches(type);
                    }
                }
                if (itemInHand.containsEnchantment(Enchantment.DAMAGE_UNDEAD)) {
                    // Smite (applies to "undead" mobs)
                    if (target.isUndead()) {
                        int level = itemInHand.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD);
                        damage += level * 2.5F;
                    }
                    if (!showMagicCrit) {
                        showMagicCrit = ToolType.SWORD.matches(type) || ToolType.AXE.matches(type);
                    }
                }
                if (showMagicCrit) {
                    target.playAnimation(EntityAnimation.MAGIC_CRITICAL_HIT);
                }

                // Apply damage. Calls the EntityDamageByEntityEvent
                target.damage(damage, player, DamageCause.ENTITY_ATTACK);
                player.incrementStatistic(Statistic.DAMAGE_DEALT, Math.round(damage));
                player.addExhaustion(0.1f);
                if (target.isDead()) {
                    player.incrementStatistic(
                        target.getType() == EntityType.PLAYER ? Statistic.PLAYER_KILLS
                            : Statistic.MOB_KILLS);
                }

                // Apply durability loss (if applicable)
                short durabilityLoss = AttackDamage.getMeleeDurabilityLoss(type);
                if (durabilityLoss > 0 && !InventoryUtil.isEmpty(itemInHand)
                    && player.getGameMode() != GameMode.CREATIVE) {
                    // Yes, this actually subtracts
                    itemInHand.setDurability((short) (itemInHand.getDurability() + durabilityLoss));
                }
            }
        } else if (message.getAction() == Action.INTERACT_AT.ordinal()) {
            // used for adjusting specific portions of armor stands
            PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(player,
                possibleTarget,
                new Vector(message.getTargetX(), message.getTargetY(), message.getTargetZ()), hand);
            eventFactory.callEvent(event);

            if (!event.isCancelled()) {
                possibleTarget.entityInteract(player, message);
            }
        } else if (message.getAction() == Action.INTERACT.ordinal()) {
            //Todo: Handle hand variable
            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, possibleTarget,
                hand);
            eventFactory.callEvent(event);

            if (!event.isCancelled()) {
                possibleTarget.entityInteract(player, message);
            }
        } else {
            GlowServer.logger.info(
                "Player " + player.getName() + " sent unknown interact action: " + message
                    .getAction());
        }
    }
}
