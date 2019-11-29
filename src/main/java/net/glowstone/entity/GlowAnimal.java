package net.glowstone.entity;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import net.glowstone.entity.ai.EntityDirector;
import net.glowstone.entity.ai.MobState;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an Animal, such as a Cow.
 */
public class GlowAnimal extends GlowAgeable implements Animals {

    private static final Set<Material> DEFAULT_BREEDING_FOODS =
            Sets.immutableEnumSet(EnumSet.noneOf(Material.class));

    /**
     * Creates a new ageable animal.
     *
     * @param location The location of the animal.
     * @param type The type of animal.
     * @param maxHealth The max health of this animal.
     */
    public GlowAnimal(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
        if (type != null) {
            EntityDirector.registerEntityMobState(type, MobState.IDLE, "look_around");
            EntityDirector.registerEntityMobState(type, MobState.IDLE, "look_player");
        }
        setState(MobState.IDLE);
    }

    @Override
    protected int getAmbientDelay() {
        return 120;
    }

    /**
     * Determines whether this entity can eat an item while healthy, and if so, applies the effects
     * of eating it.
     *
     * @param player the player feeding the entity, for statistical purposes
     * @param type an item that may be food
     * @return true if the item should be consumed; false otherwise
     */
    protected boolean tryFeed(Material type, GlowPlayer player) {
        if (!getBreedingFoods().contains(type)) {
            return false;
        }
        if (canBreed() && getLoveModeTicks() <= 0) {
            setLoveModeTicks(1000); // TODO get the correct duration
            player.incrementStatistic(Statistic.ANIMALS_BRED);
            return true;
        }
        int growth = computeGrowthAmount(type);
        if (growth > 0) {
            grow(growth);
            return true;
        }
        return false;
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (!super.entityInteract(player, message)
                && message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            GameMode gameMode = player.getGameMode();
            if (gameMode == GameMode.SPECTATOR) {
                return false;
            }
            ItemStack item = player.getInventory().getItem(message.getHandSlot());
            if (InventoryUtil.isEmpty(item)) {
                return false;
            }
            boolean successfullyUsed = tryFeed(item.getType(), player);
            if (successfullyUsed && GameMode.CREATIVE != gameMode) {
                player.getInventory().consumeItem(message.getHand());
            }
            return successfullyUsed;
        }

        return false;
    }

    /**
     * Returns an immutable set containing the breeding foods for the current animal.
     * @return an immutable set containing Material
     */
    public Set<Material> getBreedingFoods() {
        return DEFAULT_BREEDING_FOODS;
    }

    @Override
    protected int computeGrowthAmount(Material material) {
        if (canGrow() && getBreedingFoods().contains(material)) {
            return Math.abs(getAge() / 10);
        }

        return 0;
    }

    @Override
    public @Nullable UUID getBreedCause() {
        // TODO: 1.13
        throw new NotImplementedException();
    }

    @Override
    public void setBreedCause(@Nullable UUID uuid) {
        // TODO: 1.13
        throw new NotImplementedException();
    }

    @Override
    public boolean isLoveMode() {
        return getLoveModeTicks() > 0;
    }
}
