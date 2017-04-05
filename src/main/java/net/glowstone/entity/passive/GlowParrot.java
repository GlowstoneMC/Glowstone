package net.glowstone.entity.passive;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.entity.Parrot.Shoulder.LEFT;
import static org.bukkit.entity.Parrot.Shoulder.RIGHT;

public class GlowParrot extends GlowTameable implements Parrot {

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
        int variantId = metadata.getInt(MetadataIndex.PARROT_COLOR);
        return VARIANTS[(variantId >= VARIANTS.length || variantId < 0) ? 0 : variantId];
    }

    @Override
    public void setVariant(Variant variant) {
        metadata.set(MetadataIndex.PARROT_COLOR, variant.ordinal());
    }

    @Override
    public LivingEntity getImitatedEntity() {
        return null;
    }

    @Override
    public void setImitatedEntity(LivingEntity livingEntity) {

    }

    @Override
    public Player getSittingOn() {
        Player player = ((Player) getOwner());
        if (Objects.equals(this, player.getShoulderEntity(RIGHT)) || Objects.equals(this, player.getShoulderEntity(LEFT))) {
            return player;
        }
        return null;
    }

    @Override
    public void setSittingOn(Player player, Shoulder shoulder) {
        ((Player) getOwner()).setShoulderEntity(this, shoulder);
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
        if (!isTamed() && player.getItemInHand().getType() == Material.COOKIE) {
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
