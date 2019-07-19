package net.glowstone.entity.passive;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Sets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class GlowWolf extends GlowTameable implements Wolf {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.BEEF,
            Material.COOKED_BEEF,
            Material.RABBIT,
            Material.COOKED_RABBIT,
            Material.MUTTON,
            Material.COOKED_MUTTON,
            Material.PORKCHOP,
            Material.COOKED_PORKCHOP,
            Material.CHICKEN,
            Material.COOKED_CHICKEN,
            Material.ROTTEN_FLESH);

    private static final DyeColor DEFAULT_COLLAR_COLOR = DyeColor.RED;

    /**
     * Creates a wolf with a random collar color.
     *
     * @param location the location
     */
    public GlowWolf(Location location) {
        super(location, EntityType.WOLF, 8);
        setCollarColor(DEFAULT_COLLAR_COLOR);
        setBoundingBox(0.6, 0.85);
    }

    @Override
    public boolean isAngry() {
        return metadata.getBit(MetadataIndex.TAMEABLEAANIMAL_STATUS,
                MetadataIndex.TameableFlags.WOLF_IS_ANGRY);
    }

    @Override
    public void setAngry(boolean angry) {
        metadata.setBit(MetadataIndex.TAMEABLEAANIMAL_STATUS,
                MetadataIndex.TameableFlags.WOLF_IS_ANGRY, angry);
    }

    @Override
    public DyeColor getCollarColor() {
        return DyeColor.getByDyeData(metadata.getByte(MetadataIndex.WOLF_COLOR));
    }

    @Override
    public void setCollarColor(DyeColor color) {
        checkNotNull(color);
        metadata.set(MetadataIndex.WOLF_COLOR, color.getDyeData());
    }

    /**
     * Gets whether the wolf is in the 'begging' state.
     *
     * @return whether the wolf is in the 'begging' state.
     */
    public boolean isBegging() {
        return metadata.getBoolean(MetadataIndex.WOLF_BEGGING);
    }

    /**
     * Sets whether the wolf is in the 'begging' state.
     *
     * @param begging whether the wolf is in the 'begging' state.
     */
    public void setBegging(boolean begging) {
        metadata.set(MetadataIndex.WOLF_BEGGING, begging);
    }

    @Override
    public void setTamed(boolean isTamed) {
        if (isTamed() != isTamed) {
            // Change max health of wolf when he's got tamed. See MinecraftWiki for more information
            if (isTamed && getMaxHealth() == 8) {
                setMaxHealth(20);
                setHealth(20);
            }
        }
        super.setTamed(isTamed);
    }

    @Override
    public void setOwner(AnimalTamer animalTamer) {
        // TODO
        super.setOwner(animalTamer);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        GlowServer.logger.info(String.valueOf(message));
        boolean result = super.entityInteract(player, message);
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            if (result) {
                return false;
            }
            ItemStack hand = InventoryUtil.itemOrEmpty(
                    player.getInventory().getItem(message.getHandSlot()));
            if (!isTamed() && hand.getType() == Material.BONE) {
                // One in 3 chances of taming
                if (ThreadLocalRandom.current().nextInt(3) == 0
                        && fireEntityTameEvent(player)) {
                    setCollarColor(DyeColor.RED);
                    setTamed(true);
                    setOwner(player);
                    world.spawnParticle(Particle.HEART, location, 1);
                    // Replenish health
                    setHealth(getMaxHealth());
                    setSitting(true);
                }
                player.getInventory().consumeItemInHand(message.getHandSlot());
                return true;
            }
            if (isTamed() && Objects.equals(getOwnerUniqueId(), player.getUniqueId())) {
                if (hand.getType() == Material.INK_SAC) {
                    Dye dye = (Dye) hand.getData();
                    DyeColor color = dye.getColor();
                    setCollarColor(color);
                    player.getInventory().consumeItemInHand(message.getHandSlot());
                    return true;
                }
                setSitting(!isSitting());
                return true;
            }
        }
        return false;
    }

    @Override
    public void setHealth(double health) {
        metadata.set(MetadataIndex.WOLF_HEALTH, (float) health);
        super.setHealth(health);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_WOLF_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_WOLF_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_WOLF_AMBIENT;
    }

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
