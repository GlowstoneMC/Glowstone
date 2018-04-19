package net.glowstone.entity.passive;

import static net.glowstone.entity.passive.GlowParrot.Shoulder.LEFT;
import static net.glowstone.entity.passive.GlowParrot.Shoulder.RIGHT;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class GlowParrot extends GlowTameable implements Parrot {

    public static final Variant[] VARIANTS = Variant.values();
    private int endOfLife = 0;

    /**
     * Creates a parrot of a random variant.
     *
     * @param location the initial location
     */
    public GlowParrot(Location location) {
        super(location, EntityType.PARROT, 6);
        setBoundingBox(0.5, 1.0);
        setSitting(false);
        setVariant(VARIANTS[ThreadLocalRandom.current().nextInt(VARIANTS.length)]);
    }

    @Override
    public void pulse() {
        super.pulse();
        if (endOfLife == ticksLived) {
            remove();
        }
    }

    @Override
    public Variant getVariant() {
        int variantId = metadata.getInt(MetadataIndex.PARROT_VARIANT);
        return VARIANTS[(variantId >= VARIANTS.length || variantId < 0) ? 0 : variantId];
    }

    @Override
    public void setVariant(Variant variant) {
        metadata.set(MetadataIndex.PARROT_VARIANT, variant.ordinal());
    }

    public LivingEntity getImitatedEntity() {
        return null;
    }

    public void setImitatedEntity(LivingEntity livingEntity) {

    }

    /**
     * Returns the owner, if this parrot is sitting on its owner's shoulder.
     *
     * @return the owner, if this parrot is sitting on its owner's shoulder, or null otherwise
     */
    public Player getSittingOn() {
        Player player = ((Player) getOwner());
        if (Objects.equals(this, player.getShoulderEntityRight()) || Objects
            .equals(this, player.getShoulderEntityLeft())) {
            return player;
        }
        return null;
    }

    /**
     * Sits on the given player's shoulder.
     *
     * @param player the player whose shoulder to sit on
     * @param shoulder which shoulder to sit on
     */
    public void setSittingOn(Player player, Shoulder shoulder) {
        if (shoulder == LEFT) {
            player.setShoulderEntityLeft(this);
        } else {
            player.setShoulderEntityRight(this);
        }
        endOfLife = ticksLived + 1;
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            if (endOfLife != 0) {
                return false;
            }
            boolean result = super.entityInteract(player, message);
            if (result) {
                return false;
            }
            ItemStack hand = InventoryUtil
                .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));
            if (hand.getType() == Material.COOKIE) {
                damage(getHealth(), player, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
                world.spawnParticle(Particle.SPELL, location, 1);
                player.getInventory().consumeItemInMainHand();
            } else if (!isTamed() && hand.getType() == Material.SEEDS) {
                if (ThreadLocalRandom.current().nextInt(3) == 0) {
                    setTamed(true);
                    setOwner(player);
                    world.spawnParticle(Particle.HEART, location, 1);
                }
                world.playSound(getLocation(), Sound.ENTITY_PARROT_EAT, 1.0F,
                    SoundUtil.randomReal(0.2F) + 1F);
                player.getInventory().consumeItemInMainHand();
                return true;
            }
            // TODO: sitting only happens on crouch
            if (isTamed() && getOwnerUniqueId() != null && getOwnerUniqueId()
                .equals(player.getUniqueId())) {
                if (!player.getLeftShoulderTag().isEmpty() && !player.getRightShoulderTag()
                    .isEmpty()) {
                    return super.entityInteract(player, message);
                }
                if (player.getLeftShoulderTag().isEmpty()) {
                    setSittingOn(player, LEFT);
                } else {
                    setSittingOn(player, RIGHT);
                }
                return true;
            }
        }
        return true;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_PARROT_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_PARROT_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_PARROT_AMBIENT;
    }

    public enum Shoulder {
        LEFT,
        RIGHT
    }
}
