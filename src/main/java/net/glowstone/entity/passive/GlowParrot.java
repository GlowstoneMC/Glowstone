package net.glowstone.entity.passive;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.SoundUtil;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static net.glowstone.entity.passive.GlowParrot.Shoulder.LEFT;
import static net.glowstone.entity.passive.GlowParrot.Shoulder.RIGHT;

public class GlowParrot extends GlowTameable implements Parrot {
    public enum Shoulder {
        LEFT,
        RIGHT
    }

    public static final Variant[] VARIANTS = Variant.values();
    private int endOfLife = 0;

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

    public Player getSittingOn() {
        Player player = ((Player) getOwner());
        if (Objects.equals(this, player.getShoulderEntityRight()) || Objects.equals(this, player.getShoulderEntityLeft())) {
            return player;
        }
        return null;
    }

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
        if (endOfLife != 0) {
            return false;
        }
        boolean result = super.entityInteract(player, message);
        if (result) {
            return false;
        }
        if (player.getItemInHand().getType() == Material.COOKIE) {
            damage(getHealth(), player, EntityDamageEvent.DamageCause.ENTITY_ATTACK);
            world.spawnParticle(Particle.SPELL, location, 1);
        } else if (!isTamed() && player.getItemInHand().getType() == Material.SEEDS) {
            if (ThreadLocalRandom.current().nextInt(3) == 0) {
                setTamed(true);
                setOwner(player);
            }
            world.playSound(getLocation(), Sound.ENTITY_PARROT_EAT, 1.0F, SoundUtil.randomReal(0.2F) + 1F);
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            if (player.getItemInHand().getAmount() == 0) {
                player.setItemInHand(InventoryUtil.createEmptyStack());
            }
            return true;
        }
        // TODO: sitting only happens on crouch
        if (isTamed() && getOwnerUUID() != null && getOwnerUUID().equals(player.getUniqueId())) {
            if (!player.getLeftShoulderTag().isEmpty() && !player.getRightShoulderTag().isEmpty()) {
                return super.entityInteract(player, message);
            }
            if (player.getLeftShoulderTag().isEmpty()) {
                setSittingOn(player, LEFT);
            } else {
                setSittingOn(player, RIGHT);
            }
            return true;
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
}
